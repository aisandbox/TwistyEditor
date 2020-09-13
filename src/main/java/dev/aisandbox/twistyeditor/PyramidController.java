package dev.aisandbox.twistyeditor;

import static dev.aisandbox.twistyeditor.UIController.addDigitTextFormat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PyramidController {

  @Getter private boolean commit = false;
  @Getter private int levels;
  @Getter private int scale;

  @FXML
  private TextField levelField;

  @FXML
  private TextField scaleField;

  @FXML
  void okButton(ActionEvent event) {
    log.info("Ok Button event");
    try {
      levels = Integer.parseInt(levelField.getText());
      scale = Integer.parseInt(scaleField.getText());
      commit=true;
    } catch (NumberFormatException ex) {
      log.warn("Error parsing number");
    }
    closeStage(event);
  }

  @FXML
  void cancelButton(ActionEvent event) {
    closeStage(event);
  }

  private void closeStage(ActionEvent event) {
    Node source = (Node)  event.getSource();
    Stage stage  = (Stage) source.getScene().getWindow();
    stage.close();
  }

  @FXML
  void initialize() {
    assert levelField != null : "fx:id=\"levelField\" was not injected: check your FXML file 'pyramid.fxml'.";
    assert scaleField != null : "fx:id=\"scaleField\" was not injected: check your FXML file 'pyramid.fxml'.";
    addDigitTextFormat(levelField);
    addDigitTextFormat(scaleField);
  }

}
