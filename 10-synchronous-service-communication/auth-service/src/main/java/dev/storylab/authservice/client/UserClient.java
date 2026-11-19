package dev.storylab.authservice.client;

import dev.storylab.authservice.client.dto.CreateUserRequest;
import dev.storylab.authservice.client.dto.UserResponse;
import dev.storylab.authservice.exception.UserServiceUnavailableException;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class UserClient {
    private final RestClient restClient;

    public UserClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://user-service")
                .build();
    }

    public UserResponse createUser(CreateUserRequest request) {
        try {
            return restClient
                    .post()
                    .uri("/api/users")
                    .body(request)
                    .retrieve()
                    .body(UserResponse.class);
        } catch (ResourceAccessException | IllegalStateException ex) {
            throw new UserServiceUnavailableException(ex);
        }
    }
}
