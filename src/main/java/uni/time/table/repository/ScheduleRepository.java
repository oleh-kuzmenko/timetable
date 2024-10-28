package uni.time.table.repository;

import java.util.List;
import java.util.Optional;

import uni.time.table.model.Lesson;
import uni.time.table.model.Schedule;

public interface ScheduleRepository {

  void create(Schedule schedule);

  Optional<Schedule> find(String group);

  void delete(String group);

  List<Schedule> findAll();

  void deleteLesson(String group, Lesson lesson);

  void putLesson(String group, Lesson lesson);
}
