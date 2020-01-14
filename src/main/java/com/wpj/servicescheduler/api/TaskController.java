package com.wpj.servicescheduler.api;

import com.alibaba.fastjson.JSON;
import com.wpj.servicescheduler.config.SchedulerConfig;
import com.wpj.servicescheduler.config.SchedulerStarter;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author wangpejian
 * @date 2020/1/14 下午3:01
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    @Resource
    SchedulerStarter schedulerStarter;

    @PostMapping("")
    public String add(@RequestBody SchedulerConfig.Scheduler scheduler) {
        schedulerStarter.addTask(scheduler);
        return "ok";
    }

    @PutMapping("")
    public String update(@RequestBody SchedulerConfig.Scheduler scheduler) {
        schedulerStarter.updateTask(scheduler);
        return "ok";
    }

    @DeleteMapping("")
    public String remove(@RequestParam("id") String id) {
        schedulerStarter.stopTask(id);
        return "ok";
    }

    @GetMapping("")
    public Object getTasks() {
        return JSON.toJSONString(schedulerStarter.getTasks());
    }
}
