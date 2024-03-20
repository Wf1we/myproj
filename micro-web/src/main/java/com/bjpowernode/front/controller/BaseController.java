package com.bjpowernode.front.controller;

import com.bjpowernode.api.service.*;
import com.bjpowernode.front.service.HelpService;
import com.bjpowernode.util.RedisOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

public class BaseController {

    //声明使用远程用户服务
    @DubboReference(interfaceClass = UserService.class,version = "1.0")
    protected UserService userService;

    //声明使用投资远程服务
    @DubboReference(interfaceClass = InvestService.class,version = "1.0")
    protected InvestService investService;

    //声明产品服务
    @DubboReference(interfaceClass = ProductService.class,version = "1.0")
    protected ProductService productService;

    //声明资金服务
    @DubboReference(interfaceClass = FinanceAccountService.class,version = "1.0")
    protected FinanceAccountService financeAccountService;

    //充值服务
    @DubboReference(interfaceClass = RechargeService.class,version = "1.0")
    protected RechargeService rechargeService;

    @Resource
    protected RedisOperation redisOperation;

}
