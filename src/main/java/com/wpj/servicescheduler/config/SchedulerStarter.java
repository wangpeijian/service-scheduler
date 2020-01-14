package com.wpj.servicescheduler.config;

import com.wpj.servicescheduler.service.BaseTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;


/**
 * @author wangpejian
 * @date 2020/1/7 上午11:27
 */
@Slf4j
@Component
public class SchedulerStarter {

    private final SchedulerConfig schedulerConfig;
    private final List<BaseTaskService> taskServices;
    public ConcurrentHashMap<String, SchedulerWarp> scheduledMap;
    private Map<String, BaseTaskService> tasks;
    private ThreadPoolTaskScheduler taskScheduler;

    public SchedulerStarter(SchedulerConfig schedulerConfig, List<BaseTaskService> taskServices) {
        this.schedulerConfig = schedulerConfig;
        this.taskServices = taskServices;
    }

    @PostConstruct
    private void postConstruct() {
        int serviceSize = taskServices.size();
        taskScheduler = this.getThreadPool(serviceSize);
        scheduledMap = new ConcurrentHashMap<>(serviceSize);

        this.tasks = this.getTaskMap(taskServices);
        schedulerConfig.getConfigs().forEach(this::addTask);
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
     * 创建task映射
     *
     * @param taskServices
     * @return
     */
    private Map<String, BaseTaskService> getTaskMap(List<BaseTaskService> taskServices) {
        HashMap<String, BaseTaskService> map = new HashMap<>(taskServices.size());

        taskServices.forEach(baseTaskService -> {
            String code = baseTaskService.getClass().getSimpleName();
            map.put(code, baseTaskService);
        });

        return map;
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
        BaseTaskService taskService = tasks.get(task);
        CronTrigger cronTrigger = new CronTrigger(cron);

        if (taskService != null) {
            taskService.setScheduler(scheduler);
            ScheduledFuture<?> future = taskScheduler.schedule(taskService, cronTrigger);
            taskService.setFuture(future);

            if (future != null) {
                scheduledMap.put(id, new SchedulerWarp(scheduler, future));
            }

            log.info("id: [{}] task: [{}] corn: [{}] desc:[{}] 启动成功！", id, task, cron, desc);
        } else {
            log.error("[{}],没有匹配到对应的bean", task);
        }
    }

    /**
     * 停止一个任务
     *
     * @param id
     */
    public void stopTask(String id) {
        SchedulerWarp schedulerWarp = scheduledMap.get(id);
        ScheduledFuture<?> future = schedulerWarp.getFuture();
        if (future == null) {
            log.error("id [{}] 不存在", id);
        } else {
            future.cancel(true);
            scheduledMap.remove(id);
        }
    }

    /**
     * 更新一个定时任务
     *
     * @param scheduler
     */
    public void updateTask(SchedulerConfig.Scheduler scheduler) {
        String id = scheduler.getId();
        this.stopTask(id);
        this.addTask(scheduler);
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
