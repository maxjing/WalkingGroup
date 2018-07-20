package ca.cmpt276.walkinggroup.app;

/**
 * Parent Dashboard to jump to map activity and message activity
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class ParentActivity extends AppCompatActivity {
    private Session session;
    private User user;
    private Long userId;
    private WGServerProxy proxy;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();


        setMapBtn();
        setMessageBtn();


        setUnreadNo();

    }

    private void setUnreadNo(){
        count = 0;
        Call<List<Message>> caller_unread = proxy.getUnreadMessages(userId,true);
        ProxyBuilder.callProxy(ParentActivity.this, caller_unread, returnedMsg -> responseEMUnRead(returnedMsg));
    }

    private void responseEMUnRead(List<Message> returnedMsg){

        try {
            for(int i = 0; i<returnedMsg.size();i++){
                count++;
            }
        } catch (Exception e) {

        }

        Call<List<Message>> caller_read = proxy.getUnreadMessages(userId,false);
        ProxyBuilder.callProxy(ParentActivity.this, caller_read, returnedRMsg -> responseUnRead(returnedRMsg));

    }
    private void responseUnRead(List<Message> returnedMsg){
        try {
            for(int i = 0; i<returnedMsg.size();i++){
                count++;
            }
        } catch (Exception e) {

        }
        TextView NoUnread = (TextView)findViewById(R.id.txtNoUnread);
        NoUnread.setText(Integer.toString(count));
    }

    private void setMapBtn() {
        Button btn = (Button) findViewById(R.id.btnMap);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToMap = GoogleMapsActivity.makeIntent(ParentActivity.this);
                startActivity(intentToMap);
            }
        });
    }

    private void setMessageBtn() {
        Button btn = (Button) findViewById(R.id.btnMessage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenttomsg = MessagesActivity.makeIntent(ParentActivity.this);
                startActivity(intenttomsg);
            }
        });

    }

    public static Intent makeIntent(Context context) {
        return new Intent(context,ParentActivity.class);
    }
}
