package dev.aisandbox.twistyeditor;

import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Launcher {
  public static void main(String[] args) {
    log.info("Starting Main class");
    Application.launch(TwistyeditorApplication.class, args);
  }
}
