package com.bjpowernode.api.service;

import com.bjpowernode.api.model.InvestInfo;
import com.bjpowernode.api.model.ServiceResult;

import java.math.BigDecimal;
import java.util.List;

//投资服务
public interface InvestService {

    /**
     * @return 统计总的投资金额
     */
    BigDecimal statisticsInvestSumAllMoney();


    /**
     * @param productId 产品id
     * @param pageNo    页号从 1 开始
     * @param pageSize  每页大小
     * @return  某个产品最近投资记录
     */
    List<InvestInfo> queryByProductId(Integer productId,Integer pageNo,Integer pageSize);

    /**
     * 查询某个用户的投资记录
     * @param userId   用户id
     * @param pageNo   页号
     * @param pageSize 页面大小
     * @return
     */
    List<InvestInfo> queryAllBidByUserId(Integer userId,Integer pageNo,Integer pageSize);

    /**
     * 统计用户投资的总记录数量
     * @param userId 用户id
     * @return
     */
    Integer countBidByUserId(Integer userId);


    /**
     * 投资
     * @param userId         用户id
     * @param productId      产品id
     * @param bidMoney       投资金额
     * @return
     */
    ServiceResult invest(Integer userId, Integer productId, BigDecimal bidMoney);
}
