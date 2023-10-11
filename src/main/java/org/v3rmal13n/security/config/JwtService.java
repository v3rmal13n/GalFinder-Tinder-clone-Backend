package org.v3rmal13n.security.config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// (прочти в канале security про jwt, если забыл)

@Service
public class JwtService {
    // ключ подписи, по которому будет проверка jwt, который мы передадим в метод .getSigningKey()
    private static final String SECRET_KEY = "fd4c77e961ef5aa83a3706fa3655f3a5d2996dee69fa77711f95ddd2b52bbf29\n";


    // TODO : ИЗВЛЕЧЕНИЕ


    // Будет извлекать почту из токена
    public String extractUsername(String token) {
        // Этот метод извлекает определенный claim из токена
        return extractClaim(token, Claims::getSubject); // метод извлечения одного утверждения, который мы реализовали ниже, принимает в параметры не только токен, но еще и "ЛЮБУЮ" функицю, которая вернет "ЛЮБОЙ ОБЪЕКТ ЧЕГО-ТО"
    }

    // Claim - утверждение, которое храниться в payload(полезной нагрузке). утверждений может быть много. Например: почта пользователя, дата создания токена, что угодно
    // Todo: этот метод служит для того, чтобы мы могли извлекать все утверждения из токена в виде объекта Claims
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder() // Используется Jwts.parserBuilder() для создания парсера токена.
                .setSigningKey(getSigningKey()) // ключ подписи токена, служит для закодирования, декодирования.
                .build() // Далее вызывается build() для создания объекта JwtParser.
                .parseClaimsJws(token) // parseClaimsJws() выполняет проверку подписи токена и структуры его заголовка и тела.
                .getBody(); // Наконец, вызывается getBody() для получения содержимого токена (claims) в виде объекта типа Claims.
                // Возвращается объект Claims, содержащий все claims из токена.
    }

    // Параметр T в угловых скобках называется универсальным параметром, так как вместо него можно подставить любой тип.
    // `<T>` - это обобщенный параметр, который обозначает, что метод может возвращать объекты любого типа.
    public <T> T extractClaim(String token, // токен, который будет использоваться для извлечения утверждений (claims).
                               Function<Claims, T> claimsResolver) {
        // Второй параметр "claimsResolver" представляет собой функцию, которая будет принимать утверждения (claims) и возвращать результат, тип которого определен как <T>.

        final Claims claims = extractAllClaims(token);
        // Затем вызывается переданная функция claimsResolver, которая принимает в качестве параметра объект Claims и возвращает результат типа <T>.
        return claimsResolver.apply(claims); // Наконец, метод возвращает результат вызова функции claimsResolver, что позволяет извлечь и использовать необходимую информацию из утверждений (claims) токена.
    }

    // При генерации JWT-токена веб-приложение ставит подпись секретным ключом, который хранится только в веб-приложении Веб-браузер сохраняет JWT-токен и отправляет его вместе с каждым запросом в веб-приложение Веб-приложение проверяет JWT-токен и если он верный, то выполняет действие от имени авторизованного пользователя
    // В данном случае, ключ подписи JWT необходим для проверки подлинности и целостности токена. Ключ используется для подписывания токена при создании и для верификации при его проверке. При проверке токена, используя ключ подписи, можно убедиться, что токен не был изменен после создания и что он был создан с использованием правильного секретного ключа.
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes); //  Этот метод используется для создания ключа HMAC SHA для указанного массива байтов.
    }


    //TODO : GENERATE JWT

    public String generateTokenWithoutExtraClaims(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // это метод генерации токена с дополнительными утверждениями. Выше будет метод, который генерирует токен без доп. утверждений
    // extraClaims(дополнительные утверждения) например: фамилия, номер телефона или что-то другое
    public String generateToken(Map<String, Object> extraClaims, // `extraClaims` (дополнительные данные, которые будут добавлены в токен), `userDetails` (данные пользователя для которого создается токен) и `expiration` (срок действия токена в миллисекундах).
                             UserDetails userDetails) {
        return Jwts
                .builder() // `return Jwts.builder()`: создает новую экземпляр класса Jwts (часть библиотеки JSON Web Token), который используется для создания, проверки и обработки токенов.
                .setClaims(extraClaims) // `setClaims(extraClaims)`: устанавливает дополнительные данные или "тело" токена. В данном случае переданные в метод `extraClaims` значения будут добавлены в поле "клейма" токена.
                // в нашем случае username - это адрес эл.почты, но т.к. мы реализовали интерфейс UserDetails у себя в сущности, и реализовали ее методы, то один из ее методов называется .getUsername()
                .setSubject(userDetails.getUsername()) // `setSubject(userDetails.getUsername())`: устанавливает поле "субъекта" токена, которое будет содержать имя пользователя (`getUsername()` возвращает имя пользователя из переданных `userDetails`).
                .setIssuedAt(new Date(System.currentTimeMillis())) //  `setIssuedAt(new Date(System.currentTimeMillis()))`: устанавливает поле "выпуска" токена, которое указывает время и дату, когда токен был создан.
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // `setExpiration(new Date(System.currentTimeMillis() + expiration))`: устанавливает срок действия токена, указывая дату и время, когда токен истекает. В данном случае, указывается текущая дата и время с добавлением значения `expiration`, которое задано в параметрах метода.
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // `signWith(getSigningKey(), SignatureAlgorithm.HS256)`: подписывает токен, используя заданный ключ и алгоритм подписи. В данном случае используется ключ, возвращаемый методом `getSigningKey()` и алгоритм подписи `SignatureAlgorithm.HS256` (Хеш-функция SHA-256).
                .compact(); // `compact()`: сжимает и возвращает токен в формате строки.
    }


    // TODO : TOKEN VALIDATION

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Проверяем, совпадают ли имя пользователя из токена и учетные данные пользователя, и токен не истеки
        return  (username.equals(userDetails.getUsername()))
                && !isTokenExpired(token);
    }

    // срок действия токена истек
    public boolean isTokenExpired(String token) {
        // Извлекаем срок истечения из токена и проверяем, является ли он меньше текущей даты и времени.
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        // Извлекаем срок истечения из токена
        return extractClaim(token, Claims::getExpiration);
    }

}
