package jf.andro;

public class Const {
	// PARAMETER
	// KEYS
	public static final String ACTION_START_STEGANO = "start_stegano"; 
	public static final String ACTION_FINISH_STEGANO = "finish_stegano";
	public static final String EXTRA_MESSAGE = "message";
	
	public static final String EXTRA_TIME_INTERVAL = "time_interval";

	public static final String EXTRA_TEST_ITERATIONS = "test_iterations";

	public static final String EXTRA_TYPE = "type";

	public static final String EXTRA_METHOD = "method";
	
	// METHOD
	public static final int OPTION_VOLUME_MUSIC_OBSERVER = 1;
	public static final int OPTION_VOLUME_RING_OBSERVER = 2;
	public static final int OPTION_VOLUME_NOTIFICATION_OBSERVER = 3;
	public static final int OPTION_FILE_LOCK_OBSERVER = 4;
	public static final int OPTION_FILE_SIZE_OBSERVER = 5;
	public static final int OPTION_FILE_EXISTENCE_OBSERVER = 6;
	public static final int OPTION_TYPE_OF_INTENT_OBSERVER = 7;
	public static final int OPTION_TYPE_OF_INTENT_RECEIVER = 8;
	public static final int OPTION_UNIX_SOCKET_RECEIVER_ALARM = 9;
	public static final int OPTION_MEMORY_LOAD_RECEIVER_ALARM = 10;
	public static final int OPTION_SYSTEM_LOAD_RECEIVER_ALARM = 11;
	public static final int OPTION_USAGE_TREND_RECEIVER_ALARM = 12;
	
	// TYPE TABLE
public static final int TYPE_MESSAGE = 1;
public static final int TYPE_LOCATION = 2;  
public static final int TYPE_CELL_LOCATION = 3;  
public static final int TYPE_SMS = 4;  
public static final int TYPE_CONTACTS = 5;  
public static final int TYPE_IMEI = 6;  
public static final int TYPE_TEST = 7;  
public static final int TYPE_OPERATOR_NAME = 8;
public static final int TYPE_FILE = 9;  
public static final int TYPE_NEW_SMS = 10;


// Intent received data
public static final String EXTRA_ID = "_id";
public static final String EXTRA_DATA = "data";
public static final String EXTRA_TIME = "time";
public static final String EXTRA_FINISH_DATE = "finish_date";
public static final String EXTRA_START_DATE = "start_date";
public static final String EXTRA_SIZE = "size";
public static final String EXTRA_BIT_RATE = "bit_rate";
}

