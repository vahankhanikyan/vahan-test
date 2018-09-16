package com.revolut.vahan.test.data;

import com.revolut.vahan.test.exception.InsufficientAmountException;
import com.revolut.vahan.test.exception.ResourceNotFoundException;
import com.revolut.vahan.test.model.Transaction;
import com.revolut.vahan.test.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Objects;

public class TransactionRepository {
    private final EntityManagerFactory entityManagerFactory;
    private final UserRepository userRepository;

    public TransactionRepository(EntityManagerFactory entityManagerFactory, UserRepository userRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.userRepository = userRepository;
    }

    /**
     * Transfer money from one account to another.
     * Implemented with pessimistic lock on Thread level
     * Better to have optimistic on DB level
     *
     * @param senderId
     * @param receiverId
     * @param amount
     * @return
     * @throws InsufficientAmountException
     * @throws ResourceNotFoundException
     */
    public synchronized Transaction transfer(int senderId, int receiverId, double amount) throws InsufficientAmountException, ResourceNotFoundException {
        User sender = userRepository.findById(senderId);
        if (Objects.isNull(sender))
            throw new ResourceNotFoundException(String.format("Sender with Id: %d not found", senderId));
        if (sender.getAmount() < amount) {
            throw new InsufficientAmountException(senderId, amount, sender.getAmount());
        }
        User receiver = userRepository.findById(receiverId);
        if (Objects.isNull(receiver))
            throw new ResourceNotFoundException(String.format("Receiver with Id: %d not found", receiverId));

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            sender.setAmount(sender.getAmount() - amount);
            receiver.setAmount(receiver.getAmount() + amount);
            Transaction transaction = new Transaction(senderId, receiverId, amount);
            transaction = entityManager.merge(transaction);
            entityManager.merge(sender);
            entityManager.merge(receiver);
            entityManager.getTransaction().commit();
            return transaction;
        } finally {
            entityManager.close();
        }
    }
}
