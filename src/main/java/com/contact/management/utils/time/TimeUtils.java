package com.contact.management.utils.time;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class TimeUtils {

    private static final long MILLI_PER_MINUTE = 60_000;
    private static final long MILLI_PER_HOUR = 3_600_000;
    private static final long MILLI_PER_DAY = 86_400_000;
    private static final long MILLI_PER_WEEK = 604_800_000;

    private static long toEpochMilli() {
        return Instant.now()
                .atOffset(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli();
    }

    public static long truncate(final long epochMilli, final TimeUnit type) {
        OffsetDateTime offsetDateTime = Instant.ofEpochMilli(epochMilli)
                .atOffset(ZoneOffset.UTC);

        if (type == TimeUnit.Minute) {
            offsetDateTime = offsetDateTime.truncatedTo(ChronoUnit.MINUTES);
        } else if (type == TimeUnit.Hour) {
            offsetDateTime = offsetDateTime.truncatedTo(ChronoUnit.HOURS);
        } else if (type == TimeUnit.Day) {
            offsetDateTime = offsetDateTime.truncatedTo(ChronoUnit.DAYS);
        } else if (type == TimeUnit.Week) {
            offsetDateTime = offsetDateTime.truncatedTo(ChronoUnit.DAYS)
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        } else {
            offsetDateTime = offsetDateTime.truncatedTo(ChronoUnit.DAYS)
                    .with(TemporalAdjusters.firstDayOfMonth());
        }

        return offsetDateTime.toInstant()
                .toEpochMilli();
    }

    public static long nextTime(final long epochMilli, final TimeUnit type, final int duration,
            final long minEpochMilli) {
        long truncatedEpoch = truncate(epochMilli, type);
        if (truncatedEpoch >= minEpochMilli) {
            return truncatedEpoch;
        }

        if (type == TimeUnit.Minute) {
            final long distance = MILLI_PER_MINUTE * duration;
            truncatedEpoch = truncatedEpoch + (1 + (minEpochMilli - truncatedEpoch) / distance) * distance;
        } else if (type == TimeUnit.Hour) {
            final long distance = MILLI_PER_HOUR * duration;

            truncatedEpoch = truncatedEpoch + (1 + (minEpochMilli - truncatedEpoch) / distance) * distance;
        } else if (type == TimeUnit.Day) {
            final long distance = MILLI_PER_DAY * duration;

            truncatedEpoch = truncatedEpoch + (1 + (minEpochMilli - truncatedEpoch) / distance) * distance;
        } else if (type == TimeUnit.Week) {
            final long distance = MILLI_PER_WEEK * duration;

            truncatedEpoch = truncatedEpoch + (1 + (minEpochMilli - truncatedEpoch) / distance) * distance;
        } else {
            OffsetDateTime offsetDateTime = Instant.ofEpochMilli(truncatedEpoch)
                    .atOffset(ZoneOffset.UTC);

            while (offsetDateTime.toInstant()
                    .toEpochMilli() <= minEpochMilli) {
                offsetDateTime = offsetDateTime.plus(1, ChronoUnit.MONTHS);
            }

            truncatedEpoch = offsetDateTime.toInstant()
                    .toEpochMilli();
        }

        return truncatedEpoch;
    }

    public static long duration(final TimeUnit type, final int duration) {
        switch (type) {
        case Minute:
            return MILLI_PER_MINUTE * duration;

        case Hour:
            return MILLI_PER_HOUR * duration;

        case Day:
            return MILLI_PER_DAY * duration;

        case Week:
            return MILLI_PER_WEEK * duration;

        case Month:
            final long now = toEpochMilli();
            final long truncatedEpoch = truncate(now, type);
            OffsetDateTime offsetDateTime = Instant.ofEpochMilli(truncatedEpoch)
                    .atOffset(ZoneOffset.UTC);

            if (duration > 0) {
                offsetDateTime = offsetDateTime.plus(duration, ChronoUnit.MONTHS);
            }

            return offsetDateTime.toInstant()
                    .toEpochMilli() - now;

        default:
            return 0;
        }
    }
}
