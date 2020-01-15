package com.wpj.servicescheduler.task.service;

import java.io.IOException;

/**
 * @author wangpejian
 * @date 2020/1/7 下午4:53
 */
public interface TaskService extends Runnable {

    /**
     * 定时任务实现方法
     */
    void doTask() throws IOException;

}
