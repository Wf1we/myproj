package com.bjpowernode.front.controller;

import com.bjpowernode.api.model.InvestInfo;
import com.bjpowernode.api.po.FinanceAccount;
import com.bjpowernode.api.po.Product;
import com.bjpowernode.api.po.User;
import com.bjpowernode.consts.YLBConsts;
import com.bjpowernode.consts.YLBKey;
import com.bjpowernode.front.view.InvestTopBean;
import com.bjpowernode.front.view.ProductView;
import com.bjpowernode.util.YLBUtil;
import com.bjpowernode.vo.PageInfo;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class ProductController extends BaseController {


    /**
     *
     * 产品详情， 单个产品详细信息
     * id:产品主键
     * @return 产品对象
     */
    @GetMapping("/product/info")
    public String productInfo(@RequestParam Integer id, Model model,
                              HttpSession session){
        //检查产品id
        if( id < 1 ) {
            model.addAttribute("msg","产品不存在");
            return "errorInfo";
        }

        //id> 0 执行正常的业务逻辑, 查询产品
        Product product  = productService.queryProductById(id);
        if( product == null ){
            model.addAttribute("msg","产品不存在");
            return "errorInfo";
        }

        //把业务逻辑处理后的数据，转为页面需要的数据
        ProductView view  =  ProductView.turnView(product);
        model.addAttribute("view",view);

        //投资记录
        List<InvestInfo> investList = investService.queryByProductId(id, 1, 3);
        model.addAttribute("investList",investList);

        //获取资金
        User user = (User)session.getAttribute(YLBConsts.SESSION_USER);
        if( user != null){
            FinanceAccount account = financeAccountService.queryAccount(user.getId());
            if( account != null){
                model.addAttribute("accountMoney",account.getAvailableMoney());
            }
        }

        //model.addAttribute("product",product);
        //return "productInfo";
        return "productInfo2";

    }

    /**
     * 按产品类型，分页查询产品
     * @param productType 产品类型（0,1，2）
     * @param pageNo      从1开始
     * @param model
     * @return
     */
    @GetMapping("/product/show")
    public String productMore(@RequestParam("ptype") Integer productType,
                              @RequestParam(required = false,defaultValue = "1") Integer pageNo,
                              Model model){

        PageInfo pageInfo = new PageInfo();
        List<Product> productList  = new ArrayList<>();
        if( productType != null && redisOperation.checkProductType(String.valueOf(productType))){
            pageNo  = YLBUtil.pageNo(pageNo);
            //产品列表
            productList =  productService.findListByType(productType, pageNo,
                                                YLBConsts.DEFAULT_PAGE_SIZE);

            //某个产品类型的总记录数量
            Integer recordCounts = productService.queryProductRecordCount(productType);

            //封装PageInfo
            pageInfo = new PageInfo(pageNo,YLBConsts.DEFAULT_PAGE_SIZE,recordCounts);
            //@TODO 投资排行榜，后期在投资功能完成后实现
            List<InvestTopBean> topList = new ArrayList<>();
            Set<ZSetOperations.TypedTuple<String>> sets = redisOperation.reverseRangeZSet(YLBKey.INVEST_TOP_LIST, 0, 4);
            sets.forEach( s -> {
                topList.add( new InvestTopBean(s.getValue(),s.getScore()));
            });
            model.addAttribute("topList",topList);


        }
        model.addAttribute("productType",productType);
        //产品数据List
        model.addAttribute("productList",productList);
        //分页
        model.addAttribute("page",pageInfo);

        return "products";
    }
}
