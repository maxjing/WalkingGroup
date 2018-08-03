package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.cmpt276.walkinggroup.app.DialogFragment.MyToast;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class LeaderBoardActivity extends AppCompatActivity {

    private Session session;
    private User user;
    private Long userId;
    private WGServerProxy proxy;
    private List<User> allUsers = new ArrayList<>();
    private EarnedRewards current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        int bgNum = dataToGet.getInt("bg",0);
        changeBackGround(bgNum);

        populate();

    }

    private void changeBackGround(int bgNumber){
        ConstraintLayout layout = findViewById(R.id.leader_board_layout);
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
            int rank = i+1;
            if(allUsers.get(j).getTotalPointsEarned() == null){
                items[i] = ""+rank+ ": "+ "0" + " -  " + firstName + " " + lastName;
            }else{
                items[i] = ""+rank+ ": "+allUsers.get(j).getTotalPointsEarned() + " -  " + firstName + " " + lastName;
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
