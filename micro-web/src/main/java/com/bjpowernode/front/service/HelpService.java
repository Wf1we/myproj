package com.bjpowernode.front.service;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class HelpService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @param type 产品类型
     * @return true存在
     */
    public boolean checkProductType(String type){
        HashOperations<String, String, String> operation = stringRedisTemplate.opsForHash();
        List<String> values = operation.values("DIC:PRODUCT:TYPE");
        return values.contains(type);
    }
}
