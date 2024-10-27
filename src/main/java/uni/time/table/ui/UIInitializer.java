package uni.time.table.ui;

import static uni.time.table.util.TimeTableAppUtil.TIME_TABLE_TITLE;

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
import uni.time.table.model.TimeTable;
import uni.time.table.repository.TimetableRepository;
import uni.time.table.ui.manager.LabelManager;
import uni.time.table.ui.manager.TimeTableManager;

@Component
public class UIInitializer {

  private final TimetableRepository timetableRepository;

  private final LabelManager labelManager;

  private final TimeTableManager timeTableManager;

  public UIInitializer(TimetableRepository timetableRepository,
      LabelManager labelManager,
      TimeTableManager timeTableManager) {

    this.timetableRepository = timetableRepository;
    this.labelManager = labelManager;
    this.timeTableManager = timeTableManager;
  }

  public void initUI(Stage stage) {
//    TimeTableAppUtil.getDemoTimeTables().forEach(timetableRepository::createTimeTable);
    List<TimeTable> timeTables = timetableRepository.findAllTimeTables();

    GridPane grid = new GridPane(10, 10);
    grid.setGridLinesVisible(true);
    grid.setAlignment(Pos.CENTER);

    ComboBox<String> groupSelector = new ComboBox<>();
    groupSelector.getItems().addAll(timeTables.stream().map(TimeTable::group).toList());
    groupSelector.setValue(timeTables.getFirst().group());
    groupSelector.setOnAction(actionEvent -> timeTableManager.reloadTimeTable(grid, groupSelector.getValue()));

    labelManager.addWeekdayHeaders(grid);
    labelManager.addLessonSlotsColumnToGrid(grid);
    timeTableManager.populateWithLessons(grid, timeTables.getFirst());

    MenuBar menuBar = new MenuBar();
    Menu scheduleMenu = new Menu("Розклад");
    MenuItem newScheduleItem = new MenuItem("Новий розклад");
    newScheduleItem.setOnAction(event -> timeTableManager.showNewScheduleDialog(stage, grid, groupSelector));
    MenuItem deleteScheduleItem = new MenuItem("Видалити розклад");
    deleteScheduleItem.setOnAction(event -> timeTableManager.showDeleteScheduleDialog(stage, grid, groupSelector));

    scheduleMenu.getItems().add(newScheduleItem);
    scheduleMenu.getItems().add(deleteScheduleItem);
    menuBar.getMenus().add(scheduleMenu);

    HBox layout = new HBox(10, groupSelector, grid);
    layout.setAlignment(Pos.CENTER);

    VBox root = new VBox(menuBar, layout);
    Scene scene = new Scene(root, 780, 740);

    stage.setScene(scene);
    stage.setTitle(TIME_TABLE_TITLE);
    stage.setFullScreen(false);

    stage.show();
  }
}
