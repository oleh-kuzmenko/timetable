package uni.time.table.service;

import uni.time.table.model.TimeTable;

public interface TimetableService {

  void createTimeTable(TimeTable timeTable);

  TimeTable findTimeTable(String group);

  void deleteTimeTable(String group);
}
