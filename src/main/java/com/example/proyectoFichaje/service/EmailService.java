package com.example.proyectoFichaje.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @org.springframework.scheduling.annotation.Async
    public void enviarCorreoBienvenida(String toEmail, String nombre, String apellido1) {
        jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
        
        try {
            org.springframework.mail.javamail.MimeMessageHelper helper = 
                new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Bienvenido/a a FicharApp - Tu cuenta está lista");

            String htmlContent = 
                "<div style='font-family: sans-serif; background-color: #09090b; color: #ffffff; padding: 40px; border-radius: 16px; max-width: 600px; margin: auto;'>" +
                "  <h1 style='color: #6366f1; margin-bottom: 24px; font-weight: 900; text-transform: uppercase; letter-spacing: -1px;'>FicharApp</h1>" +
                "  <p style='font-size: 16px; line-height: 1.6; color: #a1a1aa;'>Hola <strong style='color: #ffffff;'>" + nombre + " " + apellido1 + "</strong>,</p>" +
                "  <p style='font-size: 16px; line-height: 1.6; color: #a1a1aa;'>Tu cuenta ha sido creada correctamente. Ya puedes acceder al panel para gestionar tus fichajes y horarios.</p>" +
                "  <div style='background-color: #18181b; padding: 24px; border-radius: 12px; border: 1px solid #27272a; margin: 32px 0;'>" +
                "    <p style='margin: 0 0 12px 0; font-size: 12px; text-transform: uppercase; font-weight: 800; color: #6366f1; letter-spacing: 1px;'>Tus datos de acceso</p>" +
                "    <p style='margin: 8px 0; font-size: 14px;'><strong style='color: #71717a;'>Nombre completo:</strong> <span style='color: #ffffff;'>" + nombre + " " + apellido1 + "</span></p>" +
                "    <p style='margin: 8px 0; font-size: 14px;'><strong style='color: #71717a;'>Email Corporativo:</strong> <span style='color: #ffffff;'>" + toEmail + "</span></p>" +
                "  </div>" +
                "  <hr style='border: 0; border-top: 1px solid #27272a; margin: 32px 0;'>" +
                "  <p style='font-size: 12px; color: #52525b; text-align: center;'>Este es un mensaje automático enviado por el sistema de gestión de FicharApp.</p>" +
                "</div>";

            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            System.out.println("Correo HTML personalizado enviado con éxito a: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("Error enviando correo HTML personalizado a " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
