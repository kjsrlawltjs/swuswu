package jf.andro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

public class EnergyLoggerService extends Service {
	
	private Timer timer = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
	private boolean firstRun = true;
	private final String filename = new String("energy.csv");
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		// Cancel old tasks
		if (timer != null)
			timer.cancel();
				
		System.out.println("JFL: Service STARTED !");
		
		File root = Environment.getExternalStorageDirectory();
		File file = new File(root, filename);
		file.delete();
		firstRun = true;
		
		
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
				
				if (firstRun)
				{
					firstRun = false;
					out.write("Date;Current now;Level%;Voltage;Charging;Stegano Receiver;Stegano Sender\n");
				}
				
				out.write(dateFormat.format(new Date()) + ";");
				String values = e.current_now + ";" + e.capacity + ";" + e.voltage_now + 
						";" + e.charge_now + ";" + e.receiverEnergy + ";" + e.senderEnergy + "\n";
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
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
