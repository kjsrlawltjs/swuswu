package jf.andro;

import java.util.HashMap;
import java.util.StringTokenizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PowerTutorReceiver extends BroadcastReceiver {

	private static int senderEnergy = 0;
	private static int receiverEnergy = 0;
	private static HashMap<Integer,Integer> allUIDPower = new HashMap<Integer,Integer>();
	
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
	
	public static synchronized HashMap<Integer,Integer> getUIDEnergy()
	{
		HashMap<Integer,Integer> tmp = allUIDPower;
		allUIDPower = new HashMap<Integer,Integer>();
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
		if (extra.containsKey("allUID"))
			addAllUIDEnergy(extra.getString("allUID"));


	}

	private void addAllUIDEnergy(String s) {
		// TODO Auto-generated method stub
		
		StringTokenizer st = new StringTokenizer(s, ";");
		while (st.hasMoreElements())
		{
			String token = st.nextToken();
			StringTokenizer st2 = new StringTokenizer(token, "=");
			if (st2.countTokens() == 2)
			{
				int uid = Integer.parseInt(st2.nextToken());
				int power = Integer.parseInt(st2.nextToken());
								
				Integer oldPower = allUIDPower.get(uid);
				if (oldPower == null)
					allUIDPower.put(uid, Integer.valueOf(power));
				else
					allUIDPower.put(uid, Integer.valueOf(power+oldPower.intValue()));
			}
		}
		
	}

}
