/*
 This is a main source of the layout interface
 *
 */

package app.android.senstatis;

import java.io.IOException;
import java.text.DecimalFormat;
import android.widget.TextView.OnEditorActionListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


//import com.snyder.tabwidgetshow.R;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import android.hardware.*;
import android.widget.TabHost.TabContentFactory;
import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.view.KeyEvent;  


import app.android.senstatis.R;
import app.android.senstatis.R.drawable;
import app.android.senstatis.R.id;
import app.android.senstatis.R.layout;
import app.android.senstatis.R.menu;



public class Androsens extends TabActivity implements TabHost.OnTabChangeListener{
	
	static final int FLOATTOINTPRECISION = 100;
	
	TextView oriHead,accHead,magHead,ligHead,proxHead,presHead,tempHead,oriAccu,accAccu,magAccu,ligAccu,proxAccu,presAccu,tempAccu,tv_orientationA,tv_orientationB,tv_orientationC,tv_accelA,tv_accelB,tv_accelC,tv_magneticA,tv_magneticB,tv_magneticC,tv_lightA,tv_proxA,tv_presA,tv_tempA,tv_overview;
	ProgressBar pb_orientationA,pb_orientationB,pb_orientationC,pb_accelA,pb_accelB,pb_accelC,pb_magneticA,pb_magneticB,pb_magneticC,pb_lightA,pb_proxA,pb_presA,pb_tempA;
	SensorManager m_sensormgr;
	List<Sensor> m_sensorlist;
	protected TabHost mTabHost;
	
	TextView orien_interval, orien_counterA,orien_counterB, orien_counterC, orien_avgA,orien_avgB, orien_avgC, orien_deviA, orien_deviB, orien_deviC;
	TextView acc_interval, acc_counterA,acc_counterB, acc_counterC, acc_avgA,acc_avgB, acc_avgC, acc_deviA, acc_deviB, acc_deviC;
	TextView mag_interval, mag_counterA,mag_counterB, mag_counterC, mag_avgA,mag_avgB, mag_avgC, mag_deviA, mag_deviB, mag_deviC;
	TextView light_interval, light_counter, light_avg, light_devi; 
	TextView proxi_interval, proxi_counter, proxi_avg, proxi_devi;
	TextView press_interval, press_counter, press_avg, press_devi;
	TextView temp_interval, temp_counter, temp_avg, temp_devi;
	
	DecimalFormat d = new DecimalFormat("#.##");
	private Integer t_interval;
	
	private Timer orien_avg_timer;
	public Handler mHandler;
	private boolean state=false;
	
	private MovingAverage queue_orientA=new MovingAverage(100000);
	private MovingAverage queue_orientB=new MovingAverage(100000);
	private MovingAverage queue_orientC=new MovingAverage(100000);
	
	private SensorLoggerService logger;
	
	
	private MovingAverage queue_accA=new MovingAverage(100000);
	private MovingAverage queue_accB=new MovingAverage(100000);
	private MovingAverage queue_accC=new MovingAverage(100000);
	
	
	private MovingAverage queue_magA=new MovingAverage(100000);
	private MovingAverage queue_magB=new MovingAverage(100000);
	private MovingAverage queue_magC=new MovingAverage(100000);
	
	
	
	private MovingAverage queue_light=new MovingAverage(100000);
	private MovingAverage queue_proxi=new MovingAverage(100000);
	private MovingAverage queue_pres=new MovingAverage(100000);
	private MovingAverage queue_temp=new MovingAverage(100000);

	
	
	
	private float orien_data_counterA=0, orien_data_counterB=0,orien_data_counterC=0, orien_data_avgA=0,orien_data_avgB=0, orien_data_avgC=0,
	orien_data_deviA=0,orien_data_deviB=0,orien_data_deviC=0;
	
	
	
	private float acc_data_counterA=0, acc_data_counterB=0,acc_data_counterC=0, acc_data_avgA=0,acc_data_avgB=0, acc_data_avgC=0,
	acc_data_deviA=0,acc_data_deviB=0,acc_data_deviC=0;
	
	private float mag_data_counterA=0, mag_data_counterB=0,mag_data_counterC=0, mag_data_avgA=0,mag_data_avgB=0, mag_data_avgC=0,
	mag_data_deviA=0,mag_data_deviB=0,mag_data_deviC=0;
	
	
	
	
	private float light_data_counter=0, proxi_data_counter=0,pres_data_counter=0, temp_data_counter=0, light_data_avg=0, proxi_data_avg=0, pres_data_avg=0, temp_data_avg=0, 
	light_data_devi=0, proxi_data_devi=0, pres_data_devi=0, temp_data_devi=0;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        int number=0;
        t_interval=new Integer(3000);
        mHandler = new Handler();
        orien_avg_timer=new Timer();
        logger=new SensorLoggerService();
        
