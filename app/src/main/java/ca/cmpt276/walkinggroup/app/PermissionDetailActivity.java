package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ca.cmpt276.walkinggroup.dataobjects.PermissionRequest;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import retrofit2.Call;

import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

public class PermissionDetailActivity extends AppCompatActivity {
    private Session session;
    private Long requestUserId;
    private WGServerProxy proxy;
    private Long userId;
    private String TAG = "PermissionDetailActivity";
    private Long permissionId;
    private boolean sendByUser = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_detail);

        session = Session.getInstance();
        proxy = session.getProxy();
        userId  =session.getUser().getId();
        Intent intent = getIntent();
        permissionId = intent.getLongExtra("permissionId",0);
        Button btnApproved = (Button)findViewById(R.id.btnApprove);
        Button btnDeny     = (Button)findViewById(R.id.btnDeny);
        btnDeny.setVisibility(View.GONE);
        btnApproved.setVisibility(View.GONE);
        populate();

        setBackBtn();



    }

    private void populate(){
        Call<PermissionRequest> caller = proxy.getPermissionById(permissionId);
        ProxyBuilder.callProxy(PermissionDetailActivity.this, caller, returned -> response(returned));


    }

    private void response(PermissionRequest permission){

        requestUserId = permission.getRequestingUser().getId();
        sendByUser = isSendByUser(requestUserId);
        Call<User> caller = proxy.getUserById(requestUserId);
        ProxyBuilder.callProxy(PermissionDetailActivity.this,caller,returnedUser -> responseRequestUser(returnedUser));

        TextView requestDetail = (TextView)findViewById(R.id.requestDetail);
        requestDetail.setText(permission.getMessage());

        Button btnApproved = (Button)findViewById(R.id.btnApprove);
        Button btnDeny     = (Button)findViewById(R.id.btnDeny);
        if (!sendByUser) {
            btnDeny.setVisibility(View.VISIBLE);
            btnApproved.setVisibility(View.VISIBLE);

        } else {

        }




    }

    private void responseRequestUser(User user){
        TextView fromUser = (TextView)findViewById(R.id.requestfromUser);
        fromUser.setText(user.getName());
    }
    private void setBackBtn(){
        Button btnGroup = (Button)findViewById(R.id.btnBacktoPermission);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = PermissionActivity.makeIntent(PermissionDetailActivity.this);
                startActivity(intent);
                finish();

            }
        });
    }

    private boolean isSendByUser(Long id){
        if (id == userId){
            return true;
        }
        return false;
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, PermissionDetailActivity.class);
    }

}
