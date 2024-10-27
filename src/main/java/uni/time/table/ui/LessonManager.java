package uni.time.table.ui;

import static uni.time.table.util.TimeTableAppUtil.DEFAULT_FONT;
import static uni.time.table.util.TimeTableAppUtil.DEFAULT_FONT_COLOR;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uni.time.table.model.Course;
import uni.time.table.model.DayOfWeek;
import uni.time.table.model.Lesson;
import uni.time.table.model.LessonSlot;
import uni.time.table.model.Teacher;
import uni.time.table.model.TimeTable;
import uni.time.table.repository.TimetableRepository;

public class LessonManager {

  private final TimetableRepository timetableRepository;

  private final LabelManager labelManager;

  private final Random random;

  public LessonManager(TimetableRepository timetableRepository, LabelManager labelManager) {
    this.timetableRepository = timetableRepository;
    this.labelManager = labelManager;
    random = new Random();
  }

  public void populateWithLessons(GridPane grid, TimeTable timeTable) {
    for (DayOfWeek day : DayOfWeek.values()) {
      for (LessonSlot lessonSlot : LessonSlot.values()) {
        timeTable.findLessonForSlot(day, lessonSlot)
            .ifPresentOrElse(lesson -> createLessonButton(grid, lesson, timeTable.group()),
                () -> createAddLessonButton(grid, timeTable.group(), day, lessonSlot));
      }
    }
  }

  private void createLessonButton(GridPane gridPane, Lesson lesson, String group) {
    Button lessonButton = createDefaultButton();
    setupLessonButton(gridPane, lessonButton, group, lesson);
    setupDragAndDrop(lessonButton, lesson.dayOfWeek(), lesson.lessonSlot());

    addNodeToGrid(gridPane, lessonButton, lesson.dayOfWeek(), lesson.lessonSlot());
  }

  private void createAddLessonButton(GridPane gridPane, String group, DayOfWeek day, LessonSlot lessonSlot) {
    Button plusLessonButton = createDefaultButton();
    plusLessonButton.setText("+");
    plusLessonButton.setOnAction(event -> showLessonCreationDialog(gridPane, day, lessonSlot, group, plusLessonButton));
    setupDragAndDropTarget(gridPane, plusLessonButton, day, lessonSlot, group);

    addNodeToGrid(gridPane, plusLessonButton, day, lessonSlot);
  }

  private void addNodeToGrid(GridPane gridPane, Node node, DayOfWeek day, LessonSlot lessonSlot) {
    gridPane.add(node, day.num(), lessonSlot.ordinal() + 1);
    GridPane.setHgrow(node, Priority.ALWAYS);
    GridPane.setVgrow(node, Priority.ALWAYS);
  }

  public void showLessonCreationDialog(GridPane gridPane, DayOfWeek dayOfWeek, LessonSlot lessonSlot, String group, Button slotButton) {
    Stage dialog = new Stage();
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.setTitle("Додати заняття");

    TextField courseField = new TextField();
    courseField.setPromptText("Заняття");

    TextField teacherField = new TextField();
    teacherField.setPromptText("Викладач");

    Button saveButton = new Button("Зберегти");
    saveButton.setFont(DEFAULT_FONT);

    saveButton.setOnAction(event -> {
      String courseName = courseField.getText();
      String teacherName = teacherField.getText();
      Lesson lesson = new Lesson(new Course(courseName), new Teacher(teacherName), dayOfWeek, lessonSlot);
      timetableRepository.putLesson(group, lesson);

      setupLessonButton(gridPane, slotButton, group, lesson);

      dialog.close();
    });

    HBox dialogLayout = new HBox(10, courseField, teacherField, saveButton);
    dialogLayout.setAlignment(Pos.CENTER);

    dialog.setScene(new Scene(dialogLayout, 500, 100));
    dialog.setTitle("Додавання заняття");
    dialog.show();
  }

  private Button createDefaultButton() {
    Button button = new Button();
    button.setMaxHeight(100);
    button.setMaxWidth(100);
    button.setMinHeight(100);
    button.setMinWidth(100);
    button.setBlendMode(BlendMode.DARKEN);
    button.setCenterShape(true);
    button.setStyle("-fx-background-color: lightgray;");
    button.setEffect(new DropShadow());
    return button;
  }

