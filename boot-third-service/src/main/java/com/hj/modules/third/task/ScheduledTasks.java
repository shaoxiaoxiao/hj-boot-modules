package com.hj.modules.third.task;

import com.hj.modules.third.bean.ServiceInterfaceUrlBean;
import com.hj.modules.third.data.ServiceTokenData;
import com.hj.modules.third.data.ServiceUrlData;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 定时任务类
 */
@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    final RequestMappingHandlerMapping requestMappingHandlerMapping;

    final ServiceUrlData serviceUrlData;

    final ServiceTokenData serviceTokenData;

    @Value("${hj.third-service.host}")
    private String host;

    @Value("${hj.third-service.name}")
    private String serviceName;

    // (每30秒执行一次)
    @Scheduled(fixedDelay = 30000)
    public void redisRefreshTask() {
        try {
            serviceUrlData.setControllerUrlByName(getAllApi());
            serviceTokenData.setServiceTokenByName(serviceName);
        } catch (Exception e) {
            log.error("任务异常", e);
        }
    }

    // 获取所有接口 构建好接口名称 路径 调用方法 ServiceInterfaceUrlBean
    private List<ServiceInterfaceUrlBean> getAllApi() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        List<ServiceInterfaceUrlBean> list = new LinkedList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> handlerMethodEntry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = handlerMethodEntry.getValue();
            RequestMappingInfo requestMappingInfo = handlerMethodEntry.getKey();
            // 类 TODO 后续可以增加排除类配置等
            Class<?> clazz = handlerMethod.getBeanType();
            // 方法
            Method method = handlerMethod.getMethod();
            // servletPath
            PathPatternsRequestCondition patternsCondition = requestMappingInfo.getPathPatternsCondition();
            if (patternsCondition == null) {
                continue;
            }
            if (!method.isAnnotationPresent(ApiOperation.class)) {
                continue;
            }
            ServiceInterfaceUrlBean interfaceUrlBean = new ServiceInterfaceUrlBean();
            Set<String> patterns = patternsCondition.getPatternValues();
            String servletPath = patterns.stream().findFirst().orElse("");
            interfaceUrlBean.setUrl(servletPath);
            interfaceUrlBean.setHost(host);
            // 请求方式
            String requestMethod = "";
            RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
            Set<RequestMethod> methods = methodsCondition.getMethods();
            if (methods.size() == 1) {
                requestMethod = methods.stream().findFirst().get().name();
                interfaceUrlBean.setMethod(requestMethod);
            }

            ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
            interfaceUrlBean.setName(apiOperation.value());

            list.add(interfaceUrlBean);
        }
        return list;
    }
}
