package com.innerfriends.userprofilepicture.infrastructure.resources;

import io.quarkus.test.common.QuarkusTestResource;

@QuarkusTestResource(ArangoDBTestResourceLifecycleManager.class)
@QuarkusTestResource(ZenkoTestResourceLifecycleManager.class)
@QuarkusTestResource(HazelcastTestResourceLifecycleManager.class)
public class QuarkusTestResources {
}
