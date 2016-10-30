package com.wang17.religiouscalendar.helper;

import com.wang17.religiouscalendar.model.DateTime;
import com.wang17.religiouscalendar.emnu.SolarTerm;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by 阿弥陀佛 on 2015/6/21.
 */
public class SolarTermReader {
    public static Map<DateTime, SolarTerm> get(Map<DateTime, SolarTerm> solarTermMap, DateTime start, DateTime end) {
        Map<DateTime, SolarTerm> result = new HashMap<DateTime, SolarTerm>();
        Set set = solarTermMap.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext()) {
            Map.Entry<DateTime, SolarTerm> solar = (Map.Entry<DateTime, SolarTerm>) i.next();
            if (solar.getKey().compareTo(start) > 0 && solar.getKey().compareTo(end) < 0) {
                result.put(solar.getKey(), solar.getValue());
            }
        }
        return result;
    }

    public static Map<Calendar, SolarTerm> get(Map<Calendar, SolarTerm> solarTermMap, Calendar start) {
        return null;
    }

    public static Map<DateTime, SolarTerm> load() {
        return null;
    }
}
