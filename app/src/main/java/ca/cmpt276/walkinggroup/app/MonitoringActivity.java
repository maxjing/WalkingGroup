package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.User;

public class MonitoringActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_MONITORINGNEW = 01;

    private User user;
    private List<User> monitorsUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        user = User.getInstance();
//        Log.i("Log",user.toString());
        populateListView();
        setAddBtn();
    }

    private void setAddBtn() {
        Button btn = (Button) findViewById(R.id.btnAdd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddUserActivity.makeIntent(MonitoringActivity.this);
                startActivityForResult(intent, REQUEST_CODE_MONITORINGNEW);
            }
        });
    }

    private void populateListView() {
        List<User> monitorsUsers = user.getMonitorsUsers();
        ArrayAdapter<User> adapter = new ArrayAdapter<User>(this,R.layout.monitoring,monitorsUsers);
        ListView list = (ListView) findViewById(R.id.listView_Monitoring);
        list.setAdapter(adapter);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, MonitoringActivity.class);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch(requestCode){
//            case REQUEST_CODE_MONITORINGNEW:
//                if(resultCode == Activity.RESULT_OK) {
//                    //Get the message
//                    Pot added = AddPot.getPotFromIntent(data);
//                    potList.addPot(added);
//                    populateListView();
//                }
//        }
//        switch(requestCode){
//            case REQUEST_CODE_EDIT:
//                if(resultCode == Activity.RESULT_OK) {
//                    Pot edited = AddPot.getPotFromIntent(data);
//                    potList.changePot(edited,index);
//                    populateListView();
//                }else if(resultCode == Activity.RESULT_FIRST_USER){
//                    int position = index;
//                    while(position < potList.countPots()-1){
//                        potList.changePot(potList.getPot(position + 1),position);
//                        position++;
//                    }
//                    potList.removePot(potList.countPots()-1);
//                    populateListView();
//                }
//        }
//    }

}
