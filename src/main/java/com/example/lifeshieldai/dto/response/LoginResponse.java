package com.example.lifeshieldai.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginResponse {
    Long id;
    String name;
    String email;
    String role;
    String message;
}
