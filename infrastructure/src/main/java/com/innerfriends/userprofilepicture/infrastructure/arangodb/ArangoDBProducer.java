package com.innerfriends.userprofilepicture.infrastructure.arangodb;

import com.arangodb.ArangoDB;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class ArangoDBProducer {

    @ApplicationScoped
    @Produces
    public ArangoDB arangoDBProducer(@ConfigProperty(name = "arangodb.host") final String host,
                                     @ConfigProperty(name = "arangodb.port") final Integer port,
                                     @ConfigProperty(name = "arangodb.user") final String user,
                                     @ConfigProperty(name = "arangodb.password") final String password) {
        return new ArangoDB.Builder()
                .host(host, port)
                .user(user)
                .password(password)
                .build();
    }

}
