package jf.andro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SteganoReceiver extends BroadcastReceiver {

	public static int finished = 0;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.w("JFL", "End of stegano transmission !");
		Long time = intent.getLongExtra(Const.EXTRA_TIME, -1);
		Toast.makeText(context, "Finished in " + time + " ms", Toast.LENGTH_LONG).show();
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
