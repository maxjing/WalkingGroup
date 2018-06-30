package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        Button btnLogout = (Button)findViewById(R.id.btnLogout);
        if (token==""){
            Toast.makeText(MainActivity.this,"no token",Toast.LENGTH_LONG).show();
            btnLogout.setVisibility(View.GONE);
        }else{
            Toast.makeText(MainActivity.this,token,Toast.LENGTH_LONG).show();
            btnLogout.setVisibility(View.VISIBLE);
        }

        setGroupBtn();
        setDevBtn();
        setMapButton();

    }
        setLogoutBtn();


    private void setMapButton() {
        Button btn = findViewById(R.id.google_map);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,GoogleMapsActivity.class));
            }
        });
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, MainActivity.class);
    }
    private void setGroupBtn(){
        if (token==""){
            Toast.makeText(MainActivity.this,"no token",Toast.LENGTH_LONG).show();
        }
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

    private void setLogoutBtn(){
        Button btnLogout = (Button)findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences dataToSave = getApplicationContext().getSharedPreferences("userPref",0);
                SharedPreferences.Editor PrefEditor = dataToSave.edit();
                PrefEditor.putString("userToken","");
                PrefEditor.apply();
                Toast.makeText(MainActivity.this,"Log out success",Toast.LENGTH_LONG).show();
                Intent intentToLogin = LoginActivity.makeIntent(MainActivity.this);
                startActivity(intentToLogin);
                finish();

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
                finish();
            }
        });
    }
}
