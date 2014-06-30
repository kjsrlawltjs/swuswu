package jf.andro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class ScenarioService extends Service {

	private Handler mHandler = new Handler();
	private int mProgressStatus = 0;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Tell the user we start
		Toast.makeText(this, "START SCENARIO: Switch off the SCREEN !", Toast.LENGTH_SHORT).show();
		Bundle extras = intent.getExtras();
		int scenario = extras.getInt("scenario");
		RedFlashLight();
		
		
		// SCENARIOS
		// *********
		switch (scenario) {
		case 1:

			Thread t = new Thread() {

				@Override
				public void run() {
					try {

						sleep(10*1000);

						// Starting Energy Collector Service
						Intent service = new Intent("jf.andro.energyservice");
						startService(service);

						sleep(60*1000);
						// Stop
						service = new Intent("jf.andro.energyservice");
						stopService(service);
						
						GreenFlashLight();

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			t.start();
			
			break;

		case 2:

			Thread t2 = new Thread() {

				@Override
				public void run() {
					try {

						sleep(10*1000);

						// Starting Energy Collector Service
						Intent service = new Intent("jf.andro.energyservice");
						startService(service);

						sleep(10*1000);

						Intent intent = new Intent();
						intent.setAction(Const.ACTION_START_STEGANO);
						intent.putExtra(Const.EXTRA_METHOD, Const.OPTION_VOLUME_MUSIC_OBSERVER);

						intent.putExtra(Const.EXTRA_TYPE, Const.TYPE_TEST);
						intent.putExtra(Const.EXTRA_TEST_ITERATIONS, 100);

						// Prepare receiver response
						/*IntentFilter mIntentFilter = new IntentFilter();
			mIntentFilter.addAction(Const.ACTION_FINISH_STEGANO);
			CCResultReceiver mResultReceiver = new CCResultReceiver();
			registerReceiver(mResultReceiver, mIntentFilter);*/

						sendBroadcast(intent);

						sleep(120*1000);
						
						// Stop
						service = new Intent("jf.andro.energyservice");
						stopService(service);
						
						GreenFlashLight();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			t2.start();
			
			break;
		}

		return Service.START_STICKY;
	}

	
	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void RedFlashLight()
    {
    NotificationManager nm = ( NotificationManager ) getSystemService( NOTIFICATION_SERVICE );
    Notification notif = new Notification();
    notif.ledARGB = 0xFFff0000;
    notif.flags = Notification.FLAG_SHOW_LIGHTS;
    notif.ledOnMS = 100; 
    notif.ledOffMS = 100; 
    int LED_NOTIFICATION_ID = 0;
    nm.notify(LED_NOTIFICATION_ID, notif);
    }
	
	private void GreenFlashLight()
    {
    NotificationManager nm = ( NotificationManager ) getSystemService( NOTIFICATION_SERVICE );
    Notification notif = new Notification();
    notif.ledARGB = 0xFF00ff00;
    notif.flags = Notification.FLAG_SHOW_LIGHTS;
    notif.ledOnMS = 100; 
    notif.ledOffMS = 100; 
    int LED_NOTIFICATION_ID = 0;
    nm.notify(LED_NOTIFICATION_ID, notif);
    }


}
