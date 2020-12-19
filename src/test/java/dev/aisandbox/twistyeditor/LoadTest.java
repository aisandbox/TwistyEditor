package dev.aisandbox.twistyeditor;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thoughtworks.xstream.XStream;
import dev.aisandbox.twistyeditor.model.Puzzle;
import java.io.File;
import org.junit.jupiter.api.Test;

public class LoadTest {
  @Test
  public void loadTest() {
    XStream x = PuzzleUtil.getCodec();
    Puzzle p = (Puzzle) x.fromXML(new File("Pyramid3.tp"));
    assertTrue(p.getMoves().size()>0);
  }

}
