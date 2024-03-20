package com.bjpowernode.front.service;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.consts.YLBKey;
import com.bjpowernode.front.settings.JdwxSmsConfig;
import com.bjpowernode.util.RedisOperation;
import com.bjpowernode.vo.BusinessResult;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class SmsService {

    @Resource
    private JdwxSmsConfig smsConfig;

    @Resource
    private RedisOperation redisOperation;


    /**
     * 验证码的处理
     * @param phone 手机号
     * @param code  请求中验证码
     * @return true：和redis中的一致， false其他
     */
    public boolean checkAuthCode(String phone,String code){
        boolean result = false;
        String key  = YLBKey.SMS_REGIST_AUTH_CODE +  phone;
        if( code != null && code.equals(  redisOperation.getStr(key ))){
            result = true;
        }
        return result;
    }

    /**
     * @param phone 手机号
     *  删除key
     */
    public void removeRedisKey(String phone){
        String key  = YLBKey.SMS_REGIST_AUTH_CODE +  phone;
        redisOperation.removeStrKey(key);
    }


    /**
     * @param phone 手机号
     */
    public boolean userRegisterSendSmsAuthCode(String phone){
        //生成6位数字验证码
        String authCode = RandomStringUtils.randomNumeric(6);
        String content = String.format(smsConfig.getContent(),authCode);
        //发送短信
        BusinessResult result  = invokeSendSms(phone,content);
        if( result.getResult() ){
            //使用redis存储短信验证码
            String key = YLBKey.SMS_REGIST_AUTH_CODE + phone;
            redisOperation.setStr(key,authCode,3);
        }

        System.out.println("短信验证码===="+authCode);
        //返回发送成功或者失败
        return result.getResult();

    }

    /**
     * 发送短信
     */
    public BusinessResult invokeSendSms(String phone, String content){

        BusinessResult businessResult = new BusinessResult(false,0,"开始处理业务请求");
        CloseableHttpClient client = HttpClients.createDefault();
        //https://way.jd.com/chuangxin/dxjk?mobile=13568813957&content=【创信】你的验证码是：5873，3分钟内有效！&appkey=您申请的APPKEY
        String url = smsConfig.getUrl()+"?mobile="+phone
                     + "&content=" + content
                     + "&appkey="+smsConfig.getAppkey();

        HttpGet get  = new HttpGet(url);

        //短信是否发送成功
        boolean isSendOk = false;
        try{
            //CloseableHttpResponse resp = client.execute(get);
            //if( resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            if( 10 > 5){
                //String result = EntityUtils.toString(resp.getEntity());

                String result = "{\n" +
                        "    \"code\": \"10000\",\n" +
                        "    \"charge\": false,\n" +
                        "    \"remain\": 1305,\n" +
                        "    \"msg\": \"查询成功\",\n" +
                        "    \"result\": {\n" +
                        "        \"ReturnStatus\": \"Success\",\n" +
                        "        \"Message\": \"ok\",\n" +
                        "        \"RemainPoint\": 420842,\n" +
                        "        \"TaskID\": 18424321,\n" +
                        "        \"SuccessCounts\": 1\n" +
                        "    }\n" +
                        "}";

                System.out.println("result="+result);
                //解析json，使用fastjson
                if(StringUtils.isNotBlank(result)){
                    JSONObject json = JSONObject.parseObject(result);

                    businessResult.setCode(json.getInteger("code"));
                    businessResult.setMsg(json.getString("msg"));

                    if("10000".equals(json.getString("code"))){
                        JSONObject jsonResult =  json.getJSONObject("result");
                        if( jsonResult != null){
                            isSendOk = "Success".equals(jsonResult.getString("ReturnStatus"));
                        }
                    }
                }
            } else {
                businessResult.setMsg("访问短信接口请求失败");
            }
            businessResult.setResult(isSendOk);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return businessResult;
    }
}
