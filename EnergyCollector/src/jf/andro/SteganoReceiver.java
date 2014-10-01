package jf.andro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SteganoReceiver extends BroadcastReceiver {

	public static int finished = 0;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		finished++;
	}
	
	public synchronized static String isTrue()
	{
		if (finished > 0)
		{
			finished = 0;
			return "1";
		}
		return "0";
	}
}
