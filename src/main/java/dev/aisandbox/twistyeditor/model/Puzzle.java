package dev.aisandbox.twistyeditor.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@XStreamAlias("puzzle")
public class Puzzle {

  public static final int WIDTH = 1280;
  public static final int HEIGHT = 1000;

  @Getter private List<Cell> cells = new ArrayList<>();
  @Getter private List<Move> moves = new ArrayList<>();
  @Getter private Map<String, CompiledMove> compiledMoves = new HashMap<>();

  public BufferedImage getCellImage(Cell highlightCell) {
    BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();
    Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    g.setFont(font);
    g.setColor(Color.white);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    // draw polygons
    for (int i = 0; i < cells.size(); i++) {
      Cell c = cells.get(i);
      Polygon poly = c.getPolygon();
      g.setColor(c.getColour().getAwtColour());
      g.fillPolygon(poly);
      Stroke stroke = g.getStroke();
      if (c == highlightCell) {
        g.setColor(Color.YELLOW);
        g.setStroke(new BasicStroke(5));
      } else {
        g.setColor(Color.DARK_GRAY);
      }
      g.drawPolygon(poly);
      g.setStroke(stroke);
      // number cell
      g.setColor(Color.DARK_GRAY);
      g.fillOval(
          c.getLocationX() - 12,
          c.getLocationY() - 12,
          24,
          24);
      g.setColor(Color.white);

      String text = Integer.toString(i);
      FontMetrics metrics = g.getFontMetrics(font);
      // Determine the X coordinate for the text
      int x = c.getLocationX() - metrics.stringWidth(text) / 2;
      // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of
      // the screen)
      // int y = c.getLocationY() + metrics.getHeight() / 2 - metrics.getAscent();
      int y = c.getLocationY() + (int) (metrics.getStringBounds(text, g).getHeight() / 3);

      g.drawString(Integer.toString(i), x, y);
    }
    return image;
  }

  public BufferedImage getMoveImage(Move selectedMove, Loop selectedLoop) {
    BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();
    g.setColor(Color.white);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    // draw polygons
    for (Cell c : cells) {
      Polygon poly = c.getPolygon();
      g.setColor(Color.lightGray);
      g.fillPolygon(poly);
      g.setColor(Color.DARK_GRAY);
      g.drawPolygon(poly);
    }
    // draw selected move
    if (selectedMove != null) {
      for (Loop loop : selectedMove.getLoops()) {
        if (loop == selectedLoop) {
          g.setColor(Color.YELLOW);
        } else {
          g.setColor(Color.DARK_GRAY);
        }
        for (Cell c : loop.getCells()) {
          g.fillOval(c.getLocationX() - 2, c.getLocationY() - 2, 5, 5);
        }
        if (loop.getCells().size() > 1) {
          for (int i = 0; i < loop.getCells().size() - 1; i++) {
            drawCellConnection(g, loop.getCells().get(i), loop.getCells().get(i + 1));
          }
          // add loop back to start
          drawCellConnection(
              g, loop.getCells().get(loop.getCells().size() - 1), loop.getCells().get(0));
        }
      }
    }
    return image;
  }

  private void drawCellConnection(Graphics2D graphics2D, Cell start, Cell end) {
    // get start position
    double startX = start.getLocationX();
    double startY = start.getLocationY();
    // work out unit vector from start to end
    double dx = end.getLocationX() - startX;
    double dy = end.getLocationY() - startY;
    double len = Math.sqrt(dx * dx + dy * dy);
    double ux = dx / len;
    double uy = dy / len;
    // create a unit vector 90 degrees off
    double tx = -uy;
    double ty = ux;
    // find the mid point of the line + 4t
    double midx = startX + dx / 2 + 5 * tx;
    double midy = startY + dy / 2 + 5 * ty;
    // straight line
    // graphics2D.drawLine(start.getLocationX(), start.getLocationY(), end.getLocationX(),
    //    end.getLocationY());
    graphics2D.drawLine(start.getLocationX(), start.getLocationY(), (int) midx, (int) midy);
    graphics2D.drawLine((int) midx, (int) midy, end.getLocationX(), end.getLocationY());
    graphics2D.drawLine(
        (int) midx, (int) midy, (int) (midx - 8 * ux + 3 * tx), (int) (midy - 8 * uy + 3 * ty));
    graphics2D.drawLine(
        (int) midx, (int) midy, (int) (midx - 8 * ux - 3 * tx), (int) (midy - 8 * uy - 3 * ty));
  }

