package ca.cmpt276.walkinggroup.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.app.DialogFragment.MyToast;
import ca.cmpt276.walkinggroup.dataobjects.Background;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.Title;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Show user's current points and total points;
 * User could spend points on new backgrounds and new titles.
 * If the user wants to change the background, the user needs to buy the specific background every time.
 * The user could only purchase higher titles.
 */

public class RewardActivity extends AppCompatActivity {
    private Session session;
    private User user;
    private Long userId;
    private WGServerProxy proxy;
    private Integer CurrentPoints;
    private Integer TotalPointsEarned;
    private List<Background> myBackground = new ArrayList<Background>();
    private List<Title> myTitle = new ArrayList<Title>();
    private String json;
    private EarnedRewards rewards;
    private EarnedRewards current;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme1);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reward);

        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        int bgNum = dataToGet.getInt("bg",0);
        changeBackGround(bgNum);

        backGround();

        setCancelBtn();

        populateBackgroundList();
        populateListView();
        registerClickCallback();

        populateTitle();
        registerTitleClickCallback();
    }
    private void changeBackGround(int bgNumber){
        ConstraintLayout layout = findViewById(R.id.reward_layout);
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

    private void backGround() {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(RewardActivity.this,caller,returned -> responseForGet(returned));
    }



    private void changeTitle() {
        if(current.getTitle().equals("Beginner")){
            level = 0;
        }
        if(current.getTitle().equals("Little bird")){
            level = 1;
        }
        if(current.getTitle().equals("Warrior")){
            level = 2;
        }
        if(current.getTitle().equals("Warlord")){
            level = 3;
        }
        if(current.getTitle().equals("Paladin")){
            level = 4;
        }
        if(current.getTitle().equals("High lord")){
            level = 5;
        }
        if(current.getTitle().equals("Dragon slayer")){
            level = 6;
        }
    }


    private void responseForGet(User returned) {
        Gson gson = new Gson();
        user = returned;
        json = returned.getCustomJson();
        current = gson.fromJson(json, EarnedRewards.class);

        changeTitle();

        if(user.getCurrentPoints() == null){
            CurrentPoints = 0;
        }else{
            CurrentPoints = user.getCurrentPoints();
        }
        if(user.getTotalPointsEarned() == null){
            TotalPointsEarned = 0;
        }else{
            TotalPointsEarned = user.getTotalPointsEarned();
        }

        setTextViews();

    }


    private void populateBackgroundList() {
        myBackground.add(new Background(0,R.drawable.background0_icon));
        myBackground.add(new Background(10,R.drawable.background1_icon));
        myBackground.add(new Background(20,R.drawable.background2_icon));
        myBackground.add(new Background(30,R.drawable.background3_icon));
        myBackground.add(new Background(40,R.drawable.background4_icon));
        myBackground.add(new Background(50,R.drawable.background5_icon));
        myBackground.add(new Background(60,R.drawable.background6_icon));
    }

    private void populateListView() {
        ArrayAdapter<Background> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.backgroundListView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Background>{
        public MyListAdapter(){
            super(RewardActivity.this,R.layout.item_view,myBackground);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view,parent,false);
            }

            Background currentBackground = myBackground.get(position);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon);
            imageView.setImageResource(currentBackground.getIconID());

            TextView pointText = (TextView) itemView.findViewById(R.id.item_txt_points);
            pointText.setText("" + currentBackground.getPoints());

            return itemView;
        }
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.backgroundListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(current.getSelectedBackground() == position){
                    MyToast.makeText(RewardActivity.this,getString(R.string.reward_has_been_earned),Toast.LENGTH_LONG).show();

                }else{
                    if(CurrentPoints >= myBackground.get(position).getPoints()){
                        Background clickedBackground = myBackground.get(position);
                        Gson gson = new Gson();
                        rewards = new EarnedRewards(current.getTitle(),new ArrayList<>(),position,null);
                        json = gson.toJson(rewards);
                        CurrentPoints = CurrentPoints - myBackground.get(position).getPoints();
                        user.setRewards(rewards);
                        user.setCustomJson(json);
                        user.setCurrentPoints(CurrentPoints);
                        Intent data = new Intent();
                        data.putExtra("rewards",json);
                        Call<User> caller = proxy.getUserById(userId);
                        ProxyBuilder.callProxy(RewardActivity.this,caller,returnedUser -> response(returnedUser));
                        setResult(Activity.RESULT_OK,data);
                        finish();
                    }else{
                        MyToast.makeText(RewardActivity.this,getString(R.string.not_enough_points),Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
    }

    private void response(User returnedUser) {
        MyToast.makeText(RewardActivity.this,json,Toast.LENGTH_SHORT);
        returnedUser.setCustomJson(json);
        returnedUser.setCurrentPoints(CurrentPoints);
        Call<User> caller = proxy.editUserById(userId,returnedUser);
        ProxyBuilder.callProxy(RewardActivity.this,caller,returned -> responseForEdit(returned));
    }

    private void responseForEdit(User returned) {
    }

    private void populateTitle() {
        myTitle.add(new Title("Little bird",100));
        myTitle.add(new Title("Warrior",500));
        myTitle.add(new Title("Warlord",1000));
        myTitle.add(new Title("Paladin",3000));
        myTitle.add(new Title("High lord",6000));
        myTitle.add(new Title("Dragon slayer",10000));
        String[] items = new String[myTitle.size()];
        for (int i = 0; i < myTitle.size(); i++) {
            items[i] = myTitle.get(i).getTitle() + " - " + myTitle.get(i).getPoint();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_title,items);
        ListView list = (ListView) findViewById(R.id.titleListView);
        list.setAdapter(adapter);
    }

    private void registerTitleClickCallback() {
        ListView list = (ListView) findViewById(R.id.titleListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position > level) {
                    MyToast.makeText(RewardActivity.this,"You need to buy lower title first.",Toast.LENGTH_LONG).show();
                }else if(position == level){
                    if(CurrentPoints >= myTitle.get(position).getPoint()){
                        Gson gson = new Gson();
                        rewards = new EarnedRewards(myTitle.get(position).getTitle(),new ArrayList<>(),current.getSelectedBackground(),null);
                        json = gson.toJson(rewards);
                        CurrentPoints = CurrentPoints - myTitle.get(position).getPoint();
                        user.setRewards(rewards);
                        user.setCustomJson(json);
                        user.setCurrentPoints(CurrentPoints);
                        Intent data = new Intent();
                        data.putExtra("rewards",json);
                        Call<User> caller = proxy.getUserById(userId);
                        ProxyBuilder.callProxy(RewardActivity.this,caller,returnedUser -> response(returnedUser));
                        setResult(Activity.RESULT_OK,data);
                        finish();
                    }else{
                        MyToast.makeText(RewardActivity.this,getString(R.string.not_enough_points),Toast.LENGTH_LONG).show();
                    }
                }else{
                    MyToast.makeText(RewardActivity.this,getString(R.string.reward_has_been_earned),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setCancelBtn() {
        Button Cancelbtn = findViewById(R.id.reward_cancel_btn);
        Cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setTextViews() {
        TextView CurrentPointsTextView = findViewById(R.id.current_points_textview);
        CurrentPointsTextView.setText(getString(R.string.use_my_points)+" " + CurrentPoints);
        TextView TotalPointsTextView = findViewById(R.id.total_points_textview);
        TotalPointsTextView.setText(getString(R.string.total_points)+" " + TotalPointsEarned);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, RewardActivity.class);
    }
}
