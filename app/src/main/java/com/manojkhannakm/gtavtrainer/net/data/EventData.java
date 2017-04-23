package com.manojkhannakm.gtavtrainer.net.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Manoj Khanna
 */

public class EventData extends Data {

    private final String mHandlerName, mName;

    public EventData(String handlerName, String name) {
        mHandlerName = handlerName;
        mName = name;
    }

    public EventData(String data) {
        super(data.substring(data.indexOf('(')));

        Matcher matcher = Pattern.compile("([a-z_0-9]+)\\.([a-z_0-9]+)\\(.*\\)").matcher(data);
        if (!matcher.find()) {
            throw new IllegalArgumentException();
        }

        mHandlerName = matcher.group(1);
        mName = matcher.group(2);
    }

    @Override
    public String toString() {
        return mHandlerName + "." + mName + super.toString();
    }

    public String getHandlerName() {
        return mHandlerName;
    }

    public String getName() {
        return mName;
    }

}
