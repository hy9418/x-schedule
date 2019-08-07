package com.wolken.schedule.quartz;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/7/1 11:51
 */
public interface TargetMethodRetrospect {

    String TRIGGER_METHOD = "call";

    void call();

}
