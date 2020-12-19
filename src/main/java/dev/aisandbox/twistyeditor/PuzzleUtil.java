package dev.aisandbox.twistyeditor;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import dev.aisandbox.twistyeditor.model.Cell;
import dev.aisandbox.twistyeditor.model.CompiledMove;
import dev.aisandbox.twistyeditor.model.Loop;
import dev.aisandbox.twistyeditor.model.Move;
import dev.aisandbox.twistyeditor.model.Puzzle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuzzleUtil {

  public static XStream getCodec() {
    XStream xstream = new XStream();
    xstream.processAnnotations(Puzzle.class);
    xstream.registerConverter(new BufferedImageConverter());
    xstream.addPermission(NoTypePermission.NONE);
    xstream.allowTypes(
        new Class[] {
          Puzzle.class,
          Cell.class,
          Move.class,
          Loop.class,
          CompiledMove.class,
          HashMap.class,
          Map.class,
          List.class,
          ArrayList.class,
          String.class,
            Integer.class
        });
    return xstream;
  }
}
