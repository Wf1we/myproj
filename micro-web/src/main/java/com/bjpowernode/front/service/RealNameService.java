package com.bjpowernode.front.service;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.api.service.UserService;
import com.bjpowernode.consts.YLBKey;
import com.bjpowernode.front.settings.JdwxRealNameConfig;
import com.bjpowernode.util.RedisOperation;
import com.bjpowernode.vo.BusinessResult;
import com.bjpowernode.vo.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Set;

@Service
public class RealNameService extends BaseService{

    @Resource
    private JdwxRealNameConfig realNameConfig;

    @DubboReference(interfaceClass = UserService.class,version = "1.0")
    private UserService userService;

    /**
     * @param userId 用户的主键id
     * @param idCard 身份证号
     * @param name   姓名
     * @return true:认证成功；false需要重新认证（每天只能认证3次）
     */
    public boolean handlerRealName(Integer userId,String idCard,String name){
        boolean modifyResult = false;
        //1.认证姓名
        BusinessResult businessResult = invokeRealNameApi(idCard, name);
        //2.更新数据库
        if( businessResult.getResult() ){
            modifyResult =  userService.modifyRealName(userId,idCard,name);
        }
        return modifyResult;
    }


    /**
     * 调用第三方实名认证接口
     * @param idCard 身份证号
     * @param name   姓名
     * @return
     */
    public BusinessResult invokeRealNameApi(String idCard,String name){
        BusinessResult businessResult = new BusinessResult(false,0,"开始处理业务请求");

        CloseableHttpClient client = HttpClients.createDefault();
        String url = realNameConfig.getUrl()+"?cardNo="+idCard
                + "&realName=" + name
                + "&appkey="+realNameConfig.getAppkey();

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
                        "        \"error_code\": 0,\n" +
                        "        \"reason\": \"成功\",\n" +
                        "        \"result\": {\n" +
                        "            \"realname\": \""+name+"\",\n" +
                        "            \"idcard\": \""+idCard+"\",\n" +
                        "            \"isok\": true\n" +
                        "        }\n" +
                        "    }\n" +
                        "}";

                System.out.println("result="+result);
                //解析json，使用fastjson
                if(StringUtils.isNotBlank(result)){
                    JSONObject level1 = JSONObject.parseObject(result);

                    businessResult.setCode(Integer.parseInt(level1.getString("code")));
                    businessResult.setMsg(level1.getString("msg"));

                    if( "10000".equals(level1.getString("code"))){
                        JSONObject level2 = level1.getJSONObject("result");
                        if( level2 != null ){
                           JSONObject level3 =  level2.getJSONObject("result");
                           if( level3 != null){
                               boolean  isOk  = level3.getBoolean("isok");
                               businessResult.setResult(isOk);
                           }
                        }
                    }
                }
            } else {
                businessResult.setMsg("访问实名认证接口请求失败");
            }

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

    /**
     * 记录实名认证失败的次数
     * @param phone 手机号
     */
    public void recordErrorTimes(String phone,String date) {

        //RNTIMES:136000000:20211101
        String key = YLBKey.REALNAME_ERROR_TIMES + phone+":"+date;
        try{
            String strTimes = redisOperation.getStr(key);
            strTimes = StringUtils.isBlank(strTimes) ? "0":strTimes;

            int times = Integer.parseInt(strTimes);
            redisOperation.setStr(key,String.valueOf(times + 1));

        }catch (Exception e){
            redisOperation.setStr(key,"1");
        }

    }

    /**
     * 检查错误次数是否超过3次
     * @param phone 手机号
     * @param date  日期
     * @return
     */
    public boolean checkErrorTimes(String phone, String date) {
        boolean flag = false; //true可以继续认证；false不可以
        String key = YLBKey.REALNAME_ERROR_TIMES  +phone+":"+date;
        try{
            String strTimes = redisOperation.getStr(key);
            strTimes = StringUtils.isBlank(strTimes) ? "0" : strTimes;
            int times = Integer.parseInt(strTimes);
            if( times <= 3){
                flag = true;
            }
        }catch (Exception e){
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    //删除实名认证使用的key
    public void removeRedisData(String date) {
        String patternKey = "RNTIMES*"+date;
        Set<String> keys = redisOperation.searchPatternKey(patternKey);
        if( keys.size() > 0 ){
            long nums =  redisOperation.removeKey(keys);
        }
    }
}
