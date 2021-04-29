/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnKeyListener {

  Boolean signUpModeActive = true;
  TextView logInTextView;
  EditText usernameEditText;
  EditText passwordEditText;

  //To switch to screen with the list of users after login or sign up or if user is already logged in.
  public void showUserList()
  {
    Intent intent = new Intent(getApplicationContext(),UserListActivity.class);
    startActivity(intent);
  }

  //For Keyboard settings.
  //When enter key is clicked after entering password, automatically submit the form.
  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {
    if(i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)
    {
      signUpClick(view);
    }

    return false;
  }

  //To toggle between log in and signUp.
  @Override
  public void onClick(View view) {
    if(view.getId() == R.id.logInTextView)
    {
      Button signUpButton = (Button) findViewById(R.id.signUpButton);
      if(signUpModeActive)
      {
        //Switching From signup to login
        signUpModeActive = false;
        signUpButton.setText("Log In");
        logInTextView.setText("or Sign Up");
      }
      else
      {
        //Switching from login to signup
        signUpModeActive = true;
        signUpButton.setText("Sign Up");
        logInTextView.setText("or Log In");
      }

    }
    else if(view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout)
    {
      //For Keyboard settings.
      //When touched on any other part of screen close the keyboard
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
  }

  //When signup or login buttom is clicked
  public void signUpClick(View view)
  {

    if(usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches(""))
    {
      //If username or password is emmpty show error
      Toast.makeText(this,"A username and a Password are required",Toast.LENGTH_SHORT).show();
    }
    else
    {
      if(signUpModeActive) {
        //Signup the User
        ParseUser user = new ParseUser();
        user.setUsername(usernameEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Log.i("Signup", "Success");
              showUserList();    //Move to next activity
            } else {
              //Show error
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      }
      else
      {
        //Login the user
        ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if(user!=null)
            {
              Log.i("Logged in","okay");
              showUserList();
            }
            else
            {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      }
    }

  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("InstaClone");

    logInTextView = (TextView) findViewById(R.id.logInTextView);
    logInTextView.setOnClickListener(this);

    usernameEditText = (EditText) findViewById(R.id.usernameEditText);
    passwordEditText = (EditText) findViewById(R.id.passwordEditText);
    ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);
    RelativeLayout backgroundLayout = (RelativeLayout) findViewById(R.id.backgroundLayout);

    //To close keyboard.
    logoImageView.setOnClickListener(this);
    backgroundLayout.setOnClickListener(this);


    //To enter automatically after filling the password.
    passwordEditText.setOnKeyListener(this);

    //If already signed in. Go move to next activity
    if(ParseUser.getCurrentUser() !=null)
    {
      showUserList();
    }


    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}