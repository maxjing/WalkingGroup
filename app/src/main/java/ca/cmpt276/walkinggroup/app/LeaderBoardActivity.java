package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class LeaderBoardActivity extends AppCompatActivity {

    private Session session;
    private User user;
    private WGServerProxy proxy;
    private List<String> allUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();

        populate();

    }

    private void populate() {
        Call<List<User>> caller = proxy.getUsers();
        ProxyBuilder.callProxy(LeaderBoardActivity.this,caller,returnedList -> response(returnedList));
    }

    private void response(List<User> returnedList) {
//        String[] items = new String[returnedList.size()];
        for(int i = 0; i < returnedList.size(); i++){
            String name = returnedList.get(i).getName();
            String lastName = "";
            String firstName= "";
            if(name.split("\\w+").length>1){
                lastName = name.substring(name.lastIndexOf(" ")+1,name.lastIndexOf(" ")+2);
                firstName = name.substring(0, name.indexOf(" "));
            }
            else{
                firstName = name;
            }
            allUsers.add(returnedList.get(i).getTotalPointsEarned() + " " + firstName + " " + lastName);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_board, allUsers);
        Collections.sort(allUsers);
        ListView list = (ListView) findViewById(R.id.list_board);
        list.setAdapter(adapter);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context,LeaderBoardActivity.class);
    }
}
