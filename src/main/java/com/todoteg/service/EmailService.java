package com.todoteg.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String userName, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Restablecer contraseña — Kiogloss");

            String resetLink = frontendUrl + "/reset-password?token=" + token;

            String html = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                        <div style="background-color: #610361; padding: 24px; border-radius: 8px 8px 0 0;">
                            <h1 style="color: white; margin: 0; font-size: 24px;">Kiogloss</h1>
                        </div>
                        <div style="background-color: #f9f9f9; padding: 32px; border-radius: 0 0 8px 8px;">
                            <h2 style="color: #333;">Hola, %s</h2>
                            <p style="color: #555; font-size: 16px;">
                                Recibimos una solicitud para restablecer la contraseña de tu cuenta.
                                Haz clic en el botón de abajo para continuar.
                            </p>
                            <div style="text-align: center; margin: 32px 0;">
                                <a href="%s"
                                   style="background-color: #610361; color: white; padding: 14px 28px;
                                          border-radius: 6px; text-decoration: none; font-size: 16px;
                                          font-weight: bold;">
                                    Restablecer contraseña
                                </a>
                            </div>
                            <p style="color: #888; font-size: 14px;">
                                Este enlace expirará en <strong>30 minutos</strong>.<br>
                                Si no solicitaste este cambio, ignora este correo.
                            </p>
                        </div>
                    </div>
                    """.formatted(userName, resetLink);

            helper.setText(html, true);
            mailSender.send(message);

            log.info("Email de reset enviado a: {}", toEmail);

        } catch (Exception e) {
            log.error("Error enviando email de reset a {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("No se pudo enviar el email. Intenta de nuevo.");
        }
    }
}
