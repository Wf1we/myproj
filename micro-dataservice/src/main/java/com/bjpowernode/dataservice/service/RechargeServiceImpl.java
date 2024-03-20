package com.bjpowernode.dataservice.service;

import com.bjpowernode.api.model.ServiceResult;
import com.bjpowernode.api.po.RechargeRecord;
import com.bjpowernode.api.service.RechargeService;
import com.bjpowernode.consts.YLBConsts;
import com.bjpowernode.dataservice.mapper.FinanceAccountMapper;
import com.bjpowernode.dataservice.mapper.RechargeRecordMapper;
import com.bjpowernode.util.YLBUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DubboService(interfaceClass = RechargeService.class,version = "1.0")
public class RechargeServiceImpl implements RechargeService {

    @Resource
    private RechargeRecordMapper rechargeRecordMapper;

    @Resource
    private FinanceAccountMapper accountMapper;


    @Override
    public List<RechargeRecord> queryByUserId(Integer userId,
                                              Integer pageNo,
                                              Integer pageSize) {
        List<RechargeRecord> rechargeList = new ArrayList<>();
        if( userId != null && userId > 0 ){
            pageNo = YLBUtil.pageNo(pageNo);
            pageSize = YLBUtil.pageSize(pageSize);
            rechargeList = rechargeRecordMapper.selectByUserId(userId, YLBUtil.offSet(pageNo,pageSize),pageSize);
        }
        return rechargeList;
    }

    @Override
    public Integer queryCountByUserId(Integer userId) {
        Integer countNums = 0;
        if( userId != null && userId > 0 ){
            countNums = rechargeRecordMapper.selectCountRechargeNumByUserId(userId);
        }
        return countNums;
    }

    /**
     * 创建充值记录
     * @param recharge
     */
    @Override
    public boolean addRecharge(RechargeRecord recharge) {
        int rows  = rechargeRecordMapper.insertSelective(recharge);
        return rows > 0 ;
    }

    /**
     * @param orderId    订单号
     * @param payResult  充值结果10：成功，11失败
     * @param payAmount  充值金额，单位分
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ServiceResult handlerKqNotify(String orderId,
                                         String payResult,
                                         String payAmount) {

        int rows  = 0;
        ServiceResult result = new ServiceResult();
        //1. 查询订单
        RechargeRecord recharge = rechargeRecordMapper.selectByRechargeNo(orderId);
        if(recharge != null){
            //2.判断记录是否处理过，当status==0才需要处理
            if( recharge.getRechargeStatus() == YLBConsts.RECHARGE_PROCESSING ){
                //3.判断金额
                String fen  = recharge.getRechargeMoney().multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString();
                if( fen.equals(payAmount)){
                    //4.根据充值结果，分别处理
                    if( "10".equals(payResult)){
                        //5.充值成功，更新资金，更新充值记录状态
                        rows = accountMapper.updateAccountMoneyByRecharge(recharge.getUid(),recharge.getRechargeMoney());
                        if( rows < 1){
                            throw new RuntimeException("充值操作，更新账户资金失败");
                        }

                        //6.更新充值记录状态
                        rows = rechargeRecordMapper.updateRechargeStatus(recharge.getId(),YLBConsts.RECHARGE_SUCCESS);
                        if( rows < 1){
                            throw new RuntimeException("充值操作，更新充值记录状态为1失败");
                        }

                        result.setResult(true);
                        result.setMsg("成功");
                        result.setCode(30000);
                    }  else  {
                        //6.更新充值记录状态
                        rows = rechargeRecordMapper.updateRechargeStatus(recharge.getId(),YLBConsts.RECHARGE_FAILURE);
                        if( rows < 1){
                            throw new RuntimeException("充值操作，更新充值记录状态为2失败");
                        }
                    }
                }
            }
        } /*else {
            result.setResult(false);
            result.setMsg("充值记录不存在");
            result.setCode(30026);
        }*/
        return result;
    }

    @Override
    public int modifyRechargeStatus(String rechargeNo, Integer newStatus) {
        int rows = rechargeRecordMapper.updateRechargeStatusByRechargeNo(rechargeNo,newStatus);
        return rows;
    }

    //根据订单号，查询充值结果
    @Override
    public RechargeRecord queryByRechargeNo(String rechargeNo) {
        RechargeRecord rechargeRecord = rechargeRecordMapper.selectByRechargeNo(rechargeNo);
        return rechargeRecord;
    }
}
