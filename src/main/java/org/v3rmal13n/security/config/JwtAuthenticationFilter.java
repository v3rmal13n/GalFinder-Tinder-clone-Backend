package org.v3rmal13n.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Этот класс проверяет наличие токена в заголовке запроса. Мы хотим, чтобы он проверял один раз, за запрос и каждый запрос. Для этого нам нужно расширить класс, который переводиться, как "Один раз за запрос"
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // класс, с помощью которого будем работать с jwt(извлекать емэйл и т.д.)
    private final JwtService jwtService;

    // Это интерфейс, но нам нужно создать класс, который его реализует, чтобы извлекать пользователя из бд правильно
    // В ApplicationConfig мы определили @Bean-компонент, через который реализовали метод userDetailsService.getUsername()
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain // цепочка фильтров. Эти три параметра не должны быть нулевыми, поэтому нам поможет @NonNull
    ) throws ServletException, IOException {

        // Если путь содержит `/api/v1/auth`, то фильтр пропускает запрос и передает его дальше по цепочке фильтров
        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response); // используя `filterChain.doFilter(request, response)`. Это означает, что в данном случае фильтрация не выполняется и запрос обрабатывается нормальным образом.
            return;
        }

        // Эта строка получает значение заголовка "Authorization" из HTTP-запроса, которое обычно содержит токен аутентификации.
        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String userEmail;

        // В этом случае, фильтр пропускает запрос и передает его дальше по цепочке фильтров, используя `filterChain.doFilter(request, response)`. Таким образом, запрос обрабатывается нормальным путем и проходит через остальные фильтры и обработчики без какой-либо дополнительной обработки или проверки аутентификации. Однако, если условие в блоке `if` ложно, то это означает, что заголовок "Authorization" присутствует и соответствует ожидаемому формату. В этом случае, можно предположить, что в заголовке содержится токен аутентификации в формате "Bearer {token}" и дальнейшая обработка запроса будет выполняться для проверки и аутентификации пользователя на основе этого токена перед передачей запроса дальше по цепочке фильтров.
        if (authHeader == null||!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        //если в проверка свыше провалилась и есть заголовок Авторизации, то метод извлекает токен аутентификации из значения заголовка "Authorization".
        // .substring(7) - значит, что отсекает первые 7 символов заголовка "Authorization", что соответствует удалению префикса "Bearer ", т.е. останется только сам токен.
        token = authHeader.substring(7);

        // нужно извлечь почту пользователя(она уникальная) и передать в UserDetailsService, чтобы проверить, есть ли такой пользователь в базе
        userEmail = jwtService.extractUsername(token); // TODO : Нужен класс(JwtService), который может манипулировать jwt token, и извлекать оттуда данные. Мы создали метод .extractUsername(), который в параметры принимает jwt и извлекает из него почту

        // Метод SecurityContextHolder.getContext().getAuthentication() возвращает текущий объект аутентификации пользователя в контексте безопасности. Этот объект содержит информацию о пользователе, такую как его имя, роли, права доступа и т.д. Если пользователь не был аутентифицирован, то метод вернет null.
        if (userEmail != null && // Эта строка проверяет, что userEmail не равен null и что пользователь не аутентифицирован.
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // Эта строка загружает информацию о пользователе по его email-адресу.
            UserDetails userDetails =
                    this.userDetailsService.loadUserByUsername(userEmail);

            // если токен валидный для пользователя
            if (jwtService.isTokenValid(token, userDetails)) {
                // создаем новый объект аутентификации UsernamePasswordAuthenticationToken, который содержит информацию о пользователе и его правах. В конструктор передаем объект userDetails, который содержит информацию о пользователе, null в качестве аргумента для credentials (пароля), так как при JWT-аутентификации пароль не используется, и userDetails.getAuthorities(), чтобы передать информацию о правах пользователя.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // TODO : В данном коде в конструктор UsernamePasswordAuthenticationToken передаются аргументы userDetails, null и userDetails.getAuthorities(), потому что при JWT-аутентификации пароль не используется, и вместо него передается null. Вместо credentials передается null, так как это необходимо для создания объекта аутентификации, но на самом деле этот аргумент не используется.
                authToken.setDetails(
                        // устанавливаем дополнительные детали аутентификации, такие как IP-адрес и браузер пользователя.
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Созданный объект аутентификации устанавливается в контекст безопасности (SecurityContextHolder), чтобы приложение могло использовать эту информацию для авторизации пользователя в дальнейшем.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        //  после всех проверок и аутентификации пользователя, запрос передается дальше по цепочке фильтров. В данном случае, вызывается метод doFilter у объекта filterChain, который передает запрос следующему фильтру или обработчику в цепочке.
        filterChain.doFilter(request, response);
    }
}
