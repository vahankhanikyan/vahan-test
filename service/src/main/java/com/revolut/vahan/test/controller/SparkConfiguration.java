package com.revolut.vahan.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.vahan.test.BeanFactory;
import com.revolut.vahan.test.Properties;
import com.revolut.vahan.test.exception.InsufficientAmountException;
import com.revolut.vahan.test.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class SparkConfiguration implements SparkController {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    @Override
    public void initSparkActions() {
        final ObjectMapper objectMapper = new ObjectMapper();

        port(Properties.WEB_PORT);
        threadPool(Properties.MAX_THREADS);

        initExceptionHandler((e) -> logger.error("initExceptionHandler", e));

        notFound((req, res) -> {
            res.type("application/json");
            res.status(404);
            return "{\"message\":\"404 error\"}";
        });

        internalServerError((req, res) -> {
            logger.error("Internal Server Error handler. Request URL: {}, Request body {}"
                    , req.url(), req.body());
            res.type("application/json");
            res.status(500);
            return "{\"message\":\"500 error. Internal server error\"}";
        });

        exception(InsufficientAmountException.class, (exception, req, res) -> {
            res.type("application/json");
            res.status(403);
            try {
                res.body(objectMapper.writeValueAsString(exception.getMessage()));
            } catch (JsonProcessingException e) {
                logger.error("Can't parse exception to json", e);
            }
        });

        exception(ResourceNotFoundException.class, (exception, req, res) -> {
            res.type("application/json");
            res.status(404);
            try {
                res.body(objectMapper.writeValueAsString(exception.getMessage()));
            } catch (JsonProcessingException e) {
                logger.error("Can't parse exception to json", e);
            }
        });
    }
}