  public Cell findCell(double x, double y) {
    Cell result = null;
    for (Cell c : cells) {
      if (c.getPolygon().contains(x, y)) {
        result = c;
      }
    }
    return result;
  }

  public void centerCells() {
    if (cells.size() > 0) {
      // work out max and min of cells
      int maxX = 0;
      int minX = Puzzle.WIDTH;
      int maxY = 0;
      int minY = Puzzle.HEIGHT;
      for (Cell c : cells) {
        Rectangle2D rect = c.getPolygon().getBounds2D();
        maxX = Math.max(maxX, (int) rect.getMaxX());
        maxY = Math.max(maxY, (int) rect.getMaxY());
        minX = Math.min(minX, (int) rect.getMinX());
        minY = Math.min(minY, (int) rect.getMinY());
      }
      // work out the spare space
      int spaceX = Puzzle.WIDTH - (maxX - minX);
      int spaceY = Puzzle.HEIGHT - (maxY - minY);
      int tx = -minX + spaceX / 2;
      int ty = -minY + spaceY / 2;
      for (Cell c : cells) {
        c.setLocationX(c.getLocationX() + tx);
        c.setLocationY(c.getLocationY() + ty);
      }
    }
  }

  /**
   * Compile the move based on the latest information
   *
   * @return returns an (optional) list of warnings
   */
  public Optional<String> compileMoves() {
    List<String> warnings = new ArrayList<>();
    // clear old compiled moves
    compiledMoves = new HashMap<>();
    for (Move move : moves) {
      CompiledMove cmove = new CompiledMove(cells.size());
      // copy move image
      cmove.setImage(move.getImageIcon());
      // copy move cost
      cmove.setCost(move.getCost());
      // setup matrix
      cmove.resetMove();
      // check we have loops
      if (move.getLoops().isEmpty()) {
        warnings.add("Move '"+move.getName()+"' has no loops");
      }
      // add each loop
      for (int i=0;i<move.getLoops().size();i++) {
        Loop loop = move.getLoops().get(i);
        // check we have at least two cells
        if (loop.getCells().size()<2) {
          warnings.add("Move '"+move.getName()+"' loop "+i+" has less than two cells - can't compile");
        } else {
          for (int j=0;j<loop.getCells().size()-1;j++) {
            cmove.setMatrixElement(
                cells.indexOf(loop.getCells().get(j+1)),
                cells.indexOf(loop.getCells().get(j))
            );
          }
          cmove.setMatrixElement(
              cells.indexOf(loop.getCells().get(0)),
              cells.indexOf(loop.getCells().get(loop.getCells().size()-1))
          );
        }
      }
      // check for duplicate name
      if (compiledMoves.containsKey(move.getName())) {
        warnings.add("Duplicate move name '"+move.getName()+"'");
      }
      compiledMoves.put(move.getName(),cmove);
    }
    if (warnings.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(String.join("\n", warnings));
    }
  }

  public BufferedImage getSpritesheet() {
    // work out the image size
    int rows = moves.size() / 6;
    if (moves.size() % 6>0) {
      rows++;
    }
    BufferedImage image = new BufferedImage(Move.MOVE_ICON_WIDTH * 6,Move.MOVE_ICON_HEIGHT*rows,BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();
    for (int i=0;i<moves.size();i++) {
      int x = i%6;
      int y=i/6;
      g.drawImage(moves.get(i).getImageIcon(),x*Move.MOVE_ICON_WIDTH,y*Move.MOVE_ICON_HEIGHT,null);
    }
    return image;
  }

  public void setSpritesheet(BufferedImage image) {
    for (int i=0;i<moves.size();i++) {
      int x=i%6;int y=i/6;
      moves.get(i).setImageIcon(image.getSubimage(x*Move.MOVE_ICON_WIDTH,y*Move.MOVE_ICON_HEIGHT,Move.MOVE_ICON_WIDTH,Move.MOVE_ICON_HEIGHT));
    }
  }
}
