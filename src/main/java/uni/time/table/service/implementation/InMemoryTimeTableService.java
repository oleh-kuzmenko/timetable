package uni.time.table.service.implementation;

import java.util.HashMap;
import java.util.Map;

import uni.time.table.model.TimeTable;
import uni.time.table.service.TimetableService;

public class InMemoryTimeTableService implements TimetableService {

  private static final Map<String, TimeTable> TIME_TABLE_MAP = new HashMap<>();

  @Override
  public void createTimeTable(TimeTable timeTable) {
    TIME_TABLE_MAP.put(timeTable.group(), timeTable);
  }

  @Override
  public TimeTable findTimeTable(String group) {
    return TIME_TABLE_MAP.get(group);
  }

  @Override
  public void deleteTimeTable(String group) {
    TIME_TABLE_MAP.remove(group);
  }
}
