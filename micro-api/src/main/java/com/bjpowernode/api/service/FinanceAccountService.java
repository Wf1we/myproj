package com.bjpowernode.api.service;

import com.bjpowernode.api.po.FinanceAccount;

// 资金服务
public interface FinanceAccountService {

    //根据userId查询资金账户
    FinanceAccount queryAccount(Integer userId);
}
