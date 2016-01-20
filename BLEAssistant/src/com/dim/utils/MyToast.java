package com.dim.utils;

import android.content.Context;
import android.widget.Toast;

public class MyToast {

	public static void Toast(Context con,String str)
	{
		Toast.makeText(con, str, Toast.LENGTH_SHORT).show();
	}
}
