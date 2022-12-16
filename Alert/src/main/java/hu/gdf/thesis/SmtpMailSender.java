package hu.gdf.thesis;

import hu.gdf.thesis.model.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SmtpMailSender {

    @Autowired
    MailSender mailSender;

    public void sendEmail(AlertEmailContent content,
                          List<Address> addressList ) {
        try {
            for (Address address : addressList ) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("${spring.mail.username}");
                message.setTo(address.getAddress());
                message.setSubject("Alert");
                message.setText(content.toString());
                mailSender.send(message);
                log.info("Sending alert e-mail to address: " + address.getAddress());
            }
        } catch (MailException ex) {
            log.error("Error when attempting to" +
                    " send e-mail to address.");
        }
    }
}
