package uni.time.table;


import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import uni.time.table.ui.UIInitializer;

public class TimeTableApplication extends Application {


  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext("uni.time.table");
    UIInitializer initializer = applicationContext.getBean(UIInitializer.class);
    initializer.initUI(stage);
  }

}
