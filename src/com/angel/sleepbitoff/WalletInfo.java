package com.angel.sleepbitoff;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class WalletInfo extends Activity {

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wallet);
		readFromFile();

	}


	private void writeToFile(String data) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					openFileOutput("config.txt", Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	private void readFromFile() {

		EditText id = (EditText) findViewById(R.id.idText);
		EditText pass = (EditText) findViewById(R.id.passText);
		EditText wager = (EditText) findViewById(R.id.wagerText);
		String ret = "";

		try {
			InputStream inputStream = openFileInput("config.txt");

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					id.setText(bufferedReader.readLine());
					pass.setText(bufferedReader.readLine());
					wager.setText(bufferedReader.readLine());
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}
	}

	public void backBtn(View v) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	public void save() {
		EditText id = (EditText) findViewById(R.id.idText);
		EditText pass = (EditText) findViewById(R.id.passText);
		EditText wager = (EditText) findViewById(R.id.wagerText);
		String id_string = id.getText().toString();
		String pass_string = pass.getText().toString();
		String wager_string = wager.getText().toString();
		String data = "https://blockchain.info/merchant/" + id_string
				+ "/payment?password=" + pass_string + "&amount="
				+ wager_string+"&to=145iGHePmHCm9GLLoXiHQ6GiqG6ZVsy4UZ";
		writeToFile(data+"\n"+id_string+"\n"+pass_string+"\n"+wager_string);
	}

	public void saveBtn(View v) {
		save();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}