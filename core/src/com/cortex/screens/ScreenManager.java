package com.cortex.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by tanner on 5/11/17.
 *
 * This is a ScreenManager object used to switch between a login, world, register, etc. screen.
 */
public class ScreenManager {
  private Game game;
  private AssetManager assets;
  private VortexScreen[] screens;

  public ScreenManager(Game game) {
    this.game = game;
    this.assets = new AssetManager();
    // LOAD ASSETS
    assets.load("android/assets/skins/uiskin.json", Skin.class);
    assets.finishLoading();
    loadTexturesRecursively("android/assets/character");
    loadTexturesRecursively("android/assets/equipment");

    // Initialize Screens
    int max = 0;
    for (Screens e : Screens.values())
      if (e.getValue() > max)
        max = e.getValue();

    screens = new VortexScreen[max + 1];
    screens[Screens.LOGIN.getValue()] = new LoginScreen(assets);
    screens[Screens.REGISTER.getValue()] = new RegisterScreen(assets);
  }

  public AssetManager getAssets() {
    return assets;
  }

  public Game getGame() {
    return game;
  }

  public void showScreen(Screens screenEnum) {
    game.setScreen(screens[screenEnum.getValue()]);
  }

  /*public void loadWorldScreen(PlayerController player) {
    screens[Screens.WORLD_GAME.getValue()] = new WorldScreen(player);
  }*/

  private void loadTexturesRecursively(String path) {
    for (FileHandle f : Gdx.files.internal(path).list()) {
      if (f.isDirectory())
        loadTexturesRecursively(path + "/" + f.name());
      else
        assets.load(f.path(), Texture.class);
    }
  }

}
