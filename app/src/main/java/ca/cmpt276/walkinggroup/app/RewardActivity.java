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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Background;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

public class RewardActivity extends AppCompatActivity {
    private Session session;
    private User user;
    private Long userId;
    private WGServerProxy proxy;
    private Integer CurrentPoints;
    private Integer TotalPointsEarned;
    private List<Background> myBackground = new ArrayList<Background>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme1);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reward);

        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();
        CurrentPoints = user.getCurrentPoints();
        TotalPointsEarned = user.getTotalPointsEarned();
        
        setTextViews();
        setConfirmBtn();

        populateBackgroundList();
        populateListView();

        Gson gson = new Gson();
        EarnedRewards rewards = new EarnedRewards("Dragon slayer",new ArrayList<>(),1, Color.BLUE);
        String json = gson.toJson(rewards);
        TextView txt = (TextView) findViewById(R.id.txt_rewards);
        txt.setText(json);

        EarnedRewards earned = gson.fromJson(json,EarnedRewards.class);
        TextView txt_ = (TextView) findViewById(R.id.textView12);
        txt_.setText(earned.getTitle()+" "+earned.getPossibleBackgroundFiles()+" "+earned.getSelectedBackground()+" "+earned.getTitleColor());
    }

    private void populateBackgroundList() {
        myBackground.add(new Background("10 points",R.drawable.background1_icon));
        myBackground.add(new Background("20 points",R.drawable.background2_icon));
        myBackground.add(new Background("30 points",R.drawable.background3_icon));
        myBackground.add(new Background("40 points",R.drawable.background4_icon));
        myBackground.add(new Background("50 points",R.drawable.background5_icon));
        myBackground.add(new Background("60 points",R.drawable.background6_icon));
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
            pointText.setText(currentBackground.getPoints());

            return itemView;
        }
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
