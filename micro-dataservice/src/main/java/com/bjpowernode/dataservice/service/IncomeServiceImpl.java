package com.bjpowernode.dataservice.service;

import com.bjpowernode.api.po.BidInfo;
import com.bjpowernode.api.po.IncomeRecord;
import com.bjpowernode.api.po.Product;
import com.bjpowernode.api.service.IncomeService;
import com.bjpowernode.consts.YLBConsts;
import com.bjpowernode.dataservice.mapper.BidInfoMapper;
import com.bjpowernode.dataservice.mapper.FinanceAccountMapper;
import com.bjpowernode.dataservice.mapper.IncomeRecordMapper;
import com.bjpowernode.dataservice.mapper.ProductMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@DubboService(interfaceClass = IncomeService.class,version = "1.0")
public class IncomeServiceImpl implements IncomeService {


    @Resource
    private ProductMapper productMapper;

    @Resource
    private BidInfoMapper bidInfoMapper;

    @Resource
    private IncomeRecordMapper incomeRecordMapper;

    @Resource
    private FinanceAccountMapper accountMapper;


    //计算预期收益，产品满标，计算收益
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void generateIncomePlan() {
        //1.查询满标的符合条件的产品
        List<Product> productList = productMapper.selectManBiaoByStatus();

        //2.查询某个产品的，所有投资记录
        Date incomeDate  = null;
        BigDecimal incomeMoney = null;
        BigDecimal dateRate = null;
        for(Product product:productList){
            //日利率
            dateRate  = product.getRate()
                    .divide(new BigDecimal("100"),10, RoundingMode.HALF_UP)
                    .divide(new BigDecimal("360"),10,RoundingMode.HALF_UP);

            List<BidInfo> bidList  = bidInfoMapper.selectBidListByProductId(product.getId());
            for(BidInfo bid:bidList){
                //3.对每笔投资记录做收益计划 income_date, income_money

                if( product.getProductType() == YLBConsts.PRODUCT_TYPE_XINSHOUBAO){
                    //天为单位周期
                    //到期时间 = 满标时间+1, +周期
                    incomeDate  = DateUtils.addDays(product.getProductFullTime(),(1+product.getCycle()));
                    //利息 = 本金 * 利息 * 周期
                    incomeMoney = bid.getBidMoney().multiply( dateRate).multiply( new BigDecimal(product.getCycle()));
                } else {
                    //月为单位
                    incomeDate =  DateUtils.addMonths( DateUtils.addDays(product.getProductFullTime(),1), product.getCycle() );
                    incomeMoney = bid.getBidMoney().multiply( dateRate ).multiply( new BigDecimal( 30 * product.getCycle()) );
                }

                //把计算结果存放收益表
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setBidId(bid.getId());
                incomeRecord.setBidMoney(bid.getBidMoney());
                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeStatus(YLBConsts.INCOME_STATUS_PLAN);
                incomeRecord.setIncomeMoney(incomeMoney);
                incomeRecord.setProdId(bid.getProdId());
                incomeRecord.setUid(bid.getUid());
                incomeRecordMapper.insertSelective(incomeRecord);

            }

            //4.更新产品的状态是 2 （满标生成收益计划）
            product.setProductStatus(YLBConsts.PRODUCT_STATUS_MANBIAO_INCOME_PLAN);;
            int rows  = productMapper.updateByPrimaryKeySelective(product);
            if(rows  < 1){
                throw new RuntimeException("生成收益计划，更新产品状态失败");
            }
        }
    }

    //收益返还
    @Transactional
    @Override
    public void generateIncomeBack() {

        //1.获取到期的产品数据。 查询income_record
        List<IncomeRecord> incomeRecords = incomeRecordMapper.selectExpireIncomeDate();

        int rows = 0;
        for(IncomeRecord ir : incomeRecords){
            //2.更新资金账户
            rows = accountMapper.updateAccountMoneyByIncomeBack(ir.getUid(),ir.getBidMoney(),ir.getIncomeMoney());
            if( rows < 1 ){
                throw new RuntimeException("收益返还，更新资金余额失败");
            }

            //3.修改income_record的status=1
            rows  = incomeRecordMapper.updateStatus(ir.getId(), YLBConsts.INCOME_STATUS_BACK);
            if( rows  < 1 ){
                throw new RuntimeException("收益返还，更新收益表状态为1失败");
            }
        }

    }
}
