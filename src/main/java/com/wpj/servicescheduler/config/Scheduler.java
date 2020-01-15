package com.wpj.servicescheduler.config;

import lombok.Data;

/**
 * @author wangpejian
 * @date 2020/1/15 上午9:15
 */
@Data
public class Scheduler {
    private String id;
    private String task;
    private String corn;
    private String desc;
    private String url;
}
