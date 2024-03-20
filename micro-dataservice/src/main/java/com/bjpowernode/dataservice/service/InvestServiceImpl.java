package com.bjpowernode.dataservice.service;

import com.bjpowernode.api.model.InvestInfo;
import com.bjpowernode.api.model.ServiceResult;
import com.bjpowernode.api.po.BidInfo;
import com.bjpowernode.api.po.FinanceAccount;
import com.bjpowernode.api.po.Product;
import com.bjpowernode.api.service.InvestService;
import com.bjpowernode.consts.YLBConsts;
import com.bjpowernode.dataservice.mapper.BidInfoMapper;
import com.bjpowernode.dataservice.mapper.FinanceAccountMapper;
import com.bjpowernode.dataservice.mapper.ProductMapper;
import com.bjpowernode.util.YLBUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//投资服务
@DubboService(interfaceClass = InvestService.class,version = "1.0")
public class InvestServiceImpl implements InvestService {

    @Resource
    private BidInfoMapper bidInfoMapper;

    @Resource
    private FinanceAccountMapper accountMapper;

    @Resource
    private ProductMapper productMapper;


    /**
     * 投资
     * @param userId         用户id
     * @param productId      产品id
     * @param bidMoney       投资金额
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ServiceResult invest(Integer userId, Integer productId, BigDecimal bidMoney) {

        int rows  = 0;
        ServiceResult result  = new ServiceResult();
        if( userId != null && userId > 0
                && productId != null && productId > 0
                && bidMoney!= null && bidMoney.intValue() >=100 && (bidMoney.intValue() % 100 == 0) ){
            //锁定数据
            FinanceAccount account = accountMapper.selectByUserIdForUpdate(userId);
            if( account != null ){
                // bidMoney金额和产品的它的金额和资金账户的判断
                if( YLBUtil.ge(account.getAvailableMoney(),bidMoney) ){
                    //资金余额充足 ，判断投资金额和产品金额
                    Product product = productMapper.selectByPrimaryKey(productId);
                    if( product != null && product.getProductStatus() == 0 ){

                        if( YLBUtil.ge(bidMoney,product.getBidMinLimit())
                                && YLBUtil.ge(product.getBidMaxLimit(), bidMoney)
                                && YLBUtil.ge(product.getLeftProductMoney(),bidMoney) ) {
                            //可以购买
                            //1.扣除资金余额
                            rows = accountMapper.updateAccountMoney(userId,bidMoney);
                            if( rows < 1 ){
                                throw new RuntimeException("投资扣除资金余额失败");
                            }

                            //2.扣除产品剩余可投资金额
                            rows = productMapper.updateLeftMoney(productId,bidMoney);
                            if( rows <1){
                                throw  new RuntimeException("投资扣除产品剩余可投资金额失败");
                            }

                            //3.创建投资记录
                            BidInfo bidInfo = new BidInfo();
                            bidInfo.setBidMoney(bidMoney);
                            bidInfo.setBidStatus(YLBConsts.BID_STATUS_SUCCESS);
                            bidInfo.setBidTime(new Date());
                            bidInfo.setProdId(productId);
                            bidInfo.setUid(userId);
                            bidInfoMapper.insert(bidInfo);

                            //4.判断产品是满标
                            Product manProduct = productMapper.selectByPrimaryKey(productId);
                            if( manProduct.getLeftProductMoney().compareTo(new BigDecimal("0")) == 0){
                                //满标，更新产品的满标状态和时间
                                Product updateProduct = new Product();
                                updateProduct.setId(manProduct.getId());
                                updateProduct.setProductStatus(YLBConsts.PRODUCT_STATUS_MANBIAO);
                                updateProduct.setProductFullTime( new Date());
                                rows  = productMapper.updateByPrimaryKeySelective(updateProduct);
                                if( rows < 1 ){
                                    throw new RuntimeException("投资更新满标状态失败");
                                }
                            }
                            result.setResult(true);
                            result.setCode(YLBConsts.SERVICE_HANDLER_SUCCESS);
                            result.setMsg("投资成功");

                        } else {
                            result.setCode(YLBConsts.ERR_BIDMONEY_VALUE);
                            result.setMsg("投资金额不正确");
                        }
                    } else {
                        result.setCode(YLBConsts.ERR_PRODUCT_NOT_INVEST);
                        result.setMsg("产品不能售卖");
                    }
                } else {
                    result.setCode(YLBConsts.ERR_ACCOUNT_MONEY_NOT_ENOUGH);
                    result.setMsg("资金不充足");
                }
            } else {
                result.setCode(YLBConsts.ERR_ACCOUNT_NOTEXISTS);
                result.setMsg("资金账户不存在");
            }
        }

        return result;
    }


    @Override
    public BigDecimal statisticsInvestSumAllMoney() {
        BigDecimal sumBidMoney  = bidInfoMapper.selectSumAllBidMoney();
        return sumBidMoney;
    }

    /**
     * @param productId 产品id
     * @param pageNo    页号从 1 开始
     * @param pageSize  每页大小
     * @return  某个产品最近投资记录
     */
    @Override
    public List<InvestInfo> queryByProductId(Integer productId,
                                             Integer pageNo,
                                             Integer pageSize) {

        List<InvestInfo> investList =  new ArrayList<>();
        if(YLBUtil.checkNullZero(productId)){
            pageNo = YLBUtil.pageNo(pageNo);
            pageSize =YLBUtil.pageSize(pageSize);
            Integer offset  = YLBUtil.offSet(pageNo,pageSize);
            investList =  bidInfoMapper.selectByProductId(productId,offset ,pageSize);
        }
        return investList;
    }

    /**
     * 查询某个用户的投资记录
     * @param userId   用户id
     * @param pageNo   页号
     * @param pageSize 页面大小
     * @return
     */
    @Override
    public List<InvestInfo> queryAllBidByUserId(Integer userId, Integer pageNo, Integer pageSize) {

        List<InvestInfo> investList = new ArrayList<>();
        if( userId != null && userId > 0 ){
            pageNo = YLBUtil.pageNo(pageNo);
            pageSize = YLBUtil.pageSize(pageSize);
            investList =  bidInfoMapper.selectByUserId(userId, YLBUtil.offSet(pageNo, pageSize), pageSize);
        }
        return investList;
    }

    /**
     * 统计用户投资的总记录数量
     * @param userId 用户id
     * @return
     */
    @Override
    public Integer countBidByUserId(Integer userId) {
        Integer countNums = 0;
        if( userId !=null && userId > 0 ){
            countNums = bidInfoMapper.selectCountBidsByUserId(userId);
        }
        return countNums;
    }




}
