package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import ca.cmpt276.walkinggroup.dataobjects.User;

public class UserinfoActivity extends AppCompatActivity {
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        user = User.getInstance();
        Toast.makeText(UserinfoActivity.this, "Email:"+user.getEmail(), Toast.LENGTH_SHORT).show();

    }
    public static Intent makeIntent(Context context){
        return new Intent(context, UserinfoActivity.class);
    }

}
