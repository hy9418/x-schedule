package com.wolken.schedule.registry.zk;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/6/27 16:52
 */
public class ZookeeperProperties {

    private String serverList;
    private String namespace;
    private int sessionTimeoutMs = 3000;
    private int connectionTimeoutMs = 2000;
    private int baseSleepTimeMs = 1000;
    private int maxSleepTimeMs = 5000;
    private int maxRetries = 10;
    private String password;

    public ZookeeperProperties(String serverList, String namespace) {
        this.serverList = serverList;
        this.namespace = namespace;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getServerList() {
        return serverList;
    }

    public String getNamespace() {
        return namespace;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }

    public int getMaxSleepTimeMs() {
        return maxSleepTimeMs;
    }

    public void setMaxSleepTimeMs(int maxSleepTimeMs) {
        this.maxSleepTimeMs = maxSleepTimeMs;
    }
}
