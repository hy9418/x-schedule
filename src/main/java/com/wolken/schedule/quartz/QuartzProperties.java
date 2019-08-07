package com.wolken.schedule.quartz;

import java.util.Properties;
import org.quartz.simpl.SimpleThreadPool;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/7/1 15:13
 */
public class QuartzProperties {

    public static Properties forSettings(QuartzTaskContext quartzTaskContext) {
        Properties properties = new Properties();
        properties.setProperty("org.quartz.threadPool.class", SimpleThreadPool.class.getName());
        properties.setProperty("org.quartz.threadPool.threadCount",
                String.valueOf(quartzTaskContext.getAsyncTaskCount()));
        properties
                .setProperty("org.quartz.scheduler.instanceName", quartzTaskContext.instanceName());
        return properties;
    }
}
