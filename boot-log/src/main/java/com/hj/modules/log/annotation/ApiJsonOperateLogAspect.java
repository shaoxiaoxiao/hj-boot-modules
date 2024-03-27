package com.hj.modules.log.annotation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hj.modules.log.data.LogTableData;
import com.hj.modules.log.data.UserData;
import com.hj.modules.log.data.UserInfo;
import com.hj.modules.log.model.bean.OperateLogBean;
import com.hj.modules.log.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * [ ApiJson操作日志记录处理 ]
 */
@Slf4j
@Aspect
@Component
public class ApiJsonOperateLogAspect {

    @Autowired
    private LogService logService;

    @Autowired
    private LogTableData tableData;

    @Pointcut("execution(* com.*..*ApiJsonController.*(..))")
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
            ApiJsonOperateLog apiJsonOperateLog = this.getAnnotationLog(joinPoint);
            if (apiJsonOperateLog == null) {
                return;
            }
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (servletRequestAttributes == null) {
                return;
            }
            HttpServletRequest request = servletRequestAttributes.getRequest();
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
            Map<String, String> map = new LinkedHashMap<>();
            // 表名
            String tableName = "";
            for (int i = 0; i < parameters.length; i++) {
                if ("dataName".equals(parameters[i])) {
                    tableName = args[i] != null ? (String) args[i] : "";
                }
                if (args[i] != null) {
                    map.put(parameters[i], JSON.toJSONString(args[i], SerializerFeature.IgnoreErrorGetter));
                } else {
                    map.put(parameters[i], null);
                }
            }
            String params = JSON.toJSONString(map);
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
            // ApiJson的模块为表名 内容为通用接口上面的注释
            ApiOperation apiOperation = this.getApiOperation(joinPoint);
            if (apiOperation != null) {
                operateLogEntity.setContent(apiOperation.value());
                // 根据表名参数获取真实的中文表名 即数据库表注释
                operateLogEntity.setModule(tableData.getTableName(tableName));
                logService.addLog(operateLogEntity);
            }
        } catch (Exception exp) {
            log.error("保存操作日志异常:{}", exp.getMessage());
        }
    }

    private ApiJsonOperateLog getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        return AnnotationUtils.findAnnotation(method.getDeclaringClass(), ApiJsonOperateLog.class);
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
