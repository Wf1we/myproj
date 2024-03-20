package com.bjpowernode.front.controller;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.api.po.RechargeRecord;
import com.bjpowernode.api.po.User;
import com.bjpowernode.consts.YLBConsts;
import com.bjpowernode.util.HttpClientUtils;
import com.bjpowernode.util.YLBUtil;
import com.bjpowernode.vo.PageInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RechargeController extends BaseController {

    @Value("${micro.pay.url}")
    private String microPayRechargeNoUrl;

    @Value("${micro.pay.receaction}")
    private String microPayRechargeRevceUrl;

    //进入到充值页面
    @GetMapping("/recharge/page/toRecharge")
    public String pageRecharge(Model model){
        //预先生成订单号
        String rechargeNo = "";
        try {
            String json = HttpClientUtils.doGet(microPayRechargeNoUrl+"?channel=kq");
            JSONObject jsonObject = JSONObject.parseObject(json);
            if( jsonObject != null){
                rechargeNo =  jsonObject.getString("data");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("receurl",microPayRechargeRevceUrl);
        model.addAttribute("rechargeNo",rechargeNo);
        return "toRecharge";
    }

    //分页查看充值记录
    @GetMapping("/recharge/myRecharge")
    public String myRecharge(@RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
                             HttpSession session,
                             Model model){
        User user  = (User) session.getAttribute(YLBConsts.SESSION_USER);
        List<RechargeRecord> rechargeList = new ArrayList<>();
        PageInfo page  = new PageInfo();

        Integer countRecharge = rechargeService.queryCountByUserId(user.getId());
        if( countRecharge > 0 ){
            rechargeList = rechargeService.queryByUserId(user.getId(),pageNo,YLBConsts.DEFAULT_PAGE_SIZE);
            page  = new PageInfo(pageNo,YLBConsts.DEFAULT_PAGE_SIZE,countRecharge);
        }
        model.addAttribute("rechargeList",rechargeList);
        model.addAttribute("page",page);

        return "myRecharge";

    }

    //弹层中按钮
    @GetMapping("/recharge/query")
    public String rechargeQuery(String rechargeNo) {
        String view = "forward:/recharge/page/toRecharge";
        //1.查询数据库
        RechargeRecord recharge = rechargeService.queryByRechargeNo(rechargeNo);
        if(recharge != null){
            int status = recharge.getRechargeStatus();
            if( status == YLBConsts.RECHARGE_SUCCESS ){
                view = "forward:/user/page/mycenter";
            } else if(status == YLBConsts.RECHARGE_FAILURE){
                view="forward:/recharge/page/toRecharge";
            } else if( status == YLBConsts.RECHARGE_PROCESSING) {
               //没有充值结果，调用快钱查询接口
                String url="http://localhost:9000/pay/kq/query?rechargeNo="+rechargeNo;
                try{
                    //{"result":false,"code":0,"msg":"未知错误","data":""}
                    String json = HttpClientUtils.doGet(url);
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    if( jsonObject.getBoolean("result")){
                        view = "forward:/user/page/mycenter";
                    } else {
                        view="forward:/recharge/page/toRecharge";
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
        return view;

    }
}
