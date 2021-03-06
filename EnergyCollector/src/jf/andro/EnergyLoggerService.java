package jf.andro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class EnergyLoggerService extends Service {

	private PowerManager.WakeLock wl;

	private Timer timer = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
	private final String filename = new String("energy.tmp");
	public final static String filenameFinal = new String("energy.csv");
	private String numberOfTests = "000";
	private Vector<Integer> uidIndex = null;
	//private Vector<String> uidNames = null;
	PowerTutorReceiver mResultReceiver;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Tell the user we stopped.
		Toast.makeText(this, "START !", Toast.LENGTH_SHORT).show();

		// KEEP CPU RUNNING !
		PowerManager pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "JFL");
		wl.acquire();
		
		// Getting the numbering of the current test if any
		Bundle extras = intent.getExtras();
		if (extras != null)
		{
			int nbTest = extras.getInt("nbTest");
			numberOfTests =	String.format("%03d", nbTest);
		}
		
		// Reset energy collected
		PowerTutorReceiver.resetEnergy();
		
		// Reset tables
		uidIndex = new Vector<Integer>();
//		uidNames = new Vector<String>();

		// Cancel old tasks
		if (timer != null)
			timer.cancel();

		Log.w("JFL", "Service STARTED !");
		
		// Read one time for purging energies
		EnergyReader.readEnergyValues();
		ScenarioService.getCCDataScheduled();
		SteganoReceiver.isTrue();
		PowerTutorReceiver.getUIDEnergy();
		PowerTutorReceiver.getUIDNames();
		
		File root = Environment.getExternalStorageDirectory();
		File file = new File(root, filename);
		file.delete();

		timer = new Timer();
		
		BlueLightOn();

		TimerTask t = new TimerTask() {

			@Override
			public void run() {

				EnergyReader.readEnergyValues();
				Energy e = EnergyReader.readLastEnergy();

				MemoryInfo mi = new MemoryInfo();
				ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
				activityManager.getMemoryInfo(mi);
				long memory = mi.availMem;


				String values = e.current_now + ";" + e.capacity + ";" + e.voltage_now + 
						";" + e.charge_now + ";" + memory + ";" + e.readCpu + ";" + e.deltaCpu + ";";
				int hiddenDataSend = ScenarioService.getCCDataScheduled();
				if (hiddenDataSend > 0)
					values += "1" + ";";
				else
					values += "0" + ";";
				
				values += SteganoReceiver.isTrue() + ";";
				values += hiddenDataSend + ";";

				if (hiddenDataSend > 0)
				{
					String md5 = ScenarioService.getCCDataScheduledMd5();
					values += md5;
				}
				else
					values += "";
				
				HashMap<Integer, Integer> h = PowerTutorReceiver.getUIDEnergy();
				HashMap<Integer, String> hname = PowerTutorReceiver.getUIDNames();

				// Scanning all uids
				SharedPreferences seenApps = getApplicationContext().getSharedPreferences("apps", Context.MODE_PRIVATE);
				int nbAppTotal = seenApps.getInt("nbAppTotal", 0);
				SharedPreferences.Editor seenAppsEditor = null;
				HashMap<Integer, String> nbToNameApp = new HashMap<Integer, String>();
				for(int uid : h.keySet())
				{
					String nameApp = hname.get(uid);
					if (seenApps.contains(nameApp))
					{
						int assignedNbApp = seenApps.getInt(nameApp, -1);
						nbToNameApp.put(assignedNbApp, nameApp);
						//Log.w("JFL", "Seen: " + nameApp + " and numbered: " + assignedNbApp);
					}
					else
					{
						nbAppTotal++;
						if (seenAppsEditor == null)
							seenAppsEditor = seenApps.edit();
						seenAppsEditor.putInt("nbAppTotal", nbAppTotal);
						seenAppsEditor.putInt(nameApp, nbAppTotal);
						nbToNameApp.put(nbAppTotal, nameApp);
						//Log.w("JFL", "Creating " + nameApp + " and numbered: " + nbAppTotal);
					}
					
				}
				
				if (seenAppsEditor != null)
					seenAppsEditor.commit();
				
				String uidEnergies = "";
				nbAppTotal = seenApps.getInt("nbAppTotal", 0);
				for(int eachApp = 1; eachApp <= nbAppTotal; eachApp++)
				{
					int appEnergyValue = 0;
					for(int uid : h.keySet())
					{
						if (hname.get(uid).equals(nbToNameApp.get(eachApp))) // this process is for app with name of app nb eachapp
						{
							Integer energy = h.get(uid);
							appEnergyValue += energy;
						}
					}
					
					uidEnergies += ";" + appEnergyValue;
				}
				
				uidEnergies += "\n";
				values += uidEnergies;
				
//				String uidEnergies = "";
//				// We search for power of already known UIDs
//				for(int uidKnown : uidIndex)
//				{
//					Integer energyUID = h.get(uidKnown);
//					if (energyUID != null)
//					{ // This already seen uid process has some value
//						uidEnergies += energyUID.toString() + ";";
//						h.remove(uidKnown);
//					}
//					else
//					{
//						uidEnergies += ";";
//					}
//				}
//
//				// Here we deal with not known UIDs that remains in h
//				for(int uid : h.keySet())
//				{
//					Integer energy = h.get(uid);
//					uidIndex.add(uid);
//					uidNames.add(hname.get(uid));
//					uidEnergies += energy.toString() + ";";				
//				}
//				uidEnergies += "\n";
//
//				values += uidEnergies;

				
				File root = Environment.getExternalStorageDirectory();
					File file = new File(root, filename);

					try {
					FileWriter filewriter = new FileWriter(file, true);
					BufferedWriter out = new BufferedWriter(filewriter);

					out.write(dateFormat.format(new Date()) + ";" + values);

				out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};

		timer.scheduleAtFixedRate(t, 1000, 1000);

		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		
		Log.w("JFL", "Service STOPPED !");
		
		timer.cancel();
		wl.release();

		File root = Environment.getExternalStorageDirectory();
		File file = new File(root, "" + numberOfTests + "_" + filenameFinal);
		file.delete();

		try {
			FileWriter filewriter = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(filewriter);
//			out.write(";;;;;;;UIDs;");
//			// We search for power of already known UIDs
//			String uids="";
//			for(int uidKnown : uidIndex)
//			{
//				uids += uidKnown + ";";
//
//			}
//
//			out.write(uids + "\n");

			out.write("Date;Current now;Level%;Voltage;Charging;Memory;ReadCPU;DeltaCPU;StartCC;EndCC;HiddenDataSent;MessageMd5");
//			String uidsnames="";
//			for(String uidNameKnown : uidNames)
//			{
//				uidsnames += uidNameKnown + ";";
//			}
//			out.write(uidsnames + "\n");
			
			SharedPreferences seenApps = getApplicationContext().getSharedPreferences("apps", Context.MODE_PRIVATE);
			int nbAppTotal = seenApps.getInt("nbAppTotal", 0);
			
			Map<String, Integer> map = (Map<String, Integer>) seenApps.getAll();
			HashMap<Integer, String> nbToNameApp = new HashMap<Integer, String>();
			for (String eachApp : map.keySet())
			{
				if (!eachApp.equals("nbAppTotal"))
					nbToNameApp.put(map.get(eachApp), eachApp);
			}
			
			for(int eachApp = 1; eachApp <= nbAppTotal; eachApp++)
			{
				out.write(";" + nbToNameApp.get(eachApp));
			}
			out.write("\n");
			
			
			FileReader filetmp = new FileReader(root + "/" +  filename);
			BufferedReader in = new BufferedReader(filetmp);

			String line = null;
			while ((line = in.readLine()) != null) {
				out.write(line + "\n");
			}

			in.close();
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Tell the user we stopped.
		Toast.makeText(this, "STOP !", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void BlueLightOn()
	{
		NotificationManager nm = ( NotificationManager ) getSystemService( NOTIFICATION_SERVICE );
		Notification notif = new Notification();
		notif.ledARGB = 0xFF0000ff;
		notif.flags = Notification.FLAG_SHOW_LIGHTS;
		notif.ledOnMS = 1; 
		notif.ledOffMS = 0; 
		int LED_NOTIFICATION_ID = 0;
		nm.notify(LED_NOTIFICATION_ID, notif);
	}

	private void BlueLightOff()
	{
		NotificationManager nm = ( NotificationManager ) getSystemService( NOTIFICATION_SERVICE );
		Notification notif = new Notification();
		notif.ledARGB = 0xFF000000;
		notif.flags = Notification.FLAG_SHOW_LIGHTS;
		notif.ledOnMS = 0; 
		notif.ledOffMS = 0; 
		int LED_NOTIFICATION_ID = 0;
		nm.notify(LED_NOTIFICATION_ID, notif);
	}

}
