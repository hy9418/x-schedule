package com.wolken.schedule.quartz;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/7/1 11:11
 */
public class QuartzContextCustomizeException extends RuntimeException {

    public QuartzContextCustomizeException(String msg) {
        super(msg);
    }

    public QuartzContextCustomizeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public QuartzContextCustomizeException(Throwable cause) {
        super(cause);
    }

}
