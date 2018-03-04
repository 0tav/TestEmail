package com.example.ireos.testemail.sent.domain.filter;

import com.example.ireos.testemail.sent.domain.model.Mail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tav on 01/03/2018.
 */

public class ActiveMailFilter implements MailFilter {
    @Override
    public List<Mail> filter(List<Mail> mail) {
        List<Mail> filteredMail = new ArrayList<>();
        for (Mail m : mail){
            if (m.isActive()){
                filteredMail.add(m);
            }
        }
        return filteredMail;
    }
}
