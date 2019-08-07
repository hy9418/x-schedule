package com.wolken.schedule;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/7/1 13:33
 */
public final class NoOp implements ScheduleContextCustomizedOption {

    @Override
    public void customize(TaskContext context) {
        //do nothing
    }
}