        oriHead = (TextView) this.findViewById(R.id.TextView_oriHead);
        accHead = (TextView) this.findViewById(R.id.TextView_accHead);
        magHead = (TextView) this.findViewById(R.id.TextView_magHead);
        ligHead = (TextView) this.findViewById(R.id.TextView_ligHead);
        proxHead = (TextView) this.findViewById(R.id.TextView_proxHead);
        presHead = (TextView) this.findViewById(R.id.TextView_presHead);
        tempHead = (TextView) this.findViewById(R.id.TextView_tempHead);
        
        oriAccu = (TextView) this.findViewById(R.id.oriAccuracy);
        accAccu = (TextView) this.findViewById(R.id.accAccuracy);
        magAccu = (TextView) this.findViewById(R.id.magAccuracy);
        ligAccu = (TextView) this.findViewById(R.id.ligAccuracy);
        proxAccu = (TextView) this.findViewById(R.id.proxAccuracy);
        presAccu = (TextView) this.findViewById(R.id.presAccuracy);
        tempAccu = (TextView) this.findViewById(R.id.tempAccuracy);
        
        
        tv_orientationA = (TextView) this.findViewById(R.id.TextView_oriA); pb_orientationA = (ProgressBar) this.findViewById(R.id.ProgressBar_oriA);
        tv_orientationB = (TextView) this.findViewById(R.id.TextView_oriB); pb_orientationB = (ProgressBar) this.findViewById(R.id.ProgressBar_oriB);
        tv_orientationC = (TextView) this.findViewById(R.id.TextView_oriC); pb_orientationC = (ProgressBar) this.findViewById(R.id.ProgressBar_oriC);
        tv_accelA = (TextView) this.findViewById(R.id.TextView_accA);       pb_accelA = (ProgressBar) this.findViewById(R.id.ProgressBar_accA);
        tv_accelB = (TextView) this.findViewById(R.id.TextView_accB);       pb_accelB = (ProgressBar) this.findViewById(R.id.ProgressBar_accB);
        tv_accelC = (TextView) this.findViewById(R.id.TextView_accC);       pb_accelC = (ProgressBar) this.findViewById(R.id.ProgressBar_accC);
        tv_magneticA = (TextView) this.findViewById(R.id.TextView_magA);    pb_magneticA = (ProgressBar) this.findViewById(R.id.ProgressBar_magA);
        tv_magneticB = (TextView) this.findViewById(R.id.TextView_magB);    pb_magneticB = (ProgressBar) this.findViewById(R.id.ProgressBar_magB);
        tv_magneticC = (TextView) this.findViewById(R.id.TextView_magC);    pb_magneticC = (ProgressBar) this.findViewById(R.id.ProgressBar_magC);
        tv_lightA = (TextView) this.findViewById(R.id.TextView_ligA);    pb_lightA = (ProgressBar) this.findViewById(R.id.ProgressBar_ligA);
        tv_proxA = (TextView) this.findViewById(R.id.TextView_proxA);    pb_proxA = (ProgressBar) this.findViewById(R.id.ProgressBar_proxA);
        tv_presA = (TextView) this.findViewById(R.id.TextView_presA);    pb_presA = (ProgressBar) this.findViewById(R.id.ProgressBar_presA);
        tv_tempA = (TextView) this.findViewById(R.id.TextView_tempA);    pb_tempA = (ProgressBar) this.findViewById(R.id.ProgressBar_tempA);
      
        orien_interval=(TextView)this.findViewById(R.id.TextView_TInterval);
       