// HELP
/*
From: Marcin Urbański <0.660162@gmail.com>
To: Jean-Francois Lalande <jean-francois.lalande@insa-cvl.fr>
Cc: Wojciech Mazurczyk <wmazurczyk@cygnus.tele.pw.edu.pl>
Subject: [stegano] Start Stegano!
Date: Sat, 24 May 2014 23:02:02 +0200

Hi!

I changed an application in way that we were discussing earlier.

So below you can find instructions how to adapt your code to work with mine.

*Set action for Sender*

intent.setAction(Const.ACTION_START_STEGANO);
> public static final String ACTION_START_STEGANO = "start_stegano";  


*Extras for Sender*

intent.putExtra(Const.EXTRA_TIME_INTERVAL, timeInterval);
> intent.putExtra(Const.EXTRA_TEST_ITERATIONS, testIterations);
> intent.putExtra(Const.EXTRA_METHOD, method);
> intent.putExtra(Const.EXTRA_TYPE, type);
> intent.putExtra(Const.EXTRA_MESSAGE, mMessage);  


public static final String EXTRA_MESSAGE = "message";
>  
public static final String EXTRA_TIME_INTERVAL = "time_interval";

public static final String EXTRA_TEST_ITERATIONS = "test_iterations";

public static final String EXTRA_TYPE = "type"

public static final String EXTRA_METHOD = "method";​


​*Important method(CC) table!:*

public static final int OPTION_VOLUME_MUSIC_OBSERVER = 1;
> public static final int OPTION_VOLUME_RING_OBSERVER = 2;
> public static final int OPTION_VOLUME_NOTIFICATION_OBSERVER = 3;
> public static final int OPTION_FILE_LOCK_OBSERVER = 4;
> public static final int OPTION_FILE_SIZE_OBSERVER = 5;
> public static final int OPTION_FILE_EXISTENCE_OBSERVER = 6;
> public static final int OPTION_TYPE_OF_INTENT_OBSERVER = 7;
> public static final int OPTION_TYPE_OF_INTENT_RECEIVER = 8;
> public static final int OPTION_UNIX_SOCKET_RECEIVER_ALARM = 9;
> public static final int OPTION_MEMORY_LOAD_RECEIVER_ALARM = 10;
> public static final int OPTION_SYSTEM_LOAD_RECEIVER_ALARM = 11;
> public static final int OPTION_USAGE_TREND_RECEIVER_ALARM = 12;​  

*​*
*Important type table!*​

​​
> ​
> ​(***) ​
> public static final int TYPE_MESSAGE = 1;
> ​
> (*)
> ​
> public static final int TYPE_LOCATION = 2;  

​
> (*)
> ​
> public static final int TYPE_CELL_LOCATION = 3;  

​
> (*)
> ​
> public static final int TYPE_SMS = 4;  

​
> (*)
> ​
> public static final int TYPE_CONTACTS = 5;  

​
> (*)
> ​
> public static final int TYPE_IMEI = 6;  

​
> (*
> ​*​
> )
> ​
> public static final int TYPE_TEST = 7;  

 ​
> (*)
> ​
> public static final int TYPE_OPERATOR_NAME = 8;  

 ​
> (*
> ​**​
> )
> ​
> public static final int TYPE_FILE = 9;  

​
> (*
> ​**​
> )
> ​
> public static final int TYPE_NEW_SMS = 10;​  

​​

​(*)  - EXTRA_TIME_ITERATIONS and EXTRA_MESSAGE does not need to be
specified

(**) - EXTRA_TIME_ITERATIONS must be specified, EXTRA_MESSAGE not

(***) - EXTRA_MESSAGE must be specified


​*Prepare Broadcast Receiver for Receiver*

​private void prepareResultReceiver() {
>         IntentFilter mIntentFilter = new IntentFilter();
>         mIntentFilter.addAction(Const.ACTION_FINISH_STEGANO);
>         registerReceiver(mResultReceiver, mIntentFilter);
>     }​  


public static final String ACTION_FINISH_STEGANO = "finish_stegano";


*Read Extras*

long myId = intent.getLongExtra(Const.EXTRA_ID, -1);
> String myData = intent.getStringExtra(Const.EXTRA_DATA);
> String myMethod = intent.getStringExtra(Const.EXTRA_METHOD);
> long mySize = intent.getLongExtra(Const.EXTRA_SIZE, 0);
> long myTime = intent.getLongExtra(Const.EXTRA_TIME, 0);
> String myFinishDate =
> ​ i​
> ntent.getStringExtra(Const.EXTRA_FINISH_DATE);
> String myStartDate = intent.getStringExtra(Const.EXTRA_START_DATE);
> String myType = intent.getStringExtra(Const.EXTRA_TYPE);
> long myTimeInterval =
> ​
>  
intent.getFloatExtra(Const.EXTRA_BIT_RATE, 0); ​
>  
i​
> ntent.getLongExtra(Const.EXTRA_TIME_INTERVAL, -1);​
> ​ ​
>  

​public static final String EXTRA_ID = "_id";
> public static final String EXTRA_DATA = "data";
> public static final String EXTRA_TIME = "time";
> public static final String EXTRA_FINISH_DATE = "finish_date";
> public static final String EXTRA_START_DATE = "start_date";
> public static final String EXTRA_SIZE = "size";
> public static final String EXTRA_BIT_RATE = "bit_rate";​  


​I'm waiting for feedback!​

​In code may be few bugs. I try to test it deeper. At this moment
everything is ok. ​

​Best Regards,
Marcin​
*/