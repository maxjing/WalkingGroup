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
 * Handle emergency message, be able to send without typing
 * able to type and then send
 */

public class MessagesEmergencyActivity extends AppCompatActivity {
    private String TAG = "MessagesEmergencyActivity";
    private String token;
    private WGServerProxy proxy;
    private Long userId;
    private User user;
    private Message message;
    private List<Group> groupsMember;
    private Session session;
    private EarnedRewards current;

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
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        int bgNum = dataToGet.getInt("bg",0);
        changeBackGround(bgNum);
    }






    private void changeBackGround(int bgNumber){
        RelativeLayout layout = findViewById(R.id.messages_emergency_layout);
//        MyToast.makeText(this,""+current.getSelectedBackground(),Toast.LENGTH_SHORT).show();
        if(bgNumber == 0){
            layout.setBackground(getResources().getDrawable(R.drawable.background0));
        }
        if(bgNumber == 1){
            layout.setBackground(getResources().getDrawable(R.drawable.background1));
        }
        if(bgNumber == 2){
            layout.setBackground(getResources().getDrawable(R.drawable.background2));
        }
        if(bgNumber == 3){
            layout.setBackground(getResources().getDrawable(R.drawable.background3));
        }
        if(bgNumber == 4){
            layout.setBackground(getResources().getDrawable(R.drawable.background4));
        }
        if(bgNumber == 5){
            layout.setBackground(getResources().getDrawable(R.drawable.background5));
        }
        if(bgNumber == 6){
            layout.setBackground(getResources().getDrawable(R.drawable.background6));
        }

    }


    private void setSendEmergencyBtn(){
        Button btnGroup = (Button)findViewById(R.id.btnSendEmergencyMsg);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                EditText editMsg = (EditText)findViewById(R.id.editEmergencyMsg);
                String msg = editMsg.getText().toString();
                if (msg.matches("")){
                    msg = getString(R.string.help_emergency);
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
        MyToast.makeText(MessagesEmergencyActivity.this, getString(R.string.emergency_message_sent), Toast.LENGTH_SHORT).show();

    }


    public static Intent makeIntent(Context context){
        return new Intent(context, MessagesEmergencyActivity.class);
    }
}
