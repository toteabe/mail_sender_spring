package org.iesvdm.mail_sender_spring.controller;

import org.iesvdm.mail_sender_spring.domain.MensajeRespuesta;
import org.iesvdm.mail_sender_spring.service.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.iesvdm.mail_sender_spring.domain.Usuario;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailSenderController {

    @Autowired
    private MailSenderService mailSenderService;

    @PostMapping("/send-mail")
    public MensajeRespuesta sennMail(@RequestBody Usuario usuario) {

        this.mailSenderService.notificarPorEmail(usuario);
        return new MensajeRespuesta("Mensaje Enviado!");

    }

}
