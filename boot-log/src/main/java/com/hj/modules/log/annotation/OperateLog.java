package com.hj.modules.log.annotation;

import java.lang.annotation.*;

/**
 * [ 用户操作日志 ]
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface OperateLog {

}
