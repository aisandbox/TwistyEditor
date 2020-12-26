package dev.aisandbox.twistyeditor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.aisandbox.twistyeditor.model.CuboidBuilder;
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

}
