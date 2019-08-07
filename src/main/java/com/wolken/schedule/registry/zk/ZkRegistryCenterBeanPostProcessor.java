package com.wolken.schedule.registry.zk;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.env.Environment;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/3/6 10:06
 */
public class ZkRegistryCenterBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {

    public static final String ZK_COORDINATOR_BEAN_NAME = "ZookeeperCoordinator";
    private Environment environment;

    public ZkRegistryCenterBeanPostProcessor(Environment environment) {
        this.environment = environment;
    }

    private <T> T getProperty(Class<T> type, String key) {
        return getProperty(type, key, null);
    }

    @SuppressWarnings("unchecked")
    private <T> T getProperty(final Class<T> type, final String key, T defaultValue) {
        T value = environment.getProperty(key, type, defaultValue);
        if (value == null) {
            return null;
        }
        return value;
    }

    private ZookeeperProperties zkProperties() {
        String zkServer = getProperty(String.class, "xschedule.zk.server", "localhost:2181");
        String zkNamespace = getProperty(String.class, "xschedule.zk.namespace");
        ZookeeperProperties properties = new ZookeeperProperties(zkServer, zkNamespace);
        Integer zkBaseSleepTime = getProperty(int.class, "xschedule.zk.sleep_time.base");
        if (zkBaseSleepTime != null) {
            properties.setBaseSleepTimeMs(zkBaseSleepTime);
        }
        Integer zkMaxSleepTime = getProperty(int.class, "xschedule.zk.sleep_time.max");
        if (zkMaxSleepTime != null) {
            properties.setMaxSleepTimeMs(zkMaxSleepTime);
        }
        Integer zkMaxRetries = getProperty(int.class, "xschedule.zk.max.retries");
        if (zkMaxRetries != null) {
            properties.setMaxRetries(zkMaxRetries);
        }
        Integer zkSessionTimeout = getProperty(int.class, "xschedule.zk.timeout.session");
        if (zkSessionTimeout != null) {
            properties.setSessionTimeoutMs(zkSessionTimeout);
        }
        Integer zkConnectionTimeout =
                getProperty(int.class, "xschedule.zk.timeout.connection");
        if (zkConnectionTimeout != null) {
            properties.setConnectionTimeoutMs(zkConnectionTimeout);
        }
        String zkPwd = getProperty(String.class, "xschedule.zk.password");
        if (zkPwd != null) {
            properties.setPassword(zkPwd);
        }
        return properties;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        BeanDefinitionBuilder builder =
                BeanDefinitionBuilder.rootBeanDefinition(ZookeeperCoordinator.class);
        builder.addConstructorArgValue(zkProperties());
        builder.setInitMethodName("init");
        registry.registerBeanDefinition(ZK_COORDINATOR_BEAN_NAME, builder.getBeanDefinition());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {

    }
}
