package uni.time.table.ui;

import static uni.time.table.util.TimeTableAppUtil.TIME_TABLE_TITLE;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import uni.time.table.model.TimeTable;
import uni.time.table.repository.TimetableRepository;

public class UIInitializer {

  private final TimetableRepository timetableRepository;

  private final LabelManager labelManager;

  private final LessonManager lessonManager;

  public UIInitializer(TimetableRepository timetableRepository,
      LabelManager labelManager,
      LessonManager lessonManager) {

    this.timetableRepository = timetableRepository;
    this.labelManager = labelManager;
    this.lessonManager = lessonManager;
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
    groupSelector.setOnAction(actionEvent -> lessonManager.reloadTimeTable(grid, groupSelector.getValue()));

    labelManager.addWeekdayHeaders(grid);
    labelManager.addLessonSlotsColumnToGrid(grid);
    lessonManager.populateWithLessons(grid, timeTables.getFirst());

    HBox layout = new HBox(10, groupSelector, grid);
    layout.setAlignment(Pos.CENTER);

    stage.setScene(new Scene(layout, 800, 900));
    stage.setTitle(TIME_TABLE_TITLE);
    stage.setFullScreen(false);

    stage.show();
  }
}