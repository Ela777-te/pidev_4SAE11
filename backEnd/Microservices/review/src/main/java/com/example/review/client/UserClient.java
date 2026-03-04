package com.example.review.client;

import com.example.review.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for the User microservice.
 * Used to fetch user email when sending review response notifications.
 */
@FeignClient(name = "user", url = "${user.service.url:http://localhost:8090}", path = "/api/users")
public interface UserClient {

    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable("id") Long id);
}
