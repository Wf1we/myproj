package com.bjpowernode.front.controller;

import com.bjpowernode.api.po.Product;
import com.bjpowernode.api.service.InvestService;
import com.bjpowernode.api.service.ProductService;
import com.bjpowernode.api.service.UserService;
import com.bjpowernode.consts.YLBConsts;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class IndexController extends BaseController{


    /**
     * 进入首页面
     * @param model 模型
     * @return
     */
    //@GetMapping({"/index","/",""})
    //@GetMapping(value = "")
    //@GetMapping()
    @GetMapping({"/index","/",""})
    public String indexPage(Model model){
        System.out.println("===========indexPage==============");
        //注册总用户数量
        Integer registUserCount = userService.registAllUserCount();
        model.addAttribute("registUserCount",registUserCount);

        //投资总金额
        BigDecimal sumBidMoney  = investService.statisticsInvestSumAllMoney();
        model.addAttribute("sumBidMoney",sumBidMoney);

        //rate历史收益率平均值
        BigDecimal avgHistoryRate = productService.computeAvgHistoryRate();
        model.addAttribute("avgHistoryRate",avgHistoryRate);

        //新手宝
        List<Product> xinProductList = productService.findListByType(
                                YLBConsts.PRODUCT_TYPE_XINSHOUBAO, 1, 1);
        model.addAttribute("xinProductList",xinProductList);

        //优选
        List<Product> youProductList =  productService.findListByType(
                YLBConsts.PRODUCT_TYPE_YOUXUAN,1,4);
        model.addAttribute("youProductList",youProductList);

        //散标
        List<Product> sanProductList = productService.findListByType(
                YLBConsts.PRODUCT_TYPE_SANBIAO,1,8);
        model.addAttribute("sanProductList",sanProductList);

        return "index";
    }
}