        orien_counterA=(TextView)this.findViewById(R.id.Textorien_counter_x);
        orien_counterB=(TextView)this.findViewById(R.id.Textorien_counter_y);
        orien_counterC=(TextView)this.findViewById(R.id.Textorien_counter_z);
        orien_avgA=(TextView)this.findViewById(R.id.Textorien_avg_x);
        orien_avgB=(TextView)this.findViewById(R.id.Textorien_avg_y);
        orien_avgC=(TextView)this.findViewById(R.id.Textorien_avg_z);
        orien_deviA=(TextView)this.findViewById(R.id.Textorien_devi_x);
        orien_deviB=(TextView)this.findViewById(R.id.Textorien_devi_y);
        orien_deviC=(TextView)this.findViewById(R.id.Textorien_devi_z);
        
        
       acc_interval=(TextView)this.findViewById(R.id.TextView_TInterval_acc);
        acc_counterA=(TextView)this.findViewById(R.id.Textacc_counter_x);
        acc_counterB=(TextView)this.findViewById(R.id.Textacc_counter_y);
        acc_counterC=(TextView)this.findViewById(R.id.Textacc_counter_z);
        acc_avgA=(TextView)this.findViewById(R.id.Textacc_avg_x);
        acc_avgB=(TextView)this.findViewById(R.id.Textacc_avg_y);
        acc_avgC=(TextView)this.findViewById(R.id.Textacc_avg_z);
        acc_deviA=(TextView)this.findViewById(R.id.Textacc_devi_x);
        acc_deviB=(TextView)this.findViewById(R.id.Textacc_devi_y);
        acc_deviC=(TextView)this.findViewById(R.id.Textacc_devi_z);
        
        
        
      
        mag_interval=(TextView)this.findViewById(R.id.TextView_TInterval_mag);
        mag_counterA=(TextView)this.findViewById(R.id.Textmag_counter_x);
        mag_counterB=(TextView)this.findViewById(R.id.Textmag_counter_y);
        mag_counterC=(TextView)this.findViewById(R.id.Textmag_counter_z);
        mag_avgA=(TextView)this.findViewById(R.id.Textmag_avg_x);
        mag_avgB=(TextView)this.findViewById(R.id.Textmag_avg_y);
        mag_avgC=(TextView)this.findViewById(R.id.Textmag_avg_z);
        mag_deviA=(TextView)this.findViewById(R.id.Textmag_devi_x);
        mag_deviB=(TextView)this.findViewById(R.id.Textmag_devi_y);
        mag_deviC=(TextView)this.findViewById(R.id.Textmag_devi_z);
        
        
        light_interval=(TextView)this.findViewById(R.id.TextView_TInterval_light);
        light_counter=(TextView)this.findViewById(R.id.Textlight_counter);
        light_avg=(TextView)this.findViewById(R.id.Textlight_avg);
        light_devi=(TextView)this.findViewById(R.id.Textlight_devi);
        
        proxi_interval=(TextView)this.findViewById(R.id.TextView_TInterval_proxi);
        proxi_counter=(TextView)this.findViewById(R.id.Textproxi_counter);
        proxi_avg=(TextView)this.findViewById(R.id.Textproxi_avg);
        proxi_devi=(TextView)this.findViewById(R.id.Textproxi_devi);
        
        press_interval=(TextView)this.findViewById(R.id.TextView_TInterval_pres);
        press_counter=(TextView)this.findViewById(R.id.Textpressure_counter);
        press_avg=(TextView)this.findViewById(R.id.Textpressure_avg);
        press_devi=(TextView)this.findViewById(R.id.Textpressure_devi);
        
        
        temp_interval=(TextView)this.findViewById(R.id.TextView_TInterval_temp);
        temp_counter=(TextView)this.findViewById(R.id.Texttemp_counter);
        temp_avg=(TextView)this.findViewById(R.id.Texttemp_avg);
        temp_devi=(TextView)this.findViewById(R.id.Texttemp_devi);
        
        
        
        final	EditText editText = (EditText) findViewById(R.id.search);
        
      /*  
       * final ImageView startButton = (ImageView) findViewById(R.id.startButton);
      
        startButton.setEnabled(false);
        
        final ImageView stopButton = (ImageView) findViewById(R.id.stopButtion);
        stopButton.setEnabled(false);
        
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				state=true;
				connectSensors();
				orien_avg_timer=new Timer();
				 orien_avg_timer.scheduleAtFixedRate(new calculateAvgTask_Orien(),1000,t_interval*1000);
				stopButton.setEnabled(true);
				editText.setEnabled(false);
				startButton.setEnabled(false);
			}
		});
        */
//		final ImageButton ib = (ImageButton) this.findViewById(R.id.controlbutton);
		
		
		
		final Drawable first = getResources().getDrawable(
	            R.drawable.start);
	    final Drawable second = getResources().getDrawable(
	            R.drawable.stop);

	    final Button testButton = (Button) findViewById(R.id.controlbutton);
	    testButton.setEnabled(false);
	    testButton.setOnClickListener(new View.OnClickListener() {

	        public void onClick(View v) {
	            if (testButton.getBackground().equals(first)) {
	                testButton.setBackgroundDrawable(second);
	                state=false;
					m_sensormgr.unregisterListener(senseventListener);
					orien_avg_timer.cancel();
					editText.setEnabled(true);
					logger.stopLogging();
	            } else {
	            	state=true;
	            	cleanup();
	            	clearcount();
					try {
						connectSensors();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					orien_avg_timer=new Timer();
					 orien_avg_timer.scheduleAtFixedRate(new calculateAvgTask_Orien(),1000,t_interval*1000);
					 editText.setEnabled(false);
	                testButton.setBackgroundDrawable(first);
	            }
	        }
	    });
	/*	
		ib.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.selector));

	


	ib.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View button) {
            if (button.isSelected()){
                button.setSelected(false);
            } else {
               // ib.setSelected(false);
                //put all the other buttons you might want to disable here...
                button.setSelected(true);
            }
        }
    });
        */
	/*	
		stopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				state=false;
				m_sensormgr.unregisterListener(senseventListener);
				orien_avg_timer.cancel();
				editText.setEnabled(true);
         stopButton.setEnabled(false);
				
				startButton.setEnabled(true);
				
			}
		});
        
	*/	
		
	
		editText.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        boolean handled = false;
		        if (actionId == EditorInfo.IME_ACTION_SEND) {
		            //sendMessage();
		        //	ib.setImageResource(R.drawable.start);
		        	//startButton.setEnabled(true);
		        	testButton.setEnabled(true);
		        	testButton.setBackgroundDrawable(first);
		        	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		        	imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		            if(editText.getText().toString().equals("")) t_interval=3;
		            else{
		        	t_interval=Integer.valueOf(editText.getText().toString());
		        	if(t_interval==0) t_interval=3;}
		        	state=true;
		          
		        	 cleanup();
		        	 clearcount();
					try { 
						connectSensors();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					orien_avg_timer=null;
					orien_avg_timer=new Timer();
					 orien_avg_timer.scheduleAtFixedRate(new calculateAvgTask_Orien(),1000,t_interval*1000);
					 cleanup();
					 editText.setEnabled(false);
		            handled = true;
		        }
		        return handled;
		    }
		});
		
			
			
			
			
			
		
		
		
        tv_overview= (TextView) this.findViewById(R.id.TextViewOverview);
        
       
        

		setupTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
