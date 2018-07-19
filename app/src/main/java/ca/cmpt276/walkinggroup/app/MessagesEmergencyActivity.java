package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MessagesEmergencyActivity extends AppCompatActivity {
    private String TAG = "MessagesEmergencyActivity";
    private String token;
    private WGServerProxy proxy;
    private Long userId;
    private User user;
    private Message message;
    private List<Group> groupsMember;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_emergency);


        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();
        
        message = new Message();
        setSendEmergencyBtn();
        setCancel();
    }


    private void setSendEmergencyBtn(){
        Button btnGroup = (Button)findViewById(R.id.btnSendEmergencyMsg);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                EditText editMsg = (EditText)findViewById(R.id.editEmergencyMsg);
                String msg = editMsg.getText().toString();
                if (msg.matches("")){
                    msg = "HELP!!!!!";
                }
                message.setText(msg);

                message.setEmergency(true);
                Call<List<Message>> caller = proxy.newMessageToParentsOf(userId,message);
                ProxyBuilder.callProxy(MessagesEmergencyActivity.this, caller, returnedMsg -> response(returnedMsg));
                groupsMember = user.getMemberOfGroups();

                try {
                    for(int i = 0; i < groupsMember.size(); i++){
                        Call<List<Message>> caller_groupLeader = proxy.newMessageToParentsOf(groupsMember.get(i).getLeader().getId(),message);
                        ProxyBuilder.callProxy(MessagesEmergencyActivity.this, caller_groupLeader, returnedMsg -> response(returnedMsg));
                    }

                } catch (Exception e) {

                }


            }
        });
    }

    private void setCancel(){
        Button btnGroup = (Button)findViewById(R.id.btnEmergencyMsgCancel);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = MainActivity.makeIntent(MessagesEmergencyActivity.this);
                startActivity(intent);
                finish();

            }
        });
    }




    private void response(List<Message> returnedMsg) {
        Toast.makeText(MessagesEmergencyActivity.this, "Emergency Message Sent", Toast.LENGTH_SHORT).show();

    }


    public static Intent makeIntent(Context context){
        return new Intent(context, MessagesEmergencyActivity.class);
    }
}
