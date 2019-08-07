package com.wolken.schedule;

import org.springframework.stereotype.Service;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/8/7 14:03
 */
@Service
public class CronTest {

    @XSchedule(cron = "0/1 * * * * ?")
    public void cron() {
        System.out.println("Hello");
    }
}
