package com.bjpowernode.api.service;

public interface IncomeService {
    //计算预期收益，产品满标，计算收益
    void generateIncomePlan();

    //收益返还
    void generateIncomeBack();
}
