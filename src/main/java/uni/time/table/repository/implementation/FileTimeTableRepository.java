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
import java.util.logging.Logger;

import uni.time.table.model.Lesson;
import uni.time.table.model.TimeTable;
import uni.time.table.repository.TimetableRepository;
import uni.time.table.util.TimeTableAppUtil;

public class FileTimeTableRepository implements TimetableRepository {

  private static final Logger LOGGER = Logger.getLogger(FileTimeTableRepository.class.getName());

  @Override
  public void createTimeTable(TimeTable timeTable) {
    try {
      Files.writeString(groupToPath(timeTable.group()), lessonsToString(timeTable.lessons()));
    } catch (IOException e) {
      LOGGER.warning("Error creating timetable: %s".formatted(e.getMessage()));
    }
  }

  @Override
  public TimeTable findTimeTable(String group) {
    try {
      return new TimeTable(Files.readAllLines(groupToPath(group))
          .stream()
          .map(TimeTableAppUtil::stringToLesson)
          .toList(), group);
    } catch (IOException e) {
      LOGGER.warning("Error finding timetable: %s".formatted(e.getMessage()));
      return null;
    }
  }

  @Override
  public void deleteTimeTable(String group) {
    try {
      Files.delete(groupToPath(group));
    } catch (IOException e) {
      LOGGER.warning("Error deleting timetable: %s".formatted(e.getMessage()));
    }
  }

  @Override
  public List<TimeTable> findAllTimeTables() {
    List<Path> files = new ArrayList<>();
    try {
      Files.walkFileTree(Path.of(""), new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
          if (file.getFileName().toString().matches(".*-timetable\\.txt")) {
            files.add(file.getFileName());
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      LOGGER.warning("Error finding timetables: %s".formatted(e.getMessage()));
    }
    return files.stream()
        .map(f -> {
          try {
            return new TimeTable(stringToLessons(Files.readAllLines(f)), pathToGroup(f));
          } catch (IOException e) {
            LOGGER.warning("Error finding timetables: %s".formatted(e.getMessage()));
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
      Files.writeString(groupToPath(group), lessonsToString(List.of(lesson)), StandardOpenOption.APPEND);
    } catch (IOException e) {
      LOGGER.warning("Error adding lesson: %s".formatted(e.getMessage()));
    }
  }
}