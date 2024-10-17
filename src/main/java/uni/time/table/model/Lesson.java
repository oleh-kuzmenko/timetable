package uni.time.table.model;

import java.time.DayOfWeek;

public record Lesson(Course course, Teacher teacher, DayOfWeek dayOfWeek, LessonSlot lessonSlot) {

}
