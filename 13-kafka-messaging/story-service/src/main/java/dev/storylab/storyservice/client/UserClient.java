package dev.storylab.storyservice.client;

import dev.storylab.storyservice.client.dto.AuthorSummary;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserClient {
    private final RestClient restClient;

    public UserClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://user-service")
                .build();
    }

    public AuthorSummary getUserByAccountId(Long accountId) {
        return restClient
                .get()
                .uri("/api/users/accounts/{accountId}", accountId)
                .retrieve()
                .body(AuthorSummary.class);
    }
}
