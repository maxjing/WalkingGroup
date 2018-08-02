package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import ca.cmpt276.walkinggroup.app.DialogFragment.MyToast;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Edit user's information
 * If the email of the current user is edited, the user may log in again.
 */

public class EditActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "ca.cmpt276.walkinggroup.app.Edit - name";
    public static final String EXTRA_EMAIL = "ca.cmpt276.walkinggroup.app.Edit - email";
    public static final String EXTRA_BIRTH_YEAR = "ca.cmpt276.walkinggroup.app.Edit - birthYear";
    public static final String EXTRA_BIRTH_MONTH = "ca.cmpt276.walkinggroup.app.Edit - birthMonth";
    public static final String EXTRA_ADDRESS = "ca.cmpt276.walkinggroup.app.Edit - address";
    public static final String EXTRA_CELL_PHONE = "ca.cmpt276.walkinggroup.app.Edit - cellPhone";
    public static final String EXTRA_HOME_PHONE = "ca.cmpt276.walkinggroup.app.Edit - homePhone";
    public static final String EXTRA_GRADE = "ca.cmpt276.walkinggroup.app.Edit - grade";
    public static final String EXTRA_TEACHER_NAME = "ca.cmpt276.walkinggroup.app.Edit - teacherName";
    public static final String EXTRA_EMERGENCY = "ca.cmpt276.walkinggroup.app.Edit - emergencyContactInfo";

    private User user;
    private List<User> monitorsUsers;
    private WGServerProxy proxy;
    private String token;
    private long userId;
    private Session session;
    private EarnedRewards current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        session = Session.getInstance();
        user = session.getUser();
        proxy = session.getProxy();
        userId = user.getId();

        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        int bgNum = dataToGet.getInt("bg",0);
        changeBackGround(bgNum);

        extractDataFromIntent();
        setOKBtn();
        setCancelBtn();

    }




    private void changeBackGround(int bgNumber){
        ConstraintLayout layout = findViewById(R.id.edit_layout);
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

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        String name = intent.getStringExtra(EXTRA_NAME);
        EditText etName = (EditText) findViewById(R.id.etName);
        etName.setText(name);
        String email = intent.getStringExtra(EXTRA_EMAIL);
        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        etEmail.setText(email);
        int birthYear = intent.getIntExtra(EXTRA_BIRTH_YEAR,0);
        EditText etYear = (EditText) findViewById(R.id.etYear);
        if(birthYear == 0){
            etYear.setText("");
        }else{
            etYear.setText(""+birthYear);
        }
        int birthMonth = intent.getIntExtra(EXTRA_BIRTH_MONTH,0);
        EditText etMonth = (EditText) findViewById(R.id.etMonth);
        if(birthMonth == 0){
            etMonth.setText("");
        }else{
            etMonth.setText(""+birthMonth);
        }
        String address = intent.getStringExtra(EXTRA_ADDRESS);
        EditText etAddress = (EditText) findViewById(R.id.etAddress);
        etAddress.setText(address);
        String cellPhone = intent.getStringExtra(EXTRA_CELL_PHONE);
        EditText etcellPhone = (EditText) findViewById(R.id.etcellPhone);
        etcellPhone.setText(cellPhone);
        String homePhone = intent.getStringExtra(EXTRA_HOME_PHONE);
        EditText ethomePhone = (EditText) findViewById(R.id.ethomePhone);
        ethomePhone.setText(homePhone);
        String grade = intent.getStringExtra(EXTRA_GRADE);
        EditText etGrade = (EditText) findViewById(R.id.etGrade);
        etGrade.setText(grade);
        String teacherName = intent.getStringExtra(EXTRA_TEACHER_NAME);
        EditText etteacherName = (EditText) findViewById(R.id.etTeacher);
        etteacherName.setText(teacherName);
        String emergency = intent.getStringExtra(EXTRA_EMERGENCY);
        EditText etEmergency = (EditText) findViewById(R.id.etEmergency);
        etEmergency.setText(emergency);
    }

    private void setOKBtn() {
        Button btn = (Button) findViewById(R.id.btnOK_Edit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editN = (EditText) findViewById(R.id.etName);
                String editName = editN.getText().toString();
                EditText editE = (EditText) findViewById(R.id.etEmail);
                String etEmail = editE.getText().toString();
                EditText editY = (EditText) findViewById(R.id.etYear);
                String etYear = editY.getText().toString();
                EditText editM = (EditText) findViewById(R.id.etMonth);
                String etMonth = editM.getText().toString();
                EditText editA = (EditText) findViewById(R.id.etAddress);
                String etAddress = editA.getText().toString();
                EditText editCP = (EditText) findViewById(R.id.etcellPhone);
                String etcellPhone = editCP.getText().toString();
                EditText editHP = (EditText) findViewById(R.id.ethomePhone);
                String ethomePhone = editHP.getText().toString();
                EditText editG = (EditText) findViewById(R.id.etGrade);
                String etGrade = editG.getText().toString();
                EditText editTN = (EditText) findViewById(R.id.etTeacher);
                String etteacherName = editTN.getText().toString();
                EditText etECI = (EditText) findViewById(R.id.etEmergency);
                String etEmergency = etECI.getText().toString();

                int year = 0;
                int month = 0;

                try {
                    year = Integer.parseInt(etYear);
                    month = Integer.parseInt(etMonth);
                    if (year < 1900 || year > 2018) {
                        MyToast.makeText(EditActivity.this, getString(R.string.year_limit), Toast.LENGTH_LONG).show();
                    }
                    if (month < 1 || month > 12) {
                        MyToast.makeText(EditActivity.this, getString(R.string.month_limit), Toast.LENGTH_LONG).show();
                    }
                    if (year >= 1900 && year <= 2018 && month >= 1 && month <= 12) {
                        user.setName(editName);
                        user.setEmail(etEmail);
                        user.setBirthYear(year);
                        user.setBirthMonth(month);
                        user.setAddress(etAddress);
                        user.setCellPhone(etcellPhone);
                        user.setHomePhone(ethomePhone);
                        user.setGrade(etGrade);
                        user.setTeacherName(etteacherName);
                        user.setEmergencyContactInfo(etEmergency);
                        setResult(Activity.RESULT_OK);
                        finish();
                    }

                } catch (NumberFormatException e) {
                    if (etYear.equals("") && etMonth.equals("")) {
                        user.setBirthYear(null);
                        user.setBirthMonth(null);
                        user.setName(editName);
                        user.setEmail(etEmail);
                        user.setAddress(etAddress);
                        user.setCellPhone(etcellPhone);
                        user.setHomePhone(ethomePhone);
                        user.setGrade(etGrade);
                        user.setTeacherName(etteacherName);
                        user.setEmergencyContactInfo(etEmergency);
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                    if (etYear.equals("") && !etMonth.equals("")) {
                        month = Integer.parseInt(etMonth);
                        if (month < 1 || month > 12) {
                            MyToast.makeText(EditActivity.this, getString(R.string.month_limit), Toast.LENGTH_LONG).show();
                        } else {
                            user.setBirthYear(null);
                            user.setBirthMonth(month);
                            user.setName(editName);
                            user.setEmail(etEmail);
                            user.setAddress(etAddress);
                            user.setCellPhone(etcellPhone);
                            user.setHomePhone(ethomePhone);
                            user.setGrade(etGrade);
                            user.setTeacherName(etteacherName);
                            user.setEmergencyContactInfo(etEmergency);
                            setResult(Activity.RESULT_OK);
                            finish();
                        }

                    }
                    if (!etYear.equals("") && etMonth.equals("")) {
                        year = Integer.parseInt(etYear);
                        if (year < 1900 || year > 2018) {
                            MyToast.makeText(EditActivity.this, getString(R.string.year_limit), Toast.LENGTH_LONG).show();
                        } else {
                            user.setBirthYear(year);
                            user.setBirthMonth(null);
                            user.setName(editName);
                            user.setEmail(etEmail);
                            user.setAddress(etAddress);
                            user.setCellPhone(etcellPhone);
                            user.setHomePhone(ethomePhone);
                            user.setGrade(etGrade);
                            user.setTeacherName(etteacherName);
                            user.setEmergencyContactInfo(etEmergency);
                            setResult(Activity.RESULT_OK);
                            finish();
                        }

                    }

                }
            }
        });
    }

    private void setCancelBtn() {
        Button btn = (Button) findViewById(R.id.btnCancel_Edit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static Intent makeIntent(Context context, User user) {
        Intent intent = new Intent(context,EditActivity.class);
        intent.putExtra(EXTRA_NAME,user.getName());
        intent.putExtra(EXTRA_EMAIL,user.getEmail());
        intent.putExtra(EXTRA_BIRTH_YEAR,user.getBirthYear());
        intent.putExtra(EXTRA_BIRTH_MONTH,user.getBirthMonth());
        intent.putExtra(EXTRA_ADDRESS,user.getAddress());
        intent.putExtra(EXTRA_CELL_PHONE,user.getCellPhone());
        intent.putExtra(EXTRA_HOME_PHONE,user.getHomePhone());
        intent.putExtra(EXTRA_GRADE,user.getGrade());
        intent.putExtra(EXTRA_TEACHER_NAME,user.getTeacherName());
        intent.putExtra(EXTRA_EMERGENCY,user.getEmergencyContactInfo());
        return intent;
    }
}
