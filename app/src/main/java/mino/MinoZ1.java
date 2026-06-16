package mino;

import java.awt.Color;

public class MinoZ1 extends Mino {
  public MinoZ1() {
    create(Color.RED);
  }

  @Override
  public void setXY(int x, int y) {
    /*
            @ (1)
      (2) @ @ (0)
      (3) @
    */

    b[0].x = x;
    b[0].y = y;
    b[1].x = b[0].x;
    b[1].y = b[0].y - Block.SIZE;
    b[2].x = b[0].x - Block.SIZE;
    b[2].y = b[0].y;
    b[3].x = b[0].x - Block.SIZE;
    b[3].y = b[0].y + Block.SIZE;
  }

  @Override
  public void getDirection1() {
    /*
            @ (1)
      (2) @ @ (0)
      (3) @
    */

    tempB[0].x = b[0].x;
    tempB[0].y = b[0].y;
    tempB[1].x = b[0].x;
    tempB[1].y = b[0].y - Block.SIZE;
    tempB[2].x = b[0].x - Block.SIZE;
    tempB[2].y = b[0].y;
    tempB[3].x = b[0].x - Block.SIZE;
    tempB[3].y = b[0].y + Block.SIZE;

    updateXY(1);
  }

  @Override
  public void getDirection2() {
    /*
      (3)(2)
       @  @
          @  @
         (0)(1)
    */

    tempB[0].x = b[0].x;
    tempB[0].y = b[0].y;
    tempB[1].x = b[0].x + Block.SIZE;
    tempB[1].y = b[0].y;
    tempB[2].x = b[0].x;
    tempB[2].y = b[0].y - Block.SIZE;
    tempB[3].x = b[0].x - Block.SIZE;
    tempB[3].y = b[0].y - Block.SIZE;

    updateXY(2);
  }

  @Override
  public void getDirection3() {
    getDirection1();
  }

  @Override
  public void getDirection4() {
    getDirection2();
  }
}
