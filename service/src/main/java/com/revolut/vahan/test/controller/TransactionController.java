package com.revolut.vahan.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.vahan.test.data.TransactionRepository;
import com.revolut.vahan.test.exception.InsufficientAmountException;
import com.revolut.vahan.test.exception.ResourceNotFoundException;
import com.revolut.vahan.test.model.Transaction;

import static spark.Spark.get;

public class TransactionController implements SparkController {
    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void initSparkActions() {
        final ObjectMapper objectMapper = new ObjectMapper();

        get("/transfer/:senderId/:receiverId/:amount", (request, response) -> {
            response.type("application/json");
            Transaction transaction = transfer(Integer.parseInt(request.params(":senderId"))
                    , Integer.parseInt(request.params(":receiverId"))
                    , Double.parseDouble(request.params(":amount")));
            return objectMapper.writeValueAsString(transaction);
        });
    }

    public Transaction transfer(int senderId, int receiverId, double amount) throws InsufficientAmountException, ResourceNotFoundException {
        return transactionRepository.transfer(senderId, receiverId, amount);
    }

}
