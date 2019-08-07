package com.wolken.schedule;

import com.wolken.schedule.quartz.TargetMethodRetrospect;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/6/5 14:32
 */
public interface TaskContext {

    String instanceName();

    TargetMethodRetrospect retrospect();

    void start();

    boolean isStarted();

    void pause();

    void resume();

    String coordinatorPath();
}
