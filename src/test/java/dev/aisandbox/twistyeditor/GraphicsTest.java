package dev.aisandbox.twistyeditor;

import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.aisandbox.twistyeditor.model.Cell;
import dev.aisandbox.twistyeditor.model.CuboidBuilder;
import dev.aisandbox.twistyeditor.model.Move;
import dev.aisandbox.twistyeditor.model.Puzzle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GraphicsTest {

  static Map<String, Puzzle> puzzles;

  @BeforeAll
  public static void setupPuzzles() throws IOException {
    puzzles = new HashMap<>();
    for (int width : List.of(2, 3, 4, 5, 10)) {
      for (int depth : List.of(2, 3, 4, 5, 10)) {
        for (int height : List.of(2, 3, 4, 5, 10)) {
          Puzzle model = new Puzzle();
          ObservableList<Cell> cells = FXCollections.observableList(model.getCells());
          ObservableList<Move> moves = FXCollections.observableList(model.getMoves());
          CuboidBuilder builder = new CuboidBuilder(cells, moves, width, height, depth);
          builder.createCuboid();
          puzzles.put("cube-" + width + "x" + height + "x" + depth, model);
        }
      }
    }
  }

  @Test
  public void iconTest() throws IOException {
    for (Entry<String, Puzzle> entry : puzzles.entrySet()) {
      for (Move move : entry.getValue().getMoves()) {
        File iconFile = new File("target/test-images/"+entry.getKey()+"/icons/"+move.getName()+".png");
        iconFile.getParentFile().mkdirs();
        ImageIO.write(move.getImageIcon(),"png",iconFile);
        assertTrue(iconFile.isFile());
      }
    }
  }
}
