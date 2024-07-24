package tech.nuqta.handihub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * The MailConfig class provides configuration for a JavaMailSender bean used for sending emails.
 */
@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender getJavaMailSender() {
        var mailSender = new JavaMailSenderImpl();

        // Set the email server details
        mailSender.setHost("mail.nuqta.tech");
        mailSender.setPort(25);

        mailSender.setUsername("info@nuqta.tech");
        mailSender.setPassword("akobir2003");

        var props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
