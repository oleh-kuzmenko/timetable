package uni.time.table.model;

import java.util.List;
import java.util.Optional;

public record TimeTable(List<Lesson> lessons, String group) {

  public Optional<Lesson> findLessonForSlot(DayOfWeek dayOfWeek, LessonSlot lessonSlot) {
    return this.lessons.stream()
        .filter(lesson -> lesson.dayOfWeek() == dayOfWeek && lesson.lessonSlot() == lessonSlot)
        .findFirst();
  }
}
