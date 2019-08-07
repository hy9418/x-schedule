package com.wolken.schedule.quartz;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.wolken.schedule.XSchedule;
import com.wolken.schedule.registry.zk.LeaderTaskCaller;
import com.wolken.schedule.registry.zk.ZookeeperCoordinator;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodIntrospector.MetadataLookup;
import org.springframework.core.annotation.AnnotatedElementUtils;


import static com.wolken.schedule.registry.zk.ZkRegistryCenterBeanPostProcessor.ZK_COORDINATOR_BEAN_NAME;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/5/8 10:52
 */
public class QuartzTaskBeanPostProcessor
        implements BeanPostProcessor, BeanFactoryAware, ApplicationListener<ContextRefreshedEvent>,
        ApplicationContextAware {

    private final List<Class<?>> skip = new ArrayList<>(64);
    private QuartzTaskContext[] contexts = new QuartzTaskContext[] {};
    private BeanFactory beanFactory;
    private ApplicationContext context;

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        Class<?> proxyClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!skip.contains(proxyClass)) {
            Map<Method, Set<XSchedule>> annotatedMethods = MethodIntrospector
                    .selectMethods(proxyClass, (MetadataLookup<Set<XSchedule>>) method -> {
                        Set<XSchedule> schedules = AnnotatedElementUtils
                                .getMergedRepeatableAnnotations(method, XSchedule.class);
                        return (!schedules.isEmpty() ? schedules : null);
                    });
            if (annotatedMethods.isEmpty()) {
                skip.add(proxyClass);
            } else {
                buildContext(bean, proxyClass, annotatedMethods);
            }
        }
        return bean;
    }

    private void buildContext(Object bean, Class<?> proxyClass,
            Map<Method, Set<XSchedule>> annotatedMethods) {
        int candidate = annotatedMethods.keySet().size();
        int newLen = contexts.length + candidate;
        contexts = Arrays.copyOf(contexts, newLen);
        AutowireCapableBeanFactory autowireCapableBeanFactory =
                context.getAutowireCapableBeanFactory();
        for (Method method : annotatedMethods.keySet()) {
            Parameter[] parameters = method.getParameters();
            if (parameters.length > 0) {
                LoggerFactory.getLogger(proxyClass)
                        .error("Ignore schedule method:[{}] - Method only available without "
                                + "parameters", proxyClass.getName() + "#" + method.getName());
                continue;
            }
            Set<XSchedule> xSchedules = annotatedMethods.get(method);
            QuartzTaskContext quartzTaskContext =
                    TaskContextFactory.newTaskContext(bean, method, xSchedules);
            quartzTaskContext.setBeanFactory(autowireCapableBeanFactory);
            contexts[newLen - candidate--] = quartzTaskContext;
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == this.context) {
            launch();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        if (this.beanFactory == null) {
            this.beanFactory = applicationContext;
        }
    }

    private void launch() {
        if (contexts != null && contexts.length > 0) {
            ZookeeperCoordinator coordinator =
                    beanFactory.getBean(ZK_COORDINATOR_BEAN_NAME, ZookeeperCoordinator.class);
            for (QuartzTaskContext quartzTaskContext : contexts) {
                synchronized (quartzTaskContext) {
                    quartzTaskContext.init();
                    new LeaderTaskCaller(coordinator, quartzTaskContext).startLatch();
                }
            }
        }
    }
}
