package hu.gdf.thesis.alert;

import hu.gdf.thesis.model.AlertEmailContent;
import hu.gdf.thesis.model.config.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpMailSender {

    private final Logger LOGGER = LoggerFactory.getLogger(SmtpMailSender.class);
    @Autowired
    JavaMailSender mailSender;

    public void sendEmail(AlertEmailContent content, Address address) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("hu.gdf.thesis.monitoring.alerts@gmail.com");
            message.setTo(address.getAddress());
            message.setSubject("Alert");
            message.setText(content.toString());
            mailSender.send(message);
        } catch (MailException ex) {
            LOGGER.error("Error when attempting to send e-mail to address.");
        }

    }
}
