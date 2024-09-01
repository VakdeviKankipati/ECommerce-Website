package com.vakya.emailservice.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.vakya.emailservice.dtos.SendEmailEventDto;
import com.vakya.emailservice.utils.EmailUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Service
public class SendEmailEventConsumer {
    private ObjectMapper objectMapper;

    public SendEmailEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "sendEmail", groupId = "emailService")
    public void handleSendEmailEvent(String message) throws JsonProcessingException {
        SendEmailEventDto emailEventDto = objectMapper.readValue(
                message, SendEmailEventDto.class
        );

        String to = emailEventDto.getTo();
        String from = emailEventDto.getFrom();
        String subject = emailEventDto.getSubject();
        String body = emailEventDto.getBody();

        //Send an Email.
        System.out.println("Email Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "uwlabljcbdhbzvf");
            }
        };
        //Session session = Session.getInstance(props, auth);
        Session session = Session.getInstance(props,auth);

        EmailUtil.sendEmail(session, to, subject, body);
    }
}
