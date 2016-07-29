package ys.oa.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.refactech.driibo.AppData;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import net.tsz.afinal.FinalBitmap;
import ys.nlga.activity.R;
import ys.oa.adapter.RidersInfoAdapter;
import ys.oa.provider.EnforcementDataManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.EditText;
import android.widget.ListView;
import cn.edu.zafu.coreprogress.helper.ProgressHelper;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;

/**
 * @author wufan
 * @category 全局工具类
 * 
 */
@SuppressLint("SimpleDateFormat")
public class Util {

	/**
	 * 获取版本码
	 * 
	 * @return
	 */
	public static String getVersionCode(Context context) {
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packInfo.versionCode + "";
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	/**
	 * 获取版本名称
	 * 
	 * @return
	 */
	public static String getVersionName(Context context) {
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packInfo.versionName + "";
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	// Copy the apk file from Assets file to other path
	public static boolean copyApkFromAssets(Context context, String fileName, String path) {
		boolean copyIsFinish = false;
		try {
			InputStream is = context.getAssets().open(fileName);
			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();
			copyIsFinish = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return copyIsFinish;
	}

	public static void setAllFocusable(View[] views, boolean focusable) {
		for (View view : views) {
			view.setClickable(focusable);
			view.setFocusable(focusable);
			view.setFocusableInTouchMode(focusable);
		}
	}

	// 由后缀名转为文件类型
	public static String getTypeByEnd(String fileName) {
		if(TextUtils.isEmpty(fileName)){
			Log.i("fcr", "unknow null");
			return "/unknow";
		}
		if (fileName.indexOf(".pdf") != -1) {
			return "application/pdf";
		} else if (fileName.indexOf(".doc") != -1 || fileName.indexOf(".docx") != -1) {
			return "application/msword";
		} else if (fileName.indexOf(".xls") != -1 || fileName.indexOf(".xlsx") != -1) {
			// return "application/vnd.ms-excel";
			return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		} else if (fileName.indexOf(".ppt") != -1 || fileName.indexOf(".pptx") != -1) {
			return "application/vnd.ms-powerpoint";
		} else if (fileName.indexOf(".png") != -1) {
			return "image/png";
		} else if (fileName.indexOf(".jpg") != -1 || fileName.indexOf(".jpeg") != -1) {
			return "image/jpeg";
		} else if (fileName.indexOf(".txt") != -1) {
			return "text/plain";
		} else
			return "/unknow";
	}

	/**
	 * 保存imageview图片至储存
	 * 
	 * @return
	 */
	public static String saveImageView(View view, String imgName) {
		// AT_MOST：我们可以指定一个上限，要保存的图片的大小不会超过它。
		// EXACTLY：我们指定了一个明确的大小，要求图片保存时满足这个条件。
		// UNSPECIFIED：图片多大，保存多大。
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		String url = null;
		try {
			File destDir = new File(getSDPath());
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			url = getSDPath() + "/" + imgName + ".jpg";
			File imageFile = new File(url);
			imageFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(imageFile);
			bitmap.compress(CompressFormat.JPEG, 50, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return url;
	}

	public static String getSDPath() {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().toString() + Constants.imageCachePath;
		} else
			return "/data/data/com.refactech.driibo" + Constants.imageCachePath;
	}

	public static boolean hasSDCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 初始化异常模块
	 * 
	 * @return
	 */
	public static void initExce(Activity activity) {
		// AppData application = (AppData) activity.getApplication();
		// application.initExce();
		// application.addActivity(activity);
	}

	/**
	 * 清空输入框
	 * 
	 * @param et
	 * @param leftdrawable
	 * @param event
	 * @return
	 */
	public static void ClearEdit(EditText et, Drawable leftdrawable, MotionEvent event) {
		et.setText("");
		et.setCompoundDrawablesWithIntrinsicBounds(leftdrawable, null, null, null);
		int cacheInputType = et.getInputType(); // 备份输入类型
		et.setInputType(InputType.TYPE_NULL); // 隐藏软键盘
		et.onTouchEvent(event);
		et.setInputType(cacheInputType); // 恢复输入类型
	}

	/**
	 * 判断用户名格式是否是a-z、A-Z、0-9
	 * 
	 * @param String
	 * @return
	 */
	public static boolean isValidUsername(String username) {
		String str = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$";
		Pattern pattern = Pattern.compile(str);
		Matcher matcher = pattern.matcher(username);
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断用户名长度
	 * 
	 * @param String
	 * @return
	 */
	public static int getAccountSize(String accountName) {
		int size = 0;
		Matcher matcher = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(accountName);
		while (matcher.find()) {
			size++;
		}
		size = size * 3 + accountName.length() - size;
		return size;
	}

	// 获取hololight风格的对话框Builder
	public static AlertDialog.Builder getHoloDialogBuilder(Context context) {
		return new AlertDialog.Builder(new ContextThemeWrapper(context,
				Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.style.Theme_Holo_Light_Dialog
						: android.R.style.Theme_Dialog));
	}

	// 获取hololight风格的ProgressDialog
	public static ProgressDialog getHoloProgressDialog(Context context) {
		return new ProgressDialog(new ContextThemeWrapper(context,
				Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.style.Theme_Holo_Light_Dialog
						: android.R.style.Theme_Dialog));
	}

	// 网络是否可用
	public static boolean isNetworkAvailable(Context context) {
		boolean isNetConnected;
		// 获得网络连接服务
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			// String name = info.getTypeName();
			// L.i("当前网络名称：" + name);
			isNetConnected = true;
		} else {
			L.i("没有可用网络");
			isNetConnected = false;
		}
		return isNetConnected;
	}

	// 返回小于规定长度的字符串，超出部分用…代替
	public static String formatText(String text, int length) {
		if (text.length() > length) {
			text = text.substring(0, length - 1);
			text += "...";
		}
		return text;
	}

	public static String getImgUrl(String docId, String format) {
		return "http://" + Constants.CONNIP + Constants.IMGPATH + docId + "/x." + format;
	}

	// 加载图片
	public static void displayImg(Context context, View imageView, String imgDocId, String imgFormat) {
		FinalBitmap mFb = FinalBitmap.create(context);// 初始化
		// 定义线程数,设置缓存大小(10MB),设置加载过程中显示的图片,加载失败
		mFb.configBitmapLoadThreadSize(3).configDiskCacheSize(1024 * 1024 * 10).configLoadingImage(R.drawable.bg)
				.configLoadfailImage(R.drawable.bg);
		File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.imageCachePath);
		if (!fileDir.exists())
			fileDir.mkdirs();

		String url = getImgUrl(imgDocId, imgFormat);
		File file = new File(fileDir.getAbsolutePath() + "/" + imgDocId + "." + imgFormat);
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		mFb.configDiskCachePath(file.getAbsolutePath());// 设置缓存目录;
		mFb.display(imageView, url);
	}

	// add by fcr for local image
	public static void displayLocalImg(Context context, View imageView, String imgDocPath, String imgFormat) {
		FinalBitmap mFb = FinalBitmap.create(context);// 初始化
		// 定义线程数,设置缓存大小(10MB),设置加载过程中显示的图片,加载失败
		mFb.configBitmapLoadThreadSize(3).configDiskCacheSize(1024 * 1024 * 10).configLoadingImage(R.drawable.bg)
				.configLoadfailImage(R.drawable.bg);
		File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.imageCachePath);
		if (!fileDir.exists())
			fileDir.mkdirs();

//		String url = getImgUrl(imgDocId, imgFormat);
//		File file = new File(fileDir.getAbsolutePath() + "/" + imgDocId + "." + imgFormat);
//		if (!file.exists())
//			try {
//				file.createNewFile();
//			} catch (IOException e) {
//			}
		mFb.configDiskCachePath(fileDir.getAbsolutePath());// 设置缓存目录;
		mFb.display(imageView, imgDocPath);
	}
	
	public static void displayLocalImg(Context context, View imageView, String imgDocPath, String imgFormat, String type) {
		FinalBitmap mFb = FinalBitmap.create(context);// 初始化
		// 定义线程数,设置缓存大小(10MB),设置加载过程中显示的图片,加载失败
		if(type.equals("headImage")){
			mFb.configBitmapLoadThreadSize(3).configDiskCacheSize(1024 * 1024 * 10).configLoadingImage(R.drawable.bg)
			.configLoadfailImage(R.drawable.people_head_bg);
		}else{
			mFb.configBitmapLoadThreadSize(3).configDiskCacheSize(1024 * 1024 * 10).configLoadingImage(R.drawable.bg)
			.configLoadfailImage(R.drawable.bg);
		}
		
		File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.imageCachePath);
		if (!fileDir.exists())
			fileDir.mkdirs();
		
//		String url = getImgUrl(imgDocId, imgFormat);
//		File file = new File(fileDir.getAbsolutePath() + "/" + imgDocId + "." + imgFormat);
//		if (!file.exists())
//			try {
//				file.createNewFile();
//			} catch (IOException e) {
//			}
		mFb.configDiskCachePath(fileDir.getAbsolutePath());// 设置缓存目录;
		mFb.display(imageView, imgDocPath);
	}
	// 返回当前时间字符串
	public static String getDateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 0);
		long date = cal.getTimeInMillis();
		return sdf.format(new Date(date));
	}

	// 按格式返回当前时间
	public static String getNowTime(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 0);
		long date = cal.getTimeInMillis();
		return sdf.format(new Date(date));
	}

	// 判断SD卡是否存在,判断项目文件是否存在，不存在创建
	public static boolean hasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(Constants.localPath);
			if (file.exists()) {
				return true;
			} else {
				file.mkdirs();
				if (file.exists())
					return true;
				else
					return false;
			}
		} else {
			return false;
		}
	}
	
	//MD5加密
	public static String md5(String string) {
	    byte[] hash;
	    try {
	        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Huh, MD5 should be supported?", e);
	    } catch (UnsupportedEncodingException e) {
	        throw new RuntimeException("Huh, UTF-8 should be supported?", e);
	    }

	    StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (byte b : hash) {
	        if ((b & 0xFF) < 0x10) hex.append("0");
	        hex.append(Integer.toHexString(b & 0xFF));
	    }
	    return hex.toString();
	}
	
	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	

	
	/**
	 * 根据路径获得突破并压缩返回bitmap用于显示
	 * 
	 * @param imagesrc
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 480, 800);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}
	
	/**
	 * 保存压缩后的图片至本地
	 * 
	 * @param intent
	 * @param initial bitmap
	 * @return
	 */
	public static String savaPhotoToLocal(Intent data,Bitmap btp){
		 //如果文件夹不存在则创建文件夹，并将bitmap图像文件保存
		 File rootdir =Environment.getExternalStorageDirectory();
		 //String filename = System.currentTimeMillis() + ".jpg";
		 String filename = getDateTime() + ".jpg";
		 File tempFile = new File(Constants.localPath, filename);
		 String filePath =tempFile.getAbsolutePath();
		 try {
		 //将bitmap转为jpg文件保存
		FileOutputStream fileOut = new FileOutputStream(tempFile);
		//图片压缩20%
		btp.compress(CompressFormat.JPEG, 60, fileOut);
		 } catch(FileNotFoundException e) {
		e.printStackTrace();
		 }
		 return filePath;
		}
	
	/**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
        return degree;
    }
    
	
	/*
	    * 旋转图片 
	    * @param angle 
	    * @param bitmap 
	    * @return Bitmap 
	    */ 
	   public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
	       //旋转图片 动作   
	       Matrix matrix = new Matrix();;  
	       matrix.postRotate(angle);  
	       System.out.println("angle2=" + angle);  
	       // 创建新的图片   
	       Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
	               bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
	       return resizedBitmap;  
	   }
	   
	   /**
	    * 身份证正面拍照识别内容 车牌识别
	    * @param mPhotoPath 正面图片拍照路径
	    * @param mHandler 更新界面Handler
	    * @param mUpdateMsg Handler返回message.what
	    */
	   public static void uploadAndRecognize(final String mPhotoPath, final int position,final Handler mHandler, final int mUpdateMsg) {
		   if (!TextUtils.isEmpty(mPhotoPath)){
	            File file=new File(mPhotoPath);
	            //构造上传请求，类似web表单
	            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
	                    .addPart(Headers.of("Content-Disposition", "form-data; name=\"callbackurl\""), RequestBody.create(null, "/idcard/"))
	                    .addPart(Headers.of("Content-Disposition", "form-data; name=\"action\""), RequestBody.create(null, "idcard"))
	                    .addPart(Headers.of("Content-Disposition", "form-data; name=\"img\"; filename=\"idcardFront_user.jpg\""), RequestBody.create(MediaType.parse("image/jpeg"), file))
	                    .build();
	            //这个是ui线程回调，可直接操作UI
	            final UIProgressListener uiProgressRequestListener = new UIProgressListener() {
	                @Override
	                public void onUIProgress(long bytesWrite, long contentLength, boolean done) {
	                    Log.e("TAG", "bytesWrite:" + bytesWrite);
	                    Log.e("TAG", "contentLength" + contentLength);
	                    Log.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
	                    Log.e("TAG", "done:" + done);
	                    Log.e("TAG", "================================");
	                    //ui层回调
	                    //mProgressBar.setProgress((int) ((100 * bytesWrite) / contentLength));
	                }
	            };
	            //进行包装，使其支持进度回调
	            final Request request = new Request.Builder()
	                    .header("Host", "ocr.ccyunmai.com")
	                    .header("Origin", "http://ocr.ccyunmai.com")
	                    .header("Referer", "http://ocr.ccyunmai.com/idcard/")
	                    .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2398.0 Safari/537.36")
	                    .url("http://ocr.ccyunmai.com/UploadImg.action")
	                    .post(ProgressHelper.addProgressRequestListener(requestBody, uiProgressRequestListener))
	                    .build();
	            
	            OkHttpClient mOkHttpClient=new OkHttpClient();
	            //开始请求
	            mOkHttpClient.newCall(request).enqueue(new Callback() {
	                @Override
	                public void onFailure(Request request, IOException e) {
	                    Log.e("TAG", "error");
	                    //modify by fcr at 2016-03-09
	                    Message obtain = Message.obtain();
	                    obtain.what=mUpdateMsg;
	                    //obtain.obj=builder.toString();
	                    obtain.obj = "fail";
	                    mHandler.sendMessage(obtain);
	                    // add end
	                }
	                @Override
	                public void onResponse(Response response) throws IOException {
	                    String result=response.body().string();
	                    Document parse = Jsoup.parse(result);
	                    Elements select = parse.select("div.left fieldset");
	                    Log.e("TAG",select.text());
	                    Document parse1 = Jsoup.parse(select.text());
	                    StringBuilder builder=new StringBuilder();
	                    Map<String, String> msgMap = new HashMap<String, String>();
	                    
	                    if(mUpdateMsg == 4){
	                    	String number = parse1.select("number").text();
	                    	msgMap.put("number", number);
	                    	String color = parse1.select("color").text();
	                    	msgMap.put("color", color);
	                    	String layer = parse1.select("layer").text();
	                    	msgMap.put("layer", layer);
	                    	Log.v("PLATE", number);
	                    	Log.v("PLATE", color);
	                    	Log.v("PLATE", layer);
	                    }else {
		                    String name=parse1.select("name").text();
		                    msgMap.put("name", name);
		                    //mEtName.setText(name);
		                    
		                    String cardno=parse1.select("cardno").text();
		                    msgMap.put("cardno", cardno);
		                    //mEtId.setText(cardno);
		                    
		                    String sex=parse1.select("sex").text();
		                    msgMap.put("sex", sex);
		                    //mEtSex.setText(sex);
		                    
		                    String folk=parse1.select("folk").text();
		                    msgMap.put("nation", folk);
		                    //mEtNation.setText(folk);
		                    
		                    String birthday=parse1.select("birthday").text();
		                    msgMap.put("birthday", birthday);
		                    //mEtBirthday.setText(birthday);
		                    
		                    String address=parse1.select("address").text();
		                    msgMap.put("address", address);
		                    //mEtAddress.setText(address);
		                    //add by fcr
		                    msgMap.put("imgPath", mPhotoPath);
		                    msgMap.put("position", position+"");
		                    String issue_authority=parse1.select("issue_authority").text();
		                    String valid_period=parse1.select("valid_period").text();
		                    builder.append("name:"+name)
		                            .append("\n")
		                            .append("cardno:" + cardno)
		                            .append("\n")
		                            .append("sex:" + sex)
		                            .append("\n")
		                            .append("folk:" + folk)
		                            .append("\n")
		                            .append("birthday:" + birthday)
		                            .append("\n")
		                            .append("address:" + address)
		                            .append("\n")
		                            .append("issue_authority:" + issue_authority)
		                            .append("\n")
		                            .append("valid_period:" + valid_period)
		                            .append("\n");
		                    Log.e("TAG", "name:" + name);
		                    Log.e("TAG","cardno:"+cardno);
		                    Log.e("TAG","sex:"+sex);
		                    Log.e("TAG","folk:"+folk);
		                    Log.e("TAG","birthday:"+birthday);
		                    Log.e("TAG","address:"+address);
		                    Log.e("TAG","issue_authority:"+issue_authority);
		                    Log.e("TAG","valid_period:"+valid_period);
	                    }
	                    Message obtain = Message.obtain();
	                    obtain.what=mUpdateMsg;
	                    //obtain.obj=builder.toString();
	                    obtain.obj = msgMap;
	                    mHandler.sendMessage(obtain);
	                }
	            });
	        }
	    }
	   
	   /**
	     * scrollview与listview合用会出现listview只显示一行多点。此方法是为了定死listview的高度就不会出现以上状况
	     * 算出listview的高度
	     */
	    public static void setListViewHeight(ListView listView) {
	        RidersInfoAdapter listAdapter = (RidersInfoAdapter) listView.getAdapter();
	        if (listAdapter == null) {
	            return;
	        }
	        int totalHeight = 0;
	        for (int i = 0; i < listAdapter.getCount(); i++) {
	            View listItem = listAdapter.getView(i, null, listView);
	            listItem.measure(1, 1);
	            totalHeight += listItem.getMeasuredHeight();
	        }

	        ViewGroup.LayoutParams params = listView.getLayoutParams();
	        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + listView.getPaddingTop() + listView.getPaddingBottom();
	        listView.setLayoutParams(params);
	    }
	    
	    public static boolean cleanCache() { 
	    	EnforcementDataManager m = EnforcementDataManager.getInstance(AppData.getContext());
	    	Log.v("fcr","清理缓存");
			m.cleanAllDate();
			if(Constants.localAPKPath != null) {
				return deleteDirectory(Constants.localAPKPath);
			}
			return false;
	    }
	    
	    public static boolean deleteDirectory(String filePath) {
		    boolean flag = false;
		        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
		        if (!filePath.endsWith(File.separator)) {
		            filePath = filePath + File.separator;
		        }
		        File dirFile = new File(filePath);
		        if (!dirFile.exists() || !dirFile.isDirectory()) {
		            return false;
		        }
		        flag = true;
		        File[] files = dirFile.listFiles();
		        //遍历删除文件夹下的所有文件(包括子目录)
		        for (int i = 0; i < files.length; i++) {
		            if (files[i].isFile()) {
		            //删除子文件
		                flag = deleteFile(files[i].getAbsolutePath());
		                if (!flag) break;
		            } else {
		            //删除子目录
		                flag = deleteDirectory(files[i].getAbsolutePath());
		                if (!flag) break;
		            }
		        }
		        if (!flag) return false;
		        //删除当前空目录
		        return dirFile.delete();
		    }
	 
	 	public static boolean deleteFile(String filePath) {
		    File file = new File(filePath);
		        if (file.isFile() && file.exists()) {
		        	return file.delete();
		        }
		        return false;
		    }
}
