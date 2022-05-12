package hu.gdf.thesis.alert;

import hu.gdf.thesis.model.AlertEmailContent;
import hu.gdf.thesis.model.config.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class SmtpMailSender {
    @Autowired
    JavaMailSender mailSender;

    public void sendEmail(AlertEmailContent content, Address address) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hu.gdf.thesis.monitoring.alerts@gmail.com");
        message.setTo(address.getAddress());
        message.setSubject("Alert");
        message.setText(content.toString());
        mailSender.send(message);
    }
}
