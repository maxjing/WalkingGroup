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
    private List<User> allUsers = new ArrayList<>();

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
        String[] items = new String[returnedList.size()];
        allUsers = returnedList;
        Collections.sort(allUsers);
        int i = 0;
        int j =  allUsers.size() - 1;
        while(i < allUsers.size() && j >= 0){
            String name = allUsers.get(j).getName();
            String lastName = "";
            String firstName= "";
            if(name.split("\\w+").length>1){
                lastName = name.substring(name.lastIndexOf(" ")+1,name.lastIndexOf(" ")+2);
                firstName = name.substring(0, name.indexOf(" "));
            }
            else{
                firstName = name;
            }
            if(allUsers.get(j).getTotalPointsEarned() == null){
                items[i] = "0" + " -  " + firstName + " " + lastName;
            }else{
                items[i] = allUsers.get(j).getTotalPointsEarned() + " -  " + firstName + " " + lastName;
            }
            i++;
            j--;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_board, items);
        ListView list = (ListView) findViewById(R.id.list_board);
        list.setAdapter(adapter);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context,LeaderBoardActivity.class);
    }
}
