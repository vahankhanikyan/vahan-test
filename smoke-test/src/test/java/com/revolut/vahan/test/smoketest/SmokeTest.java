package com.revolut.vahan.test.smoketest;

import com.revolut.vahan.test.Properties;
import com.revolut.vahan.test.model.Transaction;
import com.revolut.vahan.test.model.User;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.http.HttpResponse;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.revolut.vahan.test.smoketest.util.RetrieveUtil.get;
import static com.revolut.vahan.test.smoketest.util.RetrieveUtil.postJson;
import static org.junit.Assert.assertEquals;

public class SmokeTest {

    /**
     * Can be garbed also from VM Options
     * host and port together
     * Now it is configured that Smoke test will be executed on the same machine with web service API
     */
    public static final String HOST = "http://localhost:" + Properties.WEB_PORT;

    private static List<User> createUsers(int count) throws IOException {
        List<User> users = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            User user = new User();
            user.setName(UUID.randomUUID().toString());
            user.setAmount(new RandomDataGenerator().nextUniform(100, 1000));
            User userRetrieved = postJson(HOST + "/user", user, User.class);
            users.add(userRetrieved);
        }
        return users;
    }

    @Test
    public void shouldTransferMoney() throws IOException {
        List<User> users = createUsers(2);
        double moneyToSend = 10.99;

        Transaction transaction = get(HOST
                        + String.format("/transfer/%s/%s/%.2f", users.get(0).getId(), users.get(1).getId(), moneyToSend)
                , Transaction.class);

        User user1After = get(HOST + "/user/" + users.get(0).getId(), User.class);
        User user2After = get(HOST + "/user/" + users.get(1).getId(), User.class);

        assertEquals(transaction.getAmount(), moneyToSend, 0.001);
        assertEquals(users.get(0).getAmount() - moneyToSend, user1After.getAmount(), 0.001);
        assertEquals(users.get(1).getAmount() + moneyToSend, user2After.getAmount(), 0.001);
    }

    @Test
    public void shouldGiveExceptionWithLessAmountOfMoney() throws IOException {
        List<User> users = createUsers(2);

        HttpResponse response = get(HOST
                + String.format("/transfer/%s/%s/%.2f", users.get(0).getId(), users.get(1).getId(), users.get(0).getAmount() + 5));
        assertEquals(403, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldFailCauseOfWrongSender() throws IOException {
        List<User> users = createUsers(1);
        HttpResponse response = get(HOST
                + String.format("/transfer/%s/%s/%.2f", users.get(0).getId() + 1, users.get(0).getId(), 0.01));
        assertEquals(404, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldFailCauseOfWrongReceiver() throws IOException {
        List<User> users = createUsers(1);

        HttpResponse response = get(HOST
                + String.format("/transfer/%s/%s/%.2f", users.get(0).getId(), users.get(0).getId() + 1, users.get(0).getAmount() - 5));
        assertEquals(404, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldTransferMoney_Multithreaded_Mode() throws IOException, InterruptedException {
        List<User> users = createUsers(2);
        double moneyToSend = 1;
        int transferCount = 100;

        Thread thread1 = new Thread(() -> {
            try {
                for (int i = 0; i < transferCount; i++) {
                    get(HOST + String.format("/transfer/%s/%s/%.2f", users.get(0).getId(), users.get(1).getId(), moneyToSend));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        Thread thread2 = new Thread(() -> {
            try {
                for (int i = 0; i < transferCount; i++) {
                    get(HOST + String.format("/transfer/%s/%s/%.2f", users.get(1).getId(), users.get(0).getId(), moneyToSend));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();


        User user1After = get(HOST + "/user/" + users.get(0).getId(), User.class);
        User user2After = get(HOST + "/user/" + users.get(1).getId(), User.class);

        assertEquals(users.get(0).getAmount(), user1After.getAmount(), 0.001);
        assertEquals(users.get(1).getAmount(), user2After.getAmount(), 0.001);
    }
}
