package com.wolken.schedule.registry;

import com.wolken.schedule.MainCondition;
import com.wolken.schedule.registry.zk.ZkRegistryCenterBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.Environment;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/6/28 13:35
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Conditional(MainCondition.class)
public class RegistryConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ZkRegistryCenterBeanPostProcessor registryCenterBeanPostProcessor(
            Environment environment) {
        return new ZkRegistryCenterBeanPostProcessor(environment);
    }

}
