package com.khoatrbl.productivity.components;

import org.springframework.stereotype.Component;

import java.time.*;

@Component
public class LateRewardPolicy {

    private static final long DAY = 24 * 60;

    /** Percentage of the reward granted for a task finished this many minutes late. */
    public int percentFor(long lateMinutes) {
        if (lateMinutes <= 0) return 100;
        if (lateMinutes <= DAY) return 90;
        if (lateMinutes <= 3 * DAY) return 85;
        return 80;
    }

    /** Applies the percentage, rounding down, but never below 1 for a non-zero reward. */
    public int apply(int amount, int percent) {
        if (amount <= 0 || percent >= 100) return amount;
        return Math.max(1, amount * percent / 100);
    }

    /** Minutes past the due date/time, in the user's timezone (0 if not late). */
    public long minutesLate(LocalDate dueDate, LocalTime dueTime, String timezone) {
        ZoneId zone = ZoneId.of(timezone);
        ZonedDateTime due = LocalDateTime.of(dueDate, dueTime).atZone(zone);
        long minutes = Duration.between(due, ZonedDateTime.now(zone)).toMinutes();
        return Math.max(0, minutes);
    }
}
