package org.mschoe.aocutil.lib.util;

import java.time.LocalDate;

public class TimeUtils {

    public static int currentDay() {
        return LocalDate.now().getDayOfMonth();
    }

    public static int currentYear() {
        return LocalDate.now().getYear();
    }
}
