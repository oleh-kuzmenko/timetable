package uni.time.table.ui.service;

import static uni.time.table.util.TimeTableAppUtil.DEFAULT_GRAY_STYLE;

import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import org.springframework.stereotype.Service;
import uni.time.table.model.DayOfWeek;
import uni.time.table.model.LessonSlot;
import uni.time.table.ui.action.MoveLessonAction;

@Service
public class TimetableDragAndDropService {

  public void setupDragAndDrop(Button lessonButton, DayOfWeek day, LessonSlot slot) {
    lessonButton.setOnDragDetected(event -> {
      Dragboard db = lessonButton.startDragAndDrop(TransferMode.MOVE);
      ClipboardContent content = new ClipboardContent();
      content.putString(day.name() + "_" + slot.ordinal());
      db.setContent(content);
      event.consume();
    });
  }

  public void setupDragAndDropTarget(GridPane timetable, Button targetButton, DayOfWeek targetDay, LessonSlot targetSlot,
      String group, MoveLessonAction moveLessonAction) {
    targetButton.setOnDragOver(event -> {
      if (event.getGestureSource() != targetButton && event.getDragboard().hasString()) {
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        targetButton.setStyle("-fx-background-color: lightgreen;");
        event.consume();
      }
      event.consume();
    });

    targetButton.setOnDragExited(event -> {
      targetButton.setStyle(DEFAULT_GRAY_STYLE);
      event.consume();
    });

    targetButton.setOnDragDropped(event -> {
      Dragboard db = event.getDragboard();
      if (db.hasString()) {
        String[] sourceInfo = db.getString().split("_");
        DayOfWeek sourceDay = DayOfWeek.valueOf(sourceInfo[0]);
        LessonSlot sourceSlot = LessonSlot.values()[Integer.parseInt(sourceInfo[1])];

        moveLessonAction.move(timetable, group, sourceDay, sourceSlot, targetDay, targetSlot);
        event.setDropCompleted(true);
      }
      event.consume();
    });
  }

}
