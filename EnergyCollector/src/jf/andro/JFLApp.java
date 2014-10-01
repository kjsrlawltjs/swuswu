package jf.andro;


import jf.andro.energycollector.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;


public class JFLApp extends Activity {
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jfllayout);
		
		Button refresh = (Button)findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateReport();
			}
		});
		
			
		Button start = (Button)findViewById(R.id.startservice);
		start.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent service = new Intent("jf.andro.energyservice");
				startService(service);
			}
		});
		
		Button stop = (Button)findViewById(R.id.stopservice);
		stop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent service = new Intent("jf.andro.energyservice");
				stopService(service);
				service = new Intent("jf.andro.scenarioservice");
				stopService(service);
			}
		});

		Button startCC = (Button)findViewById(R.id.starttransmission);
		startCC.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Const.ACTION_START_STEGANO);
				intent.putExtra(Const.EXTRA_METHOD, Const.OPTION_VOLUME_MUSIC_OBSERVER);
				
				//intent.putExtra(Const.EXTRA_TYPE, Const.TYPE_MESSAGE);
				//intent.putExtra(Const.EXTRA_MESSAGE, "message to transmit");
				
//				int message_size = 5;
//				ScenarioService.setCCDataScheduled(message_size);
//				intent.putExtra(Const.EXTRA_TYPE, Const.TYPE_TEST);
//				// Nb numbers sent
//				intent.putExtra(Const.EXTRA_TEST_ITERATIONS, message_size);
				
				intent.putExtra(Const.EXTRA_TYPE, Const.TYPE_MESSAGE);
				intent.putExtra(Const.EXTRA_TEST_ITERATIONS, 1);
				intent.putExtra(Const.EXTRA_MESSAGE, "JFL");
				
				
				
				Log.w("JFL","Starting CC transmission !");
				sendBroadcast(intent);
			}
		});
		
		Button xpIDLE = (Button)findViewById(R.id.startidle);
		xpIDLE.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
						Intent service = new Intent("jf.andro.scenarioservice");
						service.putExtra("scenario", 1);
						startService(service);
			}
		});
		
		Button xpCC1 = (Button)findViewById(R.id.startCC1);
		xpCC1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
						Intent service = new Intent("jf.andro.scenarioservice");
						service.putExtra("scenario", 2);
						startService(service);
			}
		});
		
		Button xpCC100 = (Button)findViewById(R.id.startCC100);
		xpCC100.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
						Intent service = new Intent("jf.andro.scenarioservice");
						service.putExtra("scenario", 3);
						CheckBox b = (CheckBox)findViewById(R.id.activateCC);
						service.putExtra("idleCC", ! b.isChecked());
						startService(service);
			}
		});
		
		Button stopScenario = (Button)findViewById(R.id.stopscenarios);
		stopScenario.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
						Intent service = new Intent("jf.andro.scenarioservice");
						stopService(service);
			}
		});
		
		updateReport();
		
		// Prepare receiver response
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Const.ACTION_FINISH_STEGANO);
		registerReceiver(new SteganoReceiver(), mIntentFilter);
	}
	
	public void updateReport()
	{
		
		Energy e = EnergyReader.readLastEnergy();
		((TextView)findViewById(R.id.current)).setText("Current now: " + e.current_now + " mA");
		((TextView)findViewById(R.id.level)).setText("Level: " + e.capacity + " %");
		((TextView)findViewById(R.id.voltage)).setText("Battery voltage: " + e.voltage_now + " V");
		((TextView)findViewById(R.id.isCharging)).setText("Charging: " + e.charge_now);
		((TextView)findViewById(R.id.energyReceiver)).setText("Stegano Receiver: " + e.receiverEnergy + " mJ");
		((TextView)findViewById(R.id.energySender)).setText("Stegano Sender: " + e.senderEnergy + " mJ");
		
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
}
