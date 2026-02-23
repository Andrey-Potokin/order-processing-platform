package com.example.auth.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация реактивного продюсера Kafka с поддержкой Avro и Confluent Schema Registry.
 * <p>
 * Настраивает {@link KafkaSender} для асинхронной и реактивной отправки сообщений в Kafka
 * с использованием Avro-сериализации. Обеспечивает интеграцию с Spring Boot через внешние свойства.
 * </p>
 * <p>
 * Используется для отправки событий, таких как {@code user.created}, в рамках event-driven взаимодействия
 * между микросервисами (например, из {@code auth-service} в {@code user-service}).
 * </p>
 *
 * @see KafkaSender
 * @see SenderOptions
 */
@Configuration
public class ReactiveKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServers;

    @Value("${schema.registry.url}")
    String schemaRegistryUrl;

    /**
     * Создаёт и настраивает {@link SenderOptions} для реактивного Kafka-продюсера.
     * <p>
     * Включает:
     * <ul>
     *   <li>Сериализатор ключей — {@link StringSerializer}</li>
     *   <li>Сериализатор значений — {@link io.confluent.kafka.serializers.KafkaAvroSerializer}</li>
     *   <li>Подключение к Schema Registry</li>
     *   <li>Включение режима specific reader для генерируемых Avro-классов</li>
     * </ul>
     *
     * @return настроенные опции отправки сообщений
     */
    @Bean
    public SenderOptions<String, Object> senderOptions() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        props.put("schema.registry.url", schemaRegistryUrl);
        props.put("specific.avro.reader", true);

        return SenderOptions.create(props);
    }

    /**
     * Создаёт бин {@link KafkaSender} для реактивной отправки сообщений в Kafka.
     * <p>
     * Использует ранее сконфигурированные {@link #senderOptions()}.
     * Является реактивным аналогом старого {@code ReactiveKafkaProducerTemplate},
     * удалённого в Spring Kafka 3.0+.
     *
     * @return экземпляр {@link KafkaSender}, готовый к внедрению в сервисы
     */
    @Bean
    public KafkaSender<String, Object> reactiveKafkaProducerTemplate() {
        return KafkaSender.create(senderOptions());
    }
}