/*
		setupTab(new TextView(this), "Orientation");
		setupTab(new TextView(this), "Acceleration");
		setupTab(new TextView(this), "Magnetic");
		setupTab(new TextView(this), "Light");
		setupTab(new TextView(this), "Proximity");
		setupTab(new TextView(this), "Pressure");
		setupTab(new TextView(this), "Temperature");
		setupTab(new TextView(this), "Overview");
		*/
        
       // mTabHost = getTabHost();
		/*
		Intent intent = new Intent().setClass(this, FilesActivity.class);
		intent = new Intent().setClass(this, FilesActivity.class);
		TabSpec spec = mTabHost.newTabSpec("files").setIndicator(getResources().getString(R.string.files_activity_tab))
				.setContent(intent);
		mTabHost.addTab(spec);
		*/
		
        mTabHost.addTab(mTabHost.newTabSpec("tab_1").setIndicator("Orientation").setContent(R.id.Orientation));
        mTabHost.addTab(mTabHost.newTabSpec("tab_2").setIndicator("Acceleration").setContent(R.id.Acceleration));
        mTabHost.addTab(mTabHost.newTabSpec("tab_3").setIndicator("Magnetic field").setContent(R.id.Magnetic));
        mTabHost.addTab(mTabHost.newTabSpec("tab_4").setIndicator("Light").setContent(R.id.Light));
        mTabHost.addTab(mTabHost.newTabSpec("tab_5").setIndicator("Proximity").setContent(R.id.Proximity));
        mTabHost.addTab(mTabHost.newTabSpec("tab_6").setIndicator("Pressure").setContent(R.id.Pressure));
        mTabHost.addTab(mTabHost.newTabSpec("tab_7").setIndicator("Temperatur").setContent(R.id.Temperature));
        mTabHost.addTab(mTabHost.newTabSpec("tab_8").setIndicator("Overview").setContent(R.id.Overview));
     //   mTabHost.addTab(mTabHost.newTabSpec("tab_9").setIndicator("Files").setContent(intent));
        
        m_sensormgr  = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        m_sensorlist =  m_sensormgr.getSensorList(Sensor.TYPE_ALL);
        
        
		 mTabHost.setCurrentTab(0);
	        mTabHost.setOnTabChangedListener(this);
	        
	       
	       //orien_avg_timer.scheduleAtFixedRate(new calculateAvgTask_Orien(),1000,3000);
	       
	       
	      
        
        //connectSensors();
    }
    private void setupTabHost() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
	}
    private void setupTab(final View view, final String tag) {
		View tabview = createTabView(mTabHost.getContext(), tag);

		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(new TabContentFactory() {
			public View createTabContent(String tag) {return view;}
		});
		mTabHost.addTab(setContent);

	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		String temp="R"+"."+"id"+"."+text;
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}

	
	
	private void cleanup()
	{queue_orientA.setBeginindex(0);
	queue_orientA.setEndindex(0);
	
	 queue_orientB.setBeginindex(0);
	 queue_orientB.setEndindex(0);
	 
	 queue_orientC.setBeginindex(0);
	 queue_orientC.setEndindex(0);
	 
	
	queue_accA.setBeginindex(0);
	queue_accA.setEndindex(0);
	
	 queue_accB.setBeginindex(0);
	 queue_accB.setEndindex(0);
	 
	 queue_accC.setBeginindex(0);
	 queue_accC.setEndindex(0);
	 
	
	
	
	queue_magA.setBeginindex(0);
	queue_magA.setEndindex(0);
	queue_magB.setBeginindex(0);
	queue_magB.setEndindex(0);
	queue_magC.setBeginindex(0);
	queue_magC.setEndindex(0);
	
	
	
	 queue_light.setBeginindex(0);
	 queue_light.setEndindex(0);
	 
	 queue_proxi.setBeginindex(0);
	 queue_proxi.setEndindex(0);
	 queue_pres.setBeginindex(0);
	 queue_pres.setEndindex(0);
	}
	void clearcount()
	{
	 
	 orien_data_counterA=0; 
	 orien_data_counterB=0; 
	 orien_data_counterC=0;
		acc_data_counterA=0;
		acc_data_counterB=0;
		acc_data_counterC=0;
		mag_data_counterA=0;
		mag_data_counterB=0;
		mag_data_counterC=0;
		light_data_counter=0;
		proxi_data_counter=0;
		pres_data_counter=0;
		temp_data_counter=0;
		
		
		
	}
	
    SensorEventListener senseventListener = new SensorEventListener(){
    
    /* (non-Javadoc)
     * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
     */
    @SuppressWarnings("deprecation")
	@Override
    public void onSensorChanged(SensorEvent event) {
    String accuracy;
    
    switch(event.accuracy){
        case SensorManager.SENSOR_STATUS_ACCURACY_HIGH: accuracy="SENSOR_STATUS_ACCURACY_HIGH";break;
        case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM: accuracy="SENSOR_STATUS_ACCURACY_MEDIUM";break;
        case SensorManager.SENSOR_STATUS_ACCURACY_LOW: accuracy="SENSOR_STATUS_ACCURACY_LOW";break;
        case SensorManager.SENSOR_STATUS_UNRELIABLE: accuracy="SENSOR_STATUS_UNRELIABLE";break;
        default: accuracy="UNKNOWN";
    }
   
    
    if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
       // oriAccu.setText(accuracy);
    	oriAccu.setText("Raw Data");
        pb_orientationA.setProgress( Math.abs((int)event.values[0]));
        pb_orientationB.setProgress( Math.abs((int)event.values[1]));
        pb_orientationC.setProgress( Math.abs((int)event.values[2]));
        queue_orientA.pushValue(event.values[0]);
        
        queue_orientB.pushValue(event.values[1]);
        queue_orientC.pushValue(event.values[2]);
        
        orien_counterA.setText(d.format(orien_data_counterA++));
        orien_counterB.setText(d.format(orien_data_counterB++));
        orien_counterC.setText(d.format(orien_data_counterC++));
        
        tv_orientationA.setText(String.format("%.2f",event.values[0]));
        tv_orientationB.setText(String.format("%.2f",event.values[1]));
        tv_orientationC.setText(String.format("%.2f",event.values[2]));
        logger.write2csv(event);
    }
    if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
        //accAccu.setText(accuracy);
    	accAccu.setText("Raw Data");
        pb_accelA.setProgress( Math.abs( (int) event.values[0]*FLOATTOINTPRECISION ));
        pb_accelB.setProgress( Math.abs( (int) event.values[1]*FLOATTOINTPRECISION ));
        pb_accelC.setProgress( Math.abs( (int) event.values[2]*FLOATTOINTPRECISION ));
        queue_accA.pushValue(event.values[0]);
        queue_accB.pushValue(event.values[1]);
        queue_accC.pushValue(event.values[2]);
        acc_counterA.setText(d.format(acc_data_counterA++));
        acc_counterB.setText(d.format(acc_data_counterB++));
        acc_counterC.setText(d.format(acc_data_counterC++));
        tv_accelA.setText(String.format("%.2f",event.values[0]));
        tv_accelB.setText(String.format("%.2f",event.values[1]));
        tv_accelC.setText(String.format("%.2f",event.values[2]));
    }
    if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
        //magAccu.setText(accuracy);
    	magAccu.setText("Raw Data");
        pb_magneticA.setProgress( Math.abs((int)event.values[0]*FLOATTOINTPRECISION ));
        pb_magneticB.setProgress( Math.abs((int)event.values[1]*FLOATTOINTPRECISION ));
        pb_magneticC.setProgress( Math.abs((int)event.values[2]*FLOATTOINTPRECISION ));
        queue_magA.pushValue(event.values[0]);
        queue_magB.pushValue(event.values[1]);
        queue_magC.pushValue(event.values[2]);
        mag_counterA.setText(d.format(mag_data_counterA++));
        mag_counterB.setText(d.format(mag_data_counterB++));
        mag_counterC.setText(d.format(mag_data_counterC++));
        
        tv_magneticA.setText(String.format("%.2f",event.values[0]));
        tv_magneticB.setText(String.format("%.2f",event.values[1]));
        tv_magneticC.setText(String.format("%.2f",event.values[2]));
        logger.write2csv(event);
        
    }
    if(event.sensor.getType()==Sensor.TYPE_LIGHT){
        //ligAccu.setText(accuracy);
    	ligAccu.setText("Raw Data");
        pb_lightA.setProgress( Math.abs((int)event.values[0]));
        tv_lightA.setText(String.format("%.2f",event.values[0]));
        queue_light.pushValue(event.values[0]);
        light_counter.setText(d.format(light_data_counter++));
        logger.write2csv(event);
    }
    if(event.sensor.getType()==Sensor.TYPE_PROXIMITY){
      //  proxAccu.setText(accuracy);
    	proxAccu.setText("Raw Data");
        pb_proxA.setProgress( Math.abs((int)event.values[0]));
        tv_proxA.setText(String.format("%.2f",event.values[0]));
        queue_proxi.pushValue(event.values[0]);
        proxi_counter.setText(d.format(proxi_data_counter++));
        logger.write2csv(event);
    }
    if(event.sensor.getType()==Sensor.TYPE_PRESSURE){
       // presAccu.setText(accuracy);
    	presAccu.setText("Raw Data");
        pb_presA.setProgress( Math.abs((int)event.values[0]));
        tv_presA.setText(String.format("%.2f",event.values[0]));
        queue_pres.pushValue(event.values[0]);
        press_counter.setText(d.format(pres_data_counter++));
        logger.write2csv(event);
    }
    if(event.sensor.getType()==Sensor.TYPE_TEMPERATURE){
        //tempAccu.setText(accuracy);
    	tempAccu.setText("Raw Data");
        pb_tempA.setProgress( Math.abs((int)event.values[0]));
        tv_tempA.setText(String.format("%.2f",event.values[0]));
        queue_temp.pushValue(event.values[0]);
        temp_counter.setText(d.format(temp_data_counter++));
        logger.write2csv(event);
    }
}

