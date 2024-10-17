package uni.time.table.model;

import java.time.LocalTime;

public enum LessonSlot {

  FIRST(LocalTime.of(9, 0)),
  SECOND(LocalTime.of(10, 50)),
  THIRD(LocalTime.of(12, 50)),
  FOURTH(LocalTime.of(14, 40)),
  FIFTH(LocalTime.of(16, 30)),
  SIXTH(LocalTime.of(18, 20));

  private static final int LESSON_DURATION_MINUTES = 90;

  private final LocalTime from;
  private final LocalTime to;

  LessonSlot(LocalTime from) {
    this.from = from;
    this.to = from.plusMinutes(LESSON_DURATION_MINUTES);
  }

  public LocalTime getFrom() {
    return from;
  }

  public LocalTime getTo() {
    return to;
  }
}
