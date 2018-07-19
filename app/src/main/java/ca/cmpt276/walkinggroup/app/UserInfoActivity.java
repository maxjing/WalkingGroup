package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ca.cmpt276.walkinggroup.app.R;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

import static ca.cmpt276.walkinggroup.app.EditActivity.EXTRA_ADDRESS;
import static ca.cmpt276.walkinggroup.app.EditActivity.EXTRA_CELL_PHONE;
import static ca.cmpt276.walkinggroup.app.EditActivity.EXTRA_EMAIL;
import static ca.cmpt276.walkinggroup.app.EditActivity.EXTRA_EMERGENCY;
import static ca.cmpt276.walkinggroup.app.EditActivity.EXTRA_GRADE;
import static ca.cmpt276.walkinggroup.app.EditActivity.EXTRA_HOME_PHONE;
import static ca.cmpt276.walkinggroup.app.EditActivity.EXTRA_NAME;
import static ca.cmpt276.walkinggroup.app.EditActivity.EXTRA_TEACHER_NAME;
import static ca.cmpt276.walkinggroup.app.EditActivity.MONTH_INT;
import static ca.cmpt276.walkinggroup.app.EditActivity.YEAR_INT;

public class UserInfoActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_EDIT = 10;
    public static final String CHILD_ID_USER_INFO = "ca.cmpt276.walkinggroup.app.UserInfo - ChildId";
    public static final String PARENT_ID = "Parent_Id";
    private User user;
    private WGServerProxy proxy;
    private String token;
    private long userId;
    private long childId;
    private long parentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

//        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
//        token = dataToGet.getString("userToken", "");
//        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
//        user = User.getInstance();
//        userId = dataToGet.getLong("userId", 0);
//
////        Button btn = (Button) findViewById(R.id.btnEdit);
////        btn.setVisibility(View.GONE);
//
//        Intent intent = getIntent();
//        childId = intent.getLongExtra(CHILD_ID_USER_INFO,0);
//        if(childId != 0){
//            userId = childId;
////            btn.setVisibility(View.VISIBLE);
//        }
//
//        parentId = intent.getLongExtra(PARENT_ID,0);
//        if(parentId != 0){
//            userId =  parentId;
//            Button btn = (Button) findViewById(R.id.btnEdit);
//            btn.setVisibility(View.GONE);
//        }
//
//        Call<User> caller = proxy.getUserById(userId);
//        ProxyBuilder.callProxy(UserInfoActivity.this,caller,returned -> response(returned));
//
//        setEditBtn();
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
//        TextView tv = (TextView) findViewById(R.id.txtName);
//        tv.setText(returned.getName());
//        tv = (TextView) findViewById(R.id.txtEmail);
//        tv.setText(returned.getEmail());
        user = returned;
        TextView tvName = (TextView) findViewById(R.id.txtName);
        tvName.setText(returned.getName());
        //user.setName(returned.getName());
       // name = returned.getName();
        TextView tvEmail = (TextView) findViewById(R.id.txtEmail);
        tvEmail.setText(returned.getEmail());
        //user.setEmail(returned.getEmail());
        //email = returned.getEmail();
        TextView tvYear = (TextView) findViewById(R.id.txtYear);
        if(returned.getBirthYear() == null){
            tvYear.setText("");
          //  user.setBirthYear(0);
      //      birthYear = 0;
        }else{
            tvYear.setText(""+returned.getBirthYear());
     //       birthYear = returned.getBirthYear();
            //user.setBirthYear(returned.getBirthYear());
        }
        TextView tvMonth = (TextView) findViewById(R.id.txtMonth);
        if(returned.getBirthMonth() == null){
            tvMonth.setText("");
 //           birthMonth = 0;
            //user.setBirthMonth(0);
        }else{
            tvMonth.setText(""+returned.getBirthMonth());
//            birthMonth = returned.getBirthMonth();
            //user.setBirthMonth(returned.getBirthMonth());
        }
        TextView tvAddress = (TextView) findViewById(R.id.txtAddress);
        tvAddress.setText(returned.getAddress());
//        address = returned.getAddress();
        //user.setAddress(returned.getAddress());
        TextView tvCell = (TextView) findViewById(R.id.txtcellPhone);
        tvCell.setText(returned.getCellPhone());
        //cellPhone = returned.getCellPhone();
        //user.setCellPhone(returned.getCellPhone());
        TextView tvHome = (TextView) findViewById(R.id.txthomePhone);
        tvHome.setText(returned.getHomePhone());
        //homePhone = returned.getHomePhone();
        //user.setHomePhone(returned.getHomePhone());
        TextView tvGrade = (TextView) findViewById(R.id.txtGrade);
        tvGrade.setText(returned.getGrade());
       // grade = returned.getGrade();
        //user.setGrade(returned.getGrade());
        TextView tvTeacher = (TextView) findViewById(R.id.txtTeacher);
        tvTeacher.setText(returned.getTeacherName());
       // teacherName = returned.getTeacherName();
        //user.setTeacherName(returned.getTeacherName());
        TextView tvEmergency = (TextView) findViewById(R.id.txtEmergency);
        tvEmergency.setText(returned.getEmergencyContactInfo());
       // emergencyContactInfo = returned.getEmergencyContactInfo();
        //user.setEmergencyContactInfo(returned.getEmergencyContactInfo());
    }

    public static Intent makeIntent(Context context) {
        Intent intent =  new Intent(context,UserInfoActivity.class);
        return intent;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch(requestCode){
//            case REQUEST_CODE_EDIT:
//                if(resultCode == Activity.RESULT_OK) {
//                    SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
//                    token = dataToGet.getString("userToken","");
//                    proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
//                    user = User.getInstance();
//
//                    Call<User> caller = proxy.editUserById(userId,user);
//                    ProxyBuilder.callProxy(UserInfoActivity.this,caller,returnedUser -> responseForEdit(returnedUser));
//                }
//        }
//
//    }

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
