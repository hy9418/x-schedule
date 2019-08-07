package com.wolken.schedule.quartz;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/7/1 17:44
 */
public class XScheduleException extends RuntimeException {

    public XScheduleException(String msg) {
        super(msg);
    }

    public XScheduleException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public XScheduleException(Throwable cause) {
        super(cause);
    }

}
