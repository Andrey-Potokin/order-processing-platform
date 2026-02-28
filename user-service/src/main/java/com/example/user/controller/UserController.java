package com.example.user.controller;

import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Users", description = "Управление пользователями")
public class UserController {

    UserRepository userRepository;

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Изменить роль пользователя",
        description = "Доступно только администратору",
        responses = {
            @ApiResponse(responseCode = "200", description = "Роль обновлена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
        }
    )
    public Mono<ResponseEntity<User>> updateRole(
        @PathVariable Long id,
        @RequestParam String role) {

        if (!role.matches("ROLE_(USER|MANAGER|ADMIN)")) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("Пользователь не найден")))
            .map(user -> {
                user.setRole(role);
                return user;
            })
            .flatMap(userRepository::save)
            .map(ResponseEntity::ok);
    }
}