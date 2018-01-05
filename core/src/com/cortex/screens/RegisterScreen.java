package com.cortex.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.cortex.Vortex;

/**
 * Created by tanner on 5/17/17.
 */
public class RegisterScreen extends VortexScreen {

  Skin skin;

  public RegisterScreen(AssetManager assets) {
    super(512, 288);

    this.assets = assets;
    this.skin = this.assets.get("android/assets/skins/uiskin.json");

    buildStage();
  }


  public void buildStage() {

    final TextButton reg = new TextButton("Register", skin);
    final TextButton back = new TextButton("Back", skin);
    final TextField user = new TextField("Username", skin);
    user.setName("Username");
    final TextField pass = new TextField("Password", skin);
    pass.setName("Password");
    final TextField verify = new TextField("Re-type Password", skin);
    verify.setName("Re-type Password");
    final Label response = new Label("", skin);
    response.setName("SERVER_RESPONSE");

    user.setPosition(
        (this.getWidth() / 2) - user.getWidth() / 2,
        225);
    user.addListener(new FocusListener() {
      public void keyboardFocusChanged(FocusEvent e, Actor a, boolean f) {
        if (e.isFocused() && user.getText().equals("Username"))
          user.setText("");
        else if (!e.isFocused() && user.getText().equals(""))
          user.setText("Username");
      }
    });

    pass.setPosition(
        (this.getWidth() / 2) - pass.getWidth() / 2,
        190);
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

    verify.setPosition(
        (this.getWidth() / 2) - pass.getWidth() / 2,
        155);
    verify.addListener(new FocusListener() {
      public void keyboardFocusChanged(FocusEvent e, Actor a, boolean f) {
        if (e.isFocused() && verify.getText().equals("Re-type Password")) {
          verify.setText("");
          verify.setPasswordCharacter('*');
          verify.setPasswordMode(true);
        } else if (!e.isFocused() && verify.getText().equals("")) {
          verify.setText("Re-type Password");
          verify.setPasswordMode(false);
        }
      }
    });


    reg.setWidth(pass.getWidth());
    reg.setHeight(40);
    reg.setPosition(
        (this.getWidth() / 2) - reg.getWidth() / 2,
        105);
    reg.addListener(new ClickListener() {
      public void clicked(InputEvent e, float x, float y) {

        if (user.getText().equals("")
            || pass.getText().equals("")
            || verify.getText().equals("")
            || user.getText().equalsIgnoreCase("username")
            || pass.getText().equalsIgnoreCase("password")
            || verify.getText().equalsIgnoreCase("re-type password") ) {

          response.setText("ERROR: You must enter a username and password!");
          return;
        }

        if (user.getText().length() > 16) {
          response.setText("Username too long! Must be under 16 characters.");
          return;
        }

        if (!pass.getText().equals(verify.getText())) {
          response.setText("Passwords do not match!");
          return;
        }
        //Toren.send(PacketCreator.registerRequest(user.getText(), pass.getText()));
      }
    });

    back.setHeight(40);
    back.setPosition(
        (this.getWidth() / 2) - back.getWidth() / 2,
        40);
    back.addListener(new ClickListener() {
      public void clicked(InputEvent e, float x, float y) {
        Vortex.screenManager.showScreen(Screens.LOGIN);
      }
    });

    response.setPosition(
        (this.getViewport().getWorldWidth() / 2) - response.getWidth() / 2, 10);
    response.setAlignment(1);

    this.addActor(user);
    this.addActor(pass);
    this.addActor(verify);
    this.addActor(reg);
    this.addActor(back);
    this.addActor(response);
    Gdx.input.setInputProcessor(this);
  }
}
