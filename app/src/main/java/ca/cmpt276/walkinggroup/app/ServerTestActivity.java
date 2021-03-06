package ca.cmpt276.walkinggroup.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.app.DialogFragment.MyToast;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Demonstrates how to place calls to the server via the proxy.
 * Basic design:
 * - When you press the button, it executes the call. The 'response()' function then
 *   gets the callback when the call is done.
 * - Errors get logged and have a toast. Real applications should do something more with the errors.
 *   (Likely need to change the code in the ProxyBuilder class.
 * - When logging in, it calls back the onReceiveToken() function which we must then
 *   create a new proxy (via ProxyBuider) and let the old one be destroyed because we must
 *   set the token when its created.
 *
 * Some changes may be required to your 'build.gradle (Module: app)" file
 *    Compare the gradle file of this application and yours.
 */
public class ServerTestActivity extends AppCompatActivity {
    private static final String TAG = "ServerTest";
    private User user;
    private long userId = 0;
    private String userEmail;
    private String userPassword = "secret...JustKidding,That'sTooEasyToGuess!";

    private WGServerProxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_test);

//        // Build the server proxy
//        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);
//
//        // Setup UI buttons to test it.
//        setupNewUserButton();
//        setupLoginButton();
//        setupListUsers();
//        setupGetUserByEmail();
//        setupGetUserById();
//        setbtntoLogin();
    }

    // Switch to 2nd activity to help show simple interaction (getting API key)
    // ------------------------------------------------------------------------------------------
    // Note: Your application will be hard-coded with your API key, you don't need to interactively
    // ask the server for it! This is just an easy way to distribute API keys.


    // Create a new user on the server
    // ------------------------------------------------------------------------------------------
//    private void setupNewUserButton() {
//        Button btn = findViewById(R.id.btnNewUser);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Build new user (with random email to avoid conflicts)
//                user = User.getInstance();
//                int random = (int) (Math.random() * 100000);
//                user.setEmail("testuser"+random+"@test.com");
//                user.setName("I. B. Rocking");
//                user.setPassword(userPassword);
//                user.setCurrentPoints(100);
//                user.setTotalPointsEarned(2500);
//                user.setRewards(new EarnedRewards());
//
//                // Make call
//                Call<User> caller = proxy.createUser(user);
//                ProxyBuilder.callProxy(ServerTestActivity.this, caller, returnedUser -> response(returnedUser));
//            }
//        });
//    }
    private void response(User user) {
        notifyUserViaLogAndToast(getString(R.string.replied)+" "+ user.toString());
        userId = user.getId();
        userEmail = user.getEmail();
    }


    // Login to get a Token
    // ------------------------------------------------------------------------------------------
    // When server sends us a token, have the proxy store it for future use.
    // The token identifies us as a logged in user without having to revalidate passwords all the time.
//    private void setupLoginButton() {
//        Button btn = findViewById(R.id.btnLogin);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Build new user
//                user = User.getInstance();
//                user.setEmail(userEmail);
//                user.setPassword(userPassword);
//                // Register for token received:
//                ProxyBuilder.setOnTokenReceiveCallback( token -> onReceiveToken(token));
//
//                // Make call
//                Call<Void> caller = proxy.login(user);
//                ProxyBuilder.callProxy(ServerTestActivity.this, caller, returnedNothing -> response(returnedNothing));
//            }
//        });
//    }

    // Handle the token by generating a new Proxy which is encoded with it.
    private void onReceiveToken(String token) {
        // Replace the current proxy with one that uses the token!
        Log.w(TAG, "   --> NOW HAVE TOKEN: " + token);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
    }

    // Login actually completes by calling this; nothing to do as it was all done
    // when we got the token.
    private void response(Void returnedNothing) {
        notifyUserViaLogAndToast("Server replied to login request (no content was expected).");
    }


    // List all users
    // ------------------------------------------------------------------------------------------
    private void setupListUsers() {
        Button btn = findViewById(R.id.btnListUsers);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make call
                Call<List<User>> caller = proxy.getUsers();
                ProxyBuilder.callProxy(ServerTestActivity.this, caller, returnedUsers -> response(returnedUsers));
            }
        });
    }

    private void response(List<User> returnedUsers) {
        notifyUserViaLogAndToast(getString(R.string.list) +" "+ returnedUsers.size() + " "+getString(R.string.logcat));
        Log.w(TAG, "All Users:");
        for (User user : returnedUsers) {
            Log.w(TAG, "    User: " + user.toString());
        }
    }


//     Get user by Email
//     ------------------------------------------------------------------------------------------
//     Response goes the same function as created above for user responses.
    private void setupGetUserByEmail() {
        Button btn = findViewById(R.id.btnGetUserByEmail);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make call
                Call<User> caller = proxy.getUserByEmail(userEmail);
                ProxyBuilder.callProxy(ServerTestActivity.this, caller, returnedUser -> response(returnedUser));
            }
        });
    }

    // Get user by ID
    // ------------------------------------------------------------------------------------------
    // Response goes the same function as created above for user responses.
    private void setupGetUserById() {
        Button btn = findViewById(R.id.btnGetUserById);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make call
                Call<User> caller = proxy.getUserById(userId);
                ProxyBuilder.callProxy(ServerTestActivity.this, caller, returnedUser -> response(returnedUser));
            }
        });
    }



    // Put message up in toast and logcat
    // -----------------------------------------------------------------------------------------
    private void notifyUserViaLogAndToast(String message) {
        Log.w(TAG, message);
        MyToast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void setbtntoLogin(){
        Button btntoLogin= (Button)findViewById(R.id.btntoLogin);
        btntoLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ServerTestActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}
