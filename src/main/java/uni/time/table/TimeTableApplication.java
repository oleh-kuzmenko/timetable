package uni.time.table;


import javafx.application.Application;
import javafx.stage.Stage;
import uni.time.table.repository.TimetableRepository;
import uni.time.table.repository.implementation.FileTimeTableRepository;
import uni.time.table.ui.LessonManager;
import uni.time.table.ui.LabelManager;
import uni.time.table.ui.UIInitializer;

public class TimeTableApplication extends Application {


  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    TimetableRepository timetableRepository = new FileTimeTableRepository();
    LabelManager labelManager = new LabelManager();
    LessonManager lessonManager = new LessonManager(timetableRepository, labelManager);

    UIInitializer initializer = new UIInitializer(timetableRepository, labelManager, lessonManager);
    initializer.initUI(stage);
  }

}
