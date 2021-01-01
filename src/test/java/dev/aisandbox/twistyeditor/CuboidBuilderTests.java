package dev.aisandbox.twistyeditor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.aisandbox.twistyeditor.model.Cell;
import dev.aisandbox.twistyeditor.model.CuboidBuilder;
import dev.aisandbox.twistyeditor.model.Move;
import dev.aisandbox.twistyeditor.model.Puzzle;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

public class CuboidBuilderTests {

  @Test
  public void testDivision() {
    assertEquals(2, CuboidBuilder.divRoundUp(4,2),"4/2");
    assertEquals(3,CuboidBuilder.divRoundUp(5,2),"5/2");
  }

  @Test
  public void testNames() {
    assertEquals("R",CuboidBuilder.getMoveName(1,'R',1));
    assertEquals("R'",CuboidBuilder.getMoveName(1,'R',-1));
    assertEquals("R2",CuboidBuilder.getMoveName(1,'R',2));

    assertEquals("Rw",CuboidBuilder.getMoveName(2,'R',1));
    assertEquals("Rw'",CuboidBuilder.getMoveName(2,'R',-1));
    assertEquals("Rw2",CuboidBuilder.getMoveName(2,'R',2));

    assertEquals("3Rw",CuboidBuilder.getMoveName(3,'R',1));
    assertEquals("3Rw'",CuboidBuilder.getMoveName(3,'R',-1));
    assertEquals("3Rw2",CuboidBuilder.getMoveName(3,'R',2));

    assertEquals("x",CuboidBuilder.getMoveName(0,'R',1));
    assertEquals("x'",CuboidBuilder.getMoveName(0,'R',-1));
    assertEquals("x2",CuboidBuilder.getMoveName(0,'R',2));
  }

  @Test
  public void testRanges() throws IOException {
    for (int width=2;width<11;width++) {
      for (int height=2;height<11;height++) {
        for (int depth=2;depth<11;depth++) {
          Puzzle model = new Puzzle();
          ObservableList<Cell> cells = FXCollections.observableList(model.getCells());
          ObservableList<Move> moves = FXCollections.observableList(model.getMoves());
          CuboidBuilder builder = new CuboidBuilder(cells,moves,width,height,depth);
          builder.createCuboid();
        }
      }
    }
  }

}
