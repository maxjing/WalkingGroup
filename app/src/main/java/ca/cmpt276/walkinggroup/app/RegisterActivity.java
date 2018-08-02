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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.app.DialogFragment.MyToast;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Handle Register, create new user, call server to established
 * Able to back to login, if cancel pressed
 */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "Register";
    private User user;
    private long userId = 0;
    private String userEmail;
    private String userPassword;
    private String userName;

    private WGServerProxy proxy;
    private Session session;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        session = Session.getInstance();
        user = new User();
        proxy = session.getProxy();

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
                finish();
            }
        });
    }
    private void setRegisterBtn(){
        Button btnRegister= (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                user = new User();

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

        userId = user.getId();
        userEmail = user.getEmail();
        user.setId(user.getId());
        Intent intent = LoginActivity.makeIntent(RegisterActivity.this);
        MyToast.makeText(RegisterActivity.this, R.string.regSuccess, Toast.LENGTH_LONG).show();
        startActivity(intent);
        finish();


    }

    public static Intent makeIntent(Context context){
        return new Intent(context, RegisterActivity.class);
    }


}
