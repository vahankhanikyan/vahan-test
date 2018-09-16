package com.revolut.vahan.test;

import com.revolut.vahan.test.controller.SparkConfiguration;
import com.revolut.vahan.test.controller.TransactionController;
import com.revolut.vahan.test.controller.UserController;

public class Start {
    public static void main(String[] args) {
        initControllers();
    }

    private static void initControllers() {
        BeanFactory.getInstance(SparkConfiguration.class).initSparkActions();
        BeanFactory.getInstance(UserController.class).initSparkActions();
        BeanFactory.getInstance(TransactionController.class).initSparkActions();
    }
}
