package com.todoteg.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todoteg.model.PasswordResetToken;
import com.todoteg.model.UserProfile;
import com.todoteg.repository.PasswordResetTokenRepository;
import com.todoteg.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final UserProfileRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * Paso 1: El usuario pide el reset.
     * Generamos un token, lo guardamos y enviamos el email.
     * Siempre respondemos igual para no revelar si el email existe.
     */
    @Transactional
    public void requestReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {

            // Eliminar tokens anteriores del usuario para que solo haya uno vigente
            tokenRepository.deleteByUserId(user.getId());

            // Generar token único
            String token = UUID.randomUUID().toString();

            // Guardar en BD con expiración de 30 minutos
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));
            resetToken.setUsed(false);
            tokenRepository.save(resetToken);

            // Enviar email
            emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), token);
        });
    }

    /**
     * Paso 2: El usuario manda el token + nueva contraseña.
     * Validamos el token y actualizamos la contraseña.
     */
    @Transactional
    public void confirmReset(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido o no encontrado"));

        if (resetToken.getUsed()) {
            throw new RuntimeException("Este enlace ya fue utilizado");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El enlace ha expirado. Solicita uno nuevo");
        }

        // Actualizar contraseña
        UserProfile user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Marcar token como usado
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Contraseña restablecida para: {}", user.getEmail());
    }
}
