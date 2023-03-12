package com.leader.leaderelection.service;

import com.ecwid.consul.v1.kv.KeyValueConsulClient;
import com.ecwid.consul.v1.session.SessionConsulClient;
import com.leader.leaderelection.config.ClusterProperties;
import net.kinguin.leadership.consul.config.ClusterConfiguration;
import net.kinguin.leadership.consul.factory.SimpleConsulClusterFactory;
import net.kinguin.leadership.core.Member;
import net.kinguin.leadership.core.factory.AbstractClusterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.consul.ConsulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {ClusterProperties.class, ConsulProperties.class})
public class LeaderElection {
    public static final Logger LOGGER = LoggerFactory.getLogger(LeaderElection.class);

    private final ClusterProperties clusterProperties;

    private final ConsulProperties consulProperties;

    public LeaderElection(ClusterProperties clusterProperties, ConsulProperties consulProperties) {
        this.clusterProperties = clusterProperties;
        this.consulProperties = consulProperties;
    }

    @Bean
    public SessionConsulClient sessionConsulClient() {
        LOGGER.info("host {}, port {} !", consulProperties.getHost(), consulProperties.getPort());
        return new SessionConsulClient(consulProperties.getHost(), consulProperties.getPort());
    }

    @Bean
    public KeyValueConsulClient keyValueConsulClient() {
        return new KeyValueConsulClient(consulProperties.getHost(), consulProperties.getPort());
    }

    @Bean
    public Member multiInstance(SessionConsulClient sessionConsulClient, KeyValueConsulClient keyValueConsulClient) {
        Member member = new SimpleConsulClusterFactory(sessionConsulClient, keyValueConsulClient)
                .mode(SimpleConsulClusterFactory.MODE_MULTI)
                .debug(true)
                .configure(clusterProperties.getLeader())
                .build();
        member.asObservable().subscribe(x -> {
            LOGGER.info("Inform Log Election : {}", x);
            if (member.isLeader()) {
                LOGGER.info("Leader is {}", x);
            }
        });

        return member;
    }


}
