package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.api.po.RechargeRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RechargeRecordMapper {

    List<RechargeRecord> selectByUserId(@Param("userId") Integer userId,
                                        @Param("offSet") Integer offSet,
                                        @Param("rows") Integer rows);

    //用户充值总记录数
    Integer selectCountRechargeNumByUserId(@Param("userId") Integer userId);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Integer id);


    //根据订单号rechargeNo，查询充值记录
    RechargeRecord selectByRechargeNo(@Param("rechargeNo") String rechargeNo);

    //更新充值记录状态
    int updateRechargeStatus(@Param("id") Integer id, @Param("newStatus") Integer newStatus);

    //更新充值记录状态
    int updateRechargeStatusByRechargeNo(@Param("rechargeNo") String rechargeNo, @Param("newStatus") Integer newStatus);
}