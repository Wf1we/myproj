package com.bjpowernode.dataservice.service;

import com.bjpowernode.api.model.ServiceResult;
import com.bjpowernode.api.po.FinanceAccount;
import com.bjpowernode.api.po.User;
import com.bjpowernode.api.service.UserService;
import com.bjpowernode.consts.YLBConsts;
import com.bjpowernode.consts.YLBKey;
import com.bjpowernode.dataservice.mapper.FinanceAccountMapper;
import com.bjpowernode.dataservice.mapper.UserMapper;
import com.bjpowernode.util.RedisOperation;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户服务接口
 */
@DubboService(interfaceClass = UserService.class,version = "1.0")
public class UserServiceImpl implements UserService {

    @Value("${mima.salt}")
    private String salt;

    @Resource
    private UserMapper userMapper;

    @Resource
    private FinanceAccountMapper accountMapper;

    @Resource
    private RedisOperation redisOperation;

    /**
     * @return 注册总用户数量
     * 1.先从redis获取数据
     * 2.如果有数据，直接返回给调用方
     * 3.如果没有数据，从mybatis查询结果
     * 4.把数据存放到redis，设置30分钟过期
     * 5.返回数据给调用方
     */
    @Override
    public  Integer registAllUserCount() {
        //1.先从redis获取数据
        String key = YLBKey.PLAT_REGISTER_USER_COUNT;
        Integer userCount  = (Integer) redisOperation.getString(key);
        System.out.println("直接获取redis数据："+userCount);
        if( userCount == null ){
            synchronized (this){
                //在查一次redis
                userCount  = (Integer) redisOperation.getString(key);
                System.out.println("在同步块中，查询数据："+userCount);
                if(userCount == null){
                    //redis没有数据，从mybatis查询结果
                    userCount = userMapper.selectRegisterUserCount();
                    System.out.println("在同步块中，查询mybatis数据："+userCount);
                    //把数据存放到redis，设置30分钟过期
                    redisOperation.setString(key,userCount,30);
                    System.out.println("在同步块中，存数据到redis数据："+userCount);
                }
            }
        }
        return userCount;
    }

    /**
     * 根据手机号查询User
     * @param phone 手机号
     * @return
     */
    @Override
    public User queryUserByPhone(String phone) {
        User  user  = userMapper.selectByPhone(phone);
        return user;
    }

    /**
     * 注册用户
     * @param phone 手机号
     * @param mima  密码
     * @param loginIp 登录ip
     * @param loginDevice 登录设备
     * @return
     *
     * 1.添加数据到u_user
     * 2.添加资金信息 u_finance_account
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User userPhoneRegist(String phone, String mima,
                                String loginIp, String loginDevice) {

        User user  =  null;
        if(StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(mima)){
            user  = userMapper.selectByPhone(phone);
            if( user == null ){
                //添加数据到u_user表. 对原始的密码值加盐（salt） 再Md5, 存到数据库
                String mimaMd5 = DigestUtils.md5Hex(  mima+salt );
                User userInfo = new User();
                userInfo.setPhone(phone);
                userInfo.setLoginPassword(mimaMd5);
                userInfo.setLoginIp(loginIp);
                userInfo.setLoginDevice(loginDevice);
                userInfo.setAddTime( new Date());
                userMapper.insertBackNewId(userInfo);

                //获取主键id
                FinanceAccount account = new FinanceAccount();
                account.setUid(userInfo.getId());
                account.setAvailableMoney(new BigDecimal("888"));
                accountMapper.insertSelective(account);
                return userInfo;
            }
        }

        return null;
    }

    /**
     * @param userId 用户的主键id
     * @param idCard 身份证号
     * @param name   姓名
     * @return true:认证成功；false需要重新认证（每天只能认证3次）
     */
    @Override
    public boolean modifyRealName(Integer userId, String idCard, String name) {
        boolean result = false;
        if( userId != null && userId > 0
                && StringUtils.isNotBlank(idCard)
                && StringUtils.isNotBlank(name)){
            User user  = new User();
            user.setId(userId);
            user.setName(name);
            user.setIdCard(idCard);
            int rows  = userMapper.updateByPrimaryKeySelective(user);
            result = rows > 0;
        }
        return result;
    }


    /**
     * 登录
     * @param phone          手机号
     * @param loginPassword  密码
     * @param ip             登录ip
     * @param devices        设备
     * @return
     */
    @Override
    public ServiceResult userLogin(String phone, String loginPassword, String ip, String devices) {
        ServiceResult result  = new ServiceResult(false);

        if( StringUtils.isAnyBlank(phone,loginPassword,ip,devices)){
            result.setCode(30001);
            result.setMsg("请求数据服务，数据异常");
        } else {
            //查询数据库是否有数据
            loginPassword = DigestUtils.md5Hex(loginPassword +salt );
            User user  = userMapper.selectLoginUser(phone,loginPassword);
            if( user != null){
                //登录成功，数据正确，更新用户的登录时间
                User updateUser = new User();
                updateUser.setId(user.getId());
                updateUser.setLastLoginTime(new Date());
                userMapper.updateByPrimaryKeySelective(updateUser);

                result.setResult(true);
                result.setData(user);
                //判断是否异地登录
                if( ip.equals(user.getLoginIp()) ){
                    result.setCode(30000);
                    result.setMsg("登录成功");
                } else {
                    result.setCode(30002);
                    result.setMsg("登录地址不是常用地址");
                }
                //判断设备
                String platString = devices.substring(devices.indexOf(" ") + 2);
                String platInfo = platString.substring(0,platString.indexOf(";"));
                if( !user.getLoginDevice().contains(platInfo)){
                    //不是常用设备，记录日志
                }

            } else {
                result.setCode(30003);
                result.setMsg("登录失败，手机号或者密码错误");
            }

        }


        return result;
    }
}
