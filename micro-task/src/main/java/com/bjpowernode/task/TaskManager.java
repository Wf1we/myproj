package com.bjpowernode.task;

import com.bjpowernode.api.service.IncomeService;
import com.bjpowernode.util.HttpClientUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("taskManager")
public class TaskManager {

    /**
     * 测试定时任务
     * 定时任务执行的方法要求：
     * 1.public 方法
     * 2.没有返回值
     * 3.没有参数
     */
    /*@Scheduled(cron = "0/20 * * * * ?")
    public void testCron(){
        //定时任务执行的功能
        System.out.println("定时任务："+ new Date());
    }*/


    @DubboReference(interfaceClass = IncomeService.class,version = "1.0")
    private IncomeService incomeService;

    //调用远程服务，生成收益计划
    //@Scheduled(cron = "0 0 2 * * ?")
    public void invokeGenerateIncomePlan(){
        System.out.println("生成收益计划开始");
        incomeService.generateIncomePlan();
        System.out.println("生成收益计划完成");
    }

    //调用远程服务，执行收益返还
    //@Scheduled(cron = "0 0 1 * * ?")
    public void invokeGenerateIncomeBack(){
        System.out.println("收益返还开始");
        incomeService.generateIncomeBack();
        System.out.println("收益返还完成");
    }


    @Value("${micrpay.kq.url}")
    private String micrPayKuaiQianQueryUrl;

    @Value("${micrweb.realname.url}")
    private String micrWebRealnameUrl;

    //调用快钱的查询接口
    @Scheduled(cron = "0 */20 * * * ?")
    public void invokePayKuaiQianQuery(){
        System.out.println("调用快钱的查询接口开始");
        try {
            HttpClientUtils.doGet(micrPayKuaiQianQueryUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("调用快钱的查询接口完成");
    }


    //调用web服务中删除key
    @Scheduled(cron = "0 30 1 * * ?")
    public void invokeWebRemoveRedisData(){
        System.out.println("调用web服务中删除key开始");
        try {
            String yesDate = DateFormatUtils.format(  DateUtils.addDays(new Date(),-1),"yyyyMMdd" );
            HttpClientUtils.doGet(micrWebRealnameUrl+"?date="+yesDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("调用web服务中删除key完成");
    }
}
