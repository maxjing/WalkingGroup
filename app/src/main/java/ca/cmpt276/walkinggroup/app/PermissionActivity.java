package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Message;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import retrofit2.Call;

import ca.cmpt276.walkinggroup.dataobjects.PermissionRequest;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

public class PermissionActivity extends AppCompatActivity {
    private List<String> pendingList;
    private List<String> approvedList;
    private List<String> deniedList;
    private String TAG = "PermissionActivity";
    private WGServerProxy proxy;
    private Long userId;
    private User user;
    private Session session;
    private List<Long> pendingId;
    private List<Long> approvedId;
    private List<Long> deniedId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();

        populate();
        setBackBtn();
    }

    private void setBackBtn(){
        Button btnGroup = (Button)findViewById(R.id.btnPermissionBack);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = MainActivity.makeIntent(PermissionActivity.this);
                startActivity(intent);
                finish();

            }
        });
    }

    private void populate() {

        Call<List<PermissionRequest>> caller = proxy.getUserPermissionsByStatus(userId,"PENDING");
        ProxyBuilder.callProxy(PermissionActivity.this, caller, returned -> responsePending(returned));
        Call<List<PermissionRequest>> caller_approved = proxy.getUserPermissionsByStatus(userId,"APPROVED");
        ProxyBuilder.callProxy(PermissionActivity.this, caller_approved, returned -> responseApproved(returned));
        Call<List<PermissionRequest>> caller_denied = proxy.getUserPermissionsByStatus(userId,"DENIED");
        ProxyBuilder.callProxy(PermissionActivity.this, caller_denied, returned -> responseDenied(returned));

    }

    private void responsePending(List<PermissionRequest> permissions){
        pendingId    = new ArrayList<>(permissions.size());
        pendingList = new ArrayList(permissions.size());
        try{
            for(int i = 0;i<permissions.size();i++){
                pendingList.add(permissions.get(i).getMessage());
                pendingId.add(permissions.get(i).getId());
            }

        }catch(Exception e){

        }


        populatePendingPermissionList();
    }

    private void populatePendingPermissionList() {
        ArrayAdapter<String> adapterRead = new ArrayAdapter<>(this, R.layout.permissions_pending, pendingList);
        ListView listRead = (ListView) findViewById(R.id.listView_pending);
        listRead.setAdapter(adapterRead);
        listRead.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = PermissionDetailActivity.makeIntent(PermissionActivity.this);
                intent.putExtra("permissionId",pendingId.get(position));
                startActivity(intent);
            }
        });



    }

    private void responseApproved(List<PermissionRequest> permissions){
        approvedId    = new ArrayList<>(permissions.size());
        approvedList = new ArrayList(permissions.size());
        try{
            for(int i = 0;i<permissions.size();i++){
                approvedList.add(permissions.get(i).getMessage());
                approvedId.add(permissions.get(i).getId());
            }

        }catch(Exception e){

        }


        populateApprovedPermissionList();
    }

    private void populateApprovedPermissionList() {
        ArrayAdapter<String> adapterRead = new ArrayAdapter<>(this, R.layout.permissions_approved, approvedList);
        ListView listRead = (ListView) findViewById(R.id.listView_approved);
        listRead.setAdapter(adapterRead);
        listRead.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = PermissionDetailActivity.makeIntent(PermissionActivity.this);
                intent.putExtra("permissionId",approvedId.get(position));
                startActivity(intent);
            }
        });



    }

    private void responseDenied(List<PermissionRequest> permissions){
        deniedId    = new ArrayList<>(permissions.size());
        deniedList = new ArrayList(permissions.size());
        try{
            for(int i = 0;i<permissions.size();i++){
                deniedList.add(permissions.get(i).getMessage());
                deniedId.add(permissions.get(i).getId());
            }

        }catch(Exception e){

        }

        populateDeniedPermissionList();
    }

    private void populateDeniedPermissionList() {
        ArrayAdapter<String> adapterRead = new ArrayAdapter<>(this, R.layout.permissions_denied, deniedList);
        ListView listRead = (ListView) findViewById(R.id.listView_Denied);
        listRead.setAdapter(adapterRead);
        listRead.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = PermissionDetailActivity.makeIntent(PermissionActivity.this);
                intent.putExtra("permissionId",deniedId.get(position));

                startActivity(intent);
            }
        });



    }





    public static Intent makeIntent(Context context) {
        return new Intent(context,PermissionActivity.class);
    }
}
