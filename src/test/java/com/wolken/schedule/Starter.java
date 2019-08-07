package com.wolken.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/8/7 14:01
 */
@SpringBootApplication
@EnableXScheduling
public class Starter {

    public static void main(String[] args) {
        SpringApplication.run(Starter.class);
    }

}
