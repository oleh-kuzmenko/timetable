package uni.time.table.ui.service;


import static uni.time.table.util.TimeTableAppUtil.DEFAULT_FONT;
import static uni.time.table.util.TimeTableAppUtil.DEFAULT_FONT_COLOR;
import static uni.time.table.util.TimeTableAppUtil.DEFAULT_GRAY_STYLE;

import java.util.Random;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.springframework.stereotype.Service;
import uni.time.table.model.Lesson;
import uni.time.table.ui.action.DeleteLessonAction;

@Service
public class TimetableButtonService {

  private final Random random = new Random();

  public Button createDefaultButton() {
    Button button = new Button();
    button.setMaxHeight(100);
    button.setMaxWidth(100);
    button.setMinHeight(100);
    button.setMinWidth(100);
    button.setBlendMode(BlendMode.DARKEN);
    button.setCenterShape(true);
    button.setStyle(DEFAULT_GRAY_STYLE);
    button.setEffect(new DropShadow());
    return button;
  }

  public void setupLessonButton(GridPane timetable, Button lessonButton, String group, Lesson lesson, DeleteLessonAction lessonAction) {
    Text subjectText = new Text(lesson.course().title());
    subjectText.setStyle("-fx-underline: true; -fx-font-weight: bold; -fx-font-size: 11");
    subjectText.setTextAlignment(TextAlignment.CENTER);
    subjectText.setFont(DEFAULT_FONT);
    subjectText.setWrappingWidth(90);

    Text teacherText = new Text(lesson.teacher().name());
    teacherText.setTextAlignment(TextAlignment.LEFT);
    subjectText.setFont(DEFAULT_FONT);
    teacherText.setWrappingWidth(90);

    Button deleteButton = new Button("X");
    deleteButton.setFont(DEFAULT_FONT);
    deleteButton.setStyle(
        """
            -fx-font-size: 6;
            -fx-text-fill: black;
            -fx-background-color: transparent;
            -fx-border-color: black;
            -fx-border-width: 1;
            -fx-padding: 0;
            -fx-border-radius: 0;""");
    deleteButton.setBlendMode(BlendMode.DARKEN);
    deleteButton.setOnAction(event -> lessonAction.delete(timetable, group, lesson));
    StackPane.setAlignment(deleteButton, Pos.TOP_RIGHT);

    VBox textContainer = new VBox(subjectText, teacherText);
    textContainer.setAlignment(Pos.CENTER);
    textContainer.setSpacing(5);

    StackPane stackPane = new StackPane(textContainer, deleteButton);
    stackPane.setAlignment(Pos.TOP_CENTER);
    stackPane.setMaxWidth(100);

    lessonButton.setGraphic(stackPane);
    lessonButton.setStyle(getRandomStyle());
    lessonButton.setBlendMode(BlendMode.DARKEN);
    lessonButton.setEffect(new DropShadow());
    lessonButton.setTextFill(DEFAULT_FONT_COLOR);
  }

  private String getRandomStyle() {
    int red = 220 + random.nextInt(56);
    int green = 220 + random.nextInt(56);
    int blue = 220 + random.nextInt(56);
    String color = "rgb(%d, %d, %d)".formatted(red, green, blue);
    return "-fx-background-color: " + color + ";";
  }

}
