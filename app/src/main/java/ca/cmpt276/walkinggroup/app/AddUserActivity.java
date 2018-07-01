package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class AddUserActivity extends AppCompatActivity {

    private WGServerProxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        Call<List<User>> caller = proxy.getUsers();
        Log.i("Log",caller.toString());
        setCancelBtn();
        setOKBtn();
    }

    private void setCancelBtn() {
        Button btn = (Button) findViewById(R.id.btnCancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setOKBtn() {
        Button btn = (Button) findViewById(R.id.btnOK);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Extract data from UI
                EditText editN = (EditText) findViewById(R.id.editName);
                String editName = editN.getText().toString();
                EditText editW = (EditText) findViewById(R.id.editEmail);
                String editWeight = editW.getText().toString();

//                Call<List<User>> caller = proxy.getUsers();
//                Log.i("Log",caller.toString());
            }
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context,AddUserActivity.class);
    }
}