package de.xennis.lab.aic.controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class SensorListener implements SensorEventListener {

	/** Tolerance sensor accelerometer */
	public static final int TOLERANCE_ACCELEROMETER = 1;

	/** Tolerance sensor proximity */
	public static final int TOLERANCE_PROXIMITY = 1;
	
	/** Activity */
	private final Main activity;
	
	/**
	 * 
	 * @param mainActivity Activity
	 */
	public SensorListener(Main mainActivity) {
		this.activity = mainActivity;
	}
	
	/* (non-Javadoc)
	 * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/* (non-Javadoc)
	 * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			this.onAccelerometer(event.values[0], event.values[1], event.values[2], event.timestamp);
		} else if(event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
			this.onProximity(event.values[0], event.timestamp);
		}

	}
	
	/**
	 * Accelerometer sensor
	 * 
	 * @param x
	 *            Acceleration minus Gx on the x-axis, in SI units (m/s^2)
	 * @param y
	 *            Acceleration minus Gy on the x-axis, in SI units (m/s^2)
	 * @param z
	 *            Acceleration minus Gz on the x-axis, in SI units (m/s^2)
	 * @param timestamp
	 *            Event timestamp
	 */
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
		
		this.activity.setBars(forward, backward, left, right);
	}

	/**
	 * Proximity sensor
	 * 
	 * @param value
	 *            Proximity sensor distance measured, in centimeters
	 * @param timestamp
	 *            Event timestamp
	 */
	private void onProximity(float value, long timestamp) {
		if(value < TOLERANCE_PROXIMITY) {
			this.activity.setPower(true);
		} else {
			this.activity.setPower(false);
		}
	}
	
	/**
	 * Convert a accelerometer value to an integer (0-1000).
	 * 
	 * @param f
	 *            accelerometer value
	 * @return converted value
	 */
	private int convertAccelerometerValues(float f) {
		return (int) Math.abs(f * 100);
	}

}
