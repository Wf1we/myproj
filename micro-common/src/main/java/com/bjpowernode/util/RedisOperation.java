package com.bjpowernode.util;

import com.bjpowernode.consts.YLBKey;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.beans.BeanInfo;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//redis的操作类
public class RedisOperation {

    private StringRedisTemplate stringRedisTemplate;

    private RedisTemplate redisTemplate;

    public RedisOperation(RedisTemplate redisTemplate,StringRedisTemplate stringRedisTemplate){
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate =stringRedisTemplate;
    }


    //查询指定模式的key
    public Set<String> searchPatternKey(String partternKey){
        Set<String> keys = stringRedisTemplate.keys(partternKey);
        return keys;
    }

    //删除多个key
    public long removeKey(Collection<String> keys){
        return stringRedisTemplate.delete(keys);
    }

    /**
     * 创建zset类型
     * @param key
     * @param value
     * @param score
     */
    public void zset(String key, String value,double score){
        stringRedisTemplate.opsForZSet().add(key,value,score);
    }

    /***
     * 累加score ， key，value ，score
     */
    public void incrScoreZSet(String key, String value, Double score){
        ZSetOperations<String, String> zset = stringRedisTemplate.opsForZSet();
        zset.incrementScore(key,value,score);
    }

    //反向排序，获取指定索引范围的数据
    public Set<ZSetOperations.TypedTuple<String>> reverseRangeZSet(String key, int begin,int end){
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, begin, end);
        return typedTuples;
    }

    //删除zset中的某个value
    public void removeValueZSet(String key, String orderId) {
        stringRedisTemplate.opsForZSet().remove(key,orderId);
    }


    //获取zset的全部数据
    public  Set<ZSetOperations.TypedTuple<String>>  getAllZSet(String key){
        Set<ZSetOperations.TypedTuple<String>> sets = stringRedisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
        return sets;
    }


    /**
     * @param key 自增+1
     * @return
     */
    public long incr(String key){
       return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * @param type 产品类型
     * @return true存在
     */
    public boolean checkProductType(String type){
        List<String> values = getHashValues(YLBKey.DIC_PRODUCT_TYPE);
        return values.contains(type);
    }


    /**
     * @param key hash类型的key
     * @return  把hash的所有值转为List<String>
     */
    public List<String> getHashValues(String key){
        HashOperations<String, String, String> operation = stringRedisTemplate.opsForHash();
        return operation.values(key);
    }


    /**
     * 查询string类型的值，参数是key
     */
    public Object getString(String key){
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * @param key    string类型的key
     * @param value  value
     * @param minute 过期时间，单位分钟
     */
    public void setString(String key,Object value,long minute){
        redisTemplate.setKeySerializer( new StringRedisSerializer());
        redisTemplate.opsForValue().set(key,value,minute, TimeUnit.MINUTES);
    }

    /**
     * @param key     字符串
     * @param value   字符串
     * @param minute  过期时间，分钟
     */
    public void setStr(String key,String value,long minute){
        stringRedisTemplate.opsForValue().set(key,value,minute,TimeUnit.MINUTES);
    }

    public void setStr(String key,String value){
        stringRedisTemplate.opsForValue().set(key,value);
    }

    /**
     * @param key key
     * @return    值
     */
    public String getStr(String key){
        String value = stringRedisTemplate.opsForValue().get(key);
        return value;
    }

    /**
     * @param key 删除key
     */
    public void removeStrKey(String key) {
        stringRedisTemplate.delete(key);
    }


}
