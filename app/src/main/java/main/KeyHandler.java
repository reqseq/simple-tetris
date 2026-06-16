package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
  public static boolean upPressed, downPressed, leftPressed, rightPressed, pausePressed;

  @Override
  public void keyPressed(KeyEvent e) {
    int code = e.getKeyCode();
    switch (code) {
      case KeyEvent.VK_W -> upPressed = true;
      case KeyEvent.VK_A -> leftPressed = true;
      case KeyEvent.VK_S -> downPressed = true;
      case KeyEvent.VK_D -> rightPressed = true;
      case KeyEvent.VK_SPACE -> {
        pausePressed = !pausePressed;
        if (pausePressed) {
          GamePanel.music.stop();
        } else {
          GamePanel.music.play(0, true);
          GamePanel.music.loop();
        }
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {}

  @Override
  public void keyTyped(KeyEvent e) {}
}
