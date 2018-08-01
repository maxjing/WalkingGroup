package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ca.cmpt276.walkinggroup.app.DialogFragment.MyToast;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.PermissionRequest;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import retrofit2.Call;

import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

public class PermissionDetailActivity extends AppCompatActivity {
    private Set<PermissionRequest.Authorizor> authorizors;
    private Session session;
    private Long requestUserId;
    private WGServerProxy proxy;
    private Long userId;
    private String TAG = "PermissionDetailActivity";
    private Long permissionId;
    private boolean sendByUser = false;
    private List<Long> statusUserId;
    private String status;
    private String statusTemp;
    private EarnedRewards current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_detail);

        session = Session.getInstance();
        proxy = session.getProxy();
        userId = session.getUser().getId();

        backGround();

        Intent intent = getIntent();
        permissionId = intent.getLongExtra("permissionId", 0);
        Button btnApproved = (Button) findViewById(R.id.btnApprove);
        Button btnDeny = (Button) findViewById(R.id.btnDeny);
        btnDeny.setVisibility(View.GONE);
        btnApproved.setVisibility(View.GONE);
        populate();
        setApproveBtn();
        setDenyBtn();
        setBackBtn();
    }

    private void backGround() {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(PermissionDetailActivity.this,caller,returned -> responseForGet(returned));
    }

    private void responseForGet(User returned) {
        Gson gson = new Gson();
        String json = returned.getCustomJson();
        current = gson.fromJson(json, EarnedRewards.class);
        changeBackGround();
    }

    private void changeBackGround(){
        ConstraintLayout layout = findViewById(R.id.permission_detail_layout);
//        MyToast.makeText(this,""+current.getSelectedBackground(),Toast.LENGTH_SHORT).show();
        if(current.getSelectedBackground() == 0){
            layout.setBackground(getResources().getDrawable(R.drawable.background0));
        }
        if(current.getSelectedBackground() == 1){
            layout.setBackground(getResources().getDrawable(R.drawable.background1));
        }
        if(current.getSelectedBackground() == 2){
            layout.setBackground(getResources().getDrawable(R.drawable.background2));
        }
        if(current.getSelectedBackground() == 3){
            layout.setBackground(getResources().getDrawable(R.drawable.background3));
        }
        if(current.getSelectedBackground() == 4){
            layout.setBackground(getResources().getDrawable(R.drawable.background4));
        }
        if(current.getSelectedBackground() == 5){
            layout.setBackground(getResources().getDrawable(R.drawable.background5));
        }
        if(current.getSelectedBackground() == 6){
            layout.setBackground(getResources().getDrawable(R.drawable.background6));
        }

    }


    private void populate() {
        Call<PermissionRequest> caller = proxy.getPermissionById(permissionId);
        ProxyBuilder.callProxy(PermissionDetailActivity.this, caller, returned -> response(returned));

    }

    private void response(PermissionRequest permission) {
        User requestingUser = permission.getRequestingUser();
        requestUserId = requestingUser.getId();
        Call<User> caller = proxy.getUserById(requestUserId);
        ProxyBuilder.callProxy(PermissionDetailActivity.this, caller, returnedUser -> responseRequestUser(returnedUser));

        TextView requestDetail = (TextView) findViewById(R.id.requestContent);
        requestDetail.setText(permission.getMessage());

        Button btnApproved = (Button) findViewById(R.id.btnApprove);
        Button btnDeny = (Button) findViewById(R.id.btnDeny);
        statusTemp = permission.getStatus().toString();
        TextView requestStatus = (TextView) findViewById(R.id.requestStatus);


        if(statusTemp == "PENDING"){
            requestStatus.setText(statusTemp);
            btnDeny.setVisibility(View.VISIBLE);
            btnApproved.setVisibility(View.VISIBLE);
        }
        else {
            List<PermissionRequest.Authorizor> authList = new ArrayList<>(permission.getAuthorizors());
            User whoAorP = new User();

            for(int i = 0;i<authList.size();i++){
                User tempAuth = authList.get(i).getWhoApprovedOrDenied();
                String status = authList.get(i).getStatus().toString();
                if(status.equals(statusTemp) && !tempAuth.getId().equals(requestUserId)){
                    whoAorP = tempAuth;
                }
            }


            Call<User> adUser = proxy.getUserById(whoAorP.getId());
            ProxyBuilder.callProxy(PermissionDetailActivity.this, adUser, returnedUser -> setStatus(returnedUser));
        }

    }

    private void setStatus(User user){
        status = statusTemp +" "+getString(R.string.by)+" "+user.getName();
        TextView requestStatus = (TextView) findViewById(R.id.requestStatus);
        requestStatus.setText(status);
    }


    private void responseRequestUser(User user) {
        TextView fromUser = (TextView) findViewById(R.id.fromUser);
        fromUser.setText(user.getName());
    }

    private void setBackBtn() {
        Button btnGroup = (Button) findViewById(R.id.btnBacktoPermission);
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = PermissionActivity.makeIntent(PermissionDetailActivity.this);
                startActivity(intent);
                finish();

            }
        });
    }

    private void setApproveBtn() {
        Button btnGroup = (Button) findViewById(R.id.btnApprove);
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<PermissionRequest> caller = proxy.approveOrDenyPermissionRequest(permissionId, WGServerProxy.PermissionStatus.APPROVED);
                ProxyBuilder.callProxy(PermissionDetailActivity.this, caller, returnedPermission -> responseApprove(returnedPermission));
                finish();
            }
        });
    }

    private void responseApprove(PermissionRequest pr) {
        MyToast.makeText(this, getString(R.string.approved_success), Toast.LENGTH_SHORT).show();
    }

    private void setDenyBtn() {
        Button btnGroup = (Button) findViewById(R.id.btnDeny);
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<PermissionRequest> caller = proxy.approveOrDenyPermissionRequest(permissionId, WGServerProxy.PermissionStatus.DENIED);
                ProxyBuilder.callProxy(PermissionDetailActivity.this, caller, returnedPermission -> responseDenied(returnedPermission));
                finish();
            }
        });
    }

    private void responseDenied(PermissionRequest pr) {
        MyToast.makeText(this, getString(R.string.denied_success), Toast.LENGTH_SHORT).show();
    }

    private boolean isSendByUser(Long id) {
        if (id == userId) {
            return true;
        }
        return false;
    }


    public static Intent makeIntent(Context context) {
        return new Intent(context, PermissionDetailActivity.class);
    }



}
