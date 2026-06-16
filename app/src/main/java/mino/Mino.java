package mino;

import java.awt.Color;
import java.awt.Graphics2D;
import main.GamePanel;
import main.KeyHandler;
import main.PlayManager;

public abstract class Mino {
  public Block b[] = new Block[4];
  public Block tempB[] = new Block[4];
  private int autoDropCounter = 0;
  public int direction = 1;
  private boolean leftCollision, rightCollision, bottomCollision;
  public boolean active = true;
  public boolean deactivating;
  private int deactivateCounter = 0;

  public void create(Color c) {
    for (int i = 0; i < b.length; i++) {
      b[i] = new Block(c);
    }

    for (int i = 0; i < tempB.length; i++) {
      tempB[i] = new Block(c);
    }
  }

  public abstract void setXY(int x, int y);

  public void updateXY(int direction) {
    checkRotationColliston();

    if (!leftCollision && !rightCollision && !bottomCollision) {
      this.direction = direction;
      for (int i = 0; i < b.length; i++) {
        b[i].x = tempB[i].x;
        b[i].y = tempB[i].y;
      }
    }
  }

  public abstract void getDirection1();

  public abstract void getDirection2();

  public abstract void getDirection3();

  public abstract void getDirection4();

  public void checkMovementCollision() {
    leftCollision = false;
    rightCollision = false;
    bottomCollision = false;

    // check static block collision
    checkStaticBlockCollision();

    // Check frame collision
    // Left wall
    for (Block block : b) {
      if (block.x == PlayManager.left_x) {
        leftCollision = true;
      }
    }

    // Right wall
    for (Block block : b) {
      if (block.x == PlayManager.right_x - Block.SIZE) {
        rightCollision = true;
      }
    }

    // Bottom floor
    for (Block block : b) {
      if (block.y == PlayManager.bottom_y - Block.SIZE) {
        bottomCollision = true;
      }
    }
  }

  public void checkRotationColliston() {
    leftCollision = false;
    rightCollision = false;
    bottomCollision = false;

    // check static block collision
    checkStaticBlockCollision();

    // Check frame collision
    // Left wall
    for (Block block : tempB) {
      if (block.x < PlayManager.left_x) {
        leftCollision = true;
      }
    }

    // Right wall
    for (Block block : tempB) {
      if (block.x > PlayManager.right_x - Block.SIZE) {
        rightCollision = true;
      }
    }

    // Bottom floor
    for (Block block : tempB) {
      if (block.y > PlayManager.bottom_y - Block.SIZE) {
        bottomCollision = true;
      }
    }
  }

  private void checkStaticBlockCollision() {
    for (Block staticBlock : PlayManager.staticBlocks) {
      int targetX = staticBlock.x;
      int targetY = staticBlock.y;

      // check down
      for (Block block : b) {
        if (block.y + Block.SIZE == targetY && block.x == targetX) {
          bottomCollision = true;
        }
      }

      // check left
      for (Block block : b) {
        if (block.x - Block.SIZE == targetX && block.y == targetY) {
          leftCollision = true;
        }
      }

      // check right
      for (Block block : b) {
        if (block.x + Block.SIZE == targetX && block.y == targetY) {
          rightCollision = true;
        }
      }
    }
  }

  public void update() {
    if (deactivating) {
      deactivating();
    }

    if (KeyHandler.upPressed) {
      switch (direction) {
        case 1 -> getDirection2();
        case 2 -> getDirection3();
        case 3 -> getDirection4();
        case 4 -> getDirection1();
      }
      KeyHandler.upPressed = false;
      GamePanel.se.play(3, false);
    }

    checkMovementCollision();

    if (KeyHandler.downPressed) {
      if (!bottomCollision) {
        for (Block block : b) {
          block.y += Block.SIZE;
        }

        // When moved down, reset the autoDropCounter
        autoDropCounter = 0;
      }
      KeyHandler.downPressed = false;
    }

    if (KeyHandler.leftPressed) {
      if (!leftCollision) {
        for (Block block : b) {
          block.x -= Block.SIZE;
        }
      }

      KeyHandler.leftPressed = false;
    }

    if (KeyHandler.rightPressed) {
      if (!rightCollision) {
        for (Block block : b) {
          block.x += Block.SIZE;
        }
      }

      KeyHandler.rightPressed = false;
    }

    if (bottomCollision) {
      if (!deactivating) {
        GamePanel.se.play(4, false);
      }
      deactivating = true;
    } else {
      autoDropCounter++;
      if (autoDropCounter == PlayManager.dropInternal) {
        for (Block block : b) {
          block.y += Block.SIZE;
        }
        autoDropCounter = 0;
      }
    }
  }

  private void deactivating() {
    deactivateCounter++;

    // Wait 45 frames until deactivation
    if (deactivateCounter == 45) {
      deactivateCounter = 0;
      checkMovementCollision();

      if (bottomCollision) {
        active = false;
      }
    }
  }

  public void draw(Graphics2D g2) {
    g2.setColor(b[0].c);

    int margin = 2;
    for (Block block : b) {
      g2.fillRect(
          block.x + margin, block.y + margin, Block.SIZE - (margin * 2), Block.SIZE - (margin * 2));
    }
  }
}
