package com.mkandeel.smintegration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private SignInButton google_signin;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient client;
    private LoginButton facebook_signin;
    private CallbackManager manager;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        client = new GoogleApiClient.Builder(this).enableAutoManage(this,
                this::onConnectionFailed)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options).build();

        facebook_signin = findViewById(R.id.facebook_signin);
        google_signin = findViewById(R.id.google_signin);

        google_signin.setSize(SignInButton.SIZE_STANDARD);

        google_signin.setOnClickListener(this::onClick);
        facebook_signin.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_signin:
                type = "Google";
                SigInGoogle();
                break;
            case R.id.facebook_signin:
                type = "facebook";
                SignInFacebook();
                break;
        }
    }

    private void SignInFacebook() {
        manager = CallbackManager.Factory.create();
        facebook_signin.registerCallback(manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(MainActivity.this, MyProfile.class);
                String userID = loginResult.getAccessToken().getUserId();
                intent.putExtra("AccessToken",loginResult.getAccessToken());
                intent.putExtra("userID",userID);
                intent.putExtra("type","facebook");
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void SigInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(client);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (type.equals("facebook")) {
            manager.onActivityResult(requestCode, resultCode, data);
        } else {
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    Intent intent = new Intent(MainActivity.this, MyProfile.class);
                    intent.putExtra("type","Google");
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"Failed to connect",Toast.LENGTH_SHORT).show();
    }
}