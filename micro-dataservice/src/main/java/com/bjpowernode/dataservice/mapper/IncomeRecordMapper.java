package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.api.po.IncomeRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IncomeRecordMapper {

    //查询到期的收益记录
    List<IncomeRecord> selectExpireIncomeDate();

    //更新状态字段
    int updateStatus(@Param("id") Integer id, @Param("newStatus") Integer incomeStatusBack);

    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);


}