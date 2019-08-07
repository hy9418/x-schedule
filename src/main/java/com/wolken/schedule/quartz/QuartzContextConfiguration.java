package com.wolken.schedule.quartz;

import com.wolken.schedule.MainCondition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Role;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/5/8 10:20
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Conditional(MainCondition.class)
public class QuartzContextConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public QuartzTaskBeanPostProcessor quartzTaskBeanPostProcessor() {
        return new QuartzTaskBeanPostProcessor();
    }

}
