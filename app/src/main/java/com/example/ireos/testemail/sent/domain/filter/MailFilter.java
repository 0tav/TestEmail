package com.example.ireos.testemail.sent.domain.filter;

import com.example.ireos.testemail.sent.domain.model.Mail;

import java.util.List;

/**
 * Created by tav on 01/03/2018.
 */

public interface MailFilter {
    List<Mail> filter(List<Mail> mail);
}
