package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.api.model.InvestInfo;
import com.bjpowernode.api.po.BidInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BidInfoMapper {

    /**
     * @return 总的投资金额
     */
    BigDecimal selectSumAllBidMoney();

    /**
     * @param productId 产品id
     * @param offset    limit的offset
     * @param rows      limit的rows
     * @return  某个产品最近投资记录
     */
    List<InvestInfo> selectByProductId(@Param("productId") Integer productId, @Param("offset") Integer offset, @Param("rows") Integer rows);


    /**
     * @param userId    用户id
     * @param offSet    limit 的offset
     * @param pageSize  limit 的rows
     * @return
     */
    List<InvestInfo> selectByUserId(@Param("userId") Integer userId,
                                    @Param("offSet") Integer offSet,
                                    @Param("rows") Integer pageSize);


    /**
     * @param userId 用户投资数量
     * @return
     */
    Integer selectCountBidsByUserId(@Param("userId") Integer userId);

    /**
     * 某个产品的所有投资记录
     * @param productId 产品id
     * @return
     */

    List<BidInfo> selectBidListByProductId(@Param("productId") Integer productId);

    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);



}