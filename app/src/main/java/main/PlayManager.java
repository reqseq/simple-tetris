package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;
import mino.*;

public class PlayManager {
  // Main Play Area
  private final int WIDTH = 360;
  private final int HEIGHT = 600;
  public static int left_x;
  public static int right_x;
  public static int top_y;
  public static int bottom_y;

  // Mino
  private Mino currentMino;
  private final int MINO_START_X;
  private final int MINO_START_Y;
  private Mino nextMino;
  private final int NEXTMINO_X;
  private final int NEXTMINO_Y;
  public static ArrayList<Block> staticBlocks = new ArrayList<>();

  // Others
  public static int dropInternal = 60;
  protected boolean gameOver;

  // Effect
  private boolean effectCounterOn;
  private int effectCounter;
  private ArrayList<Integer> effectY = new ArrayList<>();

  // Score
  private int level = 1;
  private int lines;
  private int score;

  public PlayManager() {
    left_x = (GamePanel.WIDTH / 2) - (WIDTH / 2);
    right_x = left_x + WIDTH;
    top_y = 50;
    bottom_y = top_y + HEIGHT;

    MINO_START_X = left_x + (WIDTH / 2) - Block.SIZE;
    MINO_START_Y = top_y + Block.SIZE;

    NEXTMINO_X = right_x + 175;
    NEXTMINO_Y = top_y + 500;

    currentMino = pickMino();
    currentMino.setXY(MINO_START_X, MINO_START_Y);
    nextMino = pickMino();
    nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
  }

  private Mino pickMino() {
    int i = new Random().nextInt(7);

    Mino mino =
        switch (i) {
          case 0 -> new MinoL1();
          case 1 -> new MinoL2();
          case 2 -> new MinoSquare();
          case 3 -> new MinoBar();
          case 4 -> new MinoT();
          case 5 -> new MinoZ1();
          case 6 -> new MinoZ2();
          default -> null;
        };

    return mino;
  }

  public void update() {
    // Check if the currentMino is active
    if (currentMino.active) {
      currentMino.update();
    } else {
      for (Block block : currentMino.b) {
        staticBlocks.add(block);
      }

      // check if the game is over
      if (currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
        // this means the currentMino immediately collided a block and couldn't move at
        // all so it's xy are the same as the nextMino
        gameOver = true;
        GamePanel.music.stop();
        GamePanel.se.play(2, false);
      }

      currentMino.deactivating = false;

      // replace the currentMino with the nextMino
      currentMino = nextMino;
      currentMino.setXY(MINO_START_X, MINO_START_Y);
      nextMino = pickMino();
      nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

      // when a mino becomes inactive, check if line(s) can be deleted
      checkDelete();
    }
  }

  private void checkDelete() {
    int x = left_x;
    int y = top_y;
    int blockCount = 0;
    int lineCount = 0;

    while (x < right_x && y < bottom_y) {
      for (Block block : staticBlocks) {
        if (block.x == x && block.y == y) {
          blockCount++;
        }
      }

      x += Block.SIZE;

      if (x == right_x) {
        // if the blockCount hits 12, that means the current y lines is all filled with
        // blocks so we can delete them
        if (blockCount == 12) {
          effectCounterOn = true;
          effectY.add(y);

          for (int i = staticBlocks.size() - 1; i >= 0; i--) {
            // remove all blocks in the current y line
            if (staticBlocks.get(i).y == y) {
              staticBlocks.remove(i);
            }
          }

          lineCount++;
          lines++;

          // Drop Speed
          // if the line score hits a certain number, increase the drop speed
          // 1 is the fastest
          if (lines % 10 == 0 && dropInternal > 1) {
            level++;

            dropInternal = (dropInternal > 10) ? dropInternal - 10 : dropInternal - 1;
          }

          // a line has been deleted so no need to slow down blocks that are above it
          for (Block block : staticBlocks) {
            // if a block is above the current y, move it down by the block size
            if (y > block.y) {
              block.y += Block.SIZE;
            }
          }
        }

        blockCount = 0;
        x = left_x;
        y += Block.SIZE;
      }
    }

    // Add Score
    if (lineCount > 0) {
      GamePanel.se.play(1, false);
      int singleLineScore = 10 * level;
      score += singleLineScore * lineCount;
    }
  }

  public void draw(Graphics2D g2) {
    // Draw Play Area Frame
    g2.setColor(Color.WHITE);
    g2.setStroke(new BasicStroke(4f));
    g2.drawRect(left_x - 4, top_y - 4, WIDTH + 8, HEIGHT + 8);

    // Draw Next Mino Frame
    int x = right_x + 100;
    int y = bottom_y - 200;
    g2.drawRect(x, y, 200, 200);
    g2.setFont(new Font("Liberation Sans", Font.PLAIN, 30));
    g2.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.drawString("NEXT", x + 60, y + 60);

    // Draw Score Frame
    g2.drawRect(x, top_y, 250, 300);
    x += 40;
    y = top_y + 90;
    g2.drawString("LEVEL: " + level, x, y);
    y += 70;
    g2.drawString("LINES: " + lines, x, y);
    y += 70;
    g2.drawString("SCORE: " + score, x, y);

    // Draw the currentMino
    if (currentMino != null) {
      currentMino.draw(g2);
    }

    // Draw the nextMino
    nextMino.draw(g2);

    // Draw Static Blocks
    for (Block block : staticBlocks) {
      block.draw(g2);
    }

    // Draw Effect
    if (effectCounterOn) {
      effectCounter++;

      g2.setColor(Color.RED);
      for (int effect : effectY) {
        g2.fillRect(left_x, effect, WIDTH, Block.SIZE);
      }

      if (effectCounter == 10) {
        effectCounterOn = false;
        effectCounter = 0;
        effectY.clear();
      }
    }

    // Draw Pause or Game Over
    g2.setColor(Color.YELLOW);
    g2.setFont(g2.getFont().deriveFont(50f));
    if (gameOver) {
      x = left_x + 25;
      y = top_y + 320;
      g2.drawString("GAME OVER", x, y);
    } else if (KeyHandler.pausePressed) {
      x = left_x + 80;
      y = top_y + 320;
      g2.drawString("PAUSED", x, y);
    }

    x = 125;
    y = top_y + 320;
    g2.setColor(Color.WHITE);
    g2.setFont(new Font("Liberation Serif", Font.ITALIC, 60));
    g2.drawString("JTetris", x, y);
  }
}
