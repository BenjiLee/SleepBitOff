package com.angel.sleepbitoff;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends Activity {

	TextView textview1;
	TimePicker timepicker1;
	ImageButton changetime;
	Boolean isset;
	private CountDownTimer timer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		textview1 = (TextView) findViewById(R.id.textView2);
		timepicker1 = (TimePicker) findViewById(R.id.timePicker1);

		isset = false;
	}
	
	 // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.settings, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	  switch (item.getItemId()) {
    	    case R.id.Settings:
    	    	Intent intent = new Intent(this, WalletInfo.class);
    	        startActivity(intent);
//    	    case R.id.About:
//    	    	Intent intent = new Intent(this, About.class);
    	    default:
    	      return super.onOptionsItemSelected(item);
    	  }
    	}
    

	public void setBtnClicked(View v) {
		if (isset) {
			disableAlarm();
			stopTimer();
			(textview1).setTextColor(0xFFFFFFFF);
			isset = false;
		} else {
			setAlarm(getSeconds());
			isset = true;
		}
	}

	public int getSeconds() {
		Calendar now = Calendar.getInstance();
		int hour = now.get(Calendar.HOUR);

		int minute = now.get(Calendar.MINUTE);
		int seconds = hour * 3600 + minute * 60 + now.get(Calendar.SECOND);
		Log.w("myApp", "Calendar:" + seconds);

		int picker = timepicker1.getCurrentHour() * 3600
				+ timepicker1.getCurrentMinute() * 60;
		if (picker < seconds) {
			picker = picker + 86400;
		}
		int result = picker - seconds;
		return result;
	}

	public String formatTime(long l) {
		int hunds = (int) l;
		int hunds_tens = hunds / 10;
		int seconds = hunds_tens / 10;
		int seconds_tens = seconds / 10;
		int minutes = seconds_tens / 6;
		int minutes_tens = minutes / 10;
		int hours = minutes_tens / 6;
		int hours_tens = hours / 10;

		String time = hours_tens + "" + hours % 10 + ":" + minutes_tens % 6
				+ minutes % 10 + ":" + seconds_tens % 6 + seconds % 10 + "."
				+ hunds_tens % 10 + hunds % 10;

		return time;
	}

	public void setAlarm(int seconds) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, seconds);

		Intent intent = new Intent(this, Alarm.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 12345,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

		timer = new CountDownTimer(seconds * 1000, 1) {

			public void onTick(long millisUntilFinished) {

				textview1.setText("" + formatTime(millisUntilFinished / 10));
				(textview1).setTextColor(0xFFFF0000);
			}

			public void onFinish() {
			}
		}.start();

	}

	public void stopTimer() {
		timer.cancel();
	}

	public void disableAlarm() {
		AlarmManager am = (AlarmManager) getSystemService(getApplicationContext().ALARM_SERVICE);
		Intent I = new Intent(getApplicationContext(), MainActivity.class);
		PendingIntent P = PendingIntent.getBroadcast(getApplicationContext(),
				0, I, 0);
		am.cancel(P);
		P.cancel();
		textview1.setText("disengaged");
	}
}