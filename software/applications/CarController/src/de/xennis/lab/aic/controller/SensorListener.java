package de.xennis.lab.aic.controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class SensorListener implements SensorEventListener {

	public static final int TOLERANCE_ACCELEROMETER = 1;
	
	private final Main mainActivity;
	
	public SensorListener(Main mainActivity) {
		this.mainActivity = mainActivity;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			this.onAccelerometer(event.values[0], event.values[1], event.values[2], event.timestamp);
		} else if(event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
			this.onProximity(event.values[0], event.timestamp);
		}

	}
	
	private void onAccelerometer(float x, float y, float z, long timestamp) {
		
		int forward = 0;
		int backward = 0;
		int left = 0;
		int right = 0;

		if(x > TOLERANCE_ACCELEROMETER) {
			forward = this.convertAccelerometerValues(x);
		} else if(x < TOLERANCE_ACCELEROMETER) {
			backward = this.convertAccelerometerValues(x);
		}
		
		if(y > TOLERANCE_ACCELEROMETER) {
			right = this.convertAccelerometerValues(y);
		} else if(y < TOLERANCE_ACCELEROMETER) {
			left = this.convertAccelerometerValues(y);
		}
		
		this.mainActivity.setBars(forward, backward, left, right);
	}

	private void onProximity(float value, long timestamp) {
		if(value < 1) {
			this.mainActivity.setPower(true);
		} else {
			this.mainActivity.setPower(false);
		}
	}
	
	private int convertAccelerometerValues(float f) {
		return (int) Math.abs(f * 100);
	}

}
