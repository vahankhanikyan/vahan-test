package com.revolut.vahan.test;

import com.revolut.vahan.test.controller.SparkConfiguration;
import com.revolut.vahan.test.controller.TransactionController;
import com.revolut.vahan.test.controller.UserController;
import com.revolut.vahan.test.data.TransactionRepository;
import com.revolut.vahan.test.data.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private static final Map<Class, Object> instances = new HashMap<>();
    private static final Set<Class> requestedClasses = new HashSet<>();

    /**
     * Analog of Spring dependency injection.
     * New classes must be added here manually if you want to request it by this method
     *
     * @param key Class type
     * @param <K>
     * @return instance of the requested class. Now only singleton implementation
     */
    public static <K> K getInstance(Class<K> key) {
        Object instance = instances.get(key);
        if (instance == null) {
            synchronized (BeanFactory.class) {
                if (instance == null) {
                    // request second time the same class here means cyclic dependencies
                    if (requestedClasses.contains(key)) {
                        logger.error("Cyclic dependency with class {}", key.getCanonicalName());
                        throw new RuntimeException("Cyclic dependency with class" + key.getCanonicalName());
                    }
                    requestedClasses.add(key);

                    if (EntityManagerFactory.class.equals(key)) {
                        instance = Persistence.createEntityManagerFactory("hibernate_h2");
                    } else if (UserRepository.class.equals(key))
                        instance = new UserRepository(getInstance(EntityManagerFactory.class));
                    else if (TransactionRepository.class.equals(key))
                        instance = new TransactionRepository(getInstance(EntityManagerFactory.class), getInstance(UserRepository.class));
                    else if (UserController.class.equals(key))
                        instance = new UserController(getInstance(UserRepository.class));
                    else if (TransactionController.class.equals(key))
                        instance = new TransactionController(getInstance(TransactionRepository.class));
                    else if (SparkConfiguration.class.equals(key))
                        instance = new SparkConfiguration();
                    else
                        throw new IllegalArgumentException(String.format("Class %s is not added in BeanFactory.", key.getCanonicalName()));
                    instances.put(key, instance);
                }
            }
        }
        return (K) instance;
    }
}
