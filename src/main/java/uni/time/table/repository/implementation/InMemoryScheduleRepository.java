package uni.time.table.repository.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import uni.time.table.model.Lesson;
import uni.time.table.model.Schedule;
import uni.time.table.repository.ScheduleRepository;

@Primary
@Repository
public class InMemoryScheduleRepository implements ScheduleRepository {

  private static final Map<String, Schedule> SCHEDULE_MAP = new HashMap<>();

  @Override
  public void create(Schedule schedule) {
    SCHEDULE_MAP.put(schedule.group(), schedule);
  }

  @Override
  public Optional<Schedule> find(String group) {
    return Optional.ofNullable(SCHEDULE_MAP.get(group));
  }

  @Override
  public void delete(String group) {
    SCHEDULE_MAP.remove(group);
  }

  @Override
  public List<Schedule> findAll() {
    return SCHEDULE_MAP.values().stream().toList();
  }

  @Override
  public void deleteLesson(String group, Lesson lesson) {
    Schedule schedule = SCHEDULE_MAP.remove(group);
    SCHEDULE_MAP.put(group, new Schedule(schedule.lessons()
        .stream()
        .filter(l -> !lesson.equals(l))
        .toList(), group));
  }

  @Override
  public void putLesson(String group, Lesson lesson) {
    Schedule schedule = SCHEDULE_MAP.remove(group);
    List<Lesson> lessons = new ArrayList<>(schedule.lessons()
        .stream()
        .toList());
    lessons.add(lesson);
    SCHEDULE_MAP.put(group, new Schedule(lessons, group));
  }
}
