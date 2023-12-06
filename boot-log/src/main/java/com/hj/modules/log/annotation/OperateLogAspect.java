package com.hj.modules.log.annotation;

import com.alibaba.fastjson.JSON;
import com.hj.modules.log.data.UserData;
import com.hj.modules.log.data.UserInfo;
import com.hj.modules.log.model.bean.OperateLogBean;
import com.hj.modules.log.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * [ 操作日志记录处理,对所有OperateLog注解的Controller进行操作日志监控 ]
 */
@Slf4j
@Aspect
@Component
public class OperateLogAspect {

    @Autowired
    private LogService logService;

    @Pointcut("execution(public * com.*.controller.*Controller.*(..))")
    public void logPointCut() {
    }

    @AfterReturning(pointcut = "logPointCut()")
    public void doAfterReturning(JoinPoint joinPoint) {
        handleLog(joinPoint, null);
    }

    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e) {
        try {
            OperateLog operateLog = this.getAnnotationLog(joinPoint);
            if (operateLog == null) {
                return;
            }
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String token = request.getHeader("token");
            // 通过反射找到对应类 再调用对应方法获取到
            UserInfo adminUserInfo = UserData.getUserInfo(token);
            if (adminUserInfo == null) {
                return;
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            // 方法名
            String methodName = methodSignature.getName();
            String operateMethod = className + "." + methodName;
            // 参数名数组
            String[] parameters = methodSignature.getParameterNames();
            // 参数值
            Object[] args = joinPoint.getArgs();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parameters.length; i++) {
                sb.append(parameters[i]);
                sb.append("[");
                if (args[i] != null) {
                    sb.append(JSON.toJSONString(args[i]));
                } else {
                    sb.append(args[i]);
                }
                sb.append("] ");
            }
            String params = sb.toString();
            String failReason = null;
            int result = 0;
            if (e != null) {
                result = 1;
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw, true);
                e.printStackTrace(pw);
                failReason = sw.toString();
                pw.flush();
                pw.close();
                sw.flush();
                sw.close();
            }
            OperateLogBean operateLogEntity = OperateLogBean.builder().userId(adminUserInfo.getUserId()).userName(adminUserInfo.getAccount())
                    .url(request.getRequestURI()).method(operateMethod).param(params).failReason(failReason).result(result).build();
            ApiOperation apiOperation = this.getApiOperation(joinPoint);
            if (apiOperation != null) {
                operateLogEntity.setContent(apiOperation.value());
                Api api = this.getApi(joinPoint);
                if (api != null) {
                    String[] tags = api.tags();
                    operateLogEntity.setModule(StringUtils.join(tags, ","));
                }
                logService.addLog(operateLogEntity);
            }
        } catch (Exception exp) {
            log.error("保存操作日志异常:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    private OperateLog getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        return AnnotationUtils.findAnnotation(method.getDeclaringClass(), OperateLog.class);
    }

    /**
     * swagger API
     */
    private Api getApi(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        return AnnotationUtils.findAnnotation(method.getDeclaringClass(), Api.class);
    }

    /**
     * swagger ApiOperation
     */
    private ApiOperation getApiOperation(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(ApiOperation.class);
        }
        return null;
    }

}
