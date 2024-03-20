package com.bjpowernode.dataservice;

import com.bjpowernode.api.po.DicInfo;
import com.bjpowernode.dataservice.mapper.DicMapper;
import com.bjpowernode.util.RedisOperation;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MapperScan("com.bjpowernode.dataservice.mapper")
@EnableDubbo
@SpringBootApplication
public class MicroDataserviceApplication  implements CommandLineRunner {

	@Resource
	private DicMapper dicMapper;

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Resource
	private RedisTemplate redisTemplate;

	@Bean
	public RedisOperation redisOperation(){
		return new RedisOperation(redisTemplate,stringRedisTemplate);
	}

	public static void main(String[] args) {

		//System.out.println("1 ===========main开始=============");
		ApplicationContext ctx = SpringApplication.run(MicroDataserviceApplication.class, args);
		//System.out.println("3 *****************main after =============");
		/*DicMapper dicMapper  = ctx.getBean(DicMapper.class);
		List<DicInfo> dicInfoList  = dicMapper.selectListByCatetory("1");
		System.out.println("dicInfoList="+dicInfoList);*/
	}

	@Override
	public void run(String... args) throws Exception {
		//在spring的容器对象创建好后， 由框架调用执行run（）
		//在项目开始的时候， 所有业务逻辑之前想做的什么。
		List<DicInfo> dicInfoList  = dicMapper.selectListByCatetory("1");
		//System.out.println("2 ==================run   dicInfoList="+dicInfoList);
		//使用hash类型
		HashOperations<String, String, String> operation = stringRedisTemplate.opsForHash();
		Map<String,String> productTypeMap = new HashMap<>();

		dicInfoList.forEach( dic->{
			productTypeMap.put(dic.getName(), dic.getVal());
		});
		operation.putAll("DIC:PRODUCT:TYPE",productTypeMap);

	}
}
