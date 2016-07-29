package ys.oa.util;

import java.io.FileOutputStream;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

public final class MyTool
{
	public static final String SDCARD_DIRECTORY = Environment.getExternalStorageDirectory().toString()+"/";
	public static final String TEST_FILE_NAME1 = "request.xml";
	public static final String TEST_FILE_NAME2 = "request_result.xml";
	public static final String TEST_FILE_NAME3 = "request_result_DataSetList.txt";
	
	private MyTool()
	{
		
	}
	
	public static void writeTestStrToFile(String str, String path)
	{
		FileOutputStream fos;
		try
		{
			fos = new FileOutputStream(path, false);
			fos.write(str.getBytes());
			fos.flush();
			fos.close();
			
			System.out.println("xml请求的测试代码已写入到[" + path + "]路径下");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*public static int getCurrentAppVersionCode(Context context)
	{
		int currentVersionCode = -1;
		
		try
		{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo  packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			
			currentVersionCode = packageInfo.versionCode;
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return currentVersionCode;
	}
	
	public static int getMaxValueForArray(int[] array)
	{
		int max = array[0];
		
		for (int i = 0; i < array.length; i++)
		{
			if(max<array[i]) max = array[i];
		}
		
		return max;
	}*/
}
