package uni.time.table;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uni.time.table.model.Course;
import uni.time.table.model.Lesson;
import uni.time.table.model.LessonSlot;
import uni.time.table.model.Teacher;
import uni.time.table.model.TimeTable;
import uni.time.table.service.TimetableService;
import uni.time.table.service.implementation.InMemoryTimeTableService;
import uni.time.table.util.TimeTableAppUtil;

public class TimeTableApplication extends Application {

  private TimetableService timetableService = new InMemoryTimeTableService();
  private GridPane grid = new GridPane();

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    TimeTable defaultTimeTable = createTestTimeTable();
    timetableService.createTimeTable(defaultTimeTable);

    stage.setTitle(TimeTableAppUtil.TIME_TABLE_TITLE);

    ComboBox<String> groupSelector = new ComboBox<>();
    groupSelector.getItems().addAll(defaultTimeTable.group());
    groupSelector.setValue(defaultTimeTable.group());

    grid.setAlignment(Pos.CENTER);
    grid.setHgap(120);
    grid.setVgap(120);

    // Set up day labels
    for (DayOfWeek day : DayOfWeek.values()) {
      if (day.getValue() < 6) {
        Label label = new Label(day.toString());
        grid.add(label, day.getValue(), 0);
      }
    }

    // Set up time slot labels
    for (LessonSlot slot : LessonSlot.values()) {
      Label timeSlotLabel = new Label(slot.getFrom() + " - " + slot.getTo());
      grid.add(timeSlotLabel, 0, slot.ordinal() + 1);
    }

    // Add the plus buttons or the lesson details for each slot
    for (int day = 1; day <= 5; day++) {
      for (int slot = 1; slot <= 6; slot++) {
        Button slotButton = new Button("+");
        int finalDay = day;
        int finalSlot = slot;

        // If there's already a lesson in this slot, show it
        TimeTable timeTable = timetableService.findTimeTable(groupSelector.getValue());
        Lesson existingLesson = findLessonForSlot(timeTable.lessons(), finalDay, finalSlot);
        if (existingLesson != null) {
          slotButton.setText(existingLesson.course().name() + "\n" + existingLesson.teacher().name());
        } else {
          slotButton.setOnAction(event -> showLessonCreationDialog(finalDay, finalSlot, groupSelector.getValue(), slotButton));
        }

        GridPane.setHgrow(slotButton, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setVgrow(slotButton, javafx.scene.layout.Priority.ALWAYS);

        grid.add(slotButton, day, slot);
      }
    }

    HBox layout = new HBox(10, groupSelector, grid);
    layout.setAlignment(Pos.CENTER);
    Scene scene = new Scene(layout, 1600, 1200);

    stage.setScene(scene);
    stage.show();
  }

  private void showLessonCreationDialog(int day, int slot, String group, Button slotButton) {
    Stage dialog = new Stage();
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.setTitle("Create Lesson");

    TextField courseField = new TextField();
    courseField.setPromptText("Course Name");

    TextField teacherField = new TextField();
    teacherField.setPromptText("Teacher Name");

    Button saveButton = new Button("Save");
    saveButton.setOnAction(event -> {
      String courseName = courseField.getText();
      String teacherName = teacherField.getText();
      Lesson lesson = new Lesson(new Course(courseName), new Teacher(teacherName), DayOfWeek.of(day), LessonSlot.values()[slot - 1]);

      // Update the timeTable with the new lesson
      TimeTable timeTable = timetableService.findTimeTable(group);
      List<Lesson> updatedLessons = Stream.concat(timeTable.lessons().stream(), Stream.of(lesson)).toList();
      timetableService.createTimeTable(new TimeTable(updatedLessons, group));

      // Update the button text to show the lesson details
      slotButton.setText(lesson.course().name() + "\n" + lesson.teacher().name());

      dialog.close();
    });

    HBox dialogLayout = new HBox(10, courseField, teacherField, saveButton);
    dialogLayout.setAlignment(Pos.CENTER);
    Scene dialogScene = new Scene(dialogLayout, 800, 400);
    dialog.setScene(dialogScene);
    dialog.show();
  }

  private Lesson findLessonForSlot(List<Lesson> lessons, int day, int slot) {
    for (Lesson lesson : lessons) {
      if (lesson.dayOfWeek().getValue() == day && lesson.lessonSlot().ordinal() == slot - 1) {
        return lesson;
      }
    }
    return null; // No lesson found for this slot
  }

  private TimeTable createTestTimeTable() {
    return new TimeTable(
        List.of(
            new Lesson(new Course("Math"),
                new Teacher("Koval Oleksandr"),
                DayOfWeek.MONDAY,
                LessonSlot.FIRST)),
        "IT-33");
  }
}
