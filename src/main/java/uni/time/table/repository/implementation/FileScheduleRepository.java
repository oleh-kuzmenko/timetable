package uni.time.table.repository.implementation;

import static uni.time.table.util.TimeTableAppUtil.groupToPath;
import static uni.time.table.util.TimeTableAppUtil.lessonsToString;
import static uni.time.table.util.TimeTableAppUtil.pathToGroup;
import static uni.time.table.util.TimeTableAppUtil.stringToLesson;
import static uni.time.table.util.TimeTableAppUtil.stringToLessons;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;
import uni.time.table.model.Lesson;
import uni.time.table.model.Schedule;
import uni.time.table.repository.ScheduleRepository;
import uni.time.table.util.TimeTableAppUtil;

@Component
public class FileScheduleRepository implements ScheduleRepository {

  private static final Logger LOGGER = Logger.getLogger(FileScheduleRepository.class.getName());

  static {
    try {
      Files.createDirectories(Path.of(System.getProperty("user.home"), "timetable"));
    } catch (IOException e) {
      LOGGER.warning("Could not create directory: %s".formatted(e.getMessage()));
    }
  }

  @Override
  public void create(Schedule schedule) {
    try {
      Files.writeString(groupToPath(schedule.group()), lessonsToString(schedule.lessons()));
    } catch (IOException e) {
      LOGGER.warning("Error creating schedule: %s".formatted(e.getMessage()));
    }
  }

  @Override
  public Optional<Schedule> find(String group) {
    try {
      return Optional.of(new Schedule(Files.readAllLines(groupToPath(group))
          .stream()
          .filter(Objects::nonNull)
          .filter(line -> !line.isBlank())
          .map(TimeTableAppUtil::stringToLesson)
          .toList(), group));
    } catch (IOException e) {
      LOGGER.warning("Error finding schedule: %s".formatted(e.getMessage()));
      return Optional.empty();
    }
  }

  @Override
  public void delete(String group) {
    try {
      Files.delete(groupToPath(group));
    } catch (IOException e) {
      LOGGER.warning("Error deleting schedule: %s".formatted(e.getMessage()));
    }
  }

  @Override
  public List<Schedule> findAll() {
    List<Path> files = new ArrayList<>();
    try {
      Files.walkFileTree(Path.of(System.getProperty("user.home"), "timetable"), new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
          if (file.getFileName().toString().matches(".*-schedule.txt")) {
            files.add(file);
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      LOGGER.warning("Error finding schedule: %s".formatted(e.getMessage()));
    }
    return files.stream()
        .map(f -> {
          try {
            return new Schedule(stringToLessons(Files.readAllLines(f)), pathToGroup(f));
          } catch (IOException e) {
            LOGGER.warning("Error finding schedule: %s".formatted(e.getMessage()));
            return null;
          }
        })
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public void deleteLesson(String group, Lesson lesson) {
    Path path = groupToPath(group);
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      List<String> lessons = reader.lines()
          .filter(l -> !stringToLesson(l).equals(lesson))
          .toList();

      Files.write(path, lessons, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      LOGGER.warning("Error deleting lesson: %s".formatted(e.getMessage()));
    }
  }

  @Override
  public void putLesson(String group, Lesson lesson) {
    try {
      Files.writeString(groupToPath(group), "\n".concat(lessonsToString(List.of(lesson))), StandardOpenOption.APPEND);
    } catch (IOException e) {
      LOGGER.warning("Error adding lesson: %s".formatted(e.getMessage()));
    }
  }
}
