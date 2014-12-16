package jf.andro;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.steganomobile.common.Const;
import com.steganomobile.common.sender.model.CcInfo;
import com.steganomobile.common.sender.model.CcMethod;
import com.steganomobile.common.sender.model.CcSenderItem;
import com.steganomobile.common.sender.model.CcStatus;
import com.steganomobile.common.sender.model.CcSync;
import com.steganomobile.common.sender.model.CcType;

import java.text.SimpleDateFormat;
import java.util.Date;

import jf.andro.energycollector.R;


public class JFLApp extends Activity {

    private static final String TAG = JFLApp.class.getSimpleName();
    protected BroadcastReceiver endReceiver;
    private SteganoReceiver sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.jfllayout3);

        // Prepare receiver response
        IntentFilter mIntentFilter = new IntentFilter();
        sr = new SteganoReceiver();
        mIntentFilter.addAction(Const.ACTION_FINISH_RECEIVER_CC);
        mIntentFilter.addAction(Const.ACTION_START_STEGANO);
        registerReceiver(sr, mIntentFilter);

        // Register new receiver for the end
        endReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                TextView endDate = (TextView) findViewById(R.id.endDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                endDate.setText(dateFormat.format(date));
            }
        };
        registerReceiver(endReceiver, new IntentFilter("jf.andro.endScenario"));

        Button start = (Button) findViewById(R.id.startservice);
        start.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent service = new Intent("jf.andro.energyservice");
                startService(service);
            }
        });

        Button stop = (Button) findViewById(R.id.stopservice);
        stop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent service = new Intent("jf.andro.energyservice");
                stopService(service);
                service = new Intent("jf.andro.scenarioservice");
                stopService(service);
            }
        });

        Button startCC = (Button) findViewById(R.id.starttransmission);
        startCC.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Const.ACTION_START_SENDER_CC);

                CcStatus status = CcStatus.START;
                CcMethod method = CcMethod.VOLUME_MUSIC;
                CcSync sync = CcSync.BROADCAST_RECEIVER;
                CcType type = CcType.MESSAGE;
                int interval = 20;
                int iterations = 1;

                CcInfo info = new CcInfo(status, method, iterations, type, interval, sync);
                CcSenderItem item = new CcSenderItem("JFL", info);

//				int message_size = 5;
//				ScenarioService.setCCDataScheduled(message_size);
//				intent.putExtra(Const.EXTRA_TYPE, Const.TYPE_TEST);
//				// Nb numbers sent
//				intent.putExtra(Const.EXTRA_TEST_ITERATIONS, message_size);

                intent.putExtra(Const.EXTRA_ITEM_SENDER_CC, item);

                Log.w("JFL", "Starting CC transmission !");
                sendBroadcast(intent);
            }
        });

//		Button xpIDLE = (Button)findViewById(R.id.startidle);
//		xpIDLE.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//						Intent service = new Intent("jf.andro.scenarioservice");
//						service.putExtra("scenario", 1);
//						startService(service);
//			}
//		});
//		
//		Button xpCC1 = (Button)findViewById(R.id.startCC1);
//		xpCC1.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//						Intent service = new Intent("jf.andro.scenarioservice");
//						service.putExtra("scenario", 2);
//						startService(service);
//			}
//		});

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
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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

        Button stopScenario = (Button) findViewById(R.id.stopscenarios);
        stopScenario.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent service = new Intent("jf.andro.scenarioservice");
                stopService(service);
            }
        });

        //updateReport();


    }

    public void updateReport() {
        Energy e = EnergyReader.readLastEnergy();
        ((TextView) findViewById(R.id.current)).setText("Current now: " + e.current_now + " mA");
        ((TextView) findViewById(R.id.level)).setText("Level: " + e.capacity + " %");
        ((TextView) findViewById(R.id.voltage)).setText("Battery voltage: " + e.voltage_now + " V");
        ((TextView) findViewById(R.id.isCharging)).setText("Charging: " + e.charge_now);
        ((TextView) findViewById(R.id.energyReceiver)).setText("Stegano Receiver: " + e.receiverEnergy + " mJ");
        ((TextView) findViewById(R.id.energySender)).setText("Stegano Sender: " + e.senderEnergy + " mJ");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(endReceiver);
        unregisterReceiver(sr);
    }
}
