package jf.andro;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import jf.andro.energycollector.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
public class ScenarioService extends Service {

	private PowerManager.WakeLock wl;

	private int nbXP;

	private static int getCCDataScheduled = 0;
	private static String md5Message = "";
	private static boolean idleCC = false;
	
	public synchronized static int getCCDataScheduled() {
		int tmp = getCCDataScheduled;
		getCCDataScheduled = 0;
		return tmp;
	}
	
	public synchronized static void setCCDataScheduled(int nbdata, String md5) {
		getCCDataScheduled = nbdata;
		md5Message = md5;
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
		setCCDataScheduled(0, "");
		
		// Tell the user we start
		Toast.makeText(this, "START SCENARIO: Switch off the SCREEN !", Toast.LENGTH_SHORT).show();
		Bundle extras = intent.getExtras();
		int scenario = extras.getInt("scenario");
		idleCC = extras.getBoolean("idleCC");
		nbXP  = extras.getInt("nbXP");
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
						// Parameters for randomness
						Random r = new Random();
						int nb_second_sleep_random_max = 120; // Max sleeping time 

						sleep(10*1000);

						// Starting Energy Collector Service
						Intent service = new Intent("jf.andro.energyservice");
						startService(service);

						// Random sleep
						sleep((30 + r.nextInt(nb_second_sleep_random_max))*1000 );
						
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
						int message_size_max = 100; // Size max 100 Bytes
						int nb_second_sleep_random_max = 120; // Max sleeping time 

						sleep(10*1000); // Sleep a little before starting

						// Starting Energy Collector Service for logging
						Intent service = new Intent("jf.andro.energyservice");
						startService(service);

						// Random sleep before the first CC message sending
						sleep(r.nextInt(nb_second_sleep_random_max)*1000);

						// Choose a random number of messages to send 
						int nb_message = 1; // + r.nextInt(nb_messages_max);
						
						while (nb_message > 0) // while we have some messages to send
						{
							Intent intent = new Intent();
							intent.setAction(Const.ACTION_START_STEGANO);
							// Choose the CC method to use
							intent.putExtra(Const.EXTRA_METHOD, Const.OPTION_VOLUME_MUSIC_OBSERVER);

							// Pick a random size for the message to transmit
							int size_message_B = (1 + r.nextInt(message_size_max)); // Bytes
							int size_message = size_message_B * 8; // bits
							Log.w("JFL", "Sending message " + nb_message + " of size " + size_message_B + " Bytes (" + size_message + " bits)");
							setCCDataScheduled(size_message, "");
							//intent.putExtra(Const.EXTRA_TYPE, Const.TYPE_TEST);
							//intent.putExtra(Const.EXTRA_TEST_ITERATIONS, size_message);
							intent.putExtra(Const.EXTRA_TEST_ITERATIONS, 1);
							intent.putExtra(Const.EXTRA_TYPE, Const.TYPE_MESSAGE);
							intent.putExtra(Const.EXTRA_MESSAGE, generate(size_message_B, 0));

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
			

		case 3:
			
			File root = Environment.getExternalStorageDirectory();
			for (int i = 0; i <= 100; i++)
			{
				String numberOfTests =	String.format("%03d", i);
				File file = new File(root, numberOfTests + "_" + EnergyLoggerService.filenameFinal);
				file.delete();
			}

			t = new Thread() {

				@Override
				public void run() {
					try {
						// Parameters for randomness
						Random r = new Random();
						int nb_messages_max = nbXP; // Max nb messages
						int message_size_max = 1500; // Size max 1000 Bytes
						int nb_first_sleep_random_max = 60; // Max sleeping time 
						int nb_second_sleep_random_max = 30; // Max sleeping time 

						sleep(3*1000); // Sleep a little before starting

						// Choose a random number of messages to send 
						int nb_message = 1; // + r.nextInt(nb_messages_max);
						
						while (nb_message <= nb_messages_max) // while we have some messages to send
						{
							
							// Starting Energy Collector Service for logging
							Intent service = new Intent("jf.andro.energyservice");
							service.putExtra("nbTest", nb_message);
							startService(service);

							// Random sleep before the first CC message sending
							sleep((10 + r.nextInt(nb_first_sleep_random_max))*1000);

							if (!idleCC) // IDLE the stegano transmission
							{

								Intent intent = new Intent();
								intent.setAction(Const.ACTION_START_STEGANO);
								// Choose the CC method to use
								intent.putExtra(Const.EXTRA_METHOD, Const.OPTION_VOLUME_MUSIC_OBSERVER);

								
								//intent.putExtra(Const.EXTRA_TYPE, Const.TYPE_TEST);
								//intent.putExtra(Const.EXTRA_TEST_ITERATIONS, size_message);
								intent.putExtra(Const.EXTRA_TEST_ITERATIONS, 1);
								
								intent.putExtra(Const.EXTRA_TYPE, Const.TYPE_MESSAGE);
								String my_message = generate(message_size_max, nb_message);
								// Pick a random size for the message to transmit
								int size_message = my_message.length() * 8; // bits
								
								intent.putExtra(Const.EXTRA_MESSAGE, my_message);
								String md5 = null;
								try {
									MessageDigest md = MessageDigest.getInstance("MD5");
									md.update(my_message.getBytes(), 0, my_message.length());
									md5 = new BigInteger(1, md.digest()).toString(16); // Hashed 
								} catch (NoSuchAlgorithmException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Log.i("JFL", "Sending message " + nb_message + " of size " + size_message/8 + " Bytes (" + size_message + " bits): " + md5);
								setCCDataScheduled(size_message, md5);
								// Send the intent that asks the Stegano sender to transmit !
								sendBroadcast(intent);

							}
							// Random sleep before the first CC message sending
							sleep((60 + r.nextInt(nb_second_sleep_random_max))*1000);
							
							// Stop logging service
							service = new Intent("jf.andro.energyservice");
							stopService(service);
							
							nb_message++;
							
							// wait a little bit
							sleep(1000);
						}
						
						GreenFlashLight();
						Intent intent = new Intent();
						intent.setAction("jf.andro.endScenario");
						sendBroadcast(intent);
						
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
	
	

	public String generate(int message_size_max, int seed)
	{
		    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"; // Tu supprimes les lettres dont tu ne veux pas
		    String pass = "";
		    Random r = new Random(seed);
		    int length = (100 + r.nextInt(message_size_max));
		    for(int x=0;x<length;x++)
		    {
		       int i = r.nextInt(62); // Si tu supprimes des lettres tu diminues ce nb
		       pass += chars.charAt(i);
		    }
		    return pass;
	}

	public static String getCCDataScheduledMd5() {
		return md5Message;
	}

}
