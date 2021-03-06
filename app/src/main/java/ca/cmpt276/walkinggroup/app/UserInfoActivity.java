package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import ca.cmpt276.walkinggroup.app.DialogFragment.MyToast;
import ca.cmpt276.walkinggroup.app.R;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Show information about the specific user
 */

public class UserInfoActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_EDIT = 10;
    public static final String CHILD_ID_USER_INFO = "ca.cmpt276.walkinggroup.app.UserInfo - ChildId";
    public static final String PARENT_ID = "Parent_Id";
    private User user;
    private WGServerProxy proxy;
    private long userId;
    private long childId;
    private long parentId;
    private Session session;
    private EarnedRewards current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        int bgNum = dataToGet.getInt("bg",0);
        changeBackGround(bgNum);

        Intent intent = getIntent();
        childId = intent.getLongExtra(CHILD_ID_USER_INFO,0);
        if(childId != 0){
            userId = childId;
        }

        parentId = intent.getLongExtra(PARENT_ID,0);
        if(parentId != 0){
            userId =  parentId;
            Button btn = (Button) findViewById(R.id.btnEdit);
            btn.setVisibility(View.GONE);
        }

        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(UserInfoActivity.this,caller,returned -> response(returned));

        setEditBtn();
    }

    private void changeBackGround(int bgNumber){
        ConstraintLayout layout = findViewById(R.id.user_info_layout);
//        MyToast.makeText(this,""+current.getSelectedBackground(),Toast.LENGTH_SHORT).show();
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


    private void setEditBtn() {
        Button btn = (Button) findViewById(R.id.btnEdit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = EditActivity.makeIntent(UserInfoActivity.this,user);
                startActivityForResult(intent, REQUEST_CODE_EDIT);
            }
        });
    }

    private void response(User returned) {
        user = returned;
        TextView tvName = (TextView) findViewById(R.id.txtName);
        tvName.setText(returned.getName());

        TextView tvEmail = (TextView) findViewById(R.id.txtEmail);
        tvEmail.setText(returned.getEmail());

        TextView tvYear = (TextView) findViewById(R.id.txtYear);
        if(returned.getBirthYear() == null){
            tvYear.setText("");
        }else{
            tvYear.setText(""+returned.getBirthYear());
        }
        TextView tvMonth = (TextView) findViewById(R.id.txtMonth);
        if(returned.getBirthMonth() == null){
            tvMonth.setText("");
        }else{
            tvMonth.setText(""+returned.getBirthMonth());
        }
        TextView tvAddress = (TextView) findViewById(R.id.txtAddress);
        tvAddress.setText(returned.getAddress());

        TextView tvCell = (TextView) findViewById(R.id.txtcellPhone);
        tvCell.setText(returned.getCellPhone());

        TextView tvHome = (TextView) findViewById(R.id.txthomePhone);
        tvHome.setText(returned.getHomePhone());

        TextView tvGrade = (TextView) findViewById(R.id.txtGrade);
        tvGrade.setText(returned.getGrade());

        TextView tvTeacher = (TextView) findViewById(R.id.txtTeacher);
        tvTeacher.setText(returned.getTeacherName());

        TextView tvEmergency = (TextView) findViewById(R.id.txtEmergency);
        tvEmergency.setText(returned.getEmergencyContactInfo());

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        String userEmail = dataToGet.getString("userEmail","");
        if(childId == 0 && parentId == 0 && !userEmail.equals(session.getUser().getEmail())){
            setResult(RESULT_OK,null);
            finishActivityFromChild(this,99);

            SharedPreferences dataToSave = getApplicationContext().getSharedPreferences("userPref", 0);
            SharedPreferences.Editor PrefEditor = dataToSave.edit();
            PrefEditor.putString("userToken", "");

            PrefEditor.apply();
            session.setToken("");
            session.setProxy("");
            proxy = session.getProxy();
            MyToast.makeText(UserInfoActivity.this, R.string.log_out_success, Toast.LENGTH_LONG).show();
            Intent intentToLogin = LoginActivity.makeIntent(UserInfoActivity.this);
            startActivity(intentToLogin);
            finish();

        }

    }

    public static Intent makeIntent(Context context) {
        Intent intent =  new Intent(context,UserInfoActivity.class);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_CODE_EDIT:
                if(resultCode == Activity.RESULT_OK) {
                    session = Session.getInstance();
                    proxy = session.getProxy();
                    user = session.getUser();


                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Call<User> caller = proxy.editUserById(userId,user);
                    ProxyBuilder.callProxy(UserInfoActivity.this,caller,returnedUser -> responseForEdit(returnedUser));
                }
        }

    }

    private void responseForEdit(User returnedUser) {
        Call<User> caller = proxy.getUserById(returnedUser.getId());
        ProxyBuilder.callProxy(UserInfoActivity.this,caller,returned -> response(returned));
    }

    public static Intent makeChildIntent(Context context, long childId) {
        Intent intent = new Intent(context,UserInfoActivity.class);
        intent.putExtra(CHILD_ID_USER_INFO,childId);
        return intent;
    }

    public static Intent makeParentIntent(Context context, long parentId){
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(PARENT_ID, parentId);
        return intent;
    }
}
