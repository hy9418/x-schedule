package com.wolken.schedule.registry.zk;

import com.wolken.schedule.TaskContext;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author HuYu
 * @version $Id$
 * @since 2019/6/30 18:27
 */
public class LeaderTaskCaller implements LeaderLatchListener {

    private static final Logger logger = LoggerFactory.getLogger(LeaderTaskCaller.class);
    private ZookeeperCoordinator coordinator;
    private LeaderLatch leaderLatch;
    private String name;
    private TaskContext taskContext;

    public LeaderTaskCaller(ZookeeperCoordinator coordinator, TaskContext taskContext) {
        this.coordinator = coordinator;
        this.name = taskContext.instanceName();
        this.taskContext = taskContext;
        this.leaderLatch = new LeaderLatch(coordinator.getClient(), taskContext.coordinatorPath());
        leaderLatch.addListener(this);
    }

    @Override
    public void isLeader() {
        if (!taskContext.isStarted()) {
            taskContext.start();
        } else {
            taskContext.resume();
        }
    }

    public void startLatch() {
        try {
            leaderLatch.start();
        } catch (Exception e) {
            throw new ZookeeperRegistryException("Error occurred when start leader latch!", e);
        }
    }

    @Override
    public void notLeader() {
        logger.warn("Latch#{} is not the leader now", name);
        taskContext.pause();
    }
}
