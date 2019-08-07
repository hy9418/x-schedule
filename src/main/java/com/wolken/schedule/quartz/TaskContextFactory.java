package com.wolken.schedule.quartz;

import java.lang.reflect.Method;
import java.util.Set;
import com.wolken.schedule.XSchedule;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/5/10 20:45
 */
public class TaskContextFactory {

    public static QuartzTaskContext newTaskContext(Object target, Method method,
            XSchedule... annotation) {
        QuartzTaskContext.TaskMetadata taskMetadata =
                new QuartzTaskContext.TaskMetadata(target, method, annotation);
        return new QuartzTaskContext(taskMetadata);
    }

    public static QuartzTaskContext newTaskContext(Object target, Method method,
            Set<XSchedule> annotations) {
        if (annotations.size() == 1) {
            return TaskContextFactory.newTaskContext(target, method, annotations.iterator().next());
        } else {
            return TaskContextFactory.newTaskContext(target, method,
                    annotations.toArray(new XSchedule[annotations.size()]));
        }
    }

}
