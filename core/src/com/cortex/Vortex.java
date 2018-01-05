package com.cortex;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.cortex.client.VortexClient;
import com.cortex.client.packets.WorldSnapshot;
import com.cortex.screens.ScreenManager;
import com.cortex.screens.Screens;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class Vortex extends Game {

  public final static String SERVER_IP_ADDRESS = "localhost";
  public final static int SERVER_PORT_TCP = 5555;
  public final static int SERVER_PORT_UDP = 5556;

  public static ScreenManager screenManager;
  public static VortexClient client;

  //public static ConcurrentHashMap<Integer, Entity> entities = new ConcurrentHashMap<Integer, Entity>();

  @Override
  public void create() {
    Gdx.graphics.setVSync(true);
    screenManager = new ScreenManager(this);

    client = new VortexClient();
    client.start();
    try {
      client.connect(5000, SERVER_IP_ADDRESS, SERVER_PORT_TCP, SERVER_PORT_UDP);
    } catch (IOException e) {
      Gdx.app.error("LOGIN", e.getMessage());
    }

    screenManager.showScreen(Screens.LOGIN);
  }

}
