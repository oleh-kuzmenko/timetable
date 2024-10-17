package uni.time.table.service.implementation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.DayOfWeek;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uni.time.table.model.Course;
import uni.time.table.model.Lesson;
import uni.time.table.model.LessonSlot;
import uni.time.table.model.Teacher;
import uni.time.table.model.TimeTable;
import uni.time.table.service.TimetableService;

class InMemoryTimeTableTest {

  private TimetableService timetableService;

  @BeforeEach
  void setUp() {
    timetableService = new InMemoryTimeTableService();
  }

  @Test
  void shouldCreateTimeTable() {
    TimeTable timeTable = createTestTimeTable();

    assertDoesNotThrow(() -> timetableService.createTimeTable(timeTable));
  }

  @Test
  void shouldFindTimeTable() {
    TimeTable timeTable = createTestTimeTable();
    timetableService.createTimeTable(timeTable);

    TimeTable actualTimeTable = timetableService.findTimeTable("IT-33");

    assertEquals("IT-33", actualTimeTable.group());
    Lesson lesson = timeTable.lessons().getFirst();
    assertEquals(LessonSlot.FIRST, lesson.lessonSlot());
    assertEquals("Math", lesson.course().name());
    assertEquals("Koval Oleksandr", lesson.teacher().name());
    assertEquals(DayOfWeek.MONDAY, lesson.dayOfWeek());
  }

  @Test
  void shouldDeleteTimeTable() {
    TimeTable timeTable = createTestTimeTable();
    timetableService.createTimeTable(timeTable);

    timetableService.deleteTimeTable("IT-33");

    TimeTable actualTimeTable = timetableService.findTimeTable("IT-33");
    assertNull(actualTimeTable);
  }

  private TimeTable createTestTimeTable() {
    return new TimeTable(
        List.of(
            new Lesson(new Course("Math"),
                new Teacher("Koval Oleksandr"),
                DayOfWeek.MONDAY,
                LessonSlot.FIRST)),
        "IT-33");
  }
}
