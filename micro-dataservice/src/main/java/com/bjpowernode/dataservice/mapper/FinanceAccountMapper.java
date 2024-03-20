package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.api.po.FinanceAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface FinanceAccountMapper {

    /**
     * @param userId 用户id
     * @return 资金账户
     */
    FinanceAccount selectByUserId(@Param("userId") Integer userId);


    /**
     * @param record account
     * @return
     */
    int insertSelective(FinanceAccount record);

    /**
     * 锁定数据行
     * @param userId 用户id
     * @return
     */
    FinanceAccount selectByUserIdForUpdate(@Param("userId") Integer userId);

    /**
     * 投资，更新余额
     * @param userId    用户id
     * @param bidMoney  投资金额
     * @return
     */
    int updateAccountMoney(@Param("userId") Integer userId, @Param("bidMoney") BigDecimal bidMoney);

    /***
     * 充值更新资金
     * @param uid             用户id
     * @param rechargeMoney   充值金额
     * @return
     */
    int updateAccountMoneyByRecharge(@Param("uid") Integer uid, @Param("rechargeMoney") BigDecimal rechargeMoney);

    /**
     * 产品到期，收益返还
     * @param uid           用户id
     * @param bidMoney      本金
     * @param incomeMoney   利息
     * @return
     */
    int updateAccountMoneyByIncomeBack(@Param("uid") Integer uid,
                                       @Param("bidMoney") BigDecimal bidMoney,
                                       @Param("incomeMoney") BigDecimal incomeMoney);
}