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
    //    public static final String EXTRA_NAME = "ca.cmpt276.walkinggroup.app - UserInfo - Name";
//    public static final String EXTRA_EMAIL = "ca.cmpt276.walkinggroup.app - UserInfo - email";
//    public static final String EXTRA_BIRTH_YEAR = "ca.cmpt276.walkinggroup.app - UserInfo - birthYear";
//    public static final String EXTRA_BIRTH_MONTH = "ca.cmpt276.walkinggroup.app - UserInfo - birthMonth";
//    public static final String EXTRA_ADDRESS = "ca.cmpt276.walkinggroup.app - UserInfo - address";
//    public static final String EXTRA_CELL_PHONE = "ca.cmpt276.walkinggroup.app - UserInfo - cellPhone";
//    public static final String EXTRA_HOME_PHONE = "ca.cmpt276.walkinggroup.app - UserInfo - homePhone";
//    public static final String EXTRA_GRADE = "ca.cmpt276.walkinggroup.app - UserInfo - grade";
//    public static final String EXTRA_TEACHER_NAME = "ca.cmpt276.walkinggroup.app - UserInfo - teacherName";
//    public static final String EXTRA_EMERGENCY = "ca.cmpt276.walkinggroup.app - UserInfo - emergency";
    private User user;
    private List<User> monitorsUsers;
    private WGServerProxy proxy;
    private String token;
    private long userId;
    private String name;
    private String email;
    private int birthYear;
    private int birthMonth;
    private String address;
    private String cellPhone;
    private String homePhone;
    private String grade;
    private String teacherName;
    private String emergencyContactInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        token = dataToGet.getString("userToken", "");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        user = User.getInstance();
        userId = dataToGet.getLong("userId", 0);
        //Log.i("LOG",""+userId);
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(UserInfoActivity.this,caller,returned -> response(returned));

        setEditBtn();
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
        TextView tvName = (TextView) findViewById(R.id.txtName);
        tvName.setText(returned.getName());
        user.setName(returned.getName());
       // name = returned.getName();
        TextView tvEmail = (TextView) findViewById(R.id.txtEmail);
        tvEmail.setText(returned.getEmail());
        user.setEmail(returned.getEmail());
        //email = returned.getEmail();
        TextView tvYear = (TextView) findViewById(R.id.txtYear);
        if(returned.getBirthYear() == null){
            tvYear.setText("");
            user.setBirthYear(0);
      //      birthYear = 0;
        }else{
            tvYear.setText(""+returned.getBirthYear());
     //       birthYear = returned.getBirthYear();
            user.setBirthYear(returned.getBirthYear());
        }
        TextView tvMonth = (TextView) findViewById(R.id.txtMonth);
        if(returned.getBirthMonth() == null){
            tvMonth.setText("");
 //           birthMonth = 0;
            user.setBirthMonth(0);
        }else{
            tvMonth.setText(""+returned.getBirthMonth());
//            birthMonth = returned.getBirthMonth();
            user.setBirthMonth(returned.getBirthMonth());
        }
        TextView tvAddress = (TextView) findViewById(R.id.txtAddress);
        tvAddress.setText(returned.getAddress());
//        address = returned.getAddress();
        user.setAddress(returned.getAddress());
        TextView tvCell = (TextView) findViewById(R.id.txtcellPhone);
        tvCell.setText(returned.getCellPhone());
        //cellPhone = returned.getCellPhone();
        user.setCellPhone(returned.getCellPhone());
        TextView tvHome = (TextView) findViewById(R.id.txthomePhone);
        tvHome.setText(returned.getHomePhone());
        //homePhone = returned.getHomePhone();
        user.setHomePhone(returned.getHomePhone());
        TextView tvGrade = (TextView) findViewById(R.id.txtGrade);
        tvGrade.setText(returned.getGrade());
       // grade = returned.getGrade();
        user.setGrade(returned.getGrade());
        TextView tvTeacher = (TextView) findViewById(R.id.txtTeacher);
        tvTeacher.setText(returned.getTeacherName());
       // teacherName = returned.getTeacherName();
        user.setTeacherName(returned.getTeacherName());
        TextView tvEmergency = (TextView) findViewById(R.id.txtEmergency);
        tvEmergency.setText(returned.getEmergencyContactInfo());
       // emergencyContactInfo = returned.getEmergencyContactInfo();
        user.setEmergencyContactInfo(returned.getEmergencyContactInfo());
    }

    public static Intent makeIntent(Context context) {
        Intent intent =  new Intent(context,UserInfoActivity.class);
//        intent.putExtra(EXTRA_NAME,name);
//        intent.putExtra(EXTRA_EMAIL,email);
//        intent.putExtra(EXTRA_BIRTH_YEAR,birthYear);
//        intent.putExtra(EXTRA_BIRTH_MONTH,birthMonth);
//        intent.putExtra(EXTRA_ADDRESS,address);
//        intent.putExtra(EXTRA_CELL_PHONE,cellPhone);
//        intent.putExtra(EXTRA_HOME_PHONE,homePhone);
//        intent.putExtra(EXTRA_GRADE,grade);
//        intent.putExtra(EXTRA_TEACHER_NAME,teacherName);
//        intent.putExtra(EXTRA_EMERGENCY,emergencyContactInfo);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_CODE_EDIT:
                if(resultCode == Activity.RESULT_OK) {
                    SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref",0);
                    token = dataToGet.getString("userToken","");
                    proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
                    user = User.getInstance();
//                    Intent intent = data;
//                    user.setName(data.getStringExtra(EXTRA_NAME));
//                    // data.getStringExtra(EXTRA_NAME);
//
//                    email = data.getStringExtra(EXTRA_EMAIL);
//                    birthYear = data.getIntExtra(YEAR_INT,0);
//                    birthMonth = data.getIntExtra(MONTH_INT,0);
//                    address = data.getStringExtra(EXTRA_ADDRESS);
//                    cellPhone = data.getStringExtra(EXTRA_CELL_PHONE);
//                    homePhone =  data.getStringExtra(EXTRA_HOME_PHONE);
//                    grade = data.getStringExtra(EXTRA_GRADE);
//                    teacherName = data.getStringExtra(EXTRA_TEACHER_NAME);
//                    emergencyContactInfo = data.getStringExtra(EXTRA_EMERGENCY);
                    Call<User> caller = proxy.editUserById(userId,user);
                    ProxyBuilder.callProxy(UserInfoActivity.this,caller,returnedUser -> responseForEdit(returnedUser));

                }
        }

    }

    private void responseForEdit(User returnedUser) {
        Call<User> caller = proxy.getUserById(returnedUser.getId());
        ProxyBuilder.callProxy(UserInfoActivity.this,caller,returned -> response(returned));
//        returnedUser.setName(name);
//        returnedUser.setEmail(email);
//        if(birthYear == 0){
//        }else{
//            returnedUser.setBirthYear(birthYear);
//        }
//        if(birthMonth == 0){
//        }else{
//            returnedUser.setBirthMonth(birthMonth);
//        }
//        returnedUser.setAddress(address);
//        returnedUser.setCellPhone(cellPhone);
//        returnedUser.setHomePhone(homePhone);
//        returnedUser.setGrade(grade);
//        returnedUser.setTeacherName(teacherName);
//        returnedUser.setEmergencyContactInfo(emergencyContactInfo);
    }
}
