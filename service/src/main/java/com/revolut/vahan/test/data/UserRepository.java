package com.revolut.vahan.test.data;

import com.revolut.vahan.test.exception.ResourceNotFoundException;
import com.revolut.vahan.test.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Objects;

public class UserRepository {
    private final EntityManagerFactory entityManagerFactory;

    public UserRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public User findById(int id) throws ResourceNotFoundException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            User user = entityManager.find(User.class, id);
            if (Objects.isNull(user))
                throw new ResourceNotFoundException(String.format("User with Id: %d not found", id));
            return user;
        } finally {
            entityManager.close();
        }
    }

    public User findByName(String name) throws ResourceNotFoundException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            User user = (User) entityManager.createQuery("select u from User u where u.name = :name")
                    .setParameter("name", name).getSingleResult();
            if (Objects.isNull(user))
                throw new ResourceNotFoundException(String.format("User with name: %s not found", name));
            return user;
        } finally {
            entityManager.close();
        }
    }

    public User add(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            user.setId(0);
            entityManager.getTransaction().begin();
            entityManager.persist(user);
            entityManager.getTransaction().commit();
            return user;
        } finally {
            entityManager.close();
        }
    }
}
