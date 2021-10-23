package com.innerfriends.userprofilepicture.infrastructure;

import org.junit.jupiter.api.ClassDescriptor;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrdererContext;

import java.util.Comparator;

// https://www.wimdeblauwe.com/blog/2021/02/12/junit-5-test-class-orderer-for-spring-boot/

/**
 * Run test following this priority:
 * Run infra first
 * Run Managed next
 * End by running E2ETest
 */
public class Junit5TestClassOrder implements ClassOrderer {

    @Override
    public void orderClasses(final ClassOrdererContext classOrdererContext) {
        classOrdererContext.getClassDescriptors().sort(Comparator.comparingInt(Junit5TestClassOrder::getOrder));
    }

    private static int getOrder(final ClassDescriptor classDescriptor) {
        if (classDescriptor.getTestClass().equals(E2ETest.class)) {
            return 3;
        } else if (classDescriptor.getTestClass().getPackageName().contains("usecase")) {
            return 2;
        } else {
            return 1;
        }
    }

}
