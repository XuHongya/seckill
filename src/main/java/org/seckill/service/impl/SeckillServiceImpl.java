package org.seckill.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.seckill.web.SeckillController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;

@Service
public class SeckillServiceImpl implements SeckillService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final Jedis jedis = new RedisDao("127.0.0.1",6379).getJedis();

	private static final String REDIS_KEY = "product_cnt_";

	private static final String REDIS_KEY_PRODUCT_USER = "product_user_";
	@Autowired
	private SeckillDao seckillDao;

	@Autowired
	private SuccessKilledDao successKilledDao;
	
	//md5盐值字符串，混淆
	private final String slat = "djfjhueuweur832hsiudy87e81@&^&#";

	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	public Exposer exportSeckillUrl(long seckillId) {
		Seckill seckill = seckillDao.queryById(seckillId);
		if(seckill == null) {
			return new Exposer(false,seckillId);
		}
		Date startTime = seckill.getStartTime();
		Date endTime  = seckill.getEndTime();
		Date nowTime = new Date();
		if(nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
		}
		String md5 = getMD5(seckillId);
		return new Exposer(true,md5,seckillId);
	}

	private String getMD5(long seckillId) {
		String base = seckillId + '/' + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}
	
	@Transactional
	/**
	 * 使用注解控制事务的优点：
	 * 1：开发团队达成一致约定，明确标注事务方法的编程风格
	 * 2：保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求，或者剥离到事务外部方法
	 * 3：不是所有的方法都需要事务，一条修改或者只读
	 */
	public SeckillExecution executeSeckill(long id, long userPhone, String md5,String userName,String userAddress)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		if(md5 == null || !md5.equals(getMD5(id))) {
			throw new SeckillException("seckill data rewrite!");
		}
		Date now = new Date();
		try {
			String key = REDIS_KEY + String.valueOf(id);
			if(jedis.exists(key)){
				int cnt = Integer.parseInt(jedis.get(key));
				if(cnt > 0){
					List<String> successList =  jedis.lrange(REDIS_KEY_PRODUCT_USER + String.valueOf(id), 0, Long.MAX_VALUE);
					if (!successList.contains(String.valueOf(userPhone))){
						SuccessKilled successKilled = new SuccessKilled();//id,userPhone)
						successKilled.setSeckillId(id);
						successKilled.setUserPhone(userPhone);
						successKilled.setState(Short.valueOf("0"));
						successKilled.setCreateTime(new Date());
						successKilled.setUserName(userName);
						successKilled.setUserAddress(userAddress);
						successKilledDao.insertSuccessKilled(id,userPhone,userName,userAddress);
						seckillDao.reduceNumber(id,new Date());
						jedis.lpush(REDIS_KEY_PRODUCT_USER + String.valueOf(id),String.valueOf(userPhone));
						return new SeckillExecution(id,SeckillStateEnum.SUCCESS,successKilled);
					}else{
						throw new RepeatKillException("已经秒杀" );
					}

				}else {
					throw new SeckillCloseException("秒杀已经结束" );
				}

			}else
			{
				throw new SeckillException("系统异常" );
			}


		}catch(SeckillCloseException e1) {
			throw e1;
		} catch(RepeatKillException e2) {
			throw e2;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			//所有编译器异常转换成运行时异常
			throw new SeckillException("seckill inner error: " + e.getMessage());
		}
	}

}
