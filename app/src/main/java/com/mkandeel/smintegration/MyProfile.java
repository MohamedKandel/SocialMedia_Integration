package com.mkandeel.smintegration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class MyProfile extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient client;
    private GoogleSignInOptions options;
    private ImageView img;
    private TextView txt_name;
    private TextView txt_mail;
    private TextView txt_ID;
    private TextView txtViewname;
    private TextView txtViewmail;
    private TextView txtViewID;
    private Button btn_google;
    private Button btn_facebook;
    private LinearLayout layout;
    private String type;
    private AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        img = findViewById(R.id.img);
        txt_name = findViewById(R.id.txt_name);
        txt_mail = findViewById(R.id.txt_mail);
        txt_ID = findViewById(R.id.txt_ID);
        btn_google = findViewById(R.id.btn_google);
        btn_facebook = findViewById(R.id.btn_facebook);
        layout = findViewById(R.id.layout);
        txtViewname = findViewById(R.id.textViewName);
        txtViewmail = findViewById(R.id.textViewMail);
        txtViewID = findViewById(R.id.textViewID);

        type = getIntent().getStringExtra("type");

        if (type.equals("Google")) {

            layout.removeViewAt(5);

            options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail().build();

            client = new GoogleApiClient.Builder(this).enableAutoManage(
                    this, this::onConnectionFailed
            ).addApi(Auth.GOOGLE_SIGN_IN_API, options).build();

            btn_google.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Auth.GoogleSignInApi.signOut(client).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Intent intent = new Intent(MyProfile.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MyProfile.this, "Failed to Logout",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else {
            //Login With Facebook
            layout.removeViewAt(4);
            layout.removeViewAt(2);

            txtViewID.setTextSize(24);
            txtViewname.setTextSize(24);

            txt_ID.setTextSize(24);
            txt_name.setTextSize(24);

            String uID = getIntent().getExtras().getString("userID", "");
            accessToken = getIntent().getExtras().getParcelable("AccessToken");
            //Toast.makeText(this,uID,Toast.LENGTH_SHORT).show();*/
            txt_ID.setText(uID);
            String img_url = "https://graph.facebook.com/" + uID + "/picture?type=large";
            Picasso.get()
                    .load(img_url)
                    .into(img);

            GraphRequest request = GraphRequest.newMeRequest(accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Profile profile = Profile.getCurrentProfile();
                            String name = profile.getFirstName()+
                                    " "+profile.getMiddleName()+" " + profile.getLastName();
                            txt_name.setText(name);
                        }
                    });
            request.executeAsync();

            btn_facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(MyProfile.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null) {
                txt_mail.setText(account.getEmail());
                txt_name.setText(account.getDisplayName());
                txt_ID.setText(account.getId());
                Picasso.get()
                        .load(account.getPhotoUrl())
                        .into(img);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (type.equals("Google")) {
            OptionalPendingResult<GoogleSignInResult> result = Auth.GoogleSignInApi.silentSignIn(client);
            if (result.isDone()) {
                GoogleSignInResult mresult = result.get();
                handleSignInResult(mresult);
            } else {
                result.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(@NonNull GoogleSignInResult result) {
                        handleSignInResult(result);
                    }
                });
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (type.equals("facebook")) {
            LoginManager.getInstance().logOut();
        }
    }
}