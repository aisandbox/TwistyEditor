package dev.aisandbox.twistyeditor;

import com.thoughtworks.xstream.XStream;
import dev.aisandbox.twistyeditor.model.Cell;
import dev.aisandbox.twistyeditor.model.ColourEnum;
import dev.aisandbox.twistyeditor.model.CuboidBuilder;
import dev.aisandbox.twistyeditor.model.Loop;
import dev.aisandbox.twistyeditor.model.Move;
import dev.aisandbox.twistyeditor.model.Puzzle;
import dev.aisandbox.twistyeditor.model.shapes.ShapeEnum;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UIController {

  Random rand = new Random();

  Puzzle puzzle = new Puzzle();

  ObservableList<Cell> cellObservableList;
  ObservableList<Move> moveObservableList;

  Cell selectedCell = null;
  Move selectedMove = null;
  Loop selectedLoop = null;

  @Autowired Settings settings;

  @FXML private ListView<Cell> cellList;

  @FXML private Label puzzleName;

  @FXML private Label editorInfo;

  @FXML private TextField cellIDField;

  @FXML private ImageView puzzleCellImage;

  @FXML private ImageView puzzleMoveImage;

  @FXML private TextField rotationField;

  @FXML private TextField scaleField;

  @FXML private ChoiceBox<ShapeEnum> cellShapeField;

  @FXML private ChoiceBox<ColourEnum> colourChooser;

  @FXML private TextField locationXField;

  @FXML private TextField locationYField;

  @FXML private ListView<Move> moveList;

  @FXML private TextField moveName;

  @FXML private ListView<Loop> loopList;

  @FXML private ImageView moveIcon;
  @FXML private Spinner<Integer> moveCostSpinner;

  @FXML
  void addCell(ActionEvent event) {
    log.info("Adding new cell");
    Cell c = new Cell();
    c.setLocationX(rand.nextInt(200));
    c.setLocationY(100);
    c.setRotation(0);
    c.setScale(50);
    c.setShape(ShapeEnum.SQUARE);
    c.setColour(ColourEnum.BLUE);
    cellObservableList.add(c);
    cellList.scrollTo(c);
    cellList.getSelectionModel().select(c);
  }

  @FXML
  void savePuzzle(ActionEvent event) {
    // compile the puzzle
    log.info("Compiling the moves before saving");
    Optional<String> warnings = puzzle.compileMoves();
    if (warnings.isPresent()) {
      log.warn("Warnings generated during compile");
      Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("Puzzle Warning");
      alert.setHeaderText("Warnings when compiling puzzle.");
      alert.setContentText(warnings.get());
      alert.showAndWait();
    }
    // get the stage from the events
    Window window = ((MenuItem) event.getTarget()).getParentPopup().getScene().getWindow();
    // show a file chooser
    FileChooser fileChooser = new FileChooser();
    // Set extension filter for text files
    FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("Twisty puzzzles (*.tp)", "*.tp");
    fileChooser.getExtensionFilters().add(extFilter);
    // Show save file dialog
    File file = fileChooser.showSaveDialog(window);
    if (file != null) {
      try {
        log.info("Saving to {}", file.getAbsolutePath());
        XStream xstream = PuzzleUtil.getCodec();
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(xstream.toXML(puzzle));
        out.close();
      } catch (IOException e) {
        log.error("Error saving puzzle", e);
      }
    }
  }

  @FXML
  void generateIcon(ActionEvent event) {
    if (selectedMove != null) {
      // create a new icon
      BufferedImage image = PuzzleUtil.getMoveIcon(selectedMove.getName());
      selectedMove.setImageIcon(image);
      moveIcon.setImage(SwingFXUtils.toFXImage(image, null));
    }
  }

  @FXML
  void importSpritesheet(ActionEvent event) {
    log.info("loading spritesheet");
    // get the stage from the events
    Window window = ((MenuItem) event.getTarget()).getParentPopup().getScene().getWindow();
    // show a file chooser
    FileChooser fileChooser = new FileChooser();
    // Set extension filter for image
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG Image", "*.png");
    fileChooser.getExtensionFilters().add(extFilter);
    // Show save file dialog
    File file = fileChooser.showOpenDialog(window);
    if (file != null) {
      try {
        BufferedImage image = ImageIO.read(file);
        puzzle.setSpritesheet(image);
      } catch (IOException e) {
        log.error("Error reading spritesheet", e);
      }
    }
  }

  @FXML
  void exportSpritesheet(ActionEvent event) {
    log.info("Exporting spritesheet");
    // get the stage from the events
    Window window = ((MenuItem) event.getTarget()).getParentPopup().getScene().getWindow();
    // show a file chooser
    FileChooser fileChooser = new FileChooser();
    // Set extension filter for image
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG Image", "*.png");
    fileChooser.getExtensionFilters().add(extFilter);
    // Show save file dialog
    File file = fileChooser.showSaveDialog(window);
    if (file != null) {
      try {
        ImageIO.write(puzzle.getSpritesheet(), "png", file);
      } catch (IOException e) {
        log.error("Error writing spritesheet", e);
      }
    }
  }

  @FXML
  void loadPuzzle(ActionEvent event) {
    // get the stage from the events
    Window window = ((MenuItem) event.getTarget()).getParentPopup().getScene().getWindow();
    // show a file chooser
    FileChooser fileChooser = new FileChooser();
    // Set extension filter for text files
    FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("Twisty puzzzles (*.tp)", "*.tp");
    fileChooser.getExtensionFilters().add(extFilter);
    // Show save file dialog
    File file = fileChooser.showOpenDialog(window);
    if (file != null) {
      try {
        log.info("Loading puzzle from {}", file.getAbsolutePath());
        XStream xstream = PuzzleUtil.getCodec();
        puzzle = (Puzzle) xstream.fromXML(file);
        log.info("Puzzle loaded");
        log.info("Loaded puzzle with {} cells", puzzle.getCells().size());
        // re-point cell listing
        cellObservableList = FXCollections.observableList(puzzle.getCells());
        cellList.setItems(cellObservableList);
        // re-point move listing
        moveObservableList = FXCollections.observableList(puzzle.getMoves());
        moveList.setItems(moveObservableList);
      } catch (Exception e) {
        log.error("Error loading puzzle", e);
      }
    }
    updateUI();
  }

  @FXML
  void addMove(ActionEvent event) {
    log.info("Adding move");
    Move move = new Move();
    move.setName("Name");
    moveObservableList.add(move);
    moveList.scrollTo(move);
    moveList.getSelectionModel().select(move);
  }

  @FXML
  void addLoop(ActionEvent event) {
    // add a loop to the current move
    if (selectedMove != null) {
      Loop loop = new Loop();
      selectedMove.getLoops().add(loop);
      // add to the list of loops
      loopList.getItems().add(loop);
      // select this loop
      loopList.scrollTo(loop);
      loopList.getSelectionModel().select(loop);
      updateUI();
    }
  }

  @FXML
  void addCellToLoop(MouseEvent event) {
    double x = event.getX();
    double y = event.getY();
    log.info("Button click @ {},{}", x, y);
    if (selectedLoop != null) {
      // work out which cell this is
      Cell cell = puzzle.findCell(x, y);
      if ((cell != null) && (!selectedLoop.getCells().contains(cell))) {
        selectedLoop.getCells().add(cell);
        updateUI();
      }
    }
  }

  @FXML
  void removeLoop(ActionEvent event) {
    if (selectedLoop != null) {
      Loop loop = selectedLoop; // cache
      loopList.getItems().removeAll(loop);
      selectedMove.getLoops().remove(loop);
      updateUI();
    }
  }

  @FXML
  void flipLoop(ActionEvent event) {
    if (selectedLoop != null) {
      log.info("Reversing loop {}", selectedLoop);
      Collections.reverse(selectedLoop.getCells());
      updateUI();
    }
  }

  @FXML
  void copyMove(ActionEvent event) {
    if (selectedMove != null) {
      // duplicate move
      Move m2 = new Move();
      m2.setName(selectedMove.getName() + " - copy");
      // duplicate loops
      for (Loop loop : selectedMove.getLoops()) {
        Loop l2 = new Loop();
        for (Cell c : loop.getCells()) {
          l2.getCells().add(c);
        }
        m2.getLoops().add(l2);
      }
      // add move to puzzle
      moveObservableList.add(m2);
      moveList.scrollTo(m2);
      moveList.getSelectionModel().select(m2);
    }
  }

  @FXML
  void delMove(ActionEvent event) {
    if (selectedMove != null) {
      moveObservableList.remove(selectedMove);
    }
  }

  @FXML
  void centerCells(ActionEvent event) {
    log.info("Centering cells");
    puzzle.centerCells();
    updateUI();
  }

  @FXML
  void selectCell(MouseEvent event) {
    // work out which cell was clicked
    double x = event.getX();
    double y = event.getY();
    log.info("Button click @ {},{}", x, y);
    Cell cell = puzzle.findCell(x, y);
    if (cell != null) {
      cellList.scrollTo(cell);
      cellList.getSelectionModel().select(cell);
    }
  }

  @FXML
  void addPyramid(ActionEvent event) {
    try {
      // ask how many levels
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/pyramid.fxml"));
      Parent parent = fxmlLoader.load();
      PyramidController dialogController = fxmlLoader.<PyramidController>getController();
      // Scene scene = new Scene(parent, 400, 300);
      Scene scene = new Scene(parent);
      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setScene(scene);
      stage.showAndWait();
      if (dialogController.isCommit()) {
        // clear old puzzle
        resetPuzzle(event);
        int scale = dialogController.getScale();
        double dx = scale * Math.cos(Math.toRadians(30));
        double dy = scale * Math.sin(Math.toRadians(30)) + scale;
        double stepy = scale - scale * Math.cos(Math.toRadians(60));
        // draw pyramid
        for (int level = 0; level < dialogController.getLevels(); level++) {
          boolean up = true;
          for (int x = -level; x <= level; x++) {
            // create triangle
            log.info("Adding new cell {},{}", x, level);
            Cell c = new Cell();
            c.setLocationX((int) (x * dx));
            c.setLocationY((int) (level * dy - (up ? 0 : stepy)));
            c.setRotation(up ? 90 : -90);
            c.setScale(dialogController.getScale());
            c.setShape(ShapeEnum.EQ_TRIANGLE);
            c.setColour(ColourEnum.BLUE);
            cellObservableList.add(c);
            up = !up;
          }
        }
        // center pyramid
        centerCells(event);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  void addCuboid(ActionEvent event) {
    try {
      // ask how many levels
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cuboid.fxml"));
      Parent parent = fxmlLoader.load();
      CuboidController dialogController = fxmlLoader.<CuboidController>getController();
      // Scene scene = new Scene(parent, 400, 300);
      Scene scene = new Scene(parent);
      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setScene(scene);
      stage.showAndWait();
      if (dialogController.isCommit()) {
        // clear old puzzle
        resetPuzzle(event);
        // create cube
        CuboidBuilder builder =
            new CuboidBuilder(
                cellObservableList,
                moveObservableList,
                dialogController.getWidth(),
                dialogController.getHeight(),
                dialogController.getDepth());
        builder.createCuboid();
        // center cells
        centerCells(event);
        // update lists

      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  void moveCellDown(ActionEvent event) {
    if (selectedCell != null) {
      // save the cell
      Cell target = selectedCell;
      // work out the current index
      int index = cellObservableList.indexOf(selectedCell);
      if (index < cellObservableList.size() - 1) {
        Collections.swap(cellObservableList, index, index + 1);
      }
      // reselect the original cell
      cellList.scrollTo(target);
      cellList.getSelectionModel().select(target);
    }
  }

  @FXML
  void moveCellUp(ActionEvent event) {
    if (selectedCell != null) {
      // save the cell
      Cell target = selectedCell;
      // work out the current index
      int index = cellObservableList.indexOf(selectedCell);
      if (index > 0) {
        Collections.swap(cellObservableList, index, index - 1);
      }
      // reselect the original cell
      cellList.scrollTo(target);
      cellList.getSelectionModel().select(target);
    }
  }

  @FXML
  public void resetPuzzle(ActionEvent event) {
    // remove all moves
    moveObservableList.clear();
    // remove all cells
    cellObservableList.clear();
    // update UI
    updateUI();
  }

  @FXML
  void initialize() {
    assert cellList != null
        : "fx:id=\"cellList\" was not injected: check your FXML file 'ui.fxml'.";
    assert puzzleName != null
        : "fx:id=\"puzzleName\" was not injected: check your FXML file 'ui.fxml'.";
    assert editorInfo != null
        : "fx:id=\"editorInfo\" was not injected: check your FXML file 'ui.fxml'.";
    // setup loops (dont try to bind)

    // setup move views
    moveCostSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10,1)
    );
    moveObservableList = FXCollections.observableList(puzzle.getMoves());
    moveList.setItems(moveObservableList);
    moveList
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            ((observableValue, oldMove, newMove) -> {
              log.info("Selected move {}", newMove);
              if (newMove == null) {
                selectedMove = null;
                moveName.setText("");
                loopList.getItems().clear();
                moveIcon.setImage(
                    SwingFXUtils.toFXImage(
                        new BufferedImage(
                            Move.MOVE_ICON_WIDTH,
                            Move.MOVE_ICON_HEIGHT,
                            BufferedImage.TYPE_INT_RGB),
                        null));
              } else {
                puzzleCellImage.setImage(
                    SwingFXUtils.toFXImage(puzzle.getCellImage(selectedCell), null));
                selectedMove = newMove;
                moveName.setText(newMove.getName());
                loopList.getItems().clear();
                loopList.getItems().addAll(selectedMove.getLoops());
                moveIcon.setImage(SwingFXUtils.toFXImage(newMove.getImageIcon(), null));
                moveCostSpinner.getValueFactory().setValue(selectedMove.getCost());
              }
            }));
    moveName
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // get selected cell
              if (selectedMove != null) {
                selectedMove.setName(newValue);
                updateUI();
              }
            });
    // setup loops
    loopList
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (((observableValue, oldLoop, newLoop) -> {
              log.info("Selected loop {}", newLoop);
              selectedLoop = newLoop;
              updateUI();
            })));
    // setup cell views
    cellObservableList = FXCollections.observableList(puzzle.getCells());
    cellList.setItems(cellObservableList);
    cellList
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observableValue, oldCell, newCell) -> {
              if (newCell == null) {
                selectedCell = null;
                cellIDField.setText("");
                cellShapeField.getSelectionModel().select(null);
                locationXField.setText("");
                locationYField.setText("");
                rotationField.setText("");
                scaleField.setText("");
              } else {
                log.info("New selection {}", newCell);
                selectedCell = newCell;
                cellIDField.setText(Integer.toString(puzzle.getCells().indexOf(newCell)));
                cellShapeField.getSelectionModel().select(newCell.getShape());
                locationXField.setText(Integer.toString(newCell.getLocationX()));
                locationYField.setText(Integer.toString(newCell.getLocationY()));
                rotationField.setText(Integer.toString(newCell.getRotation()));
                scaleField.setText(Integer.toString(newCell.getScale()));
                colourChooser.getSelectionModel().select(newCell.getColour());
              }
              // update drawing
              updateUI();
            });
    // setup shape choice
    cellShapeField.setItems(FXCollections.observableArrayList(ShapeEnum.values()));
    cellShapeField
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observableVAlue, oldShape, newShape) -> {
              // get selected cell
              if (selectedCell != null) {
                selectedCell.setShape(newShape);
              }
              updateUI();
            });
    // setup colour choice
    colourChooser.setItems(FXCollections.observableArrayList(ColourEnum.values()));
    colourChooser
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            ((observableValue, oldval, newval) -> {
              if (selectedCell != null) {
                selectedCell.setColour(newval);
              }
              updateUI();
            }));
    // setup update locations
    addDigitTextFormat(locationXField);
    addDigitTextFormat(locationYField);
    locationXField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // get selected cell
              if (selectedCell != null) {
                selectedCell.setLocationX(Integer.parseInt(newValue));
                updateUI();
              }
            });
    locationYField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // get selected cell
              if (selectedCell != null) {
                selectedCell.setLocationY(Integer.parseInt(newValue));
                updateUI();
              }
            });
    // scale and rotation
    addDigitTextFormat(rotationField);
    addDigitTextFormat(scaleField);
    rotationField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (selectedCell != null) {
                selectedCell.setRotation(Integer.parseInt(newValue));
                updateUI();
              }
            });
    scaleField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // get selected cell
              Cell cell = cellList.getSelectionModel().getSelectedItem();
              if (selectedCell != null) {
                selectedCell.setScale(Integer.parseInt(newValue));
                updateUI();
              }
            });
    moveCostSpinner.getValueFactory().valueProperty().addListener(
        (observable,oldValue,newValue) -> {
          if (selectedMove!=null) {
            selectedMove.setCost(newValue);
          }
        }
    );
    // report version
    editorInfo.setText(settings.getBuildVersion() + " - " + settings.getBuildDate());
  }

  private void updateUI() {
    // redraw the cell list
    cellList.refresh();
    // redraw the puzzle cell diagram
    puzzleCellImage.setImage(SwingFXUtils.toFXImage(puzzle.getCellImage(selectedCell), null));
    // redraw the move list
    moveList.refresh();
    // redraw the move cell diagram
    puzzleMoveImage.setImage(
        SwingFXUtils.toFXImage(
            puzzle.getMoveImage(selectedMove, loopList.getSelectionModel().getSelectedItem()),
            null));
    // redraw the loop list
    loopList.refresh();
  }

  public static void addDigitTextFormat(TextField textField) {
    DecimalFormat format = new DecimalFormat("#");
    final TextFormatter<Object> decimalTextFormatter =
        new TextFormatter<>(
            change -> {
              if (change.getControlNewText().isEmpty()) {
                return change;
              }
              ParsePosition parsePosition = new ParsePosition(0);
              Object object = format.parse(change.getControlNewText(), parsePosition);

              if (object == null
                  || parsePosition.getIndex() < change.getControlNewText().length()) {
                return null;
              } else {
                return change;
              }
            });
    textField.setTextFormatter(decimalTextFormatter);
  }
}
