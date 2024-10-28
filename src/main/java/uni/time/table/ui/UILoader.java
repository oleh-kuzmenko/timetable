package uni.time.table.ui;


import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import uni.time.table.model.Schedule;
import uni.time.table.repository.ScheduleRepository;
import uni.time.table.ui.service.TimetableDialogService;
import uni.time.table.util.TimeTableAppUtil;

@Component
public class UILoader {

  private static final String TIME_TABLE_TITLE = "Розклад занять";
  private static final String NEW_TIME_TABLE_TITLE = "Новий розклад";
  private static final String DELETE_TIME_TABLE_TITLE = "Видалити розклад";
  private static final String TIME_TABLE_MENU_TITLE = "Розклад";

  private final ScheduleRepository scheduleRepository;

  private final TimetableDialogService timetableDialogService;
  private final TimetableController timeTableController;

  public UILoader(ScheduleRepository scheduleRepository, TimetableDialogService timetableDialogService, TimetableController timeTableController) {
    this.scheduleRepository = scheduleRepository;
    this.timetableDialogService = timetableDialogService;
    this.timeTableController = timeTableController;
  }

  public void load(Stage stage) {
    GridPane timetable = new GridPane(10, 10);
    timetable.setGridLinesVisible(true);
    timetable.setAlignment(Pos.CENTER);

    List<Schedule> schedules = loadTimeTables();
    ComboBox<String> groupSelector = new ComboBox<>();
    groupSelector.getItems().addAll(schedules.stream().map(Schedule::group).toList());
    groupSelector.setValue(schedules.getFirst().group());
    groupSelector.setOnAction(event -> timeTableController.reloadTimeTable(timetable, groupSelector.getValue()));

    timeTableController.loadBaseTimeTable(timetable);
    timeTableController.populateWithLessons(timetable, schedules.getFirst());

    MenuItem newSchedule = new MenuItem(NEW_TIME_TABLE_TITLE);
    newSchedule.setOnAction(event -> timetableDialogService.showNewScheduleDialog(stage, timetable, groupSelector,
        timeTableController::reloadTimeTable));

    MenuItem deleteScheduleItem = new MenuItem(DELETE_TIME_TABLE_TITLE);
    deleteScheduleItem.setOnAction(event -> timetableDialogService.showDeleteScheduleDialog(stage, groupSelector));

    Menu schedule = new Menu(TIME_TABLE_MENU_TITLE);
    schedule.getItems().addAll(newSchedule, deleteScheduleItem);

    MenuBar menu = new MenuBar();
    menu.getMenus().add(schedule);

    HBox layout = new HBox(10, groupSelector, timetable);
    layout.setAlignment(Pos.CENTER);

    VBox root = new VBox(menu, layout);
    Scene scene = new Scene(root, 780, 740);

    stage.setScene(scene);
    stage.setTitle(TIME_TABLE_TITLE);
    stage.setFullScreen(false);

    stage.show();
  }

  private List<Schedule> loadTimeTables() {
    List<Schedule> schedules = scheduleRepository.findAll();
    if (schedules.isEmpty()) {
      TimeTableAppUtil.getDemoTimeTables().forEach(scheduleRepository::create);
      return scheduleRepository.findAll();
    }
    return schedules;
  }
}
