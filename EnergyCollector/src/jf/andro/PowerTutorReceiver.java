package jf.andro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PowerTutorReceiver extends BroadcastReceiver {

	private static int senderEnergy = 0;
	private static int receiverEnergy = 0;
	
	private static synchronized void addSenderEnergy(int energy)
	{
		senderEnergy = senderEnergy + energy;
	}
	
	private static synchronized void addReceiverEnergy(int energy)
	{
		receiverEnergy = receiverEnergy + energy;
	}
	
	public static synchronized int getReceiverEnergy()
	{
		int tmp = receiverEnergy;
		receiverEnergy = 0;
		return tmp;
	}
	
	public static synchronized int getSenderEnergy()
	{
		int tmp = senderEnergy;
		senderEnergy = 0;
		return tmp;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle extra = intent.getExtras();
		
		if (extra == null)
		{
			System.err.println("JFL ERROR: extra is null !");
			return ;
		}
		if (extra.containsKey("sender"))
			addSenderEnergy(extra.getInt("sender"));
		if (extra.containsKey("receiver"))
			addReceiverEnergy(extra.getInt("receiver"));


	}

}
