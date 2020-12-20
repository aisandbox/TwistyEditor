package dev.aisandbox.twistyeditor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CuboidController {

  @Getter
  private boolean commit = false;
  @Getter private int width;
  @Getter private int height;
  @Getter private int depth;

  @FXML
  private Spinner<Integer> widthSpinner;

  @FXML
  private Spinner<Integer> heightSpinner;

  @FXML
  private Spinner<Integer> depthSpinner;

  @FXML
  void cancel(ActionEvent event) {
    closeStage(event);
  }

  @FXML
  void generateCuboid(ActionEvent event) {
    log.info("Ok Button event");
    try {
      width=widthSpinner.getValue();
      height = heightSpinner.getValue();
      depth=depthSpinner.getValue();
      commit=true;
    } catch (NumberFormatException ex) {
      log.warn("Error parsing number");
    }
    closeStage(event);
  }

  private void closeStage(ActionEvent event) {
    Node source = (Node)  event.getSource();
    Stage stage  = (Stage) source.getScene().getWindow();
    stage.close();
  }

  @FXML
  void initialize() {
    assert widthSpinner != null : "fx:id=\"widthSpinner\" was not injected: check your FXML file 'cuboid.fxml'.";
    assert heightSpinner != null : "fx:id=\"heightSpinner\" was not injected: check your FXML file 'cuboid.fxml'.";
    assert depthSpinner != null : "fx:id=\"depthSpinner\" was not injected: check your FXML file 'cuboid.fxml'.";
    widthSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10,3)
        );
    depthSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10,3)
    );
    heightSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10,3)
    );
  }

}
