package org.v3rmal13n.security.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    // это отправиться пользователю, который успешно аутентифицировался (чекни в классе AuthenticationService)
    private String token;
}
