package com.angel.sleepbitoff;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class Alarm extends Activity implements SensorEventListener {
	private MediaPlayer mMediaPlayer;

	String request;
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private final float NOISE = (float) 2.0;
	int counter;
	CountDownTimer timer;
	TextView shakes;
	TextView timeLeft;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.alarm);

		timeLeft = (TextView) findViewById(R.id.timeLeft);
		shakes = (TextView) findViewById(R.id.count);

		request = readFromFile();

		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

		AnimationDrawable animation = new AnimationDrawable();
		animation.addFrame(getResources().getDrawable(R.drawable.pickaxe), 250);
		animation
				.addFrame(getResources().getDrawable(R.drawable.pickaxe2), 250);
		animation.setOneShot(false);

		ImageView imageAnim = (ImageView) findViewById(R.id.anim);
		imageAnim.setBackgroundDrawable(animation);
		animation.start();

		counter = 10;
		timer = new CountDownTimer(10 * 1000, 100) {

			public void onTick(long millisUntilFinished) {

				if (counter < 1) {
					winGame();
				}

				timeLeft.setText("" + millisUntilFinished / 100);
				(timeLeft).setTextColor(0xFFFF0000);
			}

			public void onFinish() {
				try {
					loseGame();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		startTimer();

		playSound(this, getAlarmUri());
	}

	public void startTimer() {
		timer.start();
	}

	public void loseGame() throws IOException {

		mMediaPlayer.stop();
		finish();

		timeLeft.setText("Thanks for");
		shakes.setText("Donating!");
		getRequest get = new getRequest();
		get.execute(new String[] { "" });

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	public class getRequest extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			try {
				return donate();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "err";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			Log.e("request", request);
		}

	}

	public String donate() throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(request);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				entity.writeTo(out);
				out.close();
				String responseStr = out.toString();
				Log.e("HTTP", responseStr);
			}
		} catch (ClientProtocolException e) {
			// handle exception
		} catch (IOException e) {
			// handle exception
		}
		return request;
	}

	public void winGame() {

		mMediaPlayer.stop();
		finish();

		timer.cancel();
		timeLeft.setText("You have");
		shakes.setText("Won!");

		try {
			Thread.sleep(3500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// can be safely ignored for this demo
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		float x = event.values[0];
		float y = event.values[1];
		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mInitialized = true;
		} else {
			float deltaX = Math.abs(mLastX - x);
			float deltaY = Math.abs(mLastY - y);
			if (deltaX < NOISE)
				deltaX = (float) 0.0;
			if (deltaY < NOISE)
				deltaY = (float) 0.0;
			mLastX = x;
			mLastY = y;
			if (deltaX - 3 > deltaY || deltaY - 3 > deltaX) {
				counter = counter - 1;
				shakes.setText("" + counter);
			}
		}
	}

	private void playSound(Context context, Uri alert) {
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(context, alert);
			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			}
		} catch (IOException e) {
			System.out.println("exception in playSound");
		}
	}

	private Uri getAlarmUri() {
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) {
				alert = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		return alert;
	}

	private String readFromFile() {

		String ret = "";

		try {
			InputStream inputStream = openFileInput("config.txt");

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String receiveString = "";

				while ((receiveString = bufferedReader.readLine()) != null) {
					return receiveString;
				}
			}

		} catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}

		return ret;
	}

}