package org.iesvdm.mail_sender_spring.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.iesvdm.mail_sender_spring.domain.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MailSenderService {

    @Value("${spring.mail.username}")
    private String emailSender;

    @Value("${app.ATTACH_PATH}")
    private String ATTACH_PATH;

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    private JavaMailSender mailSender;

    public MailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(String from, String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendWithAttach(String from, String to, String subject,
                               String text, String attachName,
                               InputStreamSource inputStream) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.addAttachment(attachName, inputStream);
        mailSender.send(message);
    }


    @Async
    public void notificarPorEmail(Usuario usuario) {

//Correo en modo texto
//            emailService.send(emailSender,
//                usuario.getEmail(),
//                usuario.getNombre(),
//                "Descripción:\n"
//                        + "Blah, blah"
//                );

            Map<String, Object> templateModel = new HashMap<>();

            templateModel.put("title", usuario.getNombre());
            templateModel.put("descripcion", "Esto es una descripción en el cuerpo html del correo!!");

            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(templateModel);
            String htmlBody = thymeleafTemplateEngine.process("email.html", thymeleafContext);

            byte[] qrArr = new byte[0];
            try {
                qrArr = Files.readAllBytes(Paths.get("src/main/resources/static/img/clubdejava.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {

                this.sendWithAttach(emailSender,
                        usuario.getEmail(),
                        "Esto es un asunto",
                        htmlBody,
                        "clubdejava.png",
                        new ByteArrayResource(qrArr)
                );
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }


    }

}
