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
                "<div style='font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif; background-color: #F5F7FB; padding: 60px 20px; text-align: center;'>" +
                "  <div style='background-color: #ffffff; padding: 40px; border-radius: 40px; max-width: 600px; margin: auto; text-align: left; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.05);'>" +
                "    <div style='margin-bottom: 32px;'>" +
                "      <h1 style='color: #00B8D4; margin: 0; font-size: 28px; font-weight: 900; letter-spacing: -1.5px;'>Fich<span style='color: #1F2937;'>App</span></h1>" +
                "      <p style='font-size: 10px; font-weight: 800; text-transform: uppercase; letter-spacing: 2px; color: #9CA3AF; margin: 4px 0 0 0;'>Gestión de Jornada</p>" +
                "    </div>" +
                "    <h2 style='color: #1F2937; font-size: 24px; font-weight: 900; letter-spacing: -0.5px; margin-bottom: 16px;'>¡Hola, " + nombre + "!</h2>" +
                "    <p style='font-size: 16px; line-height: 1.6; color: #4B5563; margin-bottom: 32px;'>Tu cuenta ha sido activada con éxito en la plataforma. Ya puedes empezar a gestionar tus fichajes y ausencias desde el panel central.</p>" +
                "    <div style='background-color: #F9FAFB; padding: 24px; border-radius: 24px; border: 1px solid #F3F4F6; margin-bottom: 32px;'>" +
                "      <p style='margin: 0 0 16px 0; font-size: 10px; text-transform: uppercase; font-weight: 900; color: #00B8D4; letter-spacing: 1.5px;'>Datos de tu cuenta</p>" +
                "      <div style='margin-bottom: 8px;'>" +
                "        <span style='font-size: 12px; font-weight: 700; color: #9CA3AF; text-transform: uppercase;'>Empleado</span><br>" +
                "        <span style='font-size: 15px; font-weight: 800; color: #1F2937;'>" + nombre + " " + apellido1 + "</span>" +
                "      </div>" +
                "      <div>" +
                "        <span style='font-size: 12px; font-weight: 700; color: #9CA3AF; text-transform: uppercase;'>Usuario</span><br>" +
                "        <span style='font-size: 15px; font-weight: 800; color: #1F2937;'>" + toEmail + "</span>" +
                "      </div>" +
                "    </div>" +
                "    <div style='text-align: center;'>" +
                "        <p style='font-size: 12px; color: #6B7280; margin-bottom: 20px; font-weight: 600;'>Utiliza tus credenciales para acceder</p>" +
                "        <a href='http://localhost:4200/login' style='display: inline-block; background-color: #00B8D4; color: #ffffff; padding: 16px 32px; border-radius: 16px; text-decoration: none; font-weight: 900; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; box-shadow: 0 10px 15px -3px rgba(0, 184, 212, 0.25);'>Acceder a mi Panel</a>" +
                "    </div>" +
                "    <hr style='border: 0; border-top: 1px solid #F3F4F6; margin: 40px 0;'>" +
                "    <p style='font-size: 11px; color: #9CA3AF; text-align: center; font-weight: 600;'>Este es un mensaje automático. Por favor, no respondas a este correo.</p>" +
                "  </div>" +
                "  <p style='margin-top: 24px; font-size: 11px; font-weight: 800; text-transform: uppercase; letter-spacing: 1.5px; color: #9CA3AF;'>&copy; 2026 FichApp • All Rights Reserved</p>" +
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
