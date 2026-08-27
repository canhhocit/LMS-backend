-- ClassSchedule.dayOfWeek is an Integer in the JPA entity.
ALTER TABLE class_schedules
    ALTER COLUMN day_of_week TYPE INTEGER;
