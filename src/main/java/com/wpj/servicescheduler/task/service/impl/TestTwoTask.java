package com.wpj.servicescheduler.task.service.impl;

import com.wpj.servicescheduler.task.service.BaseTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author wangpejian
 * @date 2020/1/14 下午4:15
 */
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TestTwoTask extends BaseTaskService {
    /**
     * 定时任务实现方法
     */
    @Override
    public void doTask() {
        log.info("当前任务id：[{}]", this.getScheduler().getId());
    }
}