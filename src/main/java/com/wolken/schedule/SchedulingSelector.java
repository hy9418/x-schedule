package com.wolken.schedule;

import java.util.Map;
import com.wolken.schedule.quartz.QuartzContextConfiguration;
import com.wolken.schedule.registry.RegistryConfiguration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.scheduling.annotation.SchedulingConfiguration;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/5/8 09:41
 */
public class SchedulingSelector implements ImportSelector {

    private static final String SCHEDULING_MODE_ATTRIBUTE_NAME = "mode";

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attribute = importingClassMetadata
                .getAnnotationAttributes(EnableXScheduling.class.getName(), false);
        XScheduleMode XScheduleMode =
                (XScheduleMode) attribute.get(SCHEDULING_MODE_ATTRIBUTE_NAME);

        return doSelect(XScheduleMode);
    }

    private String[] doSelect(XScheduleMode XScheduleMode) {
        switch (XScheduleMode) {
        case SIMPLE:
            return new String[] { SchedulingConfiguration.class.getName() };
        case ZK_QUARTZ:
            return new String[] { RegistryConfiguration.class.getName(),
                    QuartzContextConfiguration.class.getName() };
        default:
            return null;
        }
    }
}
