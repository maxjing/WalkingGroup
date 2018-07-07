package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Handding login process by calling server
 * Set useremail local for future use
 * Set token for future user
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LOGIN";

    private User user;
    private String userEmail;
    private String userPassword="secret...JustKidding,That'sTooEasyToGuess!";
    private String userToken;
    private long userId = 0;
    private WGServerProxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GetPref();
        user = User.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), userToken);

        setLoginBtn();
        setRegisterBtn();


    }
    private void setRegisterBtn(){
        Button btnRegister = (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = RegisterActivity.makeIntent(LoginActivity.this);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setLoginBtn(){
        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                EditText emailInput = (EditText)findViewById(R.id.editTextEmail);
                EditText passwordInput = (EditText)findViewById(R.id.editTextPassword);

                userEmail = emailInput.getText().toString();
                userPassword = passwordInput.getText().toString();

                user = User.getInstance();
                user.setEmail(userEmail);
                user.setPassword(userPassword);

                // Register for token received:
                ProxyBuilder.setOnTokenReceiveCallback( token -> onReceiveToken(token));
                // Make call
                Call<Void> caller = proxy.login(user);
                ProxyBuilder.callProxy(LoginActivity.this, caller, returnedNothing -> response(returnedNothing));


            }
        });
    }
    // Handle the token by generating a new Proxy which is encoded with it.
    private void onReceiveToken(String token) {
        // Replace the current proxy with one that uses the token!
        Log.w(TAG, "   --> NOW HAVE TOKEN: " + token);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        userToken = token;
        savePref();



    }

    // Login actually completes by calling this; nothing to do as it was all done
    // when we got the token.
    private void response(Void returnedNothing) {

        Call<User> caller = proxy.getUserByEmail(userEmail);
        ProxyBuilder.callProxy(LoginActivity.this, caller, returnedUser -> response(returnedUser));



    }

    private void response(User user) {
        userId = user.getId();
        savePref();
        Intent intent = MainActivity.makeIntent(LoginActivity.this);
        startActivity(intent);
        finish();


    }


    public void savePref(){
        SharedPreferences dataToSave = getApplicationContext().getSharedPreferences("userPref",0);
        SharedPreferences.Editor PrefEditor = dataToSave.edit();
        PrefEditor.putString("userToken",userToken);
        PrefEditor.putString("userEmail",userEmail);
        PrefEditor.putLong("userId",userId);


        PrefEditor.apply();

    }
    public void GetPref(){
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        if (dataToGet==null)return;
        userToken = dataToGet.getString("userToken","");


    }



    public static Intent makeIntent(Context context){
        return new Intent(context, LoginActivity.class);
    }
}
