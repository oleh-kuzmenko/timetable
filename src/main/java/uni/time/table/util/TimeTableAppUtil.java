package uni.time.table.util;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import uni.time.table.model.Course;
import uni.time.table.model.DayOfWeek;
import uni.time.table.model.Lesson;
import uni.time.table.model.LessonSlot;
import uni.time.table.model.Teacher;
import uni.time.table.model.TimeTable;

public class TimeTableAppUtil {

  public static final String DEFAULT_GRAY_STYLE = "-fx-background-color: lightgray;";
  public static final Font DEFAULT_FONT = new Font("Arial Bold", 14);
  public static final Color DEFAULT_FONT_COLOR = Color.web("#333333");

  public static final String TIME_TABLE_TITLE = "Розклад занять";
  public static final String TIMETABLE_FILE_NAME_SUFFIX = "-timetable.txt";

  public static Lesson stringToLesson(String lesson) {
    String[] attributes = lesson.split(";");
    return new Lesson(
        new Course(attributes[0]), new Teacher(attributes[1]), DayOfWeek.valueOf(attributes[2]), LessonSlot.valueOf(attributes[3]));
  }

  public static List<Lesson> stringToLessons(List<String> lessons) {
    return lessons.stream()
        .filter(Objects::nonNull)
        .filter(line -> !line.isBlank())
        .map(TimeTableAppUtil::stringToLesson)
        .toList();
  }

  public static String lessonsToString(List<Lesson> lessons) {
    return lessons.stream()
        .map(lesson -> "%s;%s;%s;%s".formatted(
            lesson.course().title(), lesson.teacher().name(), lesson.dayOfWeek(), lesson.lessonSlot()))
        .collect(Collectors.joining("\n"));
  }

  public static Path groupToPath(String group) {
    return Path.of(group.concat(TIMETABLE_FILE_NAME_SUFFIX));
  }

  public static String pathToGroup(Path file) {
    return file.getFileName().toString().replace(TIMETABLE_FILE_NAME_SUFFIX, "");
  }

  public static List<TimeTable> getDemoTimeTables() {
    return List.of(new TimeTable(
        List.of(
            new Lesson(new Course("Алгоритми та структури даних"), new Teacher("Коваль Олександр"), DayOfWeek.MONDAY, LessonSlot.FIRST),
            new Lesson(new Course("Дискретна математика"), new Teacher("Шевченко Ірина"), DayOfWeek.MONDAY, LessonSlot.SECOND),
            new Lesson(new Course("Програмування на Java"), new Teacher("Петренко Марія"), DayOfWeek.MONDAY, LessonSlot.THIRD),
            new Lesson(new Course("Бази даних"), new Teacher("Іваненко Микола"), DayOfWeek.TUESDAY, LessonSlot.FIRST),
            new Lesson(new Course("Архітектура комп'ютерів"), new Teacher("Новак Катерина"), DayOfWeek.TUESDAY, LessonSlot.SECOND),
            new Lesson(new Course("Операційні системи"), new Teacher("Коваленко Сергій"), DayOfWeek.WEDNESDAY, LessonSlot.FIRST),
            new Lesson(new Course("Комп'ютерні мережі"), new Teacher("Ткаченко Анна"), DayOfWeek.WEDNESDAY, LessonSlot.SECOND),
            new Lesson(new Course("Штучний інтелект"), new Teacher("Мельник Олег"), DayOfWeek.THURSDAY, LessonSlot.THIRD),
            new Lesson(new Course("Машинне навчання"), new Teacher("Білан Оксана"), DayOfWeek.FRIDAY, LessonSlot.FIRST),
            new Lesson(new Course("Теорія обчислень"), new Teacher("Гончаренко Максим"), DayOfWeek.THURSDAY, LessonSlot.FIRST),
            new Lesson(new Course("Інженерія програмного забезпечення"), new Teacher("Литвин Олена"), DayOfWeek.THURSDAY,
                LessonSlot.SECOND),
            new Lesson(new Course("Кібербезпека"), new Teacher("Голуб Анастасія"), DayOfWeek.FRIDAY, LessonSlot.SECOND),
            new Lesson(new Course("Комп'ютерна графіка"), new Teacher("Савченко Дмитро"), DayOfWeek.FRIDAY, LessonSlot.THIRD),
            new Lesson(new Course("Моделювання та симуляція"), new Teacher("Іващенко Тарас"), DayOfWeek.MONDAY, LessonSlot.FOURTH),
            new Lesson(new Course("Об'єктно-орієнтоване програмування"), new Teacher("Романенко Юлія"), DayOfWeek.WEDNESDAY,
                LessonSlot.THIRD)
        ),
        "CS-101"
    ), new TimeTable(
        List.of(
            new Lesson(new Course("Розробка веб-додатків"), new Teacher("Петрова Марія"), DayOfWeek.MONDAY, LessonSlot.FIRST),
            new Lesson(new Course("Безпека мереж"), new Teacher("Денисенко Олексій"), DayOfWeek.MONDAY, LessonSlot.SECOND),
            new Lesson(new Course("Структури даних та алгоритми"), new Teacher("Сидоренко Наталія"), DayOfWeek.MONDAY, LessonSlot.THIRD),
            new Lesson(new Course("Операційні системи"), new Teacher("Мороз Андрій"), DayOfWeek.TUESDAY, LessonSlot.FIRST),
            new Lesson(new Course("Тестування програмного забезпечення"), new Teacher("Захарченко Сергій"), DayOfWeek.TUESDAY,
                LessonSlot.SECOND),
            new Lesson(new Course("Хмарні обчислення"), new Teacher("Коваль Олександр"), DayOfWeek.WEDNESDAY, LessonSlot.FIRST),
            new Lesson(new Course("Розробка мобільних додатків"), new Teacher("Тимошенко Вікторія"), DayOfWeek.WEDNESDAY,
                LessonSlot.SECOND),
            new Lesson(new Course("Взаємодія людини та комп'ютера"), new Teacher("Дорошенко Олексій"), DayOfWeek.THURSDAY,
                LessonSlot.FIRST),
            new Lesson(new Course("Системи управління базами даних"), new Teacher("Панасюк Олег"), DayOfWeek.THURSDAY, LessonSlot.SECOND),
            new Lesson(new Course("Штучний інтелект"), new Teacher("Гончаренко Максим"), DayOfWeek.THURSDAY, LessonSlot.THIRD),
            new Lesson(new Course("Розробка ігор"), new Teacher("Семененко Анна"), DayOfWeek.FRIDAY, LessonSlot.FIRST),
            new Lesson(new Course("Комп'ютерне зір"), new Teacher("Левченко Костянтин"), DayOfWeek.FRIDAY, LessonSlot.SECOND),
            new Lesson(new Course("Управління проектами"), new Teacher("Савченко Ірина"), DayOfWeek.FRIDAY, LessonSlot.THIRD),
            new Lesson(new Course("Теорія обчислень"), new Teacher("Литвиненко Олександр"), DayOfWeek.WEDNESDAY, LessonSlot.FOURTH),
            new Lesson(new Course("Аналіз великих даних"), new Teacher("Зінченко Ярослав"), DayOfWeek.THURSDAY, LessonSlot.FOURTH)
        ),
        "CS-102"
    ));
  }
}
