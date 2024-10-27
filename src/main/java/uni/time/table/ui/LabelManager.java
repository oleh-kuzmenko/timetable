package uni.time.table.ui;

import static uni.time.table.util.TimeTableAppUtil.DEFAULT_FONT;
import static uni.time.table.util.TimeTableAppUtil.DEFAULT_FONT_COLOR;

import java.util.Arrays;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import uni.time.table.model.DayOfWeek;
import uni.time.table.model.LessonSlot;

public class LabelManager {

  private static final String LESSON_SLOT_LABEL_TEMPLATE = "%s - %s";

  public void addLessonSlotsColumnToGrid(GridPane grid) {
    Arrays.stream(LessonSlot.values())
        .forEach(lessonSlot -> grid.add(buildTimeSlotLabel(lessonSlot), 0, lessonSlot.ordinal() + 1));
  }

  public void addWeekdayHeaders(GridPane gridPane) {
    Arrays.stream(DayOfWeek.values())
        .limit(5)
        .forEach(day -> gridPane.add(builDayLabel(day), day.num(), 0));
  }

  private Label builDayLabel(DayOfWeek dayOfWeek) {
    return buildDefaultLabel(dayOfWeek.title());
  }

  private Label buildTimeSlotLabel(LessonSlot lessonSlot) {
    return buildDefaultLabel(LESSON_SLOT_LABEL_TEMPLATE.formatted(lessonSlot.getFrom(), lessonSlot.getTo()));
  }

  private Label buildDefaultLabel(String tile) {
    Label label = new Label(tile);
    label.setFont(DEFAULT_FONT);
    label.setTextFill(DEFAULT_FONT_COLOR);
    label.setAlignment(Pos.CENTER);
    label.setPadding(new Insets(5, 5, 5, 5));
    return label;
  }
}
