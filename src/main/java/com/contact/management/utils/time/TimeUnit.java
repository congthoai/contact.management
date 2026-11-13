package com.contact.management.utils.time;

public enum TimeUnit {
    Minute(0),
    Hour(1),
    Day(2),
    Week(3),
    Month(4);

    public final int value;

    TimeUnit(final int value) {
        this.value = value;
    }

    public static TimeUnit fromValue(final int value) {
        for (final TimeUnit type : values()) {
            if (type.value == value) {
                return type;
            }
        }

        return null;
    }
}
