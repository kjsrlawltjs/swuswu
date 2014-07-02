package jf.andro;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
public class ScenarioService extends Service {

	private PowerManager.WakeLock wl;

	private static int getCCDataScheduled = 0;
	
	public synchronized static int getCCDataScheduled() {
		int tmp = getCCDataScheduled;
		getCCDataScheduled = 0;
		return tmp;
	}
	
	public synchronized static void setCCDataScheduled(int nbdata) {
		getCCDataScheduled = nbdata;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// KEEP CPU RUNNING !
		PowerManager pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "JFL");
		wl.acquire();
		
		// Reset somes values
		setCCDataScheduled(0);
		
		// Tell the user we start
		Toast.makeText(this, "START SCENARIO: Switch off the SCREEN !", Toast.LENGTH_SHORT).show();
		Bundle extras = intent.getExtras();
		int scenario = extras.getInt("scenario");
		RedFlashLight();
		
		Thread t = null;
		
		// SCENARIOS
		// *********
		switch (scenario) {
		case 1:

			t = new Thread() {

				@Override
				public void run() {
					try {

						sleep(10*1000);

						// Starting Energy Collector Service
						Intent service = new Intent("jf.andro.energyservice");
						startService(service);

						sleep(20*60*1000);
						
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

			t = new Thread() {

				@Override
				public void run() {
					try {
						// Parameters for randomness
						Random r = new Random();
						int nb_messages_max = 5 + r.nextInt(10); // Max nb messages
						int message_size_max = 10000; // Size max 1000 bytes
						int nb_second_sleep_random_max = 120; // Max sleeping time 

						sleep(10*1000); // Sleep a little before starting

						// Starting Energy Collector Service for logging
						Intent service = new Intent("jf.andro.energyservice");
						startService(service);

						// Random sleep before the first CC message sending
						sleep(r.nextInt(nb_second_sleep_random_max)*1000);

						// Choose a random number of messages to send 
						int nb_message = 3 + r.nextInt(nb_messages_max);
						
						while (nb_message > 0) // while we have some messages to send
						{
							Intent intent = new Intent();
							intent.setAction(Const.ACTION_START_STEGANO);
							// Choose the CC method to use
							intent.putExtra(Const.EXTRA_METHOD, Const.OPTION_VOLUME_MUSIC_OBSERVER);

							// Pick a random size for the message to transmit
							int size_message = r.nextInt(message_size_max);
							Log.w("JFL: ", "Sending message " + nb_message + " of size " + size_message);
							setCCDataScheduled(size_message);
							intent.putExtra(Const.EXTRA_TYPE, Const.TYPE_TEST);
							intent.putExtra(Const.EXTRA_TEST_ITERATIONS, size_message);

							// Prepare receiver response
							/*IntentFilter mIntentFilter = new IntentFilter();
			mIntentFilter.addAction(Const.ACTION_FINISH_STEGANO);
			CCResultReceiver mResultReceiver = new CCResultReceiver();
			registerReceiver(mResultReceiver, mIntentFilter);*/

							// Send the intent that asks the Stegano sender to transmit !
							sendBroadcast(intent);

							// Random sleep before sending the next messages
							sleep((30 + r.nextInt(nb_second_sleep_random_max))*1000 );
							nb_message--;
						}
						
						// Stop logging service
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
		}

		return Service.START_REDELIVER_INTENT;
	}

	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		wl.release();
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
	
	

	public String generate(int length)
	{
		    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"; // Tu supprimes les lettres dont tu ne veux pas
		    String pass = "";
		    for(int x=0;x<length;x++)
		    {
		       int i = (int)Math.floor(Math.random() * 62); // Si tu supprimes des lettres tu diminues ce nb
		       pass += chars.charAt(i);
		    }
		    return pass;
	}

}
