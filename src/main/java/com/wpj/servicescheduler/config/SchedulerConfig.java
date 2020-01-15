package com.wpj.servicescheduler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wangpejian
 * @date 2020/1/7 下午1:33
 */
@Data
@Component
@ConfigurationProperties(prefix = "scheduler")
public class SchedulerConfig {
    private boolean database;
    private List<Scheduler> configs;
}
