package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "Register";
    private long userId = 0;
    private String userEmail;
    private String userPassword;
    private String userName;

    private WGServerProxy proxy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);

        setRegisterBtn();
        setBackBtn();

    }
    private void setBackBtn(){
        Button btnBack= (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = LoginActivity.makeIntent(RegisterActivity.this);
                startActivity(intent);
            }
        });
    }
    private void setRegisterBtn(){
        Button btnRegister= (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                User user = new User();

                EditText nameInput = (EditText)findViewById(R.id.editTextName);
                EditText emailInput = (EditText)findViewById(R.id.editTextEmail);
                EditText passwordInput = (EditText)findViewById(R.id.editTextPassword);

                userName = nameInput.getText().toString();
                userEmail = emailInput.getText().toString();
                userPassword = passwordInput.getText().toString();


                user.setEmail(userEmail);
                user.setName(userName);
                user.setPassword(userPassword);

                // Make call
                Call<User> caller = proxy.createUser(user);
                ProxyBuilder.callProxy(RegisterActivity.this, caller, returnedUser -> response(returnedUser));
            }
        });
    }
    private void response(User user) {
        notifyUserViaLogAndToast("Server replied with user: " + user.toString());
        userId = user.getId();
        userEmail = user.getEmail();
        Intent intent = LoginActivity.makeIntent(RegisterActivity.this);
        startActivity(intent);

    }
    private void notifyUserViaLogAndToast(String message) {
        Log.w(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    public static Intent makeIntent(Context context){
        return new Intent(context, RegisterActivity.class);
    }


}
