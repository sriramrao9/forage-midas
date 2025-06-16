package com.jpmc.midascore;

import com.jpmc.midascore.external.Balance;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TaskFiveTests {

    private static final String BASE_URL = "http://localhost:33400/balance";

    @Test
    public void task_five_verifier() {
        System.out.println(">>>> BEGIN TaskFiveTests");

        RestTemplate restTemplate = new RestTemplate();

        // Known user
        String userId1 = "user123";
        ResponseEntity<Balance> response1 = restTemplate.exchange(
                BASE_URL + "?userId=" + userId1,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                Balance.class
        );
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        Balance balance1 = response1.getBody();
        assertNotNull(balance1);
        System.out.println(balance1);

        // Unknown user
        String unknownUser = "unknown123";
        ResponseEntity<Balance> response2 = restTemplate.exchange(
                BASE_URL + "?userId=" + unknownUser,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                Balance.class
        );
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        Balance balance2 = response2.getBody();
        assertNotNull(balance2);
        assertEquals(0.0, balance2.getBalance());
        System.out.println(balance2);

        System.out.println("<<<< END TaskFiveTests");
    }
}
