package de.xennis.lab.aic.controller;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
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
	
	private SensorListener myEventListener;
	private SensorManager sensorManager;
	private ProgressBar barLeft, barRight, barForward, barBackward;
	private ToggleButton powerButton;
	private Sensor accelerometer;
	private CheckBox autoPower;

	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
        //View myView = (View) findViewById(R.id.myView);
        this.myEventListener = new SensorListener(this);
        
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
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
        
        this.registerSensorsAllTime();

	}

	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class Looper extends BaseIOIOLooper {
		/** The on-board LED. */
//		private DigitalOutput led_;

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
//			led_ = ioio_.openDigitalOutput(0, true);
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
/*			led_.write(!button_.isChecked());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}*/
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
    		this.sensorManager.registerListener(this.myEventListener, proximity, SensorManager.SENSOR_DELAY_GAME);
    	}
    }
    
    private void registerSensors() {
    	this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	this.sensorManager.registerListener(this.myEventListener, this.accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
    
    protected void setBars(int forward, int backward, int left, int right) {
    	this.barForward.setProgress(forward);
    	this.barBackward.setProgress(backward);
    	this.barLeft.setProgress(left);
    	this.barRight.setProgress(right);
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
    	this.setBars(0, 0, 0, 0);
    	this.setPower(false);
    	this.sensorManager.unregisterListener(this.myEventListener, this.accelerometer);
    }
    
    @Override
    protected void onPause() {
    	this.stop();
    	this.sensorManager.unregisterListener(this.myEventListener);
    	super.onPause();
    }
}