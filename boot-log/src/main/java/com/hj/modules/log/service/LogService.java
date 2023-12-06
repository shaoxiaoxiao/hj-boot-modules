package com.hj.modules.log.service;

import com.hj.modules.log.model.bean.OperateLogBean;
import com.hj.modules.log.utils.BootThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * [ 日志服务 ]
 */
@Slf4j
@Service
public class LogService {

    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private OperateLogService userOperateLogService;

    @PostConstruct
    void init() {
        if (threadPoolExecutor == null) {
            threadPoolExecutor = new ThreadPoolExecutor(1, 1, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(2000),
                    BootThreadFactory.create("LogAspect"));
        }
    }

    @PreDestroy
    void destroy() {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdown();
            threadPoolExecutor = null;
        }
    }

    public void addLog(Object object) {
        try {
            if (object instanceof OperateLogBean) {
                threadPoolExecutor.execute(() -> userOperateLogService.insertOperateLog((OperateLogBean) object));
            }
        } catch (Throwable e) {
            log.error("userLogAfterAdvice:{}", e.getLocalizedMessage());
        }
    }
}
