package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.PrimitiveIterator;

import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class MessagesDetailActivity extends AppCompatActivity {
    private WGServerProxy proxy;
    private Long userId;
    private String token;
    private Long msgId;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_detail);

        session = Session.getInstance();
        proxy = session.getProxy();


        Intent intent = getIntent();
        msgId = intent.getLongExtra("msgId",0);

        Call<Message> caller = proxy.getOneMessage(msgId);
        ProxyBuilder.callProxy(MessagesDetailActivity.this, caller, returnedMsg -> response(returnedMsg));

        setBackBtn();
        setDeleteBtn();

    }

    private void response(Message message){

        TextView txtContent = (TextView)findViewById(R.id.msgDetail);

        Call<User> caller_user = proxy.getUserById(message.getFromUser().getId());
        ProxyBuilder.callProxy(MessagesDetailActivity.this, caller_user, returnedUser -> response(returnedUser));

        txtContent.setText(message.getText());


    }

    private void response(User user){
        TextView txtFromUser = (TextView)findViewById(R.id.fromUser);
        txtFromUser.setText(user.getName());
    }

    private void setBackBtn(){
        Button btnGroup = (Button)findViewById(R.id.btnBackToMsg);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


                Intent intent = MessagesActivity.makeIntent(MessagesDetailActivity.this);
                startActivity(intent);
                finish();

            }
        });
    }
    private void setDeleteBtn(){
        Button btnGroup = (Button)findViewById(R.id.btnDeleteMsg);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Call<Void> caller = proxy.deleteMessage(msgId);
                ProxyBuilder.callProxy(MessagesDetailActivity.this, caller, returnedNothing -> response(returnedNothing));

                Intent intent = MessagesActivity.makeIntent(MessagesDetailActivity.this);
                startActivity(intent);
                finish();

            }
        });
    }

    private void response(Void returnedNothing) {

    }

    public static Intent makeIntent(Context context){
        return new Intent(context, MessagesDetailActivity.class);
    }

}
