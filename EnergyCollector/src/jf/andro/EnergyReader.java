package jf.andro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Locale;
import java.util.StringTokenizer;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Build;
import android.util.Log;


public class EnergyReader {

	static final String BUILD_MODEL = Build.MODEL.toLowerCase(Locale.ENGLISH);
	
	static Energy lastRecorded = new Energy();
	
	static int oldCpu = 0;

	public static long getValue(File _f, boolean _convertToMillis) {

		String text = null;

		try {
			FileInputStream fs = new FileInputStream(_f);		
			InputStreamReader sr = new InputStreamReader(fs);
			BufferedReader br = new BufferedReader(sr);			

			text = br.readLine();

			br.close();
			sr.close();
			fs.close();				
		}
		catch (Exception ex) {
			Log.e("CurrentWidget", ex.getMessage());
			ex.printStackTrace();
		}

		long value = -1;

		if (text != null)
		{
			try
			{
				value = Long.parseLong(text);
			}
			catch (NumberFormatException nfe)
			{
				Log.e("CurrentWidget", nfe.getMessage());
			}

			if (_convertToMillis && value != -1 && value != 0)
				value = value / 1000; // convert to milli x

		}

		return value;
	}

	@TargetApi(4)
	static public void readEnergyValues() {

		File f = null;
		Energy e = new Energy();
		String prefix = null;
		
		// Galaxy S4
		if (EnergyReader.BUILD_MODEL.contains("sgh-i337")
				|| EnergyReader.BUILD_MODEL.contains("gt-i9505")
				|| EnergyReader.BUILD_MODEL.contains("gt-i9500")
				|| EnergyReader.BUILD_MODEL.contains("sch-i545")
				|| EnergyReader.BUILD_MODEL.contains("find 5")
				|| EnergyReader.BUILD_MODEL.contains("sgh-m919")
				|| EnergyReader.BUILD_MODEL.contains("sgh-i537")) {
			prefix = "/sys/devices/platform/sec-battery/power_supply/battery/";
		}
			
			// trimuph with cm7, lg ls670, galaxy s3, galaxy note 2
		if (EnergyReader.BUILD_MODEL.contains("triumph")
					|| EnergyReader.BUILD_MODEL.contains("ls670")
					|| EnergyReader.BUILD_MODEL.contains("gt-i9300")
					|| EnergyReader.BUILD_MODEL.contains("gt-n7100")
					|| EnergyReader.BUILD_MODEL.contains("sgh-i317")) {
			 prefix = "/sys/class/power_supply/battery/";
		}
			
		if (prefix != null) {
			f = new File(prefix + "current_now");
			if (f.exists()) {
				e.current_now = (int)EnergyReader.getValue(f, false);
			}
			
			f = new File(prefix + "voltage_now");
			if (f.exists()) {
				e.voltage_now = EnergyReader.getValue(f, false);
			}
			
			f = new File(prefix + "charge_now");
			if (f.exists()) {
				e.charge_now = (int)EnergyReader.getValue(f, false);
			}
			
			f = new File(prefix + "capacity");
			if (f.exists()) {
				e.capacity = (int)EnergyReader.getValue(f, false);
			}
		}
		
		   try
		    {
		        RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
		        String load = reader.readLine();
		        StringTokenizer st = new StringTokenizer(load);
		        st.nextToken();
		        int readCpu = Integer.parseInt(st.nextToken());
		        if (oldCpu == 0)
		        	oldCpu = readCpu;
		        e.deltaCpu = readCpu - oldCpu;
		        e.readCpu = readCpu;
		        oldCpu = readCpu;
		        reader.close();
		    }
		    catch (IOException ex)
		    {
		        ex.printStackTrace();
		    }
		
		recordEnergy(e);
	}
	
	static synchronized void recordEnergy(Energy e)
	{
		lastRecorded = e;
	}
	public static synchronized Energy readLastEnergy()
	{
		return lastRecorded;
	}

}
