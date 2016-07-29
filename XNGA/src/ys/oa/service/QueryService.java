package ys.oa.service;

import java.util.Timer;
import java.util.TimerTask;

import ys.nlga.activity.R;
import ys.oa.activity.CollectPeopleListActivity;
import ys.oa.task.QueryTask;
import ys.oa.task.QueryResultListener;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class QueryService extends Service {
	
		private final String TAG = "QueryService";
		private static Context context;
		private static Activity mActivity;
		private static QueryService queryService;
		private static QueryResultListener queryResultListener;

		public static void setQueryResultListener(
				QueryResultListener queryResultListener) {
			QueryService.queryResultListener = queryResultListener;
		}

		public static QueryService getInstance(Context context,Activity mActivity,QueryResultListener queryResultListener){
			QueryService.context = context;
			QueryService.mActivity = mActivity;
			if(queryService == null){
				queryService = new QueryService();
			}
			setQueryResultListener(queryResultListener);
			return queryService;
		}

		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onCreate() {
			Log.i(TAG, "onCreate");
			super.onCreate();
			context = this;
		}

		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			Log.i(TAG, "onStartCommand");
			return super.onStartCommand(intent, flags, startId);
		}

		public void destoryService(Timer timer) {
			if(timer != null){
				timer.cancel();
				timer = null;
			}
			this.stopSelf();
			queryService = null;
		}

		/**
		 * 启动计时器
		 */
		public void startTimer(int startType,String contentId){
			long time ;
			Timer timer = new Timer();
			time = 20*1000;
			timer.schedule(new QueryTask(mActivity,startType,contentId,QueryService.queryResultListener,timer),time);
		}

		/**
		 * 取消计时器
		 */
		public void cancelTimer(Timer timer){
			if(timer != null){
				timer.cancel();
				timer = null;
			}
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			endAlertTimer();
		}
		
		
		
		private Timer _timer = null;
		public void startAlertTimer() {
			_timer = new Timer();
			_timer.schedule(new AlertTask(),50*60*1000,60*60*1000);
//			_timer.schedule(new AlertTask(),5*1000,5*1000);
		}
		
		public void endAlertTimer() {
			if(_timer != null) {
				_timer.cancel();
				_timer = null;
			}
		}
		
		public class AlertTask extends TimerTask {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				creatNotify();
			}
		}
		
			/**
			 * 通知栏信息
			 */
		protected void creatNotify(){
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
			mNotificationManager.cancelAll();
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
			mBuilder.setContentTitle("通知")//设置通知栏标题
					.setContentText("您该打卡了")
					.setTicker("打卡提醒！") //通知首次出现在通知栏，带上升动画效果的
					.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
					.setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
					.setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
					.setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组
					// Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
					.setSmallIcon(R.drawable.ic_drawer);//设置通知小ICON

			Intent intent = new Intent(context,CollectPeopleListActivity.class);  
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(pendingIntent);
			Notification notification = mBuilder.build();
			mNotificationManager.notify(0, notification);
		}
		
		
}
