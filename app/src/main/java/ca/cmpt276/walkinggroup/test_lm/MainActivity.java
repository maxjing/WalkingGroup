package ca.cmpt276.walkinggroup.test_lm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        if (token==""){
            Toast.makeText(MainActivity.this,"no token",Toast.LENGTH_LONG).show();
        }
        Toast.makeText(MainActivity.this,token,Toast.LENGTH_LONG).show();
        setGroupBtn();
        setDevBtn();

    }
    public static Intent makeIntent(Context context){
        return new Intent(context, MainActivity.class);
    }
    private void setGroupBtn(){
        Button btnDev = (Button)findViewById(R.id.btnGroup);
        btnDev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch (token){
                    case "":
                        Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                        startActivity(intentToLogin);
                        break;

                }

            }
        });
    }
    private void setDevBtn(){
        Button btnDev = (Button)findViewById(R.id.btnDev);
        btnDev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this,ServerTestActivity.class);
                startActivity(intent);
            }
        });
    }
}
