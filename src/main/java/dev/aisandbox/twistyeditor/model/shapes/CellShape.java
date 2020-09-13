package dev.aisandbox.twistyeditor.model.shapes;

import java.awt.Polygon;

public interface CellShape {

  public Polygon getPolygon(int locationX, int locationY, int rotation, int scale);

}
