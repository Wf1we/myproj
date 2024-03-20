package com.bjpowernode.front.service;

import com.bjpowernode.util.RedisOperation;
import javax.annotation.Resource;

public class BaseService  {

    @Resource
    protected RedisOperation redisOperation;

}
