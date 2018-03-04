package com.example.ireos.testemail.sent.domain.filter;

import com.example.ireos.testemail.sent.MailFilterType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tav on 01/03/2018.
 */

public class FilterFactory {

    private static final Map<MailFilterType, MailFilter> mFilter = new HashMap<>();

    public FilterFactory(){
        mFilter.put(MailFilterType.ALL_MAIL, new FilterAllMailFilter());
        mFilter.put(MailFilterType.ACTIVE_MAIL, new ActiveMailFilter());
        mFilter.put(MailFilterType.FAVORITE_MAIL, new FavoriteMailFilter());
    }

    public MailFilter create(MailFilterType filterType){
        return mFilter.get(filterType);
    }
}
