package com.wolken.schedule.registry.zk;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/7/2 16:07
 */
public class ZookeeperRegistryException extends RuntimeException {

    public ZookeeperRegistryException(String msg) {
        super(msg);
    }

    public ZookeeperRegistryException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
