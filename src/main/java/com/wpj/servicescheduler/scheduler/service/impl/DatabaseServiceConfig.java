package com.wpj.servicescheduler.scheduler.service.impl;

import com.wpj.servicescheduler.config.Scheduler;
import com.wpj.servicescheduler.scheduler.service.SchedulerService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangpejian
 * @date 2020/1/15 下午3:39
 */
@Configuration
@ConditionalOnProperty(value = "scheduler.database", havingValue = "true")
public class DatabaseServiceConfig {

    @Bean
    public SchedulerService baseService() {
        return new SchedulerService() {
            List<Scheduler> configList = null;

            @Override
            public List<Scheduler> getConfig() {

                if (configList == null) {
                    configList = new ArrayList<>();
                }

                return configList;
            }

            @Override
            public void createConfig(Scheduler scheduler) {
                configList.add(scheduler);
            }

            @Override
            public void removeConfig(String id) {
                configList = configList.stream().filter(scheduler -> !id.equals(scheduler.getId())).collect(Collectors.toList());
            }

            @Override
            public void updateConfig(Scheduler scheduler) {
                int size = configList.size();
                for (int i = 0; i < size; i++) {
                    Scheduler item = configList.get(i);

                    if (item.getId().equals(scheduler.getId())) {
                        configList.set(i, scheduler);
                        break;
                    }
                }
            }
        };
    }
}