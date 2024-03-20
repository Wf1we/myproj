package com.bjpowernode.api.service;

import com.bjpowernode.api.model.ServiceResult;
import com.bjpowernode.api.po.RechargeRecord;

import java.util.List;

public interface RechargeService {

    /**
     * 分页用户充值记录
     * @param userId   userId
     * @param pageNo   页号
     * @param pageSize 每页大小
     * @return
     */
    List<RechargeRecord> queryByUserId(Integer userId, Integer pageNo, Integer pageSize);

    /**
     * 查询用户充值总记录数量
     * @param userId 用户id
     * @return
     */
    Integer queryCountByUserId(Integer userId);

    /**
     * 创建充值记录
     * @param recharge
     */
    boolean addRecharge(RechargeRecord recharge);


    /**
     * @param orderId    订单号
     * @param payResult  充值结果10：成功，11失败
     * @param payAmount  充值金额，单位分
     * @return
     */
    ServiceResult handlerKqNotify(String orderId, String payResult, String payAmount);

    //更新充值记录的状态
    int modifyRechargeStatus(String orderId, Integer rechargeFailure);

    //根据订单号，查询充值结果
    RechargeRecord queryByRechargeNo(String rechargeNo);
}
