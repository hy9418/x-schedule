package com.wolken.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.classreading.AnnotationMetadataReadingVisitor;

/**
 * 总闸
 *
 * @author HuYu
 * @version $Id$
 * @since 2019/7/3 11:05
 */
public class MainCondition implements Condition {

    private static final String VALVE = "xschedule.enable";
    private static final Logger logger = LoggerFactory.getLogger(MainCondition.class);

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Boolean property = context.getEnvironment().getProperty(VALVE, boolean.class, true);
        AnnotationMetadataReadingVisitor am = (AnnotationMetadataReadingVisitor) metadata;
        logger.info("X-Schedule configuration [{}] condition on enable: {}", am.getClassName(),
                property);
        return property;
    }
}
