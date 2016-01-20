package com.dim.bleassistant; 

import com.dim.ble.BLEActivity;
import com.dim.utils.MyToast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String TAG="BLE MAIN ACTIVITY-->>";
	private EditText editWrite;
	private EditText editTextTime;
	private CheckBox checkBoxWriteHex;
	private CheckBox checkBoxReadHex;
	
	private Button btnWrite;
	private Button btnRead;
	private Button btnAutoSend;
	private Button btnSearchBLE;
	private Button btnDisCon;
	private TextView textViewRead;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context=this;
		InitView();
		OnClick();
	}

	public void InitView()
	{
		editWrite=(EditText)findViewById(R.id.editWrite);
		editTextTime=(EditText)findViewById(R.id.editTextTime);
		checkBoxReadHex=(CheckBox)findViewById(R.id.checkBoxHexRead);
		checkBoxWriteHex=(CheckBox)findViewById(R.id.checkBoxWriteHex);
		btnWrite=(Button)findViewById(R.id.btnWrite);
		btnRead=(Button)findViewById(R.id.btnRead);
		btnAutoSend=(Button)findViewById(R.id.btnAutoSend); 
		btnSearchBLE=(Button)findViewById(R.id.btnSearchBLE);
		btnDisCon=(Button)findViewById(R.id.btnDisContected);
		textViewRead=(TextView)findViewById(R.id.textViewRead);
	}
	
	class Click implements View.OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.btnAutoSend:
				MyToast.Toast(context,"×Ô¶¯·¢ËÍ");
				break;
			case R.id.btnWrite:
				
				break;
			case R.id.btnRead:
				
				break;
			case R.id.btnSearchBLE:
				Intent i=new Intent().setClass(context, BLEActivity.class);
				startActivity(i);
				break;
			case R.id.btnDisContected:
				
				break;
			
			}
		}
		
	}
	
	private void OnClick()
	{
		  btnAutoSend.setOnClickListener(new Click());
		  btnWrite.setOnClickListener(new Click());
		  btnRead.setOnClickListener(new Click());
		  btnSearchBLE.setOnClickListener(new Click());
		  btnDisCon.setOnClickListener(new Click());
		
		checkBoxReadHex.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
			}
		});
		
		checkBoxWriteHex.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
