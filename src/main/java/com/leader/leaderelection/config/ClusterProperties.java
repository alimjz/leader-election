package com.leader.leaderelection.config;

import net.kinguin.leadership.consul.config.ClusterConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cluster")
public class ClusterProperties {
    private ClusterConfiguration leader;

    public ClusterConfiguration getLeader() {
        return leader;
    }

    public void setLeader(ClusterConfiguration leader) {
        this.leader = leader;
    }
}
