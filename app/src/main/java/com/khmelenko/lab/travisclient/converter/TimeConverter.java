package com.khmelenko.lab.travisclient.converter;

/**
 * Provides time related convertion methods
 *
 * @author Dmytro Khmelenko
 */
public final class TimeConverter {

    private TimeConverter() {
    }

    /**
     * Converts duration from seconds to formatted string
     *
     * @param duration Duration in seconds
     * @return Formatted string
     */
    public static String durationToString(long duration) {
        int hour = (int) (duration / 3600);
        int min = (int) ((duration - hour * 3600) / 60);
        int sec = (int) (duration % 60);
        StringBuilder builder = new StringBuilder();
        if (hour != 0) {
            builder.append(hour).append(" hours").append(" ");
        }
        if (min != 0) {
            builder.append(min).append(" min").append(" ");
        }
        if(sec != 0) {
            builder.append(sec).append(" sec");
        }

        return builder.toString();

    }
}
