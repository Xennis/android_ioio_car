package de.xennis.lab.aic.controller;

import java.util.Arrays;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * This is the main activity of the CarController application.
 * 
 * Des.
 */
public class Main extends IOIOActivity {

	public static final int BAR_MAX = 1000;
	
	private SensorListener eventListener;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	
	/** 0: Forward, 1: Backward, 2: Left, 3: Right */
	public static boolean[] pinsBoolean;

	// GUI
	private CheckBox autoPower;
	private ProgressBar barLeft, barRight, barForward, barBackward;
	private ToggleButton powerButton;
	
	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Init
		pinsBoolean = new boolean[4];
		Arrays.fill(pinsBoolean, false);
		
        this.eventListener = new SensorListener(this);        
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        this.initGuiComponents();
        
        this.registerSensorsAllTime();

	}

	/**
	 * Init gui components
	 */
	private void initGuiComponents() {
        this.barLeft = (ProgressBar) findViewById(R.id.barLeft);
        this.barLeft.setMax(BAR_MAX);
        this.barRight = (ProgressBar) findViewById(R.id.barRight);
        this.barRight.setMax(BAR_MAX);
        this.barForward = (ProgressBar) findViewById(R.id.barForward);
        this.barForward.setMax(BAR_MAX);
        this.barBackward = (ProgressBar) findViewById(R.id.barBackward);
        this.barBackward.setMax(BAR_MAX);
        
        this.autoPower = (CheckBox) findViewById(R.id.autoPower);
        this.autoPower.setChecked(true);
        
        this.powerButton = (ToggleButton) findViewById(R.id.powerButton);
        this.powerButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    		
    		@Override
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    			if(isChecked) {
    				start();
    			} else {
    				stop();
    			}
    			
    		}
    	});
	}
	
	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class Looper extends BaseIOIOLooper {
		
		/** Move forward */
		private DigitalOutput outForward_;
		/** Move backward */
		private DigitalOutput outBackward_;
		/** Move left */
		private DigitalOutput outLeft_;
		/** Move right */
		private DigitalOutput outRight_;

		/**
		 * Called every time a connection with IOIO has been established.
		 * Typically used to open pins.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#setup()
		 */
		@Override
		protected void setup() throws ConnectionLostException {
			outForward_ = ioio_.openDigitalOutput(41);
			outBackward_ = ioio_.openDigitalOutput(42);
			outLeft_ = ioio_.openDigitalOutput(43);
			outRight_ = ioio_.openDigitalOutput(44);
		}

		/**
		 * Called repetitively while the IOIO is connected.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
		 */
		@Override
		public void loop() throws ConnectionLostException {
			outForward_.write(Main.pinsBoolean[0]);
			outBackward_.write(Main.pinsBoolean[1]);
			outLeft_.write(Main.pinsBoolean[2]);
			outRight_.write(Main.pinsBoolean[3]);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
    
    private void registerSensorsAllTime() {
    	Sensor proximity = this.sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    	if(proximity != null) {
    		this.sensorManager.registerListener(this.eventListener, proximity, SensorManager.SENSOR_DELAY_GAME);
    	}
    }
    
    private void registerSensors() {
    	this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	this.sensorManager.registerListener(this.eventListener, this.accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
    
    protected void setBars(int forward, int backward, int left, int right) {
    	this.barForward.setProgress(forward);
    	this.barBackward.setProgress(backward);
    	this.barLeft.setProgress(left);
    	this.barRight.setProgress(right);
    	
    	pinsBoolean[0] = convertIntToBoolean(forward);
    	pinsBoolean[1] = convertIntToBoolean(backward);
    	pinsBoolean[2] = convertIntToBoolean(left);
    	pinsBoolean[3] = convertIntToBoolean(right);
    	
    	Log.v("setBars", "Pins" + pinsBoolean[0] + pinsBoolean[1] + pinsBoolean[2] + pinsBoolean[3] );
    }
    
	/**
	 * Convert integer to boolean.
	 * 
	 * @param i
	 *            value
	 * @return true, if value > 30
	 */
    private Boolean convertIntToBoolean(int i) {
    	if(i > 30) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    protected void setPower(boolean power) {
    	if(this.autoPower.isChecked()) {
    		this.powerButton.setChecked(power);
    	}
    }
    
    private void start() {
    	this.registerSensors();
    }
    
    private void stop() {
    	this.sensorManager.unregisterListener(this.eventListener, this.accelerometer);
    	this.setBars(0, 0, 0, 0);
    	this.setPower(false);
    }
    
    @Override
    protected void onPause() {
    	this.sensorManager.unregisterListener(this.eventListener);
    	this.stop();
    	super.onPause();
    }
}