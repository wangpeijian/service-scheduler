package com.wpj.servicescheduler.api;

import com.alibaba.fastjson.JSON;
import com.wpj.servicescheduler.config.Scheduler;
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
    public String add(@RequestBody Scheduler scheduler) {
        schedulerStarter.add(scheduler);
        return "ok";
    }

    @PutMapping("")
    public String update(@RequestBody Scheduler scheduler) {
        schedulerStarter.update(scheduler);
        return "ok";
    }

    @DeleteMapping("{id}")
    public String remove(@PathVariable("id") String id) {
        schedulerStarter.remove(id);
        return "ok";
    }

    @GetMapping("")
    public Object getTasks() {
        return JSON.toJSONString(schedulerStarter.getTasks());
    }
}
