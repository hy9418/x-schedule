package com.wolken.schedule.quartz;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ConcurrentReferenceHashMap;


import static com.wolken.schedule.quartz.QuartzTaskContext.QUARTZ_TASK_CONTEXT_KEY;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/7/1 09:57
 */
public class ConcreteJobCaller implements Job {

    private static final Logger logger = LoggerFactory.getLogger(ConcreteJobCaller.class);
    private final AtomicBoolean calling = new AtomicBoolean(false);
    private final Object lock = new Object();
    private final Map<QuartzTaskContext, TargetMethodRetrospect> retrospectCache =
            new ConcurrentReferenceHashMap<>();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        if (jobDataMap == null) {
            throw new XScheduleException("Can not found QuartzTaskContext!");
        }
        QuartzTaskContext quartzTaskContext =
                (QuartzTaskContext) jobDataMap.get(QUARTZ_TASK_CONTEXT_KEY);
        synchronized (lock) {
            blockAndWait(quartzTaskContext);
            if (calling.compareAndSet(false, true)) {
                if (retrospectCache.get(quartzTaskContext) == null) {
                    retrospectCache.put(quartzTaskContext, quartzTaskContext.retrospect());
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("JobCaller {} start retrospect.",
                            quartzTaskContext.instanceName());
                }
                try {
                    retrospectCache.get(quartzTaskContext).call();
                } catch (Exception e) {
                    throw new XScheduleException(
                            "Error occurred on retrospecting " + quartzTaskContext.instanceName(),
                            e);
                }
            }
            calling.set(false);
            lock.notifyAll();
        }
    }

    private void blockAndWait(QuartzTaskContext quartzTaskContext) {
        if (calling.get()) {
            //已验证，通过scheduler asyncThreadCount 控制阻塞场景。故可取消该方法。
            try {
                logger.warn("The task [{}] process is still running",
                        quartzTaskContext.instanceName());
                lock.wait();
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }

}
