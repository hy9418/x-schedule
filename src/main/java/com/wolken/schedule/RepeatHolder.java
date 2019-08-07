package com.wolken.schedule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/5/10 11:26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
public @interface RepeatHolder {

    XSchedule[] value();

}
