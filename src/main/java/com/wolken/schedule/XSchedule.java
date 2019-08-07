package com.wolken.schedule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/2/22 09:30
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RepeatHolder.class)
public @interface XSchedule {

    String cron();

    String id() default "";// default <ClassName>#<Method>(-<Index>)

    /**
     * 控制调度任务的执行线程数；单线程下，若已触发下次任务时间，而本次任务还未执行完毕，将会导致下次任务的触发失效。
     * <br/>
     * 如：线程数为 1，任务执行时间为 2s，调度周期为 1s；则第2次调度触发时，第1次任务还在执行，第2次任务将会被丢弃。若线程数为 2，则第2次任务触发时将启动另一线程处理本次任务。
     *
     * @return
     */
    int asyncTaskCount() default 1;

    Class<? extends ScheduleContextCustomizedOption>[] options() default NoOp.class;
}