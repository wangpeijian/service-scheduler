package com.wpj.servicescheduler.config;

import lombok.Data;

import java.util.concurrent.ScheduledFuture;

/**
 * @author wangpejian
 * @date 2020/1/14 下午3:36
 */
@Data
public class SchedulerWarp {
    private SchedulerConfig.Scheduler scheduler;
    private ScheduledFuture<?> future;

    SchedulerWarp(SchedulerConfig.Scheduler scheduler, ScheduledFuture<?> future) {
        this.scheduler = scheduler;
        this.future = future;
    }
}
