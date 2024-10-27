package uni.time.table.ui.manager;

import static uni.time.table.util.TimeTableAppUtil.DEFAULT_FONT;
import static uni.time.table.util.TimeTableAppUtil.DEFAULT_GRAY_STYLE;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import uni.time.table.model.Course;
import uni.time.table.model.DayOfWeek;
import uni.time.table.model.Lesson;
import uni.time.table.model.LessonSlot;
import uni.time.table.model.Teacher;
import uni.time.table.model.TimeTable;
import uni.time.table.repository.TimetableRepository;
import uni.time.table.ui.manager.action.DeleteLessonAction;
import uni.time.table.ui.manager.action.MoveLessonAction;

@Component
public class TimeTableManager {

  private final TimetableRepository timetableRepository;

  private final LabelManager labelManager;

  private final ButtonManager buttonManager;

  private final DragAndDropManager dragAndDropManager;

  public TimeTableManager(TimetableRepository timetableRepository, LabelManager labelManager, ButtonManager buttonManager,
      DragAndDropManager dragAndDropManager) {
    this.timetableRepository = timetableRepository;
    this.labelManager = labelManager;
    this.buttonManager = buttonManager;
    this.dragAndDropManager = dragAndDropManager;
  }

  public void reloadTimeTable(GridPane gridPane, String group) {
    gridPane.getChildren().clear();

    labelManager.addWeekdayHeaders(gridPane);
    labelManager.addLessonSlotsColumnToGrid(gridPane);

    TimeTable timeTable = timetableRepository.findTimeTable(group);

    populateWithLessons(gridPane, timeTable);
  }

  public void populateWithLessons(GridPane grid, TimeTable timeTable) {
    for (DayOfWeek day : DayOfWeek.values()) {
      for (LessonSlot lessonSlot : LessonSlot.values()) {
        Optional<Lesson> lessonForSlot = timeTable.findLessonForSlot(day, lessonSlot);
        if (lessonForSlot.isPresent()) {
          createLessonButton(grid, lessonForSlot.get(), timeTable.group());
        } else {
          createAddLessonButton(grid, timeTable.group(), day, lessonSlot);
        }
      }
    }
  }

  private void createLessonButton(GridPane gridPane, Lesson lesson, String group) {
    Button lessonButton = buttonManager.createDefaultButton();
    buttonManager.setupLessonButton(gridPane, lessonButton, group, lesson, getDeleteLessonButtonAction());
    dragAndDropManager.setupDragAndDrop(lessonButton, lesson.dayOfWeek(), lesson.lessonSlot());

    addNodeToGrid(gridPane, lessonButton, lesson.dayOfWeek(), lesson.lessonSlot());
  }

  private void createAddLessonButton(GridPane gridPane, String group, DayOfWeek day, LessonSlot lessonSlot) {
    Button plusLessonButton = buttonManager.createDefaultButton();
    plusLessonButton.setText("+");
    plusLessonButton.setOnAction(event -> showLessonCreationDialog(gridPane, day, lessonSlot, group, plusLessonButton));
    dragAndDropManager.setupDragAndDropTarget(gridPane, plusLessonButton, day, lessonSlot, group, getMoveLessonAction());

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
      Lesson lesson = new Lesson(new Course(courseField.getText()), new Teacher(teacherField.getText()), dayOfWeek, lessonSlot);
      buttonManager.setupLessonButton(gridPane, slotButton, group, lesson, getDeleteLessonButtonAction());
      timetableRepository.putLesson(group, lesson);
      dialog.close();
      reloadTimeTable(gridPane, group);
    });

    HBox dialogLayout = new HBox(10, courseField, teacherField, saveButton);
    dialogLayout.setAlignment(Pos.CENTER);

    dialog.setScene(new Scene(dialogLayout, 500, 100));
    dialog.setTitle("Додавання заняття");
    dialog.show();
  }

  private MoveLessonAction getMoveLessonAction() {
    return (GridPane gridPane, String group, DayOfWeek sourceDay, LessonSlot sourceSlot, DayOfWeek targetDay, LessonSlot targetSlot) -> {

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
    };
  }

  private DeleteLessonAction getDeleteLessonButtonAction() {
    return (GridPane gridPane, String group, Lesson lesson) -> {
      timetableRepository.deleteLesson(group, lesson);
      reloadTimeTable(gridPane, group);
    };
  }

  public void showNewScheduleDialog(Stage stage, GridPane grid, ComboBox<String> groupSelector) {
    Stage dialog = new Stage();
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.initOwner(stage);
    dialog.setTitle("Новий розклад");

    Label nameLabel = labelManager.buildDefaultLabel("Назва групи:");
    TextField groupNameField = new TextField();
    groupNameField.setMaxWidth(200);
    groupNameField.setStyle(DEFAULT_GRAY_STYLE);

    Button createButton = new Button("Додати");
    createButton.setStyle(DEFAULT_GRAY_STYLE);

    createButton.setOnAction(event -> {
      String groupName = groupNameField.getText().trim();
      if (!groupName.isEmpty()) {
        timetableRepository.createTimeTable(new TimeTable(List.of(), groupName));
        groupSelector.getItems().add(groupName);
        groupSelector.setValue(groupName);
        reloadTimeTable(grid, groupName);
        dialog.close();
      }
    });

    VBox dialogLayout = new VBox(10, nameLabel, groupNameField, createButton);
    dialogLayout.setAlignment(Pos.CENTER);
    Scene dialogScene = new Scene(dialogLayout, 300, 150);

    dialog.setScene(dialogScene);
    dialog.showAndWait();
  }

  public void showDeleteScheduleDialog(Stage stage, GridPane grid, ComboBox<String> groupSelector) {
    Stage dialog = new Stage();
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.initOwner(stage);
    dialog.setTitle("Видалити розклад");

    Label nameLabel = labelManager.buildDefaultLabel("Назва групи:");
    TextField groupNameField = new TextField();
    groupNameField.setMaxWidth(200);
    groupNameField.setStyle(DEFAULT_GRAY_STYLE);

    Button createButton = new Button("Видалити");
    createButton.setStyle(DEFAULT_GRAY_STYLE);

    createButton.setOnAction(event -> {
      String groupName = groupNameField.getText().trim();
      if (!groupName.isEmpty()) {
        timetableRepository.deleteTimeTable(groupName);
        groupSelector.getItems().removeIf(groupName::equals);
        groupSelector.setValue(groupSelector.getItems().getLast());
        dialog.close();
      }
    });

    VBox dialogLayout = new VBox(10, nameLabel, groupNameField, createButton);
    dialogLayout.setAlignment(Pos.CENTER);
    Scene dialogScene = new Scene(dialogLayout, 300, 150);

    dialog.setScene(dialogScene);
    dialog.showAndWait();
  }
}
