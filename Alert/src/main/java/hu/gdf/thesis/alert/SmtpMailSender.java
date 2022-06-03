package hu.gdf.thesis.alert;

import hu.gdf.thesis.model.config.Address;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SmtpMailSender {


    @Autowired
    JavaMailSender mailSender;

    public void sendEmail(AlertEmailContent content, List<Address> addressList ) {
        try {
            for (Address address : addressList ) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("hu.gdf.thesis.monitoring.alerts@gmail.com");
                message.setTo(address.getAddress());
                message.setSubject("Alert");
                message.setText(content.toString());
                mailSender.send(message);
                log.info("Sending alert e-mail to address: " + address.getAddress());
            }
        } catch (MailException ex) {
            log.error("Error when attempting to send e-mail to address.");
        }

    }
}
