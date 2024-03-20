package com.bjpowernode.api.service;

import com.bjpowernode.api.po.Product;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {


    /**
     * @return 计算历史收益率rate的平均值
     */
    BigDecimal computeAvgHistoryRate();


    /**
     * @param productType  产品类型
     * @param pageNo       页号
     * @param pageSize     每页大小
     * @return   产品的List
     */
    List<Product> findListByType(Integer productType,
                                 Integer pageNo,
                                 Integer pageSize);



    Product findByProductId(Integer productId);

    /**
     * 某个产品类型的总记录数量
     * @param productType 产品类型
     * @return
     */
    Integer queryProductRecordCount(Integer productType);

    /**
     * @param id 产品id
     * @return 单个产品对象
     */
    Product queryProductById(Integer id);
}
