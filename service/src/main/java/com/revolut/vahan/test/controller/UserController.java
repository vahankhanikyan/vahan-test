package com.revolut.vahan.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.vahan.test.data.UserRepository;
import com.revolut.vahan.test.exception.ResourceNotFoundException;
import com.revolut.vahan.test.model.User;

import static spark.Spark.get;
import static spark.Spark.post;

public class UserController implements SparkController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initSparkActions() {
        final ObjectMapper objectMapper = new ObjectMapper();

        get("/user/:id", (request, response) -> {
            response.type("application/json");
            User user = getUser(Integer.parseInt(request.params(":id")));
            return objectMapper.writeValueAsString(user);
        });

        get("/user/byname/:name", (request, response) -> {
            response.type("application/json");
            User user = getUser(request.params(":name"));
            return objectMapper.writeValueAsString(user);
        });


        post("/user", (request, response) -> {
            response.type("application/json");
            User user = objectMapper.readValue(request.body(), User.class);
            user = addUser(user);
            return objectMapper.writeValueAsString(user);
        });
    }

    public User getUser(int id) throws ResourceNotFoundException {
        return userRepository.findById(id);
    }

    public User getUser(String name) throws ResourceNotFoundException {
        return userRepository.findByName(name);
    }

    public User addUser(User user) {
        return userRepository.add(user);
    }
}
