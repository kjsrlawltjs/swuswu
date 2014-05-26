package jf.andro;


import jf.andro.energycollector.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


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
		
		Button clear = (Button)findViewById(R.id.clear);
		clear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
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
				
				intent.putExtra(Const.EXTRA_TYPE, Const.TYPE_TEST);
				intent.putExtra(Const.EXTRA_TEST_ITERATIONS, 100);
				
				// Prepare receiver response
				IntentFilter mIntentFilter = new IntentFilter();
				mIntentFilter.addAction(Const.ACTION_FINISH_STEGANO);
				CCResultReceiver mResultReceiver = new CCResultReceiver();
				registerReceiver(mResultReceiver, mIntentFilter);
				
				System.out.println("JFL: starting CC transmission !");
				sendBroadcast(intent);
			}
		});
		
		updateReport();
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
	
	public class CCResultReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("JFL: end of CC transmission !");
			Long time = intent.getLongExtra(Const.EXTRA_TIME, -1);
			Toast.makeText(getApplicationContext(), "Finished in " + time + " ms", Toast.LENGTH_LONG).show();
		}
	}

}
