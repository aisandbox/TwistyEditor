package dev.aisandbox.twistyeditor.model;

import dev.aisandbox.twistyeditor.model.shapes.CellShape;
import java.awt.Color;
import lombok.Getter;

public enum ColourEnum {
  WHITE("FFFFFF"), ORANGE("FFA600"), GREEN("068E00"), RED("8E0000"), BLUE("02008E"), YELLOW(
      "F7FF00"), OLIVE("9FD82C"), PINK("D12CD8"), CYAN("2CD1D8"), GREY("B8B8B8"), IVORY("D2D399");

  @Getter
  private final String hex;
  @Getter
  private final Color awtColour;

  ColourEnum(String hex) {
    this.hex = hex;
    awtColour = new Color(
        Integer.valueOf(hex.substring(0, 2), 16),
        Integer.valueOf(hex.substring(2, 4), 16),
        Integer.valueOf(hex.substring(4, 6), 16)
    );
  }
}
