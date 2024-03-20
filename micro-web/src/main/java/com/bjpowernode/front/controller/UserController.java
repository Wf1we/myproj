package com.bjpowernode.front.controller;

import com.bjpowernode.api.model.InvestInfo;
import com.bjpowernode.api.model.ServiceResult;
import com.bjpowernode.api.po.FinanceAccount;
import com.bjpowernode.api.po.RechargeRecord;
import com.bjpowernode.api.po.User;
import com.bjpowernode.code.RespCode;
import com.bjpowernode.consts.YLBConsts;
import com.bjpowernode.front.service.RealNameService;
import com.bjpowernode.front.service.SmsService;
import com.bjpowernode.util.YLBUtil;
import com.bjpowernode.vo.BusinessResult;
import com.bjpowernode.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Controller
public class UserController extends BaseController {

    @Resource
    private SmsService smsService;

    @Resource
    private RealNameService realNameService;



    @GetMapping("/user/page/login")
    public String userPageLogin(String returnUrl,
                                HttpServletRequest request,
                                Model model){
        //当没有returnUrl访问的是首页
        if(StringUtils.isBlank(returnUrl)){
            //returnUrl = "http://localhost:8000/ylb/index";
            returnUrl = request.getScheme()+"://"+request.getServerName()+":"
                        + request.getServerPort()
                        + request.getContextPath()
                        +"/index";
        }

        Integer registAllUserCount = userService.registAllUserCount();
        model.addAttribute("registUsers",registAllUserCount);

        BigDecimal sumAllMoney = investService.statisticsInvestSumAllMoney();
        model.addAttribute("sumBidMoney",sumAllMoney);

        BigDecimal avgHistoryRate = productService.computeAvgHistoryRate();
        model.addAttribute("avgRate",avgHistoryRate);

        model.addAttribute("returnUrl",returnUrl);
        return "login";
    }

    /**
     * @return 进入注册页面
     */
    @GetMapping("/user/page/register")
    public String userPageRegister(){
        return "register";
    }


    /**
     * @return 进入实名认证页面
     */
    @GetMapping("/user/page/realname")
    public String userPageRealName(HttpSession session, Model model){

        User user = (User) session.getAttribute(YLBConsts.SESSION_USER);
        model.addAttribute("phone",user.getPhone());

        return "realName";
    }


    @GetMapping("/user/page/mycenter")
    public String userPageMyCenter(HttpSession session,Model model){

        User user = (User)session.getAttribute(YLBConsts.SESSION_USER);

        //资金余额
        FinanceAccount account = financeAccountService.queryAccount(user.getId());
        if(account != null){
            model.addAttribute("accountMoney",account.getAvailableMoney());
        }

        //最近的5条投资记录
        List<InvestInfo> investList = investService.queryAllBidByUserId(user.getId(), 1, 5);
        model.addAttribute("investList",investList);

        //最近的5条充值记录
        List<RechargeRecord> rechargeList = rechargeService.queryByUserId(user.getId(),1,5);
        model.addAttribute("rechargeList",rechargeList);


        return "myCenter";
    }

    /**
     * 查询手机号能否注册
     * @param phone 手机号
     * @return
     */
    @GetMapping("/user/phone")
    @ResponseBody
    public Result<String> userPhoneExists(String phone){
        Result result = Result.fail();

        if( StringUtils.isBlank(phone)){
            //手机号有误
            result.setEnum(RespCode.PARAMTER_NONE);
        } else if(!YLBUtil.checkPhoneFormat(phone)){
            //手机号格式
            result.setEnum(RespCode.PHONE_ERR);
        } else {
            //手机号有数据， 执行业务处理
            User user  = userService.queryUserByPhone(phone);
            if( user == null ){
                result = Result.ok();
            } else {
                result.setEnum(RespCode.PHONE_EXISTS);
            }
        }
        return result;
    }



    @PostMapping("/user/register")
    @ResponseBody
    public Result<String> userRegister(@RequestParam String phone,
                                       @RequestParam String mima,
                                       @RequestParam String code,
                                       HttpServletRequest request,
                                       HttpSession session){
        Result<String> result = Result.fail();

        if( StringUtils.isAnyBlank(phone,mima,code)){
            result.setEnum(RespCode.PARAMTER_NONE);
        } else if( !YLBUtil.checkPhoneFormat(phone)){
            result.setEnum(RespCode.PHONE_ERR);
        } else if( mima.length() !=32 ){
            result.setEnum(RespCode.PASSWORD_ERR);
        } else if( !smsService.checkAuthCode(phone,code) ){
            result.setEnum(RespCode.SMS_CODE_INVALIDATE);
        } else {
            //注册用户了
            String ip = request.getRemoteAddr();
            String device = request.getHeader("User-Agent");
            User user  = userService.userPhoneRegist(phone,mima,ip,device);
            //注册用户成功，把用户的信息放到session，
            // 在页面前端，跳转到实名认证功能页面
            if(user != null){
                user.setLoginPassword("");
                session.setAttribute(YLBConsts.SESSION_USER,user);
                //删除redis验证码的key
                smsService.removeRedisKey(phone);
                //应答结果
                result = Result.ok();
            }
        }
        return result;

    }


