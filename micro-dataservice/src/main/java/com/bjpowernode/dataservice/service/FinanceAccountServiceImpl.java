package com.bjpowernode.dataservice.service;

import com.bjpowernode.api.po.FinanceAccount;
import com.bjpowernode.api.service.FinanceAccountService;
import com.bjpowernode.dataservice.mapper.FinanceAccountMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService(interfaceClass = FinanceAccountService.class,version = "1.0")
public class FinanceAccountServiceImpl implements FinanceAccountService {

    @Resource
    private FinanceAccountMapper accountMapper;
    //根据userId查询资金账户
    @Override
    public FinanceAccount queryAccount(Integer userId) {
        FinanceAccount account = null;
        if( userId != null && userId > 0 ){
            account = accountMapper.selectByUserId(userId);
        }
        return account;
    }
}
