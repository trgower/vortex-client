package com.cortex.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;

/**
 * Created by tanner on 5/19/17.
 */
public class WorldScreen extends VortexScreen implements InputProcessor {

  //private PlayerController player;

  public WorldScreen(/*PlayerController player*/) {
    super(386, 216);

    //this.player = player;
    //addActor(player);

    Gdx.input.setInputProcessor(this);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // Trim actors outside culling area
    // Interpolate actors to current physics position
    // Render world
    // update camera
  }
}
