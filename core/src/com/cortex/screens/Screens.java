package com.cortex.screens;

/**
 * Created by tanner on 5/11/17.
 */
public enum Screens {

  LOGIN(0),
  REGISTER(1),
  CHARACTER_SELECT(2),
  CREATE_CHARACTER(3),
  WORLD(4);

  private int id = -1;

  private Screens(int id) {
    this.id = id;
  }

  public int getValue() {
    return id;
  }

}

