//2//
package com.example.springredditclone.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor

/*
This class contains the logic to create our email message using the HTML template we are going to provide.
take email message we want to send as input
**/
public class MailContentBuilder {

    //set email message time leaves context object we are doing that by using set Variable method
    private final TemplateEngine templateEngine;

    String build(String message) {
        Context context = new Context();
        context.setVariable("message", message);
        return templateEngine.process("mailTemplate", context);
        //after that, create class to send email messages inside pkg service
    }
}
