package uni.time.table.repository;

import java.util.List;

import uni.time.table.model.Lesson;
import uni.time.table.model.TimeTable;

public interface TimetableRepository {

  void createTimeTable(TimeTable timeTable);

  TimeTable findTimeTable(String group);

  void deleteTimeTable(String group);

  List<TimeTable> findAllTimeTables();

  void deleteLesson(String group, Lesson lesson);

  void putLesson(String group, Lesson lesson);
}