/* (non-Javadoc)
 * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
 */
@Override
public void onAccuracyChanged(Sensor sensor, int accuracy) {
// TODO Auto-generated method stub

}

};


/* (non-Javadoc)
 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
 */
@Override
public boolean onOptionsItemSelected(MenuItem item) {
switch(item.getItemId()){
case R.id.quit: 
	m_sensormgr.unregisterListener(senseventListener);
	logger.stopLogging();
	if(orien_avg_timer!= null) {
		orien_avg_timer.cancel();
		orien_avg_timer.purge();
		   orien_avg_timer = null;
		}
	android.os.Process.killProcess(android.os.Process.myPid());
	 System.exit(0);
	finish();
	break;		
	
case R.id.file:
	Intent intent = new Intent().setClass(this, FilesActivity.class);
	
     this.startActivity(intent);
     break;
 
default: 
return super.onOptionsItemSelected(item);
} return true;
}

/* (non-Javadoc)
 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
 */
@Override
public boolean onCreateOptionsMenu(Menu menu) {
MenuInflater inflater = getMenuInflater();
inflater.inflate(R.menu.menu, menu);
return super.onCreateOptionsMenu(menu);
}


/* (non-Javadoc)
 * @see android.app.Activity#onPause()
 */
@Override
protected void onPause() {
// TODO Auto-generated method stub
m_sensormgr.unregisterListener(senseventListener);
logger.stopLogging();
super.onPause();
}


