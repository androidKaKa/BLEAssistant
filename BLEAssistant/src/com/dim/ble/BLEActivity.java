package com.dim.ble;
 
import java.util.ArrayList;
import java.util.List; 

import com.dim.ble.BluetoothLeClass.OnDataAvailableListener;
import com.dim.ble.BluetoothLeClass.OnServiceDiscoverListener;
import com.dim.bleassistant.R; 
import com.phlin.utils.MyLog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class BLEActivity extends Activity{
	
	private String TAG="BLE-->>";
	private final static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";

	private Context context;
    public static LeDeviceListAdapter mLeDeviceListAdapter;
    /**搜索BLE终端*/
    private BluetoothAdapter mBluetoothAdapter;
    /**读写BLE终端*/
    public static BluetoothLeClass mBLE;
    private boolean mScanning;
    private static Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    
    private Button button_scan;
    private ListView listV_devices;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_list);
		context=this;
		mHandler = new Handler();
		button_scan=(Button)findViewById(R.id.button_scan);
		listV_devices=(ListView)findViewById(R.id.listV_devices);
		
		 // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //开启蓝牙
        mBluetoothAdapter.enable();
        
        mBLE = new BluetoothLeClass(this);
        
	
        if (!mBLE.initialize()) {
            Log.e(TAG, "Unable to initialize Bluetooth");
            finish();
        }
        //发现BLE终端的Service时回调
        mBLE.setOnServiceDiscoverListener(mOnServiceDiscover);
        //收到BLE终端数据交互的事件
        mBLE.setOnDataAvailableListener(mOnDataAvailable);
        try {
        	scanLeDevice(true);
		} catch (Exception e) {
			// TODO: handle exception
			com.dim.utils.MyToast.Toast(context, "设备不支持BLE");
		}
        
        listV_devices.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

		        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
		        if (device == null) return;
		        if (mScanning) {
		            mBluetoothAdapter.stopLeScan(mLeScanCallback);
		            mScanning = false;
		        }
		        
		        mBLE.connect(device.getAddress());
		    
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mLeDeviceListAdapter = new LeDeviceListAdapter(this);
		listV_devices.setAdapter(mLeDeviceListAdapter);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		scanLeDevice(false);
        mLeDeviceListAdapter.clear();
         mBLE.disconnect(); 
	}
	
	/**
     * 搜索到BLE终端服务的事件
     */
    private BluetoothLeClass.OnServiceDiscoverListener mOnServiceDiscover = new OnServiceDiscoverListener(){

		@Override
		public void onServiceDiscover(BluetoothGatt gatt) {
			//displayGattServices(mBLE.getSupportedGattServices());
		}
    	
    };
    
    /**
     * 收到BLE终端数据交互的事件
     */
    private BluetoothLeClass.OnDataAvailableListener mOnDataAvailable = new OnDataAvailableListener(){

    	/**
    	 * BLE终端数据被读的事件
    	 */ 
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			
			 
			if (status == BluetoothGatt.GATT_SUCCESS) 
				Log.e(TAG,"onCharRead "+gatt.getDevice().getName()
						+" read "
						+characteristic.getUuid().toString()
						+" -> "
						+Utils.bytesToHexString(characteristic.getValue()));
		}
		
	    /**
	     * 收到BLE终端写入数据回调
	     */ 
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			Log.e(TAG,"onCharWrite "+gatt.getDevice().getName()
					+" write "
					+characteristic.getUuid().toString()
					+" -> "
					+new String(characteristic.getValue()));
		}
    };
   
    @SuppressLint("NewApi")
  	private void scanLeDevice(final boolean enable) {
          if (enable) {
              // Stops scanning after a pre-defined scan period.
              mHandler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      mScanning = false;
                      mBluetoothAdapter.stopLeScan(mLeScanCallback); 
                  }
              }, SCAN_PERIOD);

              mScanning = true;
             
              mBluetoothAdapter.startLeScan(mLeScanCallback);
               
          } else {
              mScanning = false;
              mBluetoothAdapter.stopLeScan(mLeScanCallback);
          } 
          
         
      }
    
    // Device scan callback.
    @SuppressLint("NewApi")
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() { 
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged(); 
                    
                    
                }
            });
        }
    };
    
    @SuppressLint("NewApi")
	public static void displayGattServices(List<BluetoothGattService> gattServices,String command) {
        if (gattServices == null) return;

        for (BluetoothGattService gattService : gattServices) { 
        	 
            //-----Characteristics的字段信息-----//
            List<BluetoothGattCharacteristic> gattCharacteristics =gattService.getCharacteristics();
            for (final BluetoothGattCharacteristic  gattCharacteristic: gattCharacteristics) {
         
        		//UUID_KEY_DATA是可以跟蓝牙模块串口通信的Characteristic
        		if(gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA)){        			
        			//测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
        			mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        	mBLE.readCharacteristic(gattCharacteristic);
                        }
                    }, 500);
        			
        			//接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
        			mBLE.setCharacteristicNotification(gattCharacteristic, true);
        			//设置数据内容
        			
        			byte[] send = hex2bytes(command);
        			gattCharacteristic.setValue(send);
        			//往蓝牙模块写入数据
        			mBLE.writeCharacteristic(gattCharacteristic);
        		} 
            }
        }
        }//
    
    public  byte [] sendMsg(String msg)
    {
    	byte[] send = hex2bytes(msg);
    	return send;
    }
    
	/**
	 * 把16禁制转换成字节流
	 * 
	 * @param hex
	 *            16禁制命令
	 * @return 16禁制命令的字节流
	 */
	public static byte[] hex2bytes(String hex)

	{

		if (hex.length() % 2 != 0) {
			// throw new IllegalArgumentException("error");
			return null;
		}
		char[] chars = hex.toCharArray();
		byte[] b = new byte[chars.length / 2];
		for (int i = 0; i < b.length; i++) {
			int high = Character.digit(chars[2 * i], 16) << 4;
			int low = Character.digit(chars[2 * i + 1], 16);
			b[i] = (byte) (high | low);
		}
		return b;
	}
}
