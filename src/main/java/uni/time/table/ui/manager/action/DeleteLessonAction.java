package uni.time.table.ui.manager.action;

import javafx.scene.layout.GridPane;
import uni.time.table.model.Lesson;

public interface DeleteLessonAction {

  void deleteLesson(GridPane gridPane, String group, Lesson lesson);
}
