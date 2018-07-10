package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MessagesActivity extends AppCompatActivity {
    private String TAG = "MessagesActivity";
    private String token;
    private WGServerProxy proxy;
    private Long userId;
    private Message message;
    private User user;
    private List<String> messagesList;
    private List<Message> messagesItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        userId = dataToGet.getLong("userId", 0);

        user = User.getInstance();
        Call<User> caller_user = proxy.getUserById(userId);
        ProxyBuilder.callProxy(MessagesActivity.this, caller_user, returnedUser -> response(returnedUser));


        setMsgBtn();
    }


    private void setMsgBtn(){
        Button btnGroup = (Button)findViewById(R.id.btnSend);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = MessagesNewActivity.makeIntent(MessagesActivity.this);
                startActivity(intent);

            }
        });
    }

    private void populateMsg() {
        ArrayAdapter<String> adapterLeader = new ArrayAdapter<>(this, R.layout.messages, messagesList);
        ListView listLeader = (ListView) findViewById(R.id.listView_Messages);
        listLeader.setAdapter(adapterLeader);
    }






    public static Intent makeIntent(Context context){
        return new Intent(context, MessagesActivity.class);
    }

    private void response(User returnedUser) {
        user = returnedUser;

            Call<List<Message>> caller = proxy.getMessages(user.getId());
            ProxyBuilder.callProxy(MessagesActivity.this, caller, returnedMsg -> response(returnedMsg));


    }

    private void response(List<Message> returnedMsg){
        messagesList = new ArrayList<>(returnedMsg.size());
        try {
        for(int i = 0; i<returnedMsg.size();i++){
            messagesList.add(returnedMsg.get(i).getText());
        }
        } catch (Exception e) {

        }

        populateMsg();
    }






}
