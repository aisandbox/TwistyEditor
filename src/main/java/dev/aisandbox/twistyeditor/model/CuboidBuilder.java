package dev.aisandbox.twistyeditor.model;

import dev.aisandbox.twistyeditor.model.shapes.ShapeEnum;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CuboidBuilder {

  private final Puzzle puzzle;
  private int scale;
  private int gap=4;

  public CuboidBuilder(Puzzle puzzle) {
    this.puzzle = puzzle;
  }

  public void createCuboid(int width,int height,int depth) {
    log.info("Creating cuboid {}x{}x{}",width,height,depth);
    // work out scale
    scale = 40;
    gap = 4;
    // create white grid
    puzzle.getCells().addAll(createGrid(0,0,width,depth,ColourEnum.WHITE));
  }

  private List<Cell> createGrid(int x,int y,int w,int h,ColourEnum colour) {
    List<Cell> cells = new ArrayList<>();
    for (int dx=0;dx<w;dx++) {
      for (int dy=0;dy<h;dy++) {
        Cell c = new Cell();
        c.setShape(ShapeEnum.SQUARE);
        c.setColour(colour);
        c.setLocationX(x+dx*scale/2);
        c.setLocationY(y+dy*scale/2);
        c.setScale(scale);
        c.setRotation(0);
        cells.add(c);
      }
    }
    return cells;
  }


}
