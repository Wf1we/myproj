package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.api.po.Product;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductMapper {

    /**
     * @return 计算历史收益率rate的平均值
     */
    BigDecimal selectAvgHistoryRate();


    /**
     * @param productType 产品类型
     * @param offset      limit的offset
     * @param rows        limit的rows
     * @return
     */
    List<Product> selectListByType(@Param("productType") Integer productType,
                                   @Param("offset") Integer offset,
                                   @Param("rows") Integer rows);


    /**
     * @param productType 产品类型
     * @return 记录总数
     */
    Integer selectCountByProductType(@Param("productType") Integer productType);


    /**
     * 扣除产品剩余可投资金额
     * */
    int updateLeftMoney(@Param("id") Integer productId, @Param("bidMoney") BigDecimal bidMoney);

    /**
     * @return 昨天的满标产品
     */
    List<Product> selectManBiaoByStatus();

    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);



}