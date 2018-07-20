package ca.cmpt276.walkinggroup.app;

/**
 * Ask the current user what information he/she wants
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChildInfoActivity extends AppCompatActivity {

    public static final String CHILD_ID = "ca.cmpt276.walkinggroup.app.ChildInfo - ChildId";
    private long childId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_info);
        Intent intent = getIntent();
        childId = intent.getLongExtra(CHILD_ID,0);

        setPersonalBtn();
        setGroupBtn();
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
