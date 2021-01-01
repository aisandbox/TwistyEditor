package dev.aisandbox.twistyeditor;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import dev.aisandbox.twistyeditor.model.Cell;
import dev.aisandbox.twistyeditor.model.CompiledMove;
import dev.aisandbox.twistyeditor.model.Loop;
import dev.aisandbox.twistyeditor.model.Move;
import dev.aisandbox.twistyeditor.model.Puzzle;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.embed.swing.SwingFXUtils;

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

  public static BufferedImage getMoveIcon(String name) {
    // create a new icon
    BufferedImage image =
        new BufferedImage(
            Move.MOVE_ICON_WIDTH, Move.MOVE_ICON_HEIGHT, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics2D = image.createGraphics();
    graphics2D.setColor(Color.WHITE);
    graphics2D.fillRect(0, 0, Move.MOVE_ICON_WIDTH, Move.MOVE_ICON_HEIGHT);
    return getMoveIcon(image,name);
  }

  public static BufferedImage getMoveIcon(BufferedImage image,String name) {
    Graphics2D graphics2D = image.createGraphics();
    Font font = new Font("Hack", Font.PLAIN, 22);
    graphics2D.setFont(font);
    graphics2D.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
    // Get the FontMetrics
    FontMetrics metrics = graphics2D.getFontMetrics(font);
    // Determine the X coordinate for the text
    int dx = (Move.MOVE_ICON_WIDTH - metrics.stringWidth(name)) / 2;
    // Set the font
    graphics2D.setColor(Color.BLACK);
    // Draw the String
    graphics2D.drawString(name, dx, Move.MOVE_ICON_HEIGHT - 4);
    return image;
  }


}
