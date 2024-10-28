package uni.time.table.ui;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.springframework.stereotype.Controller;
import uni.time.table.model.DayOfWeek;
import uni.time.table.model.Lesson;
import uni.time.table.model.LessonSlot;
import uni.time.table.model.Schedule;
import uni.time.table.repository.ScheduleRepository;
import uni.time.table.ui.service.TimetableDialogService;
import uni.time.table.ui.service.TimetableDragAndDropService;
import uni.time.table.ui.service.TimetableButtonService;
import uni.time.table.ui.service.TimetableLabelService;

@Controller
public class TimetableController {

  private final ScheduleRepository scheduleRepository;

  private final TimetableLabelService timetableLabelService;

  private final TimetableButtonService timetableButtonService;

  private final TimetableDragAndDropService dragAndDropService;

  private final TimetableDialogService timetableDialogService;

  public TimetableController(ScheduleRepository scheduleRepository, TimetableLabelService timetableLabelService,
      TimetableButtonService timetableButtonService, TimetableDragAndDropService dragAndDropService,
      TimetableDialogService timetableDialogService) {
    this.scheduleRepository = scheduleRepository;
    this.timetableLabelService = timetableLabelService;
    this.timetableButtonService = timetableButtonService;
    this.dragAndDropService = dragAndDropService;
    this.timetableDialogService = timetableDialogService;
  }

  public void reloadTimeTable(GridPane timetable, String group) {
    timetable.getChildren().clear();

    loadBaseTimeTable(timetable);

    scheduleRepository.find(group).ifPresent(schedule -> populateWithLessons(timetable, schedule));
  }

  public void populateWithLessons(GridPane timetable, Schedule schedule) {
    for (DayOfWeek day : DayOfWeek.values()) {
      for (LessonSlot lessonSlot : LessonSlot.values()) {
        Optional<Lesson> lessonForSlot = schedule.findLessonForSlot(day, lessonSlot);
        if (lessonForSlot.isPresent()) {
          createLessonButton(timetable, lessonForSlot.get(), schedule.group());
        } else {
          createAddLessonButton(timetable, schedule.group(), day, lessonSlot);
        }
      }
    }
  }

  public void loadBaseTimeTable(GridPane timetable) {
    timetableLabelService.addWeekdayHeaders(timetable);
    timetableLabelService.addLessonSlotsColumn(timetable);
  }

  private void createLessonButton(GridPane timetable, Lesson lesson, String group) {
    Button lessonButton = timetableButtonService.createDefaultButton();
    timetableButtonService.setupLessonButton(timetable, lessonButton, group, lesson, this::deleteLessonButtonAction);
    dragAndDropService.setupDragAndDrop(lessonButton, lesson.dayOfWeek(), lesson.lessonSlot());

    addNodeToGrid(timetable, lessonButton, lesson.dayOfWeek(), lesson.lessonSlot());
  }

  private void createAddLessonButton(GridPane timetable, String group, DayOfWeek day, LessonSlot lessonSlot) {
    Button plusLessonButton = timetableButtonService.createDefaultButton();
    plusLessonButton.setText("+");
    plusLessonButton.setOnAction(event ->
        timetableDialogService.showLessonCreationDialog(timetable, day, lessonSlot, group, plusLessonButton,
            this::reloadTimeTable, this::deleteLessonButtonAction));
    dragAndDropService.setupDragAndDropTarget(timetable, plusLessonButton, day, lessonSlot, group, this::moveLessonAction);

    addNodeToGrid(timetable, plusLessonButton, day, lessonSlot);
  }

  private void addNodeToGrid(GridPane timetable, Node node, DayOfWeek day, LessonSlot lessonSlot) {
    timetable.add(node, day.num(), lessonSlot.ordinal() + 1);
    GridPane.setHgrow(node, Priority.ALWAYS);
    GridPane.setVgrow(node, Priority.ALWAYS);
  }

  private void moveLessonAction(GridPane timetable, String group, DayOfWeek sourceDay, LessonSlot sourceSlot, DayOfWeek targetDay, LessonSlot targetSlot) {
    Optional<Schedule> schedule = scheduleRepository.find(group);
    Optional<Lesson> lessonOptional = schedule.flatMap(sh -> sh.findLessonForSlot(sourceDay, sourceSlot));

    lessonOptional.ifPresent(lesson -> {
      List<Lesson> updatedLessons = schedule.get().lessons().stream()
          .filter(l -> !(l.dayOfWeek() == sourceDay && l.lessonSlot() == sourceSlot))
          .toList();

      Lesson updatedLesson = new Lesson(lesson.course(), lesson.teacher(), targetDay, targetSlot);
      updatedLessons = Stream.concat(updatedLessons.stream(), Stream.of(updatedLesson)).toList();
      scheduleRepository.delete(group);
      scheduleRepository.create(new Schedule(updatedLessons, group));
      reloadTimeTable(timetable, group);
    });
  }

  private void deleteLessonButtonAction(GridPane timetable, String group, Lesson lesson) {
    scheduleRepository.deleteLesson(group, lesson);
    reloadTimeTable(timetable, group);
  }

}
