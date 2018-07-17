package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ParentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        setMapBtn();
        setMessageBtn();
    }

    private void setMapBtn() {
        Button btn = (Button) findViewById(R.id.btnMap);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToMap = GoogleMapsActivity.makeIntent(ParentActivity.this);
                startActivity(intentToMap);
            }
        });
    }

    private void setMessageBtn() {
        Button btn = (Button) findViewById(R.id.btnMessage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenttomsg = MessagesActivity.makeIntent(ParentActivity.this);
                startActivity(intenttomsg);
            }
        });

    }

    public static Intent makeIntent(Context context) {
        return new Intent(context,ParentActivity.class);
    }
}
