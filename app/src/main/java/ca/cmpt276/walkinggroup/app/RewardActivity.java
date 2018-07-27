package ca.cmpt276.walkinggroup.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

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

        Gson gson = new Gson();
        EarnedRewards rewards = new EarnedRewards("Dragon slayer",new ArrayList<>(),1, Color.BLUE);
        String json = gson.toJson(rewards);
        TextView txt = (TextView) findViewById(R.id.txt_rewards);
        txt.setText(json);

        EarnedRewards earned = gson.fromJson(json,EarnedRewards.class);
        TextView txt_ = (TextView) findViewById(R.id.textView12);
        txt_.setText(earned.getTitle()+" "+earned.getPossibleBackgroundFiles()+" "+earned.getSelectedBackground()+" "+earned.getTitleColor());
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
