package com.example.ireos.testemail.sent.domain.filter;

import com.example.ireos.testemail.sent.domain.model.Mail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tav on 01/03/2018.
 */

class FilterAllMailFilter  implements MailFilter{
    @Override
    public List<Mail> filter(List<Mail> mail) {
        return new ArrayList<>(mail);
    }
}
