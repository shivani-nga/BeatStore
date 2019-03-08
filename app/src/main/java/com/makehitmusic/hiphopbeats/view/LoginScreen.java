package com.makehitmusic.hiphopbeats.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.makehitmusic.hiphopbeats.R;

public class LoginScreen extends Activity {

    Button facebookButton, googleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        facebookButton = (Button) findViewById(R.id.facebook_login);
        googleButton = (Button) findViewById(R.id.google_login);

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);
                Intent i = new Intent(LoginScreen.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);
                Intent i = new Intent(LoginScreen.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        });
    }
}
