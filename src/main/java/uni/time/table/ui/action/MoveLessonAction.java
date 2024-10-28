package uni.time.table.ui.action;

import javafx.scene.layout.GridPane;
import uni.time.table.model.DayOfWeek;
import uni.time.table.model.LessonSlot;

public interface MoveLessonAction {

  void move(GridPane timetable, String group, DayOfWeek sourceDay, LessonSlot sourceSlot, DayOfWeek targetDay, LessonSlot targetSlot);

}
