package com.bjpowernode.util;

import java.math.BigDecimal;

public class YLBUtil {

    //手机号格式v true:格式正确， false失败
    public static boolean checkPhoneFormat(String phone){
        boolean flag = false;
        if( phone != null ){
            flag = phone.matches("^1[1-9]\\d{9}$");
        }
        return flag;
    }

    //默认的pageNo
    public static Integer pageNo(Integer pageNo){
        int pno  = pageNo;
        if( pageNo == null || pageNo < 1){
            pno = 1;
        }
        return pno;
    }

    //默认的pageSize
    public static Integer pageSize(Integer pageSize){
        int psize = pageSize;
        if( pageSize  == null || pageSize < 1 || pageSize > 100){
            psize = 10;
        }
        return psize;
    }


    /***********limit 的 offset *****************/
    public static Integer offSet(Integer pageNo, Integer pageSize){
        int offset  = (pageNo - 1 ) * pageSize ;
        return offset;
    }
    /***********判断对象非null 值大于 0 **********************/
    public static boolean checkNullZero(Integer id){
        boolean flag = false;
        if( id != null && id > 0 ){
            flag = true;
        }
        return flag;
    }

    /**********BigDecimal的数据比较  n1>=n2 ：true **********************/
    public static boolean ge(BigDecimal n1, BigDecimal n2){
        boolean result = false;
        if( n1 == null  || n2 == null){
            throw new RuntimeException("比较的数据是null");
        } else{
            result  = n1.compareTo(n2) >=0;
        }
        return result;
    }



}
