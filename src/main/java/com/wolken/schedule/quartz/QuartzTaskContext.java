package com.wolken.schedule.quartz;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;
import com.wolken.schedule.XSchedule;
import com.wolken.schedule.ScheduleContextCustomizedOption;
import com.wolken.schedule.TaskContext;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/5/10 15:47
 */
public class QuartzTaskContext implements TaskContext {

    public static final String QUARTZ_TASK_CONTEXT_KEY = "QuartzTaskContext";
    private static final String ZNODE_PATH = "/";
    private final TaskMetadata taskMetadata;
    private volatile boolean init;
    private JobDetail jobDetail;
    private Scheduler scheduler;
    private int asyncTaskCount;
    private AutowireCapableBeanFactory beanFactory;

    public QuartzTaskContext(TaskMetadata taskMetadata) {
        assert taskMetadata != null;
        this.taskMetadata = taskMetadata;
    }

    public void init() {
        synchronized (this.taskMetadata) {
            try {
                Set<CronTrigger> cronTriggers = newTriggersAndAsyncTaskCount();
                StdSchedulerFactory factory = new StdSchedulerFactory();
                factory.initialize(QuartzProperties.forSettings(this));
                scheduler = factory.getScheduler();
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put(QUARTZ_TASK_CONTEXT_KEY, this);
                jobDetail = JobBuilder.newJob(ConcreteJobCaller.class).setJobData(jobDataMap)
                        .withIdentity(instanceName()).build();

                if (!scheduler.checkExists(jobDetail.getKey())) {
                    scheduler.scheduleJob(jobDetail, cronTriggers, false);
                }
            } catch (SchedulerException e) {
                throw new XScheduleException(
                        "Load task [" + instanceName() + "] to scheduler " + "error!", e);
            }
            contextCustomize();
            init = true;
            this.taskMetadata.notifyAll();
        }
    }

    private Set<CronTrigger> newTriggersAndAsyncTaskCount() {
        Set<CronTrigger> triggers = new LinkedHashSet<>();
        int total = 0;
        for (XSchedule annotation : taskMetadata.annotations) {
            total += annotation.asyncTaskCount();
            CronTrigger trigger =
                    TriggerBuilder.newTrigger().withIdentity(instanceName() + "_trigger").startNow()
                            .withSchedule(CronScheduleBuilder.cronSchedule(annotation.cron()))
                            .build();
            triggers.add(trigger);
        }
        this.asyncTaskCount = total;
        return triggers;
    }

    private void contextCustomize() {
        for (XSchedule annotation : taskMetadata.annotations) {
            Class<? extends ScheduleContextCustomizedOption>[] options = annotation.options();
            if (options != null) {
                for (Class<? extends ScheduleContextCustomizedOption> option : options) {
                    invokeCustomize(option);
                }
            }
        }
    }

    private void invokeCustomize(Class<? extends ScheduleContextCustomizedOption> option) {
        ScheduleContextCustomizedOption ob = null;
        try {
            Constructor<? extends ScheduleContextCustomizedOption> constructor =
                    option.getDeclaredConstructor();
            ob = BeanUtils.instantiateClass(constructor);
            ob.customize(this);
        } catch (NoSuchMethodException e) {
            throw new QuartzContextCustomizeException("ContextOption:" + ob.getClass().getName()
                    + " constructor with arg TaskContext " + "not found!", e);
        } catch (Exception e) {
            throw new QuartzContextCustomizeException(
                    "Error occurred when invoke customize. Option" + ob.getClass().getName(), e);
        }
    }

    @Override
    public String instanceName() {
        return taskMetadata.taskInstanceName();
    }

    @Override
    public TargetMethodRetrospect retrospect() {
        TargetMethodRetrospect proxy = (TargetMethodRetrospect) JobCallerCGlibProxy
                .createProxy(taskMetadata.targetType, taskMetadata.method);
        if (beanFactory != null) {
            beanFactory.autowireBean(proxy);
        }
        return proxy;
    }

    @Override
    public void start() {
        try {
            synchronized (this.taskMetadata) {
                while (!init) {
                    this.taskMetadata.wait(500);
                }
            }
        } catch (InterruptedException e) {
            //ignore
        }
        try {
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
        } catch (SchedulerException e) {
            throw new XScheduleException("Error occurred when startLatch scheduler!", e);
        }
    }

    @Override
    public boolean isStarted() {
        try {
            return scheduler.isStarted();
        } catch (SchedulerException e) {
            throw new XScheduleException("Error occurred when check status!", e);
        }
    }

    @Override
    public void pause() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.pauseJob(jobDetail.getKey());
            }
        } catch (SchedulerException e) {
            throw new XScheduleException("Error occurred when pause job!", e);
        }
    }

    @Override
    public void resume() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.resumeJob(jobDetail.getKey());
            }
        } catch (SchedulerException e) {
            throw new XScheduleException("Error occurred when resume job", e);
        }
    }

    @Override
    public String coordinatorPath() {
        return taskMetadata.path();
    }

    public int getAsyncTaskCount() {
        return asyncTaskCount;
    }

    public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public static class TaskMetadata {

        private static final String TYPE_METHOD_SPLIT = "#";
        private Class<?> targetType;
        private Object target;
        private Method method;
        private XSchedule[] annotations;
        private int index;

        public TaskMetadata(Object target, Method method, XSchedule... annotations) {
            this(target.getClass(), target, method, annotations);
        }

        public TaskMetadata(Class<?> targetType, Object target, Method method,
                XSchedule... annotations) {
            this.targetType = targetType;
            this.target = target;
            this.method = method;
            this.annotations = annotations;
            this.index = annotations.length;
        }

        String taskInstanceName() {
            if (index > 0) {
                return targetType.getName() + TYPE_METHOD_SPLIT + method.getName() + "-" + index;
            }
            return targetType.getName() + TYPE_METHOD_SPLIT + method.getName();
        }

        String path() {
            return ZNODE_PATH + targetType.getName().replaceAll("\\.", ZNODE_PATH) + method
                    .getName();
        }

        public Class<?> getTargetType() {
            return targetType;
        }

        public Method getMethod() {
            return method;
        }

        public XSchedule[] getAnnotations() {
            return annotations;
        }

        public int getIndex() {
            return index;
        }

        public Object getTarget() {
            return target;
        }
    }

}
