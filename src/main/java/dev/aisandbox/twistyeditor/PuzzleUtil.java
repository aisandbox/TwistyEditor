package dev.aisandbox.twistyeditor;

import dev.aisandbox.twistyeditor.model.Puzzle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class PuzzleUtil {

  public static void savePuzzleToFile(Puzzle puzzle, File file) throws IOException {
    FileOutputStream fileOutputStream
        = new FileOutputStream(file);
    ObjectOutputStream objectOutputStream
        = new ObjectOutputStream(fileOutputStream);
    objectOutputStream.writeObject(puzzle);
    objectOutputStream.flush();
    objectOutputStream.close();
  }

  public static void loadPuzzleFromFile(Puzzle puzzle,File file) {
    // load
  }

}
