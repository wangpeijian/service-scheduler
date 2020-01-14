package com.wpj.servicescheduler.config;

import com.wpj.servicescheduler.service.BaseTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;


/**
 * @author wangpejian
 * @date 2020/1/7 上午11:27
 */
@Slf4j
@Component
public class SchedulerStarter implements ApplicationContextAware {

    private final SchedulerConfig schedulerConfig;

    private ConcurrentHashMap<String, SchedulerWarp> scheduledMap;
    private ThreadPoolTaskScheduler taskScheduler;
    private ApplicationContext applicationContext;

    public SchedulerStarter(SchedulerConfig schedulerConfig) {
        this.schedulerConfig = schedulerConfig;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

        int serviceSize = schedulerConfig.getConfigs().size();

        this.taskScheduler = this.getThreadPool(serviceSize);
        this.scheduledMap = new ConcurrentHashMap<>(serviceSize);

        this.schedulerConfig.getConfigs().forEach(this::addTask);
    }

    /**
     * 创建任务线程池
     *
     * @param size
     * @return
     */
    private ThreadPoolTaskScheduler getThreadPool(int size) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(size);
        taskScheduler.initialize();

        return taskScheduler;
    }

    /**
     * 新增定时任务
     *
     * @param scheduler
     */
    public void addTask(SchedulerConfig.Scheduler scheduler) {
        String id = scheduler.getId();

        if (scheduledMap.containsKey(id)) {
            log.error("id: [{}] 已经存在", id);
            return;
        }

        String task = scheduler.getTask();
        String cron = scheduler.getCorn();
        String desc = scheduler.getDesc();
        BaseTaskService taskService = null;


        try {
            taskService = applicationContext.getBean(task, BaseTaskService.class);
        } catch (Exception e) {
            log.error("[{}],没有匹配到对应的bean", task, e);
        }

        if (taskService != null) {
            taskService.setScheduler(scheduler);

            // 没有cron表达式，直接触发一次
            if (StringUtils.isEmpty(cron)) {
                taskScheduler.submit(taskService);
            } else {
                // 有corn表达式，按定时任务处理
                CronTrigger cronTrigger = new CronTrigger(cron);
                ScheduledFuture<?> future = taskScheduler.schedule(taskService, cronTrigger);
                taskService.setFuture(future);

                scheduledMap.put(id, new SchedulerWarp(scheduler, future));
            }

            log.info("id: [{}] task: [{}] corn: [{}] desc:[{}] 启动成功！", id, task, cron, desc);
        }
    }

    /**
     * 停止一个任务
     *
     * @param id
     */
    public void stopTask(String id) {
        log.info("准备移除定时任务：[{}]", id);

        SchedulerWarp schedulerWarp = scheduledMap.get(id);

        if (schedulerWarp == null) {
            log.error("id [{}] 不存在", id);
        } else {
            ScheduledFuture<?> future = schedulerWarp.getFuture();
            future.cancel(true);
            scheduledMap.remove(id);
            log.info("移除定时任务：[{}]", id);
        }
    }

    /**
     * 更新一个定时任务
     *
     * @param scheduler
     */
    public void updateTask(SchedulerConfig.Scheduler scheduler) {
        String id = scheduler.getId();
        log.info("准备更新定时任务：[{}]", id);

        SchedulerWarp schedulerWarp = scheduledMap.get(id);

        if (schedulerWarp == null) {
            log.error("id [{}] 不存在", id);
        } else {
            this.stopTask(id);
            this.addTask(scheduler);
            log.info("更新定时任务：[{}]", id);
        }
    }

    /**
     * 获取任务列表
     *
     * @return
     */
    public List<SchedulerConfig.Scheduler> getTasks() {
        return scheduledMap.values().stream().map(SchedulerWarp::getScheduler).collect(Collectors.toList());
    }


}
