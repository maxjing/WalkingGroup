package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_detail);

        session = Session.getInstance();
        proxy = session.getProxy();
        userId = session.getUser().getId();
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

    private void populate() {
        Call<PermissionRequest> caller = proxy.getPermissionById(permissionId);
        ProxyBuilder.callProxy(PermissionDetailActivity.this, caller, returned -> response(returned));


    }

    private void response(PermissionRequest permission) {

        requestUserId = permission.getRequestingUser().getId();

        sendByUser = isSendByUser(requestUserId);
        Call<User> caller = proxy.getUserById(requestUserId);
        ProxyBuilder.callProxy(PermissionDetailActivity.this, caller, returnedUser -> responseRequestUser(returnedUser));

        TextView requestDetail = (TextView) findViewById(R.id.requestContent);
        requestDetail.setText(permission.getMessage());

        Button btnApproved = (Button) findViewById(R.id.btnApprove);
        Button btnDeny = (Button) findViewById(R.id.btnDeny);
        statusTemp = permission.getStatus().toString();
        TextView requestStatus = (TextView) findViewById(R.id.requestStatus);

        if(sendByUser){
            if(statusTemp == "PENDING"){
                requestStatus.setText("PENDING");
            }else{
                Iterator<PermissionRequest.Authorizor> it = permission.getAuthorizors().iterator();
                if(statusTemp == "APPROVED"){
                    statusUserId = new ArrayList<>();
                    while(it.hasNext()){
                        statusUserId.add(it.next().getWhoApprovedOrDenied().getId());
                        for(int i = 0 ;i<statusUserId.size();i++) {
                            if(statusUserId.get(i) != userId){
                                Call<User> adUser = proxy.getUserById(statusUserId.get(i));
                                ProxyBuilder.callProxy(PermissionDetailActivity.this, adUser, returnedUser -> setStatus(returnedUser));
                            }
                        }
                    }
                }else{
                    Call<User> adUser = proxy.getUserById(it.next().getWhoApprovedOrDenied().getId());
                    ProxyBuilder.callProxy(PermissionDetailActivity.this, adUser, returnedUser -> setStatus(returnedUser));
                }
            }
        }else{
            Iterator<PermissionRequest.Authorizor> it = permission.getAuthorizors().iterator();
            if(statusTemp == "PENDING"){
                btnDeny.setVisibility(View.VISIBLE);
                btnApproved.setVisibility(View.VISIBLE);
                requestStatus.setText("PENDING");

            }else {

                Call<User> adUser = proxy.getUserById(it.next().getWhoApprovedOrDenied().getId());
                ProxyBuilder.callProxy(PermissionDetailActivity.this, adUser, returnedUser -> setStatus(returnedUser));
            }

        }
    }



    private void setStatus(User user){
        status = statusTemp +" By "+user.getName();
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
        Toast.makeText(this, "Approved Success", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, "Denied Success", Toast.LENGTH_SHORT).show();
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
