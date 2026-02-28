package com.example.user.service;

import com.example.auth.event.UserCreatedEvent;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserEventHandler {

    KafkaReceiver<String, Object> kafkaReceiver;
    UserRepository userRepository;

    @PostConstruct
    public void consumeUserEvents() {
        kafkaReceiver.receive()
            .concatMap(this::processMessage)
            .subscribe(
                null,
                error -> log.error("Неожиданная ошибка в стриме Kafka", error),
                () -> log.info("Kafka consumer stream завершён")
            );
    }

    private Mono<Void> processMessage(ReceiverRecord<String, Object> receiverRecord) {
        ReceiverOffset offset = receiverRecord.receiverOffset();

        return Mono.fromCallable(() -> (UserCreatedEvent) receiverRecord.value())
            .onErrorMap(ex -> new RuntimeException("Ошибка десериализации UserCreatedEvent", ex))
            .flatMap(event -> {
                log.info("Получено событие: userId={}, email={}, role={}",
                    event.getUserId(), event.getEmail(), event.getRole());

                User user = User.builder()
                    .id(event.getUserId())
                    .email(event.getEmail())
                    .role("ROLE_" + event.getRole().name())
                    .build();

                return userRepository.findById(event.getUserId())
                    .flatMap(existing -> {
                        existing.setEmail(user.getEmail());
                        existing.setRole(user.getRole());
                        return userRepository.save(existing);
                    })
                    .switchIfEmpty(userRepository.save(user))
                    .doOnSuccess(saved -> log.info(
                        "Пользователь сохранён: id={}, email={}",
                        saved.getId(),
                        saved.getEmail()));
            })
            .onErrorResume(ex -> {
                log.error("Ошибка при обработке события UserCreatedEvent: {}", ex.getMessage(), ex);
                return Mono.empty();
            })
            .then(Mono.fromRunnable(offset::acknowledge));
    }
}