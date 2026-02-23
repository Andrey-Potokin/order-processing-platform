package com.example.auth.service;

import com.example.auth.dto.JwtResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.User;
import com.example.auth.entity.UserRole;
import com.example.auth.event.UserCreatedEvent;
import com.example.auth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.time.Instant;
import java.util.Set;

/**
 * Сервис аутентификации и регистрации пользователей.
 * <p>
 * Отвечает за:
 * <ul>
 *   <li>Регистрацию новых пользователей с валидацией уникальности email</li>
 *   <li>Генерацию JWT и refresh-токенов при успешной регистрации и входе</li>
 *   <li>Аутентификацию пользователей по email и паролю</li>
 *   <li>Отправку события {@code user.created} в Apache Kafka после успешной регистрации</li>
 * </ul>
 * </p>
 *
 * <p>Все операции выполняются асинхронно с использованием Project Reactor ({@link Mono}),
 * что обеспечивает неблокирующую обработку запросов.</p>
 *
 * <p>При регистрации:
 * <ol>
 *   <li>Проверяется, существует ли уже пользователь с таким email</li>
 *   <li>Если не существует — создается новый пользователь с хешированным паролем и ролью USER</li>
 *   <li>Сохраняется в базу данных</li>
 *   <li>Формируется и отправляется событие {@link UserCreatedEvent} в топик Kafka "user.created"</li>
 *   <li>Генерируются токены доступа и обновления</li>
 * </ol>
 * </p>
 *
 * <p>При входе:
 * <ol>
 *   <li>Находится пользователь по email</li>
 *   <li>Проверяется соответствие пароля с использованием {@link PasswordEncoder}</li>
 *   <li>При успехе — генерируются токены</li>
 * </ol>
 * </p>
 *
 * @see RegisterRequest
 * @see LoginRequest
 * @see JwtResponse
 * @see TokenService
 * @see UserCreatedEvent
 * @see KafkaSender
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    TokenService tokenService;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    KafkaSender<String, Object> kafkaSender;

    /**
     * Регистрирует нового пользователя.
     * <p>
     * Проверяет уникальность email, сохраняет пользователя с хешированным паролем,
     * отправляет событие о создании пользователя в Kafka и генерирует JWT-токены.
     * </p>
     *
     * @param request данные для регистрации (email, пароль)
     * @return {@link Mono} с объектом {@link JwtResponse}, содержащим access и refresh токены
     *         или ошибку, если пользователь с таким email уже существует
     * @throws RuntimeException если пользователь с указанным email уже зарегистрирован
     */
    public Mono<JwtResponse> register(RegisterRequest request) {
        return userRepository.findByEmail(request.email())
            .hasElement()
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new RuntimeException("Пользователь с таким email уже существует"));
                }
                User user = User.builder()
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .roles(Set.of(UserRole.USER))
                    .build();
                return userRepository.save(user);
            })
            .flatMap(savedUser -> {
                UserCreatedEvent event = UserCreatedEvent.newBuilder()
                    .setUserId(savedUser.getId())
                    .setUsername(savedUser.getUsername())
                    .setRole(com.example.auth.event.UserRole.USER)
                    .setTimestamp(Instant.now())
                    .build();

                SenderRecord<String, Object, Long> record = SenderRecord.create(
                    "user.created",
                    null,
                    null,
                    savedUser.getId().toString(),
                    event,
                    savedUser.getId()
                );

                return kafkaSender.send(Mono.just(record))
                    .doOnNext(result -> {
                        if (result.exception() != null) {
                            System.err.println("Ошибка при отправке в Kafka: " + result.exception().getMessage());
                        }
                    })
                    .then(Mono.just(savedUser));
            })
            .flatMap(tokenService::generateTokens);
    }

    /**
     * Аутентифицирует существующего пользователя по email и паролю.
     * <p>
     * Находит пользователя по email, проверяет правильность пароля,
     * и при успешной проверке генерирует пару токенов.
     * </p>
     *
     * @param request данные для входа (email, пароль)
     * @return {@link Mono} с объектом {@link JwtResponse}, содержащим access и refresh токены
     *         или ошибку, если пользователь не найден или пароль неверен
     * @throws RuntimeException если пользователь не найден или пароль не совпадает
     */
    public Mono<JwtResponse> login(LoginRequest request) {
        return userRepository.findByEmail(request.email()) // ← Исправлено: findByEmail
            .switchIfEmpty(Mono.error(new RuntimeException("Пользователь не найден")))
            .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
            .switchIfEmpty(Mono.error(new RuntimeException("Неверный пароль")))
            .flatMap(tokenService::generateTokens);
    }
}