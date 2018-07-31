package ca.cmpt276.walkinggroup.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class RewardActivity extends AppCompatActivity {
    private Session session;
    private User user;
    private Long userId;
    private WGServerProxy proxy;
    private Integer CurrentPoints;
    private Integer TotalPointsEarned;
    private List<Background> myBackground = new ArrayList<Background>();
    private String json;
    private EarnedRewards rewards;
    private EarnedRewards current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme1);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reward);

        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();
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
        //rewards = user.getRewards();

        setTextViews();
        setConfirmBtn();

        populateBackgroundList();
        populateListView();
        registerClickCallback();

        //Gson gson = new Gson();
//        EarnedRewards rewards = new EarnedRewards("Dragon slayer",new ArrayList<>(),1, Color.BLUE);
//        json = gson.toJson(rewards);
//        TextView txt = (TextView) findViewById(R.id.txt_rewards);
//        txt.setText(json);

        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(RewardActivity.this,caller,returned -> responseForGet(returned));
//        EarnedRewards earned = gson.fromJson(json,EarnedRewards.class);
//        TextView txt_ = (TextView) findViewById(R.id.textView12);
//        txt_.setText(earned.getTitle()+" "+earned.getPossibleBackgroundFiles()+" "+earned.getSelectedBackground()+" "+earned.getTitleColor());
    }

    private void responseForGet(User returned) {
        Gson gson = new Gson();
        user = returned;
        json = returned.getCustomJson();
        MyToast.makeText(RewardActivity.this,json,Toast.LENGTH_SHORT).show();
       // EarnedRewards earned = gson.fromJson(json,EarnedRewards.class);
        //TextView txt_ = (TextView) findViewById(R.id.textView12);
        if(!json.equals("null")) {
            current = gson.fromJson(json, EarnedRewards.class);
        }else{
            current = new EarnedRewards("null",new ArrayList<>(),0,null);
        }
        TextView txt_ = (TextView) findViewById(R.id.textView12);
        txt_.setText(""+current.getSelectedBackground());
//        }else{
//            txt_.setText("null");
//        }

    }

    private void populateBackgroundList() {
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
            setTheme(R.style.AppTheme_blank);
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
                if(current.getSelectedBackground() == position +1){
                    MyToast.makeText(RewardActivity.this,"The reward has already earned!",Toast.LENGTH_LONG).show();

                }else{
                    if(CurrentPoints >= myBackground.get(position).getPoints()){
                        MyToast.makeText(RewardActivity.this,"selected" + position,Toast.LENGTH_SHORT).show();
                        Background clickedBackground = myBackground.get(position);
                        Gson gson = new Gson();
                        rewards = new EarnedRewards("null",new ArrayList<>(),position+1,null);
                        json = gson.toJson(rewards);
                        Call<User> caller = proxy.getUserById(userId);
                        ProxyBuilder.callProxy(RewardActivity.this,caller,returnedUser -> response(returnedUser));
                        CurrentPoints = CurrentPoints - myBackground.get(position).getPoints();
                        TextView CurrentPointsTextView = findViewById(R.id.current_points_textview);
                        CurrentPointsTextView.setText("USE MY POINTS: " + CurrentPoints);
                    }else{
                        MyToast.makeText(RewardActivity.this,"Not enough points!",Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
    }

    private void response(User returnedUser) {
        MyToast.makeText(RewardActivity.this,json,Toast.LENGTH_SHORT);
        user = returnedUser;
        user.setRewards(rewards);
        user.setCustomJson(json);
        user.setCurrentPoints(CurrentPoints);
        Call<User> caller = proxy.editUserById(userId,user);
        ProxyBuilder.callProxy(RewardActivity.this,caller,returned -> responseForEdit(returned));
    }

    private void responseForEdit(User returned) {
    }

    private void setConfirmBtn() {
        Button OKbtn = findViewById(R.id.reward_ok_btn);
        OKbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheme(R.style.AppTheme1);
                setContentView(R.layout.activity_main);
                finish();
            }
        });


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
        CurrentPointsTextView.setText("USE MY POINTS: " + CurrentPoints);
        TextView TotalPointsTextView = findViewById(R.id.total_points_textview);
        TotalPointsTextView.setText("TOTAL POINTS: " + TotalPointsEarned);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, RewardActivity.class);
    }
}