  private String getRandomLightColor() {
    int red = 220 + random.nextInt(56);
    int green = 220 + random.nextInt(56);
    int blue = 220 + random.nextInt(56);
    return "rgb(%d, %d, %d)".formatted(red, green, blue);
  }

  public void setupDragAndDrop(Button lessonButton, DayOfWeek day, LessonSlot slot) {
    lessonButton.setOnDragDetected(event -> {
      Dragboard db = lessonButton.startDragAndDrop(TransferMode.MOVE);
      ClipboardContent content = new ClipboardContent();
      content.putString(day.name() + "_" + slot.ordinal());
      db.setContent(content);
      event.consume();
    });
  }

  public void setupDragAndDropTarget(GridPane gridPane, Button targetButton, DayOfWeek targetDay, LessonSlot targetSlot, String group) {
    targetButton.setOnDragOver(event -> {
      if (event.getGestureSource() != targetButton && event.getDragboard().hasString()) {
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        targetButton.setStyle("-fx-background-color: lightgreen;");
        event.consume();
      }
      event.consume();
    });

    targetButton.setOnDragExited(event -> {
      targetButton.setStyle("-fx-background-color: lightgray;");
      event.consume();
    });

    targetButton.setOnDragDropped(event -> {
      Dragboard db = event.getDragboard();
      if (db.hasString()) {
        String[] sourceInfo = db.getString().split("_");
        DayOfWeek sourceDay = DayOfWeek.valueOf(sourceInfo[0]);
        LessonSlot sourceSlot = LessonSlot.values()[Integer.parseInt(sourceInfo[1])];

        moveLesson(gridPane, group, sourceDay, sourceSlot, targetDay, targetSlot);
        event.setDropCompleted(true);
      }
      event.consume();
    });
  }

  private void moveLesson(GridPane gridPane, String group, DayOfWeek sourceDay, LessonSlot sourceSlot, DayOfWeek targetDay,
      LessonSlot targetSlot) {
    TimeTable timeTable = timetableRepository.findTimeTable(group);
    Optional<Lesson> lessonOpt = timeTable.findLessonForSlot(sourceDay, sourceSlot);

    lessonOpt.ifPresent(lesson -> {
      List<Lesson> updatedLessons = timeTable.lessons().stream()
          .filter(l -> !(l.dayOfWeek() == sourceDay && l.lessonSlot() == sourceSlot))
          .toList();

      Lesson updatedLesson = new Lesson(lesson.course(), lesson.teacher(), targetDay, targetSlot);
      updatedLessons = Stream.concat(updatedLessons.stream(), Stream.of(updatedLesson)).toList();
      timetableRepository.deleteTimeTable(group);
      timetableRepository.createTimeTable(new TimeTable(updatedLessons, group));
      reloadTimeTable(gridPane, group);
    });
  }

  private void setupLessonButton(GridPane gridPane, Button lessonButton, String group, Lesson lesson) {
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
    deleteButton.setOnAction(event -> {
      timetableRepository.deleteLesson(group, lesson);
      reloadTimeTable(gridPane, group);
    });
    StackPane.setAlignment(deleteButton, Pos.TOP_RIGHT);

    VBox textContainer = new VBox(subjectText, teacherText);
    textContainer.setAlignment(Pos.CENTER);
    textContainer.setSpacing(5);

    StackPane stackPane = new StackPane(textContainer, deleteButton);
    stackPane.setAlignment(Pos.TOP_CENTER);
    stackPane.setMaxWidth(100);

    lessonButton.setGraphic(stackPane);
    lessonButton.setStyle("-fx-background-color: " + getRandomLightColor() + ";");
    lessonButton.setBlendMode(BlendMode.DARKEN);
    lessonButton.setEffect(new DropShadow());

    lessonButton.setStyle("-fx-background-color: " + getRandomLightColor() + ";");
    lessonButton.setBlendMode(BlendMode.DARKEN);
    lessonButton.setEffect(new DropShadow());

    lessonButton.setTextFill(DEFAULT_FONT_COLOR);
  }

  public void reloadTimeTable(GridPane gridPane, String group) {
    gridPane.getChildren().clear();

    labelManager.addWeekdayHeaders(gridPane);
    labelManager.addLessonSlotsColumnToGrid(gridPane);

    TimeTable timeTable = timetableRepository.findTimeTable(group);

    populateWithLessons(gridPane, timeTable);
  }
}
