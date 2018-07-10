package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MessagesNewActivity extends AppCompatActivity {
    private String token;
    private WGServerProxy proxy;
    private Long userId;
    private Message message;
    private List<Group> groupsMember;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_new);

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
        token = dataToGet.getString("userToken","");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        userId = dataToGet.getLong("userId", 0);
        Call<User> caller_user = proxy.getUserById(userId);
        ProxyBuilder.callProxy(MessagesNewActivity.this, caller_user, returnedUser -> response(returnedUser));
        message = new Message();
        setSendToGroup();
        setSendToPLBtn();
    }




    public static Intent makeIntent(Context context){
        return new Intent(context, MessagesNewActivity.class);
    }

    private void setSendToPLBtn(){
        Button btnGroup = (Button)findViewById(R.id.btnSendToPL);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                EditText editMsg = (EditText)findViewById(R.id.editTextMsg);
                String msg = editMsg.getText().toString();
                message.setText(msg);
                Call<List<Message>> caller = proxy.newMessageToParentsOf(userId,message);
                ProxyBuilder.callProxy(MessagesNewActivity.this, caller, returnedMsg -> response(returnedMsg));
                groupsMember = user.getMemberOfGroups();

                try {
                    for(int i = 0; i < groupsMember.size(); i++){
                        Call<List<Message>> caller_groupLeader = proxy.newMessageToParentsOf(groupsMember.get(i).getLeader().getId(),message);
                        ProxyBuilder.callProxy(MessagesNewActivity.this, caller_groupLeader, returnedMsg -> response(returnedMsg));
                    }

                } catch (Exception e) {

                }


            }
        });
    }
    private void setSendToGroup(){
        Button btnGroup = (Button)findViewById(R.id.btnSendToGroup);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = GroupActivity.makeIntent(MessagesNewActivity.this);
                startActivity(intent);

            }
        });
    }
    private void response(List<Message> returnedMsg) {

    }
    private void response(User returnedUser) {

        user = returnedUser;

    }




}
