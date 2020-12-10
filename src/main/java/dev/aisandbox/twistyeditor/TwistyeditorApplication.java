package dev.aisandbox.twistyeditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class TwistyeditorApplication extends Application {

  private ConfigurableApplicationContext context;
  private Parent rootNode;

  @Override
  public void init() throws Exception {
    log.info("Initialising application - FX");
    SpringApplicationBuilder builder = new SpringApplicationBuilder(TwistyeditorApplication.class);
    builder.headless(false);
    context = builder.run(getParameters().getRaw().toArray(new String[0]));
    // load the root FXML screen, using spring to create the controller
    FXMLLoader loader =
        new FXMLLoader(getClass().getResource("/ui.fxml"));
    loader.setControllerFactory(context::getBean);
    rootNode = loader.load();
  }

  /** {@inheritDoc} */
  @Override
  public void start(Stage primaryStage) throws Exception {
    log.info("Starting application - FX");
    primaryStage.setScene(new Scene(rootNode, 800, 600));
    primaryStage.centerOnScreen();
    primaryStage.setTitle("Twisty Puzzle Editor");
    primaryStage.show();
  }

  /** {@inheritDoc} */
  @Override
  public void stop() throws Exception {
    log.info("Stopping application");
    context.close();
    System.exit(0);
  }

}
