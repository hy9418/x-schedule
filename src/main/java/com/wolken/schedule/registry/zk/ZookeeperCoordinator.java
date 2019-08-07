package com.wolken.schedule.registry.zk;

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.google.common.base.Charsets;
import com.wolken.schedule.registry.Coordinator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/6/27 16:51
 */
public class ZookeeperCoordinator implements Coordinator {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperCoordinator.class);
    private ZookeeperProperties zookeeperProperties;
    private CuratorFramework client;

    public ZookeeperCoordinator(ZookeeperProperties zookeeperProperties) {
        assert zookeeperProperties != null;
        this.zookeeperProperties = zookeeperProperties;
    }

    public void init() {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().retryPolicy(
                new ExponentialBackoffRetry(zookeeperProperties.getBaseSleepTimeMs(),
                        zookeeperProperties.getMaxRetries(),
                        zookeeperProperties.getMaxSleepTimeMs()))
                .connectString(zookeeperProperties.getServerList());
        if (null != zookeeperProperties.getNamespace()) {
            builder.namespace(zookeeperProperties.getNamespace());
        }
        if (0 < zookeeperProperties.getConnectionTimeoutMs()) {
            builder.connectionTimeoutMs(zookeeperProperties.getConnectionTimeoutMs());
        }
        if (0 < zookeeperProperties.getSessionTimeoutMs()) {
            builder.sessionTimeoutMs(zookeeperProperties.getSessionTimeoutMs());
        }
        if (null != zookeeperProperties.getPassword()) {
            builder.authorization("digest",
                    zookeeperProperties.getPassword().getBytes(Charsets.UTF_8))
                    .aclProvider(new ACLProvider() {
                        @Override
                        public List<ACL> getDefaultAcl() {
                            return ZooDefs.Ids.CREATOR_ALL_ACL;
                        }

                        @Override
                        public List<ACL> getAclForPath(String path) {
                            return ZooDefs.Ids.CREATOR_ALL_ACL;
                        }
                    });
        }
        logger.info("Establish zookeeper client:[Server={}, namespace={}, baseSleepTime={}, "
                        + "maxRetries={}, maxSleepTime={}, connectionTimeout={}, sessionTimeout={}, "
                        + "password={}]", zookeeperProperties.getServerList(),
                zookeeperProperties.getNamespace(), zookeeperProperties.getBaseSleepTimeMs(),
                zookeeperProperties.getMaxRetries(), zookeeperProperties.getMaxSleepTimeMs(),
                zookeeperProperties.getConnectionTimeoutMs(),
                zookeeperProperties.getSessionTimeoutMs(), zookeeperProperties.getPassword());
        client = builder.build();
        client.start();
        try {
            if (!client.blockUntilConnected(
                    zookeeperProperties.getMaxSleepTimeMs() * zookeeperProperties.getMaxRetries(),
                    TimeUnit.MILLISECONDS)) {
                client.close();
                throw new KeeperException.OperationTimeoutException();
            }
        } catch (final Exception ex) {
            logger.error("Zookeeper connection error occurred, do interrupt!", ex);
            Thread.currentThread().interrupt();
        }

    }

    public CuratorFramework getClient() {
        return client;
    }
}
