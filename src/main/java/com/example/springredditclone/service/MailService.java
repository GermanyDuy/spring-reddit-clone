//3//
package com.example.springredditclone.service;


//import com.programming.techie.springredditclone.exception.SpringRedditException;
import com.example.springredditclone.exceptions.SpringRedditException;
import com.example.springredditclone.model.NotificationEmail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
class MailService {

    private final JavaMailSender mailSender;
    private final MailContentBuilder mailContentBuilder;
    @Async
    /*so, because time to response when receives email and reply about 10s
        it so slow.and to fix it we use async, first we back to SpringRedditCloneApplication
        to @EnableAsync and come back here to @Async.
    */
    void sendMail(NotificationEmail notificationEmail) throws SpringRedditException {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            //Constructing an instant of type MimeMessageHelper inside lamda
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            //To do this MimeMessageHelper we passing the data inside
            messageHelper.setFrom("hoducduy19@email.com");
            //setTo, setSub,... to mapping this field from noti email obj
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()));
        };
        try {
            mailSender.send(messagePreparator);
            log.info("Activation email sent!!");
        } catch (MailException e) {
            throw new SpringRedditException("Exception occurred when sending mail to " + notificationEmail.getRecipient());
        }
    }

}