    /**
     * 一天认证3次，超过3次，次天再认证
     * 1.使用的string类型
     * 2.key：手机号相关的，例如 USER:REALNAME:TIMES:136000000
     *   value： 当天的认证次数（int）
     *
     *
     *    1）RNTIMES:136000000:20211101  2 , 设置过期时间
     *    2）写其他程序，删除无效的key
     *
     *
     * @param phone
     * @param idCard
     * @param name
     * @param session
     * @return
     */
    @PostMapping("/user/realname")
    @ResponseBody
    public Result<String> userRealName(
                            @RequestParam String phone,
                            @RequestParam String idCard,
                            @RequestParam String name,
                            HttpSession session ) {
        Result<String> result = Result.fail();
        //数据验证
        String date = DateFormatUtils.format(new Date(),"yyyyMMdd");

        User user = (User) session.getAttribute(YLBConsts.SESSION_USER);
        if( StringUtils.isAnyBlank(phone,idCard,name)){
            result.setEnum(RespCode.PARAMTER_NONE);
        } else if( !YLBUtil.checkPhoneFormat(phone)){
            result.setEnum(RespCode.PHONE_ERR);
        } else if( !phone.equals(user.getPhone())){
            result.setEnum(RespCode.REAL_NAME_PHONE);
        } else if( !realNameService.checkErrorTimes(phone,date)){
            result.setEnum(RespCode.REAL_NAME_TIMES_ERR);
        } else {
            boolean ok = realNameService.handlerRealName(user.getId(), idCard, name);
            if( ok ){
                result = Result.ok();
                //更新session，把name更新user中
                user.setName(name);
            } else {
                //认证失败，记录次数
                realNameService.recordErrorTimes(phone,date);
                result.setEnum(RespCode.REAL_NAME_TIP);
            }
        }
        return result;
    }


    @PostMapping("/user/login")
    @ResponseBody
    public Result<String> userLogin(@RequestParam String phone,
                                    @RequestParam("mima") String loginPassword,
                                    HttpServletRequest request){
        Result<String> result = Result.fail();
        //检查处理。 异地登录
        if(StringUtils.isAnyBlank(phone,loginPassword)){
            result.setEnum(RespCode.PARAMTER_NONE);
        } else if( !YLBUtil.checkPhoneFormat(phone)){
            result.setEnum(RespCode.PHONE_ERR);
        } else if( loginPassword.length() != 32){
            result.setEnum(RespCode.PASSWORD_ERR);
        } else {
            //执行登录逻辑
            String ip = request.getRemoteAddr();
            String devices = request.getHeader("User-Agent");
            ServiceResult loginResult = userService.userLogin(phone,loginPassword,ip,devices);
            if( loginResult.getCode() == 30000 ){
                //登录成功,把登录的用户存放到session
                User user  = (User)loginResult.getData();
                request.getSession().setAttribute(YLBConsts.SESSION_USER,user);

                //视图结果
                result = Result.ok();

            } else if( loginResult.getCode() == 30002){
                result.setEnum(RespCode.LOGIN_IP_ERR);
            } else if( loginResult.getCode() == 30003){
                result.setEnum(RespCode.LOGIN_DATA_ERR);
            }
        }

        return result;
    }


    /**
     * 1.让session无效，清除session中的数据
     * 2.回到首页
     * @return
     */
    @GetMapping("/user/logout")
    public String userLogOut(HttpSession session){
        //session无效
        session.removeAttribute(YLBConsts.SESSION_USER);
        session.invalidate();

        //回到首页
        return "redirect:/index";
    }

    @GetMapping("/user/account")
    @ResponseBody
    public Result<String> queryUserAccount(HttpSession session){
        Result<String> result = Result.fail();

        User user = (User)session.getAttribute(YLBConsts.SESSION_USER);
        FinanceAccount account = financeAccountService.queryAccount(user.getId());
        if( account != null){
            result = Result.ok();
            result.setData( account.getAvailableMoney().toPlainString());
        }

        return result;
    }


    //删除实名认证的次数 redis的key和数据
    @GetMapping("/realname/remove/redis")
    @ResponseBody
    public String removeRealNameTimeKey(String date){
        realNameService.removeRedisData(date);
        return "ok";
    }

}
