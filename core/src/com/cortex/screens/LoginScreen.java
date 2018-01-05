package com.cortex.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.cortex.Vortex;
import com.cortex.client.packets.LoginRequest;

/**
 * Created by tanner on 5/17/17.
 */
public class LoginScreen extends VortexScreen {

  private Skin skin;

  public LoginScreen(AssetManager assets) {
    super(512, 288);

    this.assets = assets;
    this.skin = this.assets.get("android/assets/skins/uiskin.json");

    buildStage();
  }

  public void buildStage() {

    final TextButton btn = new TextButton("Login", skin);
    final TextButton reg = new TextButton("Create Account", skin);
    final TextField user = new TextField("Username", skin);
    final TextField pass = new TextField("Password", skin);
    final Label response = new Label("", skin);

    user.setPosition(
        (this.getWidth() / 2) - user.getWidth() / 2,
        170);
    user.addListener(new FocusListener() {
      public void keyboardFocusChanged(FocusEvent e, Actor a, boolean f) {
        if (e.isFocused() && user.getText().equals("Username"))
          user.setText("");
        else if (!e.isFocused() && user.getText().equals(""))
          user.setText("Username");
      }
    });
    user.setName("Username");

    pass.setPosition(
        (this.getWidth() / 2) - pass.getWidth() / 2,
        135);
    pass.addListener(new InputListener() {
      public boolean keyUp(InputEvent event, int keycode) {
        if (keycode == Input.Keys.ENTER) {
          if (user.getText().equals("") || pass.getText().equals("")
              || user.getText().equalsIgnoreCase("username")
              || pass.getText().equalsIgnoreCase("password") ) {
            response.setText("ERROR: You must enter a username and password!");
            return false;
          }
          // send login request
          LoginRequest lr = new LoginRequest();
          lr.username = user.getText();
          lr.password = pass.getText();
          Vortex.client.sendTCP(lr);
          return true;
        }
        return false;
      }
    });
    pass.addListener(new FocusListener() {
      public void keyboardFocusChanged(FocusEvent e, Actor a, boolean f) {
        if (e.isFocused() && pass.getText().equals("Password")) {
          pass.setText("");
          pass.setPasswordCharacter('*');
          pass.setPasswordMode(true);
        } else if (!e.isFocused() && pass.getText().equals("")) {
          pass.setText("Password");
          pass.setPasswordMode(false);
        }
      }
    });

    btn.setWidth(pass.getWidth());
    btn.setHeight(40);
    btn.setPosition(
        (this.getWidth() / 2) - btn.getWidth() / 2,
        85);
    btn.addListener(new ClickListener() {
      public void clicked(InputEvent e, float x, float y) {
        if (user.getText().equals("") || pass.getText().equals("")
            || user.getText().equalsIgnoreCase("username")
            || pass.getText().equalsIgnoreCase("password") ) {
          response.setText("ERROR: You must enter a username and password!");
          return;
        }
        LoginRequest lr = new LoginRequest();
        lr.username = user.getText();
        lr.password = pass.getText();
        Vortex.client.sendTCP(lr);
      }
    });

    reg.setHeight(30);
    reg.setPosition(
        (this.getWidth() / 2) - reg.getWidth() / 2,
        40);
    reg.addListener(new ClickListener() {
      public void clicked(InputEvent e, float x, float y) {
        Vortex.screenManager.showScreen(Screens.REGISTER);
      }
    });

    response.setPosition(
        (this.getViewport().getWorldWidth() / 2) - response.getWidth() / 2, 10);
    response.setAlignment(1);
    response.setName("SERVER_RESPONSE");

    this.addActor(user);
    this.addActor(pass);
    this.addActor(btn);
    this.addActor(reg);
    this.addActor(response);
    Gdx.input.setInputProcessor(this);
  }

}
