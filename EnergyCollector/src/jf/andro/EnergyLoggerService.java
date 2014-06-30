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
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class EnergyLoggerService extends Service {
	
	private Timer timer = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
	private final String filename = new String("energy.tmp");
	private final String filenameFinal = new String("energy.csv");
	private final Vector<Integer> uidIndex = new Vector<Integer>();
	private final Vector<String> uidNames = new Vector<String>();
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		// Tell the user we stopped.
        Toast.makeText(this, "START !", Toast.LENGTH_SHORT).show();
        
        // Reset energy collected
        PowerTutorReceiver.resetEnergy();
        
		// Cancel old tasks
		if (timer != null)
			timer.cancel();
				
		Log.w("JFL", "Service STARTED !");
		
		File root = Environment.getExternalStorageDirectory();
		File file = new File(root, filename);
		file.delete();
		
		timer = new Timer();
		
		TimerTask t = new TimerTask() {
			
			@Override
			public void run() {
			
		        
				EnergyReader.readEnergyValues();
				Energy e = EnergyReader.readLastEnergy();
				
				File root = Environment.getExternalStorageDirectory();
				File file = new File(root, filename);
				
				try {
				FileWriter filewriter = new FileWriter(file, true);
				BufferedWriter out = new BufferedWriter(filewriter);
				
				
				
				out.write(dateFormat.format(new Date()) + ";");
				String values = e.current_now + ";" + e.capacity + ";" + e.voltage_now + 
						";" + e.charge_now + ";";
				
				String uidEnergies = "";
				HashMap<Integer, Integer> h = PowerTutorReceiver.getUIDEnergy();
				HashMap<Integer, String> hname = PowerTutorReceiver.getUIDNames();
				
				// We search for power of already known UIDs
				for(int uidKnown : uidIndex)
				{
					Integer energyUID = h.get(uidKnown);
					if (energyUID != null)
					{ // This already seen uid process has some value
						uidEnergies += energyUID.toString() + ";";
						h.remove(uidKnown);
					}
					else
					{
						uidEnergies += ";";
					}
				}
				
				// Here we deal with not known UIDs that remains in h
				for(int uid : h.keySet())
				{
					Integer energy = h.get(uid);
					uidIndex.add(uid);
					uidNames.add(hname.get(uid));
					uidEnergies += energy.toString() + ";";				
				}
				uidEnergies += "\n";
				//PowerTutorReceiver.getSenderEnergy();
				//PowerTutorReceiver.getReceiverEnergy();
				
				values += uidEnergies;
				
				// WRITTING
				out.write(values);
				//System.out.println(values);
				
				out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				            
			}
		};
		
		timer.scheduleAtFixedRate(t, 0, 1000);
		
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		
		

		
		File root = Environment.getExternalStorageDirectory();
		File file = new File(root, filenameFinal);
		file.delete();
		
		try {
			FileWriter filewriter = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(filewriter);
			out.write(";;;;UIDs;");
			// We search for power of already known UIDs
			String uids="";
			for(int uidKnown : uidIndex)
			{
				uids += uidKnown + ";";
				
			}
			
			out.write(uids + "\n");
			
			out.write("Date;Current now;Level%;Voltage;Charging;");
			String uidsnames="";
			for(String uidNameKnown : uidNames)
			{
				uidsnames += uidNameKnown + ";";
			}
			out.write(uidsnames + "\n");
			
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

}
