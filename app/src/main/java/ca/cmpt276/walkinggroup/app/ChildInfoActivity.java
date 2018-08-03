package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Ask the current user what information he/she wants
 */

public class ChildInfoActivity extends AppCompatActivity {

    public static final String CHILD_ID = "ca.cmpt276.walkinggroup.app.ChildInfo - ChildId";
    private long childId;

    private Session session;
    private User user;
    private WGServerProxy proxy;
    private long userId;
    private EarnedRewards current;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_info);
        Intent intent = getIntent();
        childId = intent.getLongExtra(CHILD_ID,0);

        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();


        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        int bgNum = dataToGet.getInt("bg",0);
        changeBackGround(bgNum);

        setPersonalBtn();
        setGroupBtn();
    }




    private void changeBackGround(int bgNumber){
        ConstraintLayout layout = findViewById(R.id.childInfo_layout);
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


    private void setPersonalBtn() {
        Button btn = (Button) findViewById(R.id.btnPersonal);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = UserInfoActivity.makeChildIntent(ChildInfoActivity.this,childId);
                startActivity(intent);
            }
        });
    }

    private void setGroupBtn() {
        Button btn = (Button)  findViewById(R.id.btnGroup);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = GroupActivity.makeChildIntent(ChildInfoActivity.this,childId);
                startActivity(intent);
            }
        });
    }

    public static Intent makeChildIntent(Context context, Long id) {
        Intent intent = new Intent(context,ChildInfoActivity.class);
        intent.putExtra(CHILD_ID,id);
        return intent;
    }
}
