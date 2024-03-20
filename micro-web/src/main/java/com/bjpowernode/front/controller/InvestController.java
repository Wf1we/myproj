package com.bjpowernode.front.controller;

import com.bjpowernode.api.model.InvestInfo;
import com.bjpowernode.api.model.ServiceResult;
import com.bjpowernode.api.po.User;
import com.bjpowernode.code.RespCode;
import com.bjpowernode.consts.YLBConsts;
import com.bjpowernode.consts.YLBKey;
import com.bjpowernode.util.YLBUtil;
import com.bjpowernode.vo.PageInfo;
import com.bjpowernode.vo.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class InvestController extends BaseController {

    //用户购买理财产品
    @RequestMapping("/invest/product")
    @ResponseBody
    public Result<String> investProduct(@RequestParam("productId") Integer productId,
                                        @RequestParam("bidMoney") Integer bidMoney,
                                        HttpSession session){
        Result<String> result = Result.fail();
        if( productId != null && productId  > 0 && bidMoney != null && (bidMoney % 100) ==0){
            //调用数据服务的方法
            User user  = (User)session.getAttribute(YLBConsts.SESSION_USER);
            ServiceResult serviceResult = investService.invest(user.getId(),productId,new BigDecimal(bidMoney));
            if( serviceResult.getCode() == YLBConsts.SERVICE_HANDLER_SUCCESS){
                result = Result.ok();
                //更新投资排行榜
                redisOperation.incrScoreZSet(YLBKey.INVEST_TOP_LIST,user.getPhone(),bidMoney.doubleValue());
            } else {
                result.setMsg(serviceResult.getMsg());
            }
            //排行榜
        } else {
            result.setEnum(RespCode.PARAMTER_NONE);
        }

        return result;

    }

    //查看全部投资
    @GetMapping("/invest/myInvest")
    public String showAll(
            @RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
            HttpSession session, Model model){
        User user = (User)session.getAttribute(YLBConsts.SESSION_USER);

        PageInfo pageInfo = new PageInfo();
        List<InvestInfo> investList = new ArrayList<>();
        //投资记录总数
        Integer countRecords = investService.countBidByUserId(user.getId());
        if( countRecords > 0 ){
            //投资记录的集合
            investList  = investService.queryAllBidByUserId(user.getId(),pageNo, YLBConsts.DEFAULT_PAGE_SIZE);
            //创建PageInfo
            pageInfo = new PageInfo(pageNo,YLBConsts.DEFAULT_PAGE_SIZE,countRecords);
       }
        model.addAttribute("investList",investList);
        model.addAttribute("page",pageInfo);
        return "myInvest";
    }




}
