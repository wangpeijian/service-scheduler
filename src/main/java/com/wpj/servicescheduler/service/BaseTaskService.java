package com.wpj.servicescheduler.service;

import com.wpj.servicescheduler.config.SchedulerConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledFuture;

/**
 * @author wangpejian
 * @date 2020/1/7 下午4:56
 */
@Slf4j
@Data
public abstract class BaseTaskService implements TaskService {

    private ScheduledFuture<?> future;
    private SchedulerConfig.Scheduler scheduler;

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        String currentClass = this.getClass().getName();

        String id = scheduler.getId();

        try {
            log.info("[{}]-[{}]开始执行: ", id, currentClass);
            this.doTask();
            log.info("[{}]-[{}]执行完成, 用时：[{}]ms", id, currentClass, System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("[{}]-[{}]执行异常: , 用时：[{}]ms.", id, currentClass, System.currentTimeMillis() - start, e);
        }

    }
}
