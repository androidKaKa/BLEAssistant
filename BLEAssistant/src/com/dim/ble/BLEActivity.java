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
    /**����BLE�ն�*/
    private BluetoothAdapter mBluetoothAdapter;
    /**��дBLE�ն�*/
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
            Toast.makeText(this, "��֧��BLE", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "�豸��֧��", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //��������
        mBluetoothAdapter.enable();
        
        mBLE = new BluetoothLeClass(this);
        
	
        if (!mBLE.initialize()) {
            Log.e(TAG, "Unable to initialize Bluetooth");
            finish();
        }
        //����BLE�ն˵�Serviceʱ�ص�
        mBLE.setOnServiceDiscoverListener(mOnServiceDiscover);
        //�յ�BLE�ն����ݽ������¼�
        mBLE.setOnDataAvailableListener(mOnDataAvailable);
        try {
        	scanLeDevice(true);
		} catch (Exception e) {
			// TODO: handle exception
			com.dim.utils.MyToast.Toast(context, "�豸��֧��BLE");
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
     * ������BLE�ն˷�����¼�
     */
    private BluetoothLeClass.OnServiceDiscoverListener mOnServiceDiscover = new OnServiceDiscoverListener(){

		@Override
		public void onServiceDiscover(BluetoothGatt gatt) {
			//displayGattServices(mBLE.getSupportedGattServices());
		}
    	
    };
    
    /**
     * �յ�BLE�ն����ݽ������¼�
     */
    private BluetoothLeClass.OnDataAvailableListener mOnDataAvailable = new OnDataAvailableListener(){

    	/**
    	 * BLE�ն����ݱ������¼�
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
	     * �յ�BLE�ն�д�����ݻص�
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
        	 
            //-----Characteristics���ֶ���Ϣ-----//
            List<BluetoothGattCharacteristic> gattCharacteristics =gattService.getCharacteristics();
            for (final BluetoothGattCharacteristic  gattCharacteristic: gattCharacteristics) {
         
        		//UUID_KEY_DATA�ǿ��Ը�����ģ�鴮��ͨ�ŵ�Characteristic
        		if(gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA)){        			
        			//���Զ�ȡ��ǰCharacteristic���ݣ��ᴥ��mOnDataAvailable.onCharacteristicRead()
        			mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        	mBLE.readCharacteristic(gattCharacteristic);
                        }
                    }, 500);
        			
        			//����Characteristic��д��֪ͨ,�յ�����ģ������ݺ�ᴥ��mOnDataAvailable.onCharacteristicWrite()
        			mBLE.setCharacteristicNotification(gattCharacteristic, true);
        			//������������
        			
        			byte[] send = hex2bytes(command);
        			gattCharacteristic.setValue(send);
        			//������ģ��д������
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
	 * ��16����ת�����ֽ���
	 * 
	 * @param hex
	 *            16��������
	 * @return 16����������ֽ���
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
