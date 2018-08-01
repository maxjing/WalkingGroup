package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import ca.cmpt276.walkinggroup.app.DialogFragment.MyToast;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * able to send a new message to a group or parents and leaders
 */

public class MessagesNewActivity extends AppCompatActivity {
    private String token;
    private WGServerProxy proxy;
    private Long userId;
    private Message message;
    private List<Group> groupsMember;
    private User user;
    private Session session;
    private EarnedRewards current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_new);

        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();

        backGround();

        message = new Message();
        setSendToGroup();
        setSendToPLBtn();
        setCancel();
    }

    private void backGround() {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(MessagesNewActivity.this,caller,returned -> responseForGet(returned));
    }

    private void responseForGet(User returned) {
        Gson gson = new Gson();
        String json = returned.getCustomJson();
        current = gson.fromJson(json, EarnedRewards.class);
        changeBackGround();
    }

    private void changeBackGround(){
        RelativeLayout layout = findViewById(R.id.messages_new_layout);
        MyToast.makeText(this,""+current.getSelectedBackground(),Toast.LENGTH_SHORT).show();
        if(current.getSelectedBackground() == 0){
            layout.setBackground(getResources().getDrawable(R.drawable.background0));
        }
        if(current.getSelectedBackground() == 1){
            layout.setBackground(getResources().getDrawable(R.drawable.background1));
        }
        if(current.getSelectedBackground() == 2){
            layout.setBackground(getResources().getDrawable(R.drawable.background2));
        }
        if(current.getSelectedBackground() == 3){
            layout.setBackground(getResources().getDrawable(R.drawable.background3));
        }
        if(current.getSelectedBackground() == 4){
            layout.setBackground(getResources().getDrawable(R.drawable.background4));
        }
        if(current.getSelectedBackground() == 5){
            layout.setBackground(getResources().getDrawable(R.drawable.background5));
        }
        if(current.getSelectedBackground() == 6){
            layout.setBackground(getResources().getDrawable(R.drawable.background6));
        }

    }


    private void setSendToPLBtn(){
        Button btnGroup = (Button)findViewById(R.id.btnSendToPL);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                EditText editMsg = (EditText)findViewById(R.id.editTextMsg);
                String msg = editMsg.getText().toString();


                if(msg.matches("")) {

                    MyToast.makeText(MessagesNewActivity.this, "Message can not be empty", Toast.LENGTH_SHORT).show();

                }else{
                    message.setText(msg);
                    Call<List<Message>> caller = proxy.newMessageToParentsOf(userId, message);
                    ProxyBuilder.callProxy(MessagesNewActivity.this, caller, returnedMsg -> response(returnedMsg));
                    groupsMember = user.getMemberOfGroups();

                    try {
                        for (int i = 0; i < groupsMember.size(); i++) {
                            Call<List<Message>> caller_groupLeader = proxy.newMessageToParentsOf(groupsMember.get(i).getLeader().getId(), message);
                            ProxyBuilder.callProxy(MessagesNewActivity.this, caller_groupLeader, returnedMsg -> response(returnedMsg));
                        }

                    } catch (Exception e) {

                    }
                }


            }
        });
    }
    private void setSendToGroup(){
        Button btnGroup = (Button)findViewById(R.id.btnSendToGroup);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText editMsg = (EditText)findViewById(R.id.editTextMsg);
                String msg = editMsg.getText().toString();
                SharedPreferences dataToSave = getApplicationContext().getSharedPreferences("userPref",0);
                SharedPreferences.Editor PrefEditor = dataToSave.edit();
                PrefEditor.putString("pMessage",msg);

                PrefEditor.apply();
                if(msg.matches("")){
                    MyToast.makeText(MessagesNewActivity.this, "Message can not be empty", Toast.LENGTH_SHORT).show();
                }else{
                Intent intent = MessagesGroupsActivity.makeIntent(MessagesNewActivity.this);
                startActivity(intent);}


            }
        });
    }

    private void setCancel(){
        Button btnGroup = (Button)findViewById(R.id.btnMsgCancel);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = MessagesActivity.makeIntent(MessagesNewActivity.this);
                startActivity(intent);
                finish();

            }
        });
    }
    private void response(List<Message> returnedMsg) {
        MyToast.makeText(MessagesNewActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

    }


    public static Intent makeIntent(Context context){
        return new Intent(context, MessagesNewActivity.class);
    }





}
