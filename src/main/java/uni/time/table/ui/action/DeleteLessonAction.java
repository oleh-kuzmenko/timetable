package uni.time.table.ui.action;

import javafx.scene.layout.GridPane;
import uni.time.table.model.Lesson;

public interface DeleteLessonAction {

  void delete(GridPane timetable, String group, Lesson lesson);
}
