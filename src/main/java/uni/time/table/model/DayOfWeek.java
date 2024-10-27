package uni.time.table.model;

import java.util.Arrays;

public enum DayOfWeek {
  MONDAY("Понеділок", 1),
  TUESDAY("Вівторок", 2),
  WEDNESDAY("Середа", 3),
  THURSDAY("Четвер", 4),
  FRIDAY("П’ятниця", 5);

  private final String title;
  private final int num;

  DayOfWeek(String title, int num) {
    this.title = title;
    this.num = num;
  }

  public String title() {
    return title;
  }

  public int num() {
    return num;
  }

  public static DayOfWeek of(int num) {
    return Arrays.stream(values())
        .filter(dayOfWeek -> dayOfWeek.num == num)
        .findFirst()
        .orElseThrow();
  }
}
