package com.cortex;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tanner on 5/15/17.
 */
public class PhysicsThread extends Thread {

  public static final int TICK_FREQUENCY = 20;
  private boolean running;

  private long dt, currentTime, accumulator;
  private int tick;

  // state shit

  public PhysicsThread() {
    running = false;
  }

  @Override
  public void run() {
    running = true;
    tick = 0;
    dt = 1000000000 / TICK_FREQUENCY;

    currentTime = System.nanoTime();
    accumulator = 0;

    while (running) {
      long newTime = System.nanoTime();
      long frameTime = newTime - currentTime;
      if (frameTime > 250000000) //  limit to 250ms
        frameTime = 250000000;
      currentTime = newTime;

      accumulator += frameTime;

      while (accumulator >= dt) {
        // prevState = currState;
        // calc next state
        tick++;
        accumulator -= dt;
      }
      try {
        Thread.yield();
        Thread.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
