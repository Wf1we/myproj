package com.bjpowernode.front.controller;

import com.bjpowernode.code.RespCode;
import com.bjpowernode.front.service.SmsService;
import com.bjpowernode.util.YLBUtil;
import com.bjpowernode.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class SmsController {

    @Resource
    private SmsService smsService;
    /**
     * @param phone 接收发送短信的请求
     * @return
     */
    @PostMapping("/sms/send/authcode")
    public Result<String> smsSend(@RequestParam String phone){

        Result<String> result = Result.fail();
        if(!YLBUtil.checkPhoneFormat(phone)){
            result.setEnum(RespCode.PHONE_ERR);
        } else {
           boolean sendOk =  smsService.userRegisterSendSmsAuthCode(phone);
           if( sendOk ){
               result = Result.ok();
           }
        }
        return result;
    }
}
