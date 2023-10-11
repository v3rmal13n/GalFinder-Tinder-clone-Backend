package org.v3rmal13n.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.v3rmal13n.security.config.JwtService;
import org.v3rmal13n.security.repository.UserRepository;
import org.v3rmal13n.security.user.Role;
import org.v3rmal13n.security.user.User;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Возвращаем наш объект нашего специального класса с полем, хранящим токен
    public AuthenticationResponse register(RegisterRequest request) {

        var user = User.builder() // содзаем такого пользователя из отправленных данных с помощью объекта класса RegisterRequest
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        // есть два метода, проверь, если снова забыл: сгенерировать токен с доп.утверждениями(это нам не нужно), но метод называется просто generateToken(), а есть метод генерации токена без доп. утверждений - это тот, который мы используем
        var jwtToken = jwtService.generateTokenWithoutExtraClaims(user);
        return AuthenticationResponse.builder() // в классе AuthenticationResponse мы дали аннотацию lombok @Builder, поэтому можем создать и вернуть новый объект этого класса, положив в него токен, сгенерированный с помощью jwtService
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateTokenWithoutExtraClaims(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


}