/* (non-Javadoc)
 * @see android.app.Activity#onResume()
 */
@Override
protected void onResume() {
// TODO Auto-generated method stub
	
try {
	connectSensors();
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
super.onResume();
}

protected String getSensorInfo(Sensor sen){
String sensorInfo="INVALID";
String snsType;

switch(sen.getType()){
case Sensor.TYPE_ACCELEROMETER     : snsType="TYPE_ACCELEROMETER";break;
case Sensor.TYPE_ALL               : snsType="TYPE_ALL";break;
case Sensor.TYPE_GYROSCOPE         : snsType="TYPE_GYROSCOPE";break;
case Sensor.TYPE_LIGHT             : snsType="TYPE_LIGHT";break;
case Sensor.TYPE_MAGNETIC_FIELD    : snsType="TYPE_MAGNETIC_FIELD";break;
case Sensor.TYPE_ORIENTATION       : snsType="TYPE_ORIENTATION";break;
case Sensor.TYPE_PRESSURE          : snsType="TYPE_PRESSURE";break;
case Sensor.TYPE_PROXIMITY         : snsType="TYPE_PROXIMITY";break;
case Sensor.TYPE_TEMPERATURE      : snsType="TYPE_AMBIENT_TEMPERATURE";break;
default: snsType="UNKNOWN_TYPE "+sen.getType();break;
}

sensorInfo=sen.getName()+"\n";
sensorInfo+="Version: "+sen.getVersion()+"\n";
sensorInfo+="Vendor: "+sen.getVendor()+"\n";
sensorInfo+="Type: "+sen.getType()+"\n";
sensorInfo+="MaxRange: "+sen.getMaximumRange()+"\n";
sensorInfo+="Resolution: "+sen.getResolution()+"\n";
sensorInfo+="Power: "+sen.getPower()+"\n";
return sensorInfo;
}


protected void connectSensors() throws IOException{
	
m_sensormgr.unregisterListener(senseventListener);
if(state)
{
	//logger.startLogging(m_sensorlist);
	
if(!m_sensorlist.isEmpty()){
Sensor snsr;
for(int i=0;i<m_sensorlist.size();i++){
snsr=m_sensorlist.get(i);
m_sensormgr.registerListener(senseventListener, snsr, SensorManager.SENSOR_DELAY_NORMAL);

if(snsr.getType()==Sensor.TYPE_ORIENTATION && mTabHost.getCurrentTab()== 0 ){
oriHead.setText(getSensorInfo(snsr));
pb_orientationA.setMax((int)snsr.getMaximumRange());
pb_orientationB.setMax((int)snsr.getMaximumRange());
pb_orientationC.setMax((int)snsr.getMaximumRange());
logger.startLogging(snsr);
//m_sensormgr.registerListener(senseventListener, snsr, SensorManager.SENSOR_DELAY_NORMAL);
}
if(snsr.getType()==Sensor.TYPE_ACCELEROMETER ){
accHead.setText(getSensorInfo(snsr));
pb_accelA.setMax((int)(snsr.getMaximumRange()*SensorManager.GRAVITY_EARTH*FLOATTOINTPRECISION));
pb_accelB.setMax((int)(snsr.getMaximumRange()*SensorManager.GRAVITY_EARTH*FLOATTOINTPRECISION));
pb_accelC.setMax((int)(snsr.getMaximumRange()*SensorManager.GRAVITY_EARTH*FLOATTOINTPRECISION));
logger.startLogging(snsr);
//m_sensormgr.registerListener(senseventListener, snsr, SensorManager.SENSOR_DELAY_NORMAL);
}
if(snsr.getType()==Sensor.TYPE_MAGNETIC_FIELD ){
magHead.setText(getSensorInfo(snsr));
pb_magneticA.setMax((int)(snsr.getMaximumRange()*FLOATTOINTPRECISION));
pb_magneticB.setMax((int)(snsr.getMaximumRange()*FLOATTOINTPRECISION));
pb_magneticC.setMax((int)(snsr.getMaximumRange()*FLOATTOINTPRECISION));
logger.startLogging(snsr);
//m_sensormgr.registerListener(senseventListener, snsr, SensorManager.SENSOR_DELAY_NORMAL);
}
if(snsr.getType()==Sensor.TYPE_LIGHT ){
ligHead.setText(getSensorInfo(snsr));
pb_lightA.setMax((int)(snsr.getMaximumRange()));
logger.startLogging(snsr);
//m_sensormgr.registerListener(senseventListener, snsr, SensorManager.SENSOR_DELAY_NORMAL);
}
if(snsr.getType()==Sensor.TYPE_PROXIMITY ){
proxHead.setText(getSensorInfo(snsr));
pb_proxA.setMax((int)(snsr.getMaximumRange()));
logger.startLogging(snsr);
//m_sensormgr.registerListener(senseventListener, snsr, SensorManager.SENSOR_DELAY_NORMAL);
}
if(snsr.getType()==Sensor.TYPE_PRESSURE){
presHead.setText(getSensorInfo(snsr));
pb_presA.setMax((int)(snsr.getMaximumRange()));
logger.startLogging(snsr);
//m_sensormgr.registerListener(senseventListener, snsr, SensorManager.SENSOR_DELAY_NORMAL);
}
if(snsr.getType()==Sensor.TYPE_TEMPERATURE){
tempHead.setText(getSensorInfo(snsr));
pb_tempA.setMax((int)(snsr.getMaximumRange()));
logger.startLogging(snsr);
//m_sensormgr.registerListener(senseventListener, snsr, SensorManager.SENSOR_DELAY_NORMAL);
}
{
tv_overview.append(getSensorInfo(snsr)+"\n\n");
}
}
}
}
}


/* (non-Javadoc)
 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
 */
@Override
public void onTabChanged(String arg0) {
tv_overview.setText("");

try {
	connectSensors();
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
}


public void onBackPressed() {
	// TODO Auto-generated method stub

	finish();
	 System.exit(0);
		}

class calculateAvgTask_Orien extends TimerTask{
	public void run()
	{
		//light_devi=queue.getDevi();
		orien_data_avgA=queue_orientA.getValue();
		orien_data_avgB=queue_orientB.getValue();
		orien_data_avgC=queue_orientC.getValue();
		
	//	orien_data_counterA=queue_orientA.getCount();
		//orien_data_counterB=queue_orientB.getCount();
		//orien_data_counterC=queue_orientC.getCount();
		
		orien_data_deviA=queue_orientA.getDevi();
		orien_data_deviB=queue_orientB.getDevi();
		orien_data_deviC=queue_orientC.getDevi();
		
		queue_orientA.afterwards();
		queue_orientB.afterwards();
		queue_orientC.afterwards();
	
		
		
		acc_data_avgA=queue_accA.getValue();
		acc_data_avgB=queue_accB.getValue();
		acc_data_avgC=queue_accC.getValue();
		
	//	acc_data_counterA=queue_accA.getCount();
	//	acc_data_counterB=queue_accB.getCount();
	//	acc_data_counterC=queue_accC.getCount();
		
		acc_data_deviA=queue_accA.getDevi();
		acc_data_deviB=queue_accB.getDevi();
		acc_data_deviC=queue_accC.getDevi();
		
		queue_accA.afterwards();
		queue_accB.afterwards();
		queue_accC.afterwards();
		
		
		
		
		mag_data_avgA=queue_magA.getValue();
		mag_data_avgB=queue_magB.getValue();
		mag_data_avgC=queue_magC.getValue();
		
//		mag_data_counterA=queue_magA.getCount();
//		mag_data_counterB=queue_magB.getCount();
//		mag_data_counterC=queue_magC.getCount();
		
		mag_data_deviA=queue_magA.getDevi();
		mag_data_deviB=queue_magB.getDevi();
		mag_data_deviC=queue_magC.getDevi();
		
		queue_magA.afterwards();
		queue_magB.afterwards();
		queue_magC.afterwards();
		
		
		
		
		light_data_avg=queue_light.getValue();
	//	light_data_counter=queue_light.getCount();
		light_data_devi=queue_light.getDevi();
		queue_light.afterwards();
		
		
		proxi_data_avg=queue_proxi.getValue();
		//proxi_data_counter=queue_proxi.getCount();
		proxi_data_devi=queue_proxi.getDevi();
		queue_proxi.afterwards();
		
		
		pres_data_avg=queue_pres.getValue();
	//	pres_data_counter=queue_pres.getCount();
		pres_data_devi=queue_pres.getDevi();
		queue_pres.afterwards();
		
		
		temp_data_avg=queue_temp.getValue();
	//	temp_data_counter=queue_temp.getCount();
		temp_data_devi=queue_temp.getDevi();
		queue_temp.afterwards();
		
		
		
		
		mHandler.post(updateOreintationDisplayTask);
		
		clearcount();
		
	}
	
	
}

public void updateOreintationDisplay() {
    //	switch(radioSelection) 
    	{
    	//case 0:
    		
    		
           orien_interval.setText(d.format(t_interval)+" s");
           
            orien_avgA.setText(d.format(orien_data_avgA));
            orien_avgB.setText(d.format(orien_data_avgB));
            orien_avgC.setText(d.format(orien_data_avgC));
            
            orien_deviA.setText(d.format(orien_data_deviA));
            orien_deviB.setText(d.format(orien_data_deviB));
            orien_deviC.setText(d.format(orien_data_deviC));
            
            
           acc_interval.setText(d.format(t_interval)+" s");
           
            acc_avgA.setText(d.format(acc_data_avgA));
            acc_avgB.setText(d.format(acc_data_avgB));
            acc_avgC.setText(d.format(acc_data_avgC));
            
            acc_deviA.setText(d.format(acc_data_deviA));
            acc_deviB.setText(d.format(acc_data_deviB));
            acc_deviC.setText(d.format(acc_data_deviC));
            
            
            mag_interval.setText(d.format(t_interval)+" s");
            
            mag_avgA.setText(d.format(mag_data_avgA));
            mag_avgB.setText(d.format(mag_data_avgB));
            mag_avgC.setText(d.format(mag_data_avgC));
            
            mag_deviA.setText(d.format(mag_data_deviA));
            mag_deviB.setText(d.format(mag_data_deviB));
            mag_deviC.setText(d.format(mag_data_deviC));
            
            
            
            light_interval.setText(d.format(t_interval)+" s");
            
            
            light_avg.setText(d.format(light_data_avg));
           
            light_devi.setText(d.format(light_data_devi));
           
            
            temp_interval.setText(d.format(t_interval)+" s");
           
            temp_avg.setText(d.format(temp_data_avg));
           
            temp_devi.setText(d.format(temp_data_devi));
            
         
            proxi_interval.setText(d.format(t_interval)+" s");
          
            
            proxi_avg.setText(d.format(proxi_data_avg));
           
            proxi_devi.setText(d.format(proxi_data_devi));
           
            press_interval.setText(d.format(t_interval)+" s");
          
            press_avg.setText(d.format(proxi_data_avg));
           
            press_devi.setText(d.format(proxi_data_devi));
            
            
            
            
          ;
    	//	break;
    	}
}


private Runnable updateOreintationDisplayTask = new Runnable() {
	public void run() {
		updateOreintationDisplay();
	}
};
}