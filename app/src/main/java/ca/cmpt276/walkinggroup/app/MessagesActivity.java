package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;


/**
 * Handle message list, with a new message button, able to show emergency first
 * sperate into two list , read and unread
 */




public class MessagesActivity extends AppCompatActivity {
    private static final Handler handler = new Handler();
    private String TAG = "MessagesActivity";
    private WGServerProxy proxy;
    private Long userId;
    private User user;
    private List<String> readMsgList;
    private List<String> unreadMsgList;
    private List<Long> readId;
    private List<Long> unreadId;
    private Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);


        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();

        populate();
        setMsgBtn();
//        setDeleteAllBtn();
        setCancel();
        handler.postDelayed(update, 1000*5);


    }


    Runnable update = new Runnable() {
        @Override
        public void run() {
            populate();
            handler.postDelayed(this, 5000);
        }
    };


    private void populate(){

        Call<List<Message>> caller_read = proxy.getReadMessages(userId,true);
        ProxyBuilder.callProxy(MessagesActivity.this, caller_read, returnedMsg -> responseEMRead(returnedMsg));
        Call<List<Message>> caller_unread = proxy.getUnreadMessages(userId,true);
        ProxyBuilder.callProxy(MessagesActivity.this, caller_unread, returnedMsg -> responseEMUnRead(returnedMsg));



    }
    private void setMsgBtn(){
        Button btnGroup = (Button)findViewById(R.id.btnSend);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = MessagesNewActivity.makeIntent(MessagesActivity.this);
                startActivity(intent);
                finish();

            }
        });
    }

    private void populateMsgRead() {
        ArrayAdapter<String> adapterRead = new ArrayAdapter<>(this, R.layout.messages_read, readMsgList);
        ListView listRead = (ListView) findViewById(R.id.listView_Messages_read);
        listRead.setAdapter(adapterRead);
        listRead.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Call<Message> caller = proxy.markMessageAsRead(readId.get(position),true);
                ProxyBuilder.callProxy(MessagesActivity.this, caller, returnedMsg -> response(returnedMsg));

                Intent intent = MessagesDetailActivity.makeIntent(MessagesActivity.this);
                intent.putExtra("msgId",readId.get(position));
                startActivity(intent);
            }
        });

        listRead.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View viewClicked, int position, long id) {
                Call<Message> caller = proxy.markMessageAsRead(readId.get(position),false);
                ProxyBuilder.callProxy(MessagesActivity.this, caller, returnedMsg -> response(returnedMsg));
                return true;
            }
        });

    }

    private void populateMsgUnRead() {
        ArrayAdapter<String> adapterUnread = new ArrayAdapter<>(this, R.layout.messages_unread, unreadMsgList);
        ListView listUnRead = (ListView) findViewById(R.id.listView_Messages_unread);
        listUnRead.setAdapter(adapterUnread);
        listUnRead.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Call<Message> caller = proxy.markMessageAsRead(unreadId.get(position),true);
                ProxyBuilder.callProxy(MessagesActivity.this, caller, returnedMsg -> response(returnedMsg));

                Intent intent = MessagesDetailActivity.makeIntent(MessagesActivity.this);
                intent.putExtra("msgId",unreadId.get(position));
                startActivity(intent);

            }
        });



    }



    private void response(Message message){
        populate();
    }


    private void response(User returnedUser) {



    }


    private void responseEMRead(List<Message> returnedMsg){
        readMsgList = new ArrayList<>(returnedMsg.size());
        readId      = new ArrayList<>(returnedMsg.size());
        try {
            for(int i = 0; i<returnedMsg.size();i++){
                readMsgList.add("Emergency: "+returnedMsg.get(i).getText());
                readId.add(returnedMsg.get(i).getId());
            }
        } catch (Exception e) {

        }

        Call<List<Message>> caller_read = proxy.getReadMessages(user.getId(),false);
        ProxyBuilder.callProxy(MessagesActivity.this, caller_read, returnedRMsg -> responseRead(returnedRMsg));
    }

    private void responseRead(List<Message> returnedMsg){
        try {
            for(int i = 0; i<returnedMsg.size();i++){
                readMsgList.add(returnedMsg.get(i).getText());
                readId.add(returnedMsg.get(i).getId());
            }
        } catch (Exception e) {

        }
        populateMsgRead();
    }

    private void responseEMUnRead(List<Message> returnedMsg){
        unreadMsgList = new ArrayList<>(returnedMsg.size());
        unreadId      = new ArrayList<>(returnedMsg.size());
        try {
            for(int i = 0; i<returnedMsg.size();i++){
                unreadMsgList.add("Emergency: "+returnedMsg.get(i).getText());
                unreadId.add(returnedMsg.get(i).getId());
            }
        } catch (Exception e) {

        }

        Call<List<Message>> caller_read = proxy.getUnreadMessages(userId,false);
        ProxyBuilder.callProxy(MessagesActivity.this, caller_read, returnedRMsg -> responseUnRead(returnedRMsg));

    }

    private void responseUnRead(List<Message> returnedMsg){
        try {
            for(int i = 0; i<returnedMsg.size();i++){
                unreadMsgList.add(returnedMsg.get(i).getText());
                unreadId.add(returnedMsg.get(i).getId());
            }
        } catch (Exception e) {

        }
        populateMsgUnRead();
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, MessagesActivity.class);
    }

//    private void setDeleteAllBtn(){
//        Button btnGroup = (Button)findViewById(R.id.btnDeleteAll);
//        btnGroup.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//
//                Call<List<Message>> caller = proxy.getMessages();
//                ProxyBuilder.callProxy(MessagesActivity.this, caller, returnedMsg -> responseDeleteAll(returnedMsg));
//
//
//            }
//        });
//    }

    private void responseDeleteAll(List<Message> messages){
        for(int i = 0;i<messages.size();i++){
            Call<Void> caller = proxy.deleteMessage(messages.get(i).getId());
            ProxyBuilder.callProxy(MessagesActivity.this, caller, returnedNothing -> response(returnedNothing));

        }


    }
    private void response(Void returnedNothing) {

    }

    private void setCancel(){
        Button btnGroup = (Button)findViewById(R.id.btnMsgBack);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = MainActivity.makeIntent(MessagesActivity.this);
                startActivity(intent);
                finish();

            }
        });
    }




}
