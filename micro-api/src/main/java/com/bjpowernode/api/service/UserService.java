package com.bjpowernode.api.service;

import com.bjpowernode.api.model.ServiceResult;
import com.bjpowernode.api.po.User;

//用户服务接口
public interface UserService {

    /**
     * 注册用户总数
     */
    Integer registAllUserCount();

    /**
     * 根据手机号查询User
     * @param phone 手机号
     * @return
     */
    User queryUserByPhone(String phone);

    /**
     * 注册用户
     * @param phone 手机号
     * @param mima  密码
     * @param loginIp 登录ip
     * @param loginDevice 登录设备
     * @return
     */
    User userPhoneRegist(String phone, String mima,String loginIp, String loginDevice);

    /**
     * @param userId 用户的主键id
     * @param idCard 身份证号
     * @param name   姓名
     * @return true:认证成功；false需要重新认证（每天只能认证3次）
     */
    boolean modifyRealName(Integer userId, String idCard, String name);

    ServiceResult userLogin(String phone, String loginPassword, String ip, String devices);
}
