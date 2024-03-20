package com.bjpowernode.pay;

import com.bjpowernode.util.RedisOperation;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import sun.awt.geom.AreaOp;

import javax.annotation.Resource;

@EnableDubbo
@SpringBootApplication
public class MicroPayApplication  {

	@Resource
	private RedisTemplate redisTemplate;
	@Resource
	private StringRedisTemplate stringRedisTemplate;


	@Bean
	public RedisOperation redisOperation(){
		RedisOperation redisOperation = new RedisOperation(redisTemplate,stringRedisTemplate);
		return redisOperation;
	}

	public static void main(String[] args) {
		SpringApplication.run(MicroPayApplication.class, args);
	}


}
