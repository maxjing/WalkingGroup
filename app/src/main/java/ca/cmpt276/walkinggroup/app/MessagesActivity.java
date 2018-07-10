package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

public class MessagesActivity extends AppCompatActivity {

    private String token;
    private WGServerProxy proxy;
    private Long userId;
    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        userId = dataToGet.getLong("userId", 0);

        message = new Message();

        setSendMsgBtn();
    }


    private void setSendMsgBtn(){
        Button btnGroup = (Button)findViewById(R.id.btnSend);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = MessagesNewActivity.makeIntent(MessagesActivity.this);
                startActivity(intent);

            }
        });
    }





    public static Intent makeIntent(Context context){
        return new Intent(context, MessagesActivity.class);
    }




}
