package dev.aisandbox.twistyeditor;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thoughtworks.xstream.XStream;
import dev.aisandbox.twistyeditor.model.Cell;
import dev.aisandbox.twistyeditor.model.CuboidBuilder;
import dev.aisandbox.twistyeditor.model.Move;
import dev.aisandbox.twistyeditor.model.Puzzle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ExportTest {
  static Map<String, Puzzle> puzzles;

  @BeforeAll
  public static void setupPuzzles() throws IOException {
    puzzles = new HashMap<>();
    for (int width : List.of(2, 3, 4, 5, 6, 7, 8, 9, 10)) {
      for (int depth : List.of(2, 3, 4, 5, 6, 7, 8, 9, 10)) {
        for (int height : List.of(2, 3, 4, 5, 6, 7, 8, 9, 10)) {
          Puzzle model = new Puzzle();
          ObservableList<Cell> cells = FXCollections.observableList(model.getCells());
          ObservableList<Move> moves = FXCollections.observableList(model.getMoves());
          CuboidBuilder builder = new CuboidBuilder(cells, moves, width, height, depth);
          builder.createCuboid();
          // work out file name
          if (width == depth && depth == height) {
            puzzles.put("Cube" + width, model);
          } else {
            puzzles.put("Cuboid" + width + "x" + height + "x" + depth, model);
          }
        }
      }
    }
  }

  @Test
  public void iconTest() throws IOException {
    for (Entry<String, Puzzle> entry : puzzles.entrySet()) {
      File exportFile = new File("target/test-export/" + entry.getKey() + ".tp");
      exportFile.getParentFile().mkdirs();
      Optional<String> errors = entry.getValue().compileMoves();
      assertTrue(errors.isEmpty());
      XStream xstream = PuzzleUtil.getCodec();
      BufferedWriter out = new BufferedWriter(new FileWriter(exportFile));
      out.write(xstream.toXML(entry.getValue()));
      out.close();
      assertTrue(exportFile.isFile());
    }
  }
}
