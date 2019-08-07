package com.wolken.schedule;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/5/7 21:03
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SchedulingSelector.class)
@Documented
public @interface EnableXScheduling {

    XScheduleMode mode() default XScheduleMode.ZK_QUARTZ;

}
