package uni.time.table.ui.service;

import static uni.time.table.util.TimeTableAppUtil.DEFAULT_FONT;
import static uni.time.table.util.TimeTableAppUtil.DEFAULT_GRAY_STYLE;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Service;
import uni.time.table.model.Course;
import uni.time.table.model.DayOfWeek;
import uni.time.table.model.Lesson;
import uni.time.table.model.LessonSlot;
import uni.time.table.model.Schedule;
import uni.time.table.model.Teacher;
import uni.time.table.repository.ScheduleRepository;
import uni.time.table.ui.action.DeleteLessonAction;
import uni.time.table.ui.action.ReloadTimetableAction;

@Service
public class TimetableDialogService {

  private final ScheduleRepository scheduleRepository;
  private final TimetableLabelService timetableLabelService;
  private final TimetableButtonService timetableButtonService;

  public TimetableDialogService(ScheduleRepository scheduleRepository, TimetableLabelService timetableLabelService,
      TimetableButtonService timetableButtonService) {
    this.scheduleRepository = scheduleRepository;
    this.timetableLabelService = timetableLabelService;
    this.timetableButtonService = timetableButtonService;
  }

  public void showDeleteScheduleDialog(Stage stage, ComboBox<String> groupSelector) {
    Stage dialog = new Stage();
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.initOwner(stage);
    dialog.setTitle("Видалити розклад");

    Label nameLabel = timetableLabelService.buildDefaultLabel("Назва групи:");
    TextField groupNameField = new TextField();
    groupNameField.setMaxWidth(200);
    groupNameField.setStyle(DEFAULT_GRAY_STYLE);

    Button createButton = new Button("Видалити");
    createButton.setStyle(DEFAULT_GRAY_STYLE);

    createButton.setOnAction(event -> {
      String groupName = groupNameField.getText().trim();
      if (!groupName.isEmpty()) {
        scheduleRepository.delete(groupName);
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

  public void showNewScheduleDialog(Stage stage, GridPane timetable, ComboBox<String> groupSelector, ReloadTimetableAction timetableAction) {
    Stage dialog = new Stage();
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.initOwner(stage);
    dialog.setTitle("Новий розклад");

    Label nameLabel = timetableLabelService.buildDefaultLabel("Назва групи:");
    TextField groupNameField = new TextField();
    groupNameField.setMaxWidth(200);
    groupNameField.setStyle(DEFAULT_GRAY_STYLE);

    Button createButton = new Button("Додати");
    createButton.setStyle(DEFAULT_GRAY_STYLE);

    createButton.setOnAction(event -> {
      String groupName = groupNameField.getText().trim();
      if (!groupName.isEmpty()) {
        scheduleRepository.create(new Schedule(List.of(), groupName));
        groupSelector.getItems().add(groupName);
        groupSelector.setValue(groupName);
        timetableAction.reload(timetable, groupName);
        dialog.close();
      }
    });

    VBox dialogLayout = new VBox(10, nameLabel, groupNameField, createButton);
    dialogLayout.setAlignment(Pos.CENTER);
    Scene dialogScene = new Scene(dialogLayout, 300, 150);

    dialog.setScene(dialogScene);
    dialog.showAndWait();
  }

  public void showLessonCreationDialog(GridPane timetable, DayOfWeek dayOfWeek, LessonSlot lessonSlot, String group, Button slotButton,
      ReloadTimetableAction timetableAction, DeleteLessonAction lessonAction) {
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
      timetableButtonService.setupLessonButton(timetable, slotButton, group, lesson, lessonAction);
      scheduleRepository.putLesson(group, lesson);
      dialog.close();
      timetableAction.reload(timetable, group);
    });

    HBox dialogLayout = new HBox(10, courseField, teacherField, saveButton);
    dialogLayout.setAlignment(Pos.CENTER);

    dialog.setScene(new Scene(dialogLayout, 500, 100));
    dialog.setTitle("Додавання заняття");
    dialog.show();
  }
}
