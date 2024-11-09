package uni.time.table.repository.implementation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uni.time.table.model.Course;
import uni.time.table.model.DayOfWeek;
import uni.time.table.model.Lesson;
import uni.time.table.model.LessonSlot;
import uni.time.table.model.Teacher;
import uni.time.table.model.Schedule;
import uni.time.table.repository.ScheduleRepository;

class FileScheduleRepositoryTest {

  private static final String TEST_GROUP = "CS-102";

  private ScheduleRepository scheduleRepository;

  @BeforeEach
  void setUp() {
    scheduleRepository = new FileScheduleRepository();
  }

  @AfterEach
  void cleanUp() throws IOException {
    List<Path> files = new ArrayList<>();
    Files.walkFileTree(Paths.get(System.getProperty("user.home"), "timetable"), new SimpleFileVisitor<>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (file.getFileName().toString().matches(".*-schedule\\.txt")) {
          files.add(file);
        }
        return FileVisitResult.CONTINUE;
      }
    });
    for (Path file : files) {
      Files.deleteIfExists(file);
    }
  }

  @AfterAll
  static void deleteTimetableDir() throws IOException {
    Files.deleteIfExists(Paths.get(System.getProperty("user.home"), "timetable"));
  }

  @Test
  void shouldCreate() throws IOException {
    Schedule schedule = createTestTimeTable(TEST_GROUP);

    assertDoesNotThrow(() -> scheduleRepository.create(schedule));

    assertTrue(Files.exists(Path.of(System.getProperty("user.home"), "timetable", TEST_GROUP.concat("-schedule.txt"))));
    List<String> lessons = Files.readAllLines(groupToPath(TEST_GROUP));
    assertEquals(4, lessons.size());
    assertEquals("Комп'ютерне зір;Левченко Костянтин;FRIDAY;SECOND", lessons.get(0));
    assertEquals("Управління проектами;Савченко Ірина;FRIDAY;THIRD", lessons.get(1));
    assertEquals("Теорія обчислень;Литвиненко Олександр;WEDNESDAY;FOURTH", lessons.get(2));
    assertEquals("Аналіз великих даних;Зінченко Ярослав;THURSDAY;FOURTH", lessons.get(3));
  }

  @Test
  void shouldFind() {
    Schedule schedule = createTestTimeTable(TEST_GROUP);
    scheduleRepository.create(schedule);

    Optional<Schedule> actualSchedule = scheduleRepository.find(TEST_GROUP);

    assertTrue(actualSchedule.isPresent());
    assertEquals(TEST_GROUP, actualSchedule.get().group());
    Lesson lesson = actualSchedule.get().lessons().getFirst();
    assertEquals(LessonSlot.SECOND, lesson.lessonSlot());
    assertEquals("Комп'ютерне зір", lesson.course().title());
    assertEquals("Левченко Костянтин", lesson.teacher().name());
    assertEquals(DayOfWeek.FRIDAY, lesson.dayOfWeek());
  }

  @Test
  void shouldFindAllTimeTable() {
    Schedule firstSchedule = createTestTimeTable(TEST_GROUP);
    Schedule secondSchedule = createTestTimeTable("CS-101");
    scheduleRepository.create(firstSchedule);
    scheduleRepository.create(secondSchedule);

    List<Schedule> actualSchedules = scheduleRepository.findAll();

    assertEquals(2, actualSchedules.size());
    assertTrue(actualSchedules.containsAll(List.of(firstSchedule, secondSchedule)));
  }

  @Test
  void shouldDelete() {
    Schedule schedule = createTestTimeTable(TEST_GROUP);
    scheduleRepository.create(schedule);

    scheduleRepository.delete(TEST_GROUP);

    Optional<Schedule> actualSchedule = scheduleRepository.find(TEST_GROUP);
    assertTrue(actualSchedule.isEmpty());
  }

  @Test
  void shouldDeleteLesson() {
    Schedule schedule = createTestTimeTable(TEST_GROUP);
    scheduleRepository.create(schedule);

    scheduleRepository.deleteLesson(TEST_GROUP, schedule.lessons().getFirst());

    Optional<Schedule> actualSchedule = scheduleRepository.find(TEST_GROUP);
    assertTrue(actualSchedule.isPresent());
    assertEquals(3, actualSchedule.get().lessons().size());
  }

  private Schedule createTestTimeTable(String group) {
    return new Schedule(
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
