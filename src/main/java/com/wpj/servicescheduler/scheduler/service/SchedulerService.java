package com.wpj.servicescheduler.scheduler.service;

import com.wpj.servicescheduler.config.Scheduler;

import java.util.List;

/**
 * @author wangpejian
 * @date 2020/1/15 上午9:08
 */
public interface SchedulerService {

    /**
     * 获取计划任务配置
     *
     * @return
     */
    List<Scheduler> getConfig();

    /**
     * 创建新任务
     */
    void createConfig(Scheduler scheduler);

    /**
     * 移除任务
     */
    void removeConfig(String id);

    /**
     * 更新任务
     *
     * @param scheduler
     */
    void updateConfig(Scheduler scheduler);
}
