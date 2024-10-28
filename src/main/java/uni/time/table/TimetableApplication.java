package uni.time.table;


import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import uni.time.table.ui.UILoader;

public class TimetableApplication extends Application {


  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext("uni.time.table");
    UILoader initializer = applicationContext.getBean(UILoader.class);
    initializer.load(stage);
  }

}
