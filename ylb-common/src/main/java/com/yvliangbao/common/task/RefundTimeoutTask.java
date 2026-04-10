package com.yvliangbao.common.task;

import com.yvliangbao.common.service.order.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 退款超时处理定时任务
 * 
 * 功能：自动处理超过24小时未审核的退款申请
 * 执行时间：每小时执行一次
 *
 * @author 余量宝
 */
@Slf4j
@Component
public class RefundTimeoutTask {

    private static final String LOCK_KEY = "lock:refund:timeout";
    private static final long LOCK_EXPIRE_SECONDS = 50;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 处理超时退款申请
     * 
     * cron表达式：0 0 * * * ? 表示每小时整点执行
     * 即：每天 0:00, 1:00, 2:00, ... 23:00 执行
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void processTimeoutRefunds() {
        String lockValue = UUID.randomUUID().toString();
        
        try {
            Boolean acquired = stringRedisTemplate.opsForValue()
                    .setIfAbsent(LOCK_KEY, lockValue, LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
            
            if (!Boolean.TRUE.equals(acquired)) {
                log.debug("获取分布式锁失败，另一实例正在执行");
                return;
            }
            
            log.info("========== 开始执行退款超时检查任务 ==========");
        
            int count = orderInfoService.processTimeoutRefunds();
            log.info("========== 退款超时检查任务完成，处理{}笔 ==========", count);
        } catch (Exception e) {
            log.error("========== 退款超时检查任务执行异常 ==========", e);
        }
    }
}
