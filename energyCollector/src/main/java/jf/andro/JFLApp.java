package jf.andro;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import jf.andro.energycollector.R;


public class JFLApp extends Activity {

    private static final String TAG = JFLApp.class.getSimpleName();
    protected BroadcastReceiver endReceiver;
    protected BroadcastReceiver counterReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.jfllayout3);

        // Register new receiver for the end
        endReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                TextView endDate = (TextView) findViewById(R.id.endDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
                Date date = new Date();
                endDate.setText(dateFormat.format(date));
            }
        };
        registerReceiver(endReceiver, new IntentFilter("jf.andro.endScenario"));

        counterReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                TextView counter = (TextView) findViewById(R.id.counter);
                int c = intent.getIntExtra("counter",-1);
               counter.setText("" + c);
            }
        };
        registerReceiver(counterReceiver, new IntentFilter("jf.andro.counterIncrement"));


        Button xpCC100 = (Button) findViewById(R.id.startCC100);
        xpCC100.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                RadioGroup rg = (RadioGroup) findViewById(R.id.typeCC);
                int radioButtonID = rg.getCheckedRadioButtonId();
                View radioButton = rg.findViewById(radioButtonID);
                int idx = rg.indexOfChild(radioButton);

                if (idx != -1) {
                    // Updating UI for end/start dates
                    TextView startDate = (TextView) findViewById(R.id.startDate);
                    TextView endDate = (TextView) findViewById(R.id.endDate);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
                    Date date = new Date();
                    startDate.setText(dateFormat.format(date));
                    endDate.setText("***");


                    Intent service = new Intent("jf.andro.scenarioservice");
                    service.putExtra("scenario", 3);
                    CheckBox b = (CheckBox) findViewById(R.id.activateCC);
                    EditText nbXP = (EditText) findViewById(R.id.nbXP);
                    service.putExtra("email", getEmail());
                    service.putExtra("idleCC", !b.isChecked());
                    service.putExtra("nbXP", Integer.parseInt(nbXP.getText().toString()));
                    service.putExtra("idCC", idx);

                    startService(service);
                } else {
                    Toast.makeText(getApplicationContext(), "Please choose a CC method !", Toast.LENGTH_SHORT).show();
                }
            }

            public String getEmail() {
                EditText emailText = (EditText) findViewById(R.id.emailText);
                return emailText.getText().toString();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(endReceiver);
        unregisterReceiver(counterReceiver);
    }
}
