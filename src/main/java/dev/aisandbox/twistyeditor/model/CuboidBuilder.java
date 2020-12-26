package dev.aisandbox.twistyeditor.model;

import dev.aisandbox.twistyeditor.model.shapes.ShapeEnum;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CuboidBuilder {

  private int scale;
  private int gap = 4;

  private final ObservableList<Cell> cellObservableList;
  private final ObservableList<Move> moveObservableList;

  private final List<Cell> left = new ArrayList<>();
  private final List<Cell> right = new ArrayList<>();
  private final List<Cell> top = new ArrayList<>();
  private final List<Cell> bottom = new ArrayList<>();
  private final List<Cell> front = new ArrayList<>();
  private final List<Cell> back = new ArrayList<>();

  private final int width;
  private final int height;
  private final int depth;

  public CuboidBuilder(
      ObservableList<Cell> cellObservableList,
      ObservableList<Move> moveObservableList,
      int width,
      int height,
      int depth) {
    this.cellObservableList = cellObservableList;
    this.moveObservableList = moveObservableList;
    this.width = width;
    this.height = height;
    this.depth = depth;
  }

  public void createCuboid() {
    log.info("Creating cuboid {}x{}x{}", width, height, depth);
    // work out scale
    scale = 40;
    gap = 4;
    // create white (top) grid
    top.addAll(createGrid(0, 0, width, depth, ColourEnum.WHITE));
    // create orange (left) grid
    left.addAll(
        createGrid(
            -depth * scale * 2 - gap, depth * scale * 2 + gap, depth, height, ColourEnum.ORANGE));
    // create green (front) grid
    front.addAll(createGrid(0, depth * scale * 2 + gap, width, height, ColourEnum.GREEN));
    // create red (right) grid
    right.addAll(
        createGrid(
            width * scale * 2 + gap, depth * scale * 2 + gap, depth, height, ColourEnum.RED));
    // create blue (back) grid
    back.addAll(
        createGrid(
            (width + depth) * scale * 2 + gap * 2,
            depth * scale * 2 + gap,
            width,
            height,
            ColourEnum.BLUE));
    // create yellow (bottom) grid
    bottom.addAll(
        createGrid(0, (depth + height) * scale * 2 + gap * 2, width, depth, ColourEnum.YELLOW));
    // add all cells to puzzle
    cellObservableList.addAll(top);
    cellObservableList.addAll(left);
    cellObservableList.addAll(front);
    cellObservableList.addAll(right);
    cellObservableList.addAll(back);
    cellObservableList.addAll(bottom);
    // create moves
    Move move;
    if (width == height) {
      // we can have F,F',B,B',z,z' moves
      for (int deep = 1; deep < depth; deep++) {
        // create F moves
        move = new Move();
        move.setName(getMoveName(deep,'F',1));
        log.info("Generating {}", move.getName());
        move.getLoops().addAll(faceTurn(front, width, height));
        for (int layer = 1; layer <= deep; layer++) {
          move.getLoops().addAll(frontSideTurn(layer));
        }
        moveObservableList.add(move);
        // create F' move
        move = new Move();
        move.setName(getMoveName(deep,'F',-1));
        log.info("Generating {}", move.getName());
        move.getLoops().addAll(faceReverseTurn(front, width, height));
        for (int layer = 1; layer <= deep; layer++) {
          move.getLoops().addAll(frontSideReverseTurn(layer));
        }
        moveObservableList.add(move);
        // B moves
        move = new Move();
        move.setName(getMoveName(deep,'B',1));
        move.getLoops().addAll(faceTurn(back,width,height));
        for (int layer=1;layer<=deep;layer++) {
          move.getLoops().addAll(frontSideReverseTurn(depth-layer+1));
        }
        moveObservableList.addAll(move);
        // B' moves
        move = new Move();
        move.setName(getMoveName(deep,'B',-1));
        move.getLoops().addAll(faceReverseTurn(back,width,height));
        for (int layer=1;layer<=deep;layer++) {
          move.getLoops().addAll(frontSideTurn(depth-layer+1));
        }
        moveObservableList.addAll(move);
      }
      // z move
      move = new Move();
      move.setName(getMoveName(0,'F',1));
      move.getLoops().addAll(faceTurn(front, width, height));
      move.getLoops().addAll(faceReverseTurn(back,width,height));
      for (int layer = 1; layer <= depth; layer++) {
        move.getLoops().addAll(frontSideTurn(layer));
      }
      moveObservableList.add(move);
      // z' move
      move = new Move();
      move.setName(getMoveName(0,'F',-1));
      move.getLoops().addAll(faceReverseTurn(front, width, height));
      move.getLoops().addAll(faceTurn(back,width,height));
      for (int layer = 1; layer <= depth; layer++) {
        move.getLoops().addAll(frontSideReverseTurn(layer));
      }
      moveObservableList.add(move);
    }
    if (width == depth) {
      // we can have U,U',D,D',y,y'
      for (int deep = 1; deep < height; deep++) {
        // U Move
        move=new Move();
        move.setName(getMoveName(deep,'U',1));
        move.getLoops().addAll(faceTurn(top,width,depth));
        for (int layer=1;layer<=deep;layer++) {
          move.getLoops().addAll(topSideTurn(layer));
        }
        moveObservableList.add(move);
        // U' move
        move=new Move();
        move.setName(getMoveName(deep,'U',-1));
        move.getLoops().addAll(faceReverseTurn(top,width,depth));
        for (int layer=1;layer<=deep;layer++) {
          move.getLoops().addAll(topSideReverseTurn(layer));
        }
        moveObservableList.add(move);
        // D move
        move=new Move();
        move.setName(getMoveName(deep,'D',1));
        move.getLoops().addAll(faceTurn(bottom,width,depth));
        for (int layer=1;layer<=deep;layer++) {
          move.getLoops().addAll(topSideReverseTurn(height-layer+1));
        }
        moveObservableList.add(move);
        // D' move
        move=new Move();
        move.setName(getMoveName(deep,'D',-1));
        move.getLoops().addAll(faceReverseTurn(bottom,width,depth));
        for (int layer=1;layer<=deep;layer++) {
          move.getLoops().addAll(topSideTurn(height-layer+1));
        }
        moveObservableList.add(move);
      }
      // y move
      move=new Move();
      move.setName(getMoveName(0,'U',1));
      move.getLoops().addAll(faceTurn(top,width,depth));
      move.getLoops().addAll(faceReverseTurn(bottom,width,depth));
      for (int layer=1;layer<=depth;layer++) {
        move.getLoops().addAll(topSideTurn(layer));
      }
      moveObservableList.add(move);
      // y' move
      move=new Move();
      move.setName(getMoveName(0,'U',-1));
      move.getLoops().addAll(faceReverseTurn(top,width,depth));
      move.getLoops().addAll(faceTurn(bottom,width,depth));
      for (int layer=1;layer<=depth;layer++) {
        move.getLoops().addAll(topSideReverseTurn(layer));
      }
      moveObservableList.add(move);
    }
    if (depth==height) {
      // we can have R,R',L,L',z,z'
      for (int deep=1;deep<width;deep++) {
        // R
        move=new Move();
        move.setName(getMoveName(deep,'R',1));
        move.getLoops().addAll(faceTurn(right,depth,height));
        for (int layer=1;layer<=deep;layer++) {
          move.getLoops().addAll(rightSideTurn(layer));
        }
        moveObservableList.add(move);
        // R'
        move=new Move();
        move.setName(getMoveName(deep,'R',-1));
        move.getLoops().addAll(faceReverseTurn(right,depth,height));
        for (int layer=1;layer<=deep;layer++) {
          move.getLoops().addAll(rightSideReverseTurn(layer));
        }
        moveObservableList.add(move);
        // L
        move=new Move();
        move.setName(getMoveName(deep,'L',1));
        move.getLoops().addAll(faceTurn(left,depth,height));
        for (int layer=1;layer<=deep;layer++) {
          move.getLoops().addAll(rightSideReverseTurn(width-layer+1));
        }
        moveObservableList.add(move);
        // L'
        move=new Move();
        move.setName(getMoveName(deep,'L',-1));
        move.getLoops().addAll(faceReverseTurn(left,depth,height));
        for (int layer=1;layer<=deep;layer++) {
          move.getLoops().addAll(rightSideTurn(width-layer+1));
        }
        moveObservableList.add(move);
      }
      // z
      move=new Move();
      move.setName(getMoveName(0,'R',1));
      move.getLoops().addAll(faceTurn(right,depth,height));
      move.getLoops().addAll(faceReverseTurn(left,depth,height));
      for (int layer=1;layer<=width;layer++) {
        move.getLoops().addAll(rightSideTurn(layer));
      }
      moveObservableList.add(move);
      // z'
      move=new Move();
      move.setName(getMoveName(0,'R',-1));
      move.getLoops().addAll(faceReverseTurn(right,depth,height));
      move.getLoops().addAll(faceTurn(left,depth,height));
      for (int layer=1;layer<=width;layer++) {
        move.getLoops().addAll(rightSideReverseTurn(layer));
      }
      moveObservableList.add(move);
    }
    // we can always have double turns
    for (int deep = 1; deep < depth; deep++) {
      // F2 moves
      move = new Move();
      move.setName(getMoveName(deep,'F',2));
      move.getLoops().addAll(faceDoubleTurn(front, width, height));
      for (int layer = 1; layer <= deep; layer++) {
        move.getLoops().addAll(frontSideDoubleTurn(layer));
      }
      moveObservableList.add(move);
      // TODO B2
      // TODO z2
    }
    for (int deep=1;deep<height;deep++) {
      // TODO U2
      // TODO D2
      // TODO y2
    }
    for (int deep=1;deep<width;deep++) {
      // TODO R2
      // TODO L2
      // TODO z2
    }
  }

  /**
   * Create the move name, if depth<1 then assume a cube rotation
   *
   * @param depth the depth of the turn 0 for all layers
   * @param face the face to turn
   * @param quarterTurns the number of quarter turns
   * @return
   */
  public static String getMoveName(int depth, char face, int quarterTurns) {
    StringBuilder result = new StringBuilder();
    // outer block moves
    if (depth > 2) {
      result.append(depth);
    }
    // face
    if (depth > 0) {
      result.append(face);
    } else {
      switch (face) {
        case 'R':
          result.append("x");
          break;
        case 'U':
          result.append("y");
          break;
        case 'F':
          result.append("z");
          break;
        default:
          result.append("?");
      }
    }
    // outer block move
    if (depth > 1) {
      result.append("w");
    }
    // rotations
    if (quarterTurns == 2) {
      result.append("2");
    } else if (quarterTurns == -1) {
      result.append("'");
    }
    return result.toString();
  }

  private List<Cell> createGrid(int x, int y, int w, int h, ColourEnum colour) {
    List<Cell> cells = new ArrayList<>();
    for (int dy = 0; dy < h; dy++) {
      for (int dx = 0; dx < w; dx++) {
        Cell c = new Cell();
        c.setShape(ShapeEnum.SQUARE);
        c.setColour(colour);
        c.setLocationX(x + dx * scale * 2);
        c.setLocationY(y + dy * scale * 2);
        c.setScale(scale);
        c.setRotation(0);
        cells.add(c);
      }
    }
    return cells;
  }

  private List<Loop> frontSideTurn(int layer) {
    if (width != height) {
      throw new IllegalStateException();
    }
    List<Loop> result = new ArrayList<>();
    for (int i = 0; i < width; i++) {
      Loop loop = new Loop();
      loop.getCells().add(top.get(i + width * (depth - layer)));
      loop.getCells().add(right.get(layer - 1 + i * depth));
      loop.getCells().add(bottom.get(width - i - 1 + width * (layer - 1)));
      loop.getCells().add(left.get(depth - layer + (height - i - 1) * depth));
      result.add(loop);
    }
    return result;
  }

  private List<Loop> topSideTurn(int layer) {
    if (width != depth) {
      throw new IllegalStateException();
    }
    List<Loop> result = new ArrayList<>();
    for (int i = 0; i < width; i++) {
      Loop loop = new Loop();
      loop.getCells().add(front.get(i + (layer - 1) * width));
      loop.getCells().add(left.get(i + (layer - 1) * width));
      loop.getCells().add(back.get(i + (layer - 1) * width));
      loop.getCells().add(right.get(i + (layer - 1) * depth));
      result.add(loop);
    }
    return result;
  }

  private List<Loop> rightSideTurn(int layer) {
    if (height != depth) {
      throw new IllegalStateException();
    }
    List<Loop> result = new ArrayList<>();
    for (int i = 0; i < width; i++) {
      Loop loop = new Loop();
      loop.getCells().add(front.get(width-layer+i*width));
      loop.getCells().add(top.get(width-layer+i * width));
      loop.getCells().add(back.get(width-layer+(height-i-1) * width));
      loop.getCells().add(bottom.get(width-layer+i*width));
      result.add(loop);
    }
    return result;
  }

  private List<Loop> frontSideReverseTurn(int layer) {
    List<Loop> result = frontSideTurn(layer);
    for (Loop loop : result) {
      Collections.reverse(loop.getCells());
    }
    return result;
  }

  private List<Loop> topSideReverseTurn(int layer) {
    List<Loop> result = topSideTurn(layer);
    for (Loop loop : result) {
      Collections.reverse(loop.getCells());
    }
    return result;
  }

  private List<Loop> rightSideReverseTurn(int layer) {
    List<Loop> result = rightSideTurn(layer);
    for (Loop loop : result) {
      Collections.reverse(loop.getCells());
    }
    return result;
  }

  private List<Loop> frontSideDoubleTurn(int layer) {
    List<Loop> result = new ArrayList<>();
    for (int i = 0; i < width; i++) {
      Loop l = new Loop();
      l.getCells().add(top.get(i + (depth - layer) * width));
      l.getCells().add(bottom.get(width - i - 1 + (layer - 1) * width));
      result.add(l);
    }
    for (int i = 0; i < height; i++) {
      Loop l = new Loop();
      l.getCells().add(right.get(layer - 1 + i * depth));
      l.getCells().add(left.get(depth - layer + (height - i - 1) * depth));
      result.add(l);
    }
    return result;
  }

  private List<Loop> faceTurn(List<Cell> face, int width, int height) {
    List<Loop> result = new ArrayList<>();
    for (int dx = 0; dx < divRoundUp(width, 2); dx++) {
      for (int dy = 0; dy < height / 2; dy++) {
        Loop loop = new Loop();
        loop.getCells().add(face.get(dx + dy * width));
        loop.getCells().add(face.get((width - dy - 1) + width * dx));
        loop.getCells().add(face.get(width - dx - 1 + width * (height - dy - 1)));
        loop.getCells().add(face.get(dy + width * (height - dx - 1)));
        result.add(loop);
      }
    }
    return result;
  }

  private List<Loop> faceReverseTurn(List<Cell> face, int width, int height) {
    List<Loop> result = faceTurn(face, width, height);
    for (Loop loop : result) {
      Collections.reverse(loop.getCells());
    }
    return result;
  }

  private List<Loop> faceDoubleTurn(List<Cell> face, int width, int height) {
    List<Loop> result = new ArrayList<>();
    for (int dx = 0; dx < width; dx++) {
      for (int dy = 0; dy < height / 2; dy++) {
        Loop loop = new Loop();
        loop.getCells().add(face.get(dx + dy * width));
        loop.getCells().add(face.get(width - dx - 1 + width * (height - dy - 1)));
        result.add(loop);
      }
    }
    // special case for odd numbered heights
    if (height % 2 == 1) {
      int dy = divRoundUp(height, 2) - 1;
      for (int dx = 0; dx < width / 2; dx++) {
        Loop loop = new Loop();
        loop.getCells().add(face.get(dx + dy * width));
        loop.getCells().add(face.get(width - dx - 1 + dy * width));
        result.add(loop);
      }
    }
    return result;
  }

  public static int divRoundUp(int num, int divisor) {
    return (num + divisor - 1) / divisor;
  }
}
