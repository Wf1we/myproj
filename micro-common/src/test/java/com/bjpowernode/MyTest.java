package com.bjpowernode;

import com.bjpowernode.util.YLBUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class MyTest {

    @Test
    public void test01(){

        String phone = "800000000";
        boolean b  = YLBUtil.checkPhoneFormat(phone);
        System.out.println("b = " + b);
    }


    @Test
    public void test02(){
        String s="【动力金融】你的验证码是：%s，3分钟内有效！请注意保护信息";

        String format = String.format(s, "123456");
        System.out.println("format = " + format);
    }

    @Test
    public void test03(){
        String s  = RandomStringUtils.randomNumeric(6);
        System.out.println("s = " + s);
    }

    @Test
    public void test04(){
        String str="Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:93.0) Gecko/20100101 Firefox/93.0";
        int pos = str.indexOf(" ");
        System.out.println(pos);

        String w = str.substring(str.indexOf(" ") + 2);
        String xitong = w.substring(0,w.indexOf(";"));
        System.out.println("xitong = " + xitong);

    }

    @Test
    public void test05(){
        BigDecimal money = new BigDecimal("0.01");
        String str =  money.multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString();
        System.out.println("str = " + str);
    }

    @Test
    public void test06(){
        BigDecimal rate = new BigDecimal("5.6");
        BigDecimal a1 = rate.divide(new BigDecimal("100"),10, RoundingMode.HALF_UP);
        BigDecimal a2 = a1.divide(new BigDecimal("360"),10,RoundingMode.HALF_UP);
        System.out.println("a1 = " + a1);
        System.out.println("a2 = " + a2);
    }

    @Test
    public void test07(){
        Double d  = 1636426035349D;
        System.out.println(d);

        BigDecimal bigDecimal = new BigDecimal(d);

        System.out.println("d.toString() = " + bigDecimal.toPlainString());

        System.out.println("new Date().getTime() = " + new Date().getTime());

    }
}
