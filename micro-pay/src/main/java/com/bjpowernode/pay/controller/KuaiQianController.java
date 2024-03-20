package com.bjpowernode.pay.controller;

import com.bjpowernode.api.model.ServiceResult;
import com.bjpowernode.api.po.RechargeRecord;
import com.bjpowernode.api.service.RechargeService;
import com.bjpowernode.consts.YLBConsts;
import com.bjpowernode.pay.service.KuaiQianService;
import com.bjpowernode.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
public class KuaiQianController {

    @Resource
    private KuaiQianService kqService;

    @DubboReference(version = "1.0")
    private RechargeService rechargeService;

    /**
     * 生成对应渠道的rechargeNo(订单号)
     * @param channel 渠道（使用哪个做充值，快钱=kq）
     * @return
     */
    @GetMapping("/apply/rechargeno")
    @ResponseBody
    public Result<String> getRechargeNo(String channel){
        //不同的渠道，可能有不同的订单号规则。
        if(StringUtils.isBlank(channel)){
            channel = "kq";
        }
        String rechargeNo = kqService.generateRechargeNo(channel);

        Result<String> result = Result.ok();
        result.setData(rechargeNo);
        return result;

    }

    //接收web服务，充值的请求
    @GetMapping("/kq/receweb")
    public String receRequestFromWeb(
            @RequestParam("rechargeMoney") BigDecimal money,
            @RequestParam("channel") String channel,
            @RequestParam("userId") Integer userId,
            @RequestParam("rechargeNo") String rechargeNo,
            Model model){

        try{
            //生成快钱表单数据
            Map<String,String> param =  kqService.generateKqFormData(userId,rechargeNo,money);
            //生成充值记录
            RechargeRecord recharge = new RechargeRecord();
            recharge.setAction(1);
            recharge.setChannel(channel);
            recharge.setRechargeDesc("支付充值");
            recharge.setRechargeMoney(money);
            recharge.setRechargeStatus(YLBConsts.RECHARGE_PROCESSING);
            recharge.setRechargeTime(new Date());
            recharge.setUid(userId);
            recharge.setRechargeNo(rechargeNo);
            rechargeService.addRecharge(recharge);

            model.addAllAttributes(param);
        }catch (Exception e){
            e.printStackTrace();
        }

        return "kqForm";

    }

    //接收快钱的异步通知
    @GetMapping("/kq/notify")
    @ResponseBody
    public String kqNotify(HttpServletRequest request){
        System.out.println("接收快钱的异步通知");
        Map<String,String> map = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        Enumeration<String> names = request.getParameterNames();
        while(names.hasMoreElements()){
            String name  = names.nextElement();
            String value  = request.getParameter(name);
            builder.append(name).append("=").append(value).append("&");
            map.put(name,value);
        }
        System.out.println("参数："+builder.toString());
        //处理异步通知
        ServiceResult result = kqService.handlerNotify(request);
        //从redis删除处过的订单记录
        kqService.removeOrderFromRedis(request.getParameter("orderId"));
        //用户中心页面（外围地址）
        return "<result>1</result><redirecturl>http://localhost:8000/ylb/index</redirecturl>";
    }


    //接收查询的 请求接口
    @GetMapping("/kq/query")
    @ResponseBody
    public Result<String> kqQuery(String rechargeNo){
        Result<String> result = Result.fail();
        ServiceResult serviceResult = kqService.handlerQuery(rechargeNo);
        if(serviceResult.isResult() ){
            result = Result.ok();
        }
        return result;

    }


    //接收定时任务的请求，调用快钱查询接口
    @GetMapping("/kq/task/query")
    @ResponseBody
    public String kqTaskQuery(){
       System.out.println("====/kq/task/query=====");
       //调用service的方法， 获取orderId，在调取查询接口
       kqService.handlerTaskQuery();
       return "ok";
    }
}
