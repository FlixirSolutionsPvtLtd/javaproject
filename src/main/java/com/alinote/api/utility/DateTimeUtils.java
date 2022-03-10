package com.alinote.api.utility;

import java.util.concurrent.*;

public final class DateTimeUtils {

    private DateTimeUtils() {}

    public static long timeDiffInMinutes(long startTime, long endTime) {
        long diffInMillis = endTime - startTime;
        return (diffInMillis / 1000) / 60;
    }
}
