package com.bjpowernode.dataservice.service;

import com.bjpowernode.api.po.Product;
import com.bjpowernode.api.service.ProductService;
import com.bjpowernode.dataservice.mapper.ProductMapper;
import com.bjpowernode.util.RedisOperation;
import com.bjpowernode.util.YLBUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.qos.command.impl.Help;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 产品服务
 */
@DubboService(interfaceClass = ProductService.class,version = "1.0")
public class ProductServiceImpl implements ProductService {

    @Resource
    private RedisOperation redisOperation;

    @Resource
    private ProductMapper productMapper;


    /**
     * @return 计算历史收益率rate的平均值
     *
     */
    @Override
    public BigDecimal computeAvgHistoryRate() {
        BigDecimal avgHistoryRate = productMapper.selectAvgHistoryRate();
        return avgHistoryRate;
    }

    @Override
    public Product findByProductId(Integer productId) {
        return null;
    }


    /**
     * @param productType  产品类型
     * @param pageNo       页号
     * @param pageSize     每页大小
     * @return   产品的List
     */
    @Override
    public List<Product> findListByType(Integer productType,
                                        Integer pageNo,
                                        Integer pageSize) {

        List<Product> productList  = new ArrayList<>();
        if( redisOperation.checkProductType( String.valueOf(productType) )){
            //当方法的参数是正确时，执行数据库查询
            pageNo = YLBUtil.pageNo(pageNo);
            pageSize  = YLBUtil.pageSize(pageSize);
            int offset  = YLBUtil.offSet(pageNo,pageSize);
            productList = productMapper.selectListByType(productType,offset, pageSize );
        }
        return productList;
    }



    /**
     * 某个产品类型的总记录数量
     * @param productType 产品类型
     * @return
     */
    @Override
    public Integer queryProductRecordCount(Integer productType) {

        Integer counts = 0;
        if( redisOperation.checkProductType(String.valueOf(productType))){
            counts  = productMapper.selectCountByProductType(productType);
        }
        return counts;
    }

    /**
     * @param id 产品id
     * @return 单个产品对象 或 null
     */
    @Override
    public Product queryProductById(Integer id) {
        Product product = null;
        if(YLBUtil.checkNullZero(id)) {
            product = productMapper.selectByPrimaryKey(id);
        }
        return product;
    }
}
