package uni.time.table.repository.implementation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uni.time.table.util.TimeTableAppUtil.groupToPath;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uni.time.table.model.Course;
import uni.time.table.model.DayOfWeek;
import uni.time.table.model.Lesson;
import uni.time.table.model.LessonSlot;
import uni.time.table.model.Teacher;
import uni.time.table.model.TimeTable;
import uni.time.table.repository.TimetableRepository;

class FileTimeTableRepositoryTest {

  private static final String TEST_GROUP = "CS-102";

  private TimetableRepository timetableRepository;

  @BeforeEach
  void setUp() {
    timetableRepository = new FileTimeTableRepository();
  }

  @AfterEach
  void cleanUp() throws IOException {
    List<Path> files = new ArrayList<>();
    Files.walkFileTree(Paths.get(""), new SimpleFileVisitor<>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (file.getFileName().toString().matches(".*-timetable\\.txt")) {
          files.add(file.getFileName());
        }
        return FileVisitResult.CONTINUE;
      }
    });
    for (Path file : files) {
      Files.deleteIfExists(file);
    }
  }

  @Test
  void shouldCreateTimeTable() throws IOException {
    TimeTable timeTable = createTestTimeTable(TEST_GROUP);

    assertDoesNotThrow(() -> timetableRepository.createTimeTable(timeTable));

    assertTrue(Files.exists(Path.of(TEST_GROUP.concat("-timetable.txt"))));
    List<String> lessons = Files.readAllLines(groupToPath(TEST_GROUP));
    assertEquals(4, lessons.size());
    assertEquals("Комп'ютерне зір;Левченко Костянтин;FRIDAY;SECOND", lessons.get(0));
    assertEquals("Управління проектами;Савченко Ірина;FRIDAY;THIRD", lessons.get(1));
    assertEquals("Теорія обчислень;Литвиненко Олександр;WEDNESDAY;FOURTH", lessons.get(2));
    assertEquals("Аналіз великих даних;Зінченко Ярослав;THURSDAY;FOURTH", lessons.get(3));
  }

  @Test
  void shouldFindTimeTable() {
    TimeTable timeTable = createTestTimeTable(TEST_GROUP);
    timetableRepository.createTimeTable(timeTable);

    TimeTable actualTimeTable = timetableRepository.findTimeTable(TEST_GROUP);

    assertEquals(TEST_GROUP, actualTimeTable.group());
    Lesson lesson = timeTable.lessons().getFirst();
    assertEquals(LessonSlot.SECOND, lesson.lessonSlot());
    assertEquals("Комп'ютерне зір", lesson.course().title());
    assertEquals("Левченко Костянтин", lesson.teacher().name());
    assertEquals(DayOfWeek.FRIDAY, lesson.dayOfWeek());
  }

  @Test
  void shouldFindAllTimeTable() {
    TimeTable firstTimeTable = createTestTimeTable(TEST_GROUP);
    TimeTable secondTimeTable = createTestTimeTable("CS-101");
    timetableRepository.createTimeTable(firstTimeTable);
    timetableRepository.createTimeTable(secondTimeTable);

    List<TimeTable> actualTimeTables = timetableRepository.findAllTimeTables();

    assertEquals(2, actualTimeTables.size());
    Assertions.assertArrayEquals(new TimeTable[]{firstTimeTable, secondTimeTable}, actualTimeTables.toArray());
  }

  @Test
  void shouldDeleteTimeTable() {
    TimeTable timeTable = createTestTimeTable(TEST_GROUP);
    timetableRepository.createTimeTable(timeTable);

    timetableRepository.deleteTimeTable(TEST_GROUP);

    TimeTable actualTimeTable = timetableRepository.findTimeTable(TEST_GROUP);
    assertNull(actualTimeTable);
  }

  @Test
  void shouldDeleteLesson() {
    TimeTable timeTable = createTestTimeTable(TEST_GROUP);
    timetableRepository.createTimeTable(timeTable);

    timetableRepository.deleteLesson(TEST_GROUP, timeTable.lessons().getFirst());

    assertEquals(3, timetableRepository.findTimeTable(TEST_GROUP).lessons().size());
  }

  private TimeTable createTestTimeTable(String group) {
    return new TimeTable(
        List.of(
            new Lesson(new Course("Комп'ютерне зір"), new Teacher("Левченко Костянтин"), DayOfWeek.FRIDAY, LessonSlot.SECOND),
            new Lesson(new Course("Управління проектами"), new Teacher("Савченко Ірина"), DayOfWeek.FRIDAY, LessonSlot.THIRD),
            new Lesson(new Course("Теорія обчислень"), new Teacher("Литвиненко Олександр"), DayOfWeek.WEDNESDAY, LessonSlot.FOURTH),
            new Lesson(new Course("Аналіз великих даних"), new Teacher("Зінченко Ярослав"), DayOfWeek.THURSDAY, LessonSlot.FOURTH)
        ),
        group
    );
  }
}
