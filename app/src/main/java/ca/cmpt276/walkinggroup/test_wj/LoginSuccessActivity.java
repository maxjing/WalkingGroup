package ca.cmpt276.walkinggroup.test_wj;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ca.cmpt276.walkinggroup.app.R;

public class LoginSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);
        setLogoutBtn();
    }

    private void setLogoutBtn(){
        Button btnLogout = (Button)findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences dataToSave = getApplicationContext().getSharedPreferences("userPref",0);
                SharedPreferences.Editor PrefEditor = dataToSave.edit();
                PrefEditor.putString("userToken","");
                PrefEditor.apply();
                Intent intent = MainActivity.makeIntent(LoginSuccessActivity.this);
                startActivity(intent);

            }
        });
    }
    public static Intent makeIntent(Context context){
        return new Intent(context, LoginSuccessActivity.class);
    }

}
