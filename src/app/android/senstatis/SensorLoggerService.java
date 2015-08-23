/****
 * 
 * This file deals with the sensor logging functionality 
 * 
 * 
 */



package app.android.senstatis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import android.content.Context;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import app.android.senstatis.R;



public class SensorLoggerService  {
	//private static final String TAG = "at.feichtinger.sensorlogger.services.SensorLoggerService";
	
	public static final String DATA_STATUS = "data_status";
	 public static final String LOG_DIRECTORY = "SensorLoggerData";
	 private String filePathUniqueIdentifier;
	 private FileWriter filewriter;
	 
	 private long referenceTime;

		/**
		 * Remembers when logging started. Used for creating a file name.
		 */
		private Date startTime;

		/**
		 * Used to keep phone listening to sensor values when the screen is turned
		 * off.
		 */
		private PowerManager.WakeLock partialWakeLock;

		/**
		 * Stores the sensor events of each sensor in a queue. Keys are the IDs of
		 * the used sensors. The SensorEventListener will store the events in this
		 * map. A blocking queue is used to store the events because its content
		 * will be written to files by a separate thread.
		 */
		private HashMap<Integer, BufferedWriter> sensorMap=new HashMap<Integer, BufferedWriter>();

	 
	 
	/**
	 * 
	 * Handler of incoming messages from clients.
	 */
	public void startLogging(Sensor snsr) throws IOException {
		//	currentActivity = data.getString(DATA_ACTIVITY);
		
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		
		
		
		
		
		
		
		
			referenceTime = SystemClock.uptimeMillis();
			startTime = new Date();
			 generateNewFilePathUniqueIdentifier();
			
			
				int sensor_type=snsr.getType();
				
	             File file = getLogFile(snsr);
	            
	            	 
	            	 
	             try {
	            	 
	            	 if(file.exists())
	            	 {  filewriter=new FileWriter(file,true); 
	            	 System.out.println("file exit!");
	            	 }
	            	 else  
	            		 { System.out.println("file does not exit!");
	            		 
	            		System.out.println(file.getAbsolutePath());
	            		
	            		if(file.createNewFile())
	            			System.out.println("true");
	            		else System.out.println("false");
	            	//	System.out.println("file exit?="+file.createNewFile());
	            		 filewriter = new FileWriter(file,true);
	            		
	            		 }
	            	 
	                 BufferedWriter writer = new BufferedWriter(filewriter);
	                 
	 
	                	
	                	 
	                	 
	                	 writer.write("time[ms], x-axis y-axis, z-axis\n");
	                	 writer.flush();
	                
	                 sensorMap.put(sensor_type, writer);
	             } catch (IOException e) {
	                 e.printStackTrace();
	             }
				
	             }	
		
			//}
		
			
			

			
		
	 
	    public void Createfilepath() {
	        
	        generateNewFilePathUniqueIdentifier();
	    }

	    public void generateNewFilePathUniqueIdentifier() {
	        Date date = new Date();
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
	        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	        filePathUniqueIdentifier = sdf.format(date);
	    }

	    public void resetFilePathUniqueIdentifier() {
	        filePathUniqueIdentifier = null;
	    }

	    /**
	     * Returns the filePathUniqueIdentifier that can be used for saving files.
	     *
	     * @throw IllegalStateException if the filePathUniqueIdentifier hasn't been
	     *        initialized.
	     */
	    public String getFilePathUniqueIdentifier() {
	        if (filePathUniqueIdentifier == null) {
	            throw new IllegalStateException(
	                    "filePathUniqueIdentifier has not been initialized for the app.");
	        }
	        return filePathUniqueIdentifier;
	    }

	 
	    

	    
		private File getLogFile(Sensor sensor) throws IOException {
			
			
			
          
          String  RootDir = Environment.getExternalStorageDirectory()
                    + File.separator + "SensorLoggerData";
          String RootDir2=RootDir+File.separator+getFilePathUniqueIdentifier();
         
            File RootFile = new File(RootDir);
            RootFile.mkdir();
            File RootFile2 = new File(RootDir2);
            RootFile2.mkdir();
            
			File file1=new File(RootFile, getFileName(sensor)+ ".csv");
			//Log.i(TAG, "file created: " + file.getAbsolutePath());
			return file1;
		}

		public void stopLogging() {
		
			for (BufferedWriter writer : sensorMap.values()) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
				//	Log.e(TAG, e.getMessage(), e);
				}
			}
			
		}

		private String getFileName(Sensor sensor) {
			final SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
			String filename = dateFormat.format(startTime) + "-"+sensor.getName().toLowerCase().replace(" ", "");
			
			return filename;
		}
	//}

	

	
	
	/**
	 * The sensor event listener. Puts sensor values into the corresponding
	 * queue (depending on the sensor type).
	 */
	/*
		 * Conversion factor from nanoseconds to milliseconds
		 * 
		 * <pre>
		 * nano  = 10^-9 
		 * mikro = 10^-6
		 * milli = 10^-3
		 * </pre>
		 */
		private final static int CONVERSION_FACTOR = 1000 * 1000;

		public void write2csv(final SensorEvent event) {
			// Log.i(TAG, "onSensorChanged called:" + toCSVString(event));

			try {
				// get the corresponding queue
				System.out.println("!!!!!!!!!!!!!****************************");
				System.out.println("get sensor type="+event.sensor.getType());
				final BufferedWriter writer = sensorMap.get(event.sensor.getType());
				
					writer.write(toCSVString(event));
					writer.flush();
				
			} catch (IOException e) {
			//	Log.e(TAG, e.getMessage(), e);
			}
		}

		
		
		/**
		 * Creates a .csv representation out of a sensor event.
		 * 
		 * @param event
		 * @return
		 */
		private String toCSVString(final SensorEvent event) {
			return (event.timestamp / CONVERSION_FACTOR) - referenceTime + "," + event.values[0] + ", "
					+ event.values[1] + "," + event.values[2] + "\n";
		}
	

	/* **************************************************************************
	 * Service life-cycle
	 */

	/**
	 * Called by the system when the service is first created. Do not call this
	 * method directly.
	 */
	
	
}
