package com.wpj.servicescheduler.task.service.impl;

import com.wpj.servicescheduler.task.service.BaseTaskService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * @author wangpejian
 * @date 2020/1/15 上午9:06
 */
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class HttpTask extends BaseTaskService {

    OkHttpClient client = new OkHttpClient();

    /**
     * 定时任务实现方法
     */
    @Override
    public void doTask() throws IOException {
        String url = this.getScheduler().getUrl();

        if (StringUtils.isEmpty(url)) {
            log.error("http request url is empty");
            return;
        }

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            log.info("http response code: [{}] result: [{}]", response.code(), Objects.requireNonNull(response.body()).string());
        }
    }
}
