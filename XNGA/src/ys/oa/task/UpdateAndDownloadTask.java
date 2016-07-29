package ys.oa.task;

import java.io.File;
import java.util.ArrayList;

import ys.nlga.activity.R;
import ys.oa.activity.MainActivity;
import ys.oa.util.Constants;
import ys.oa.util.T;
import ys.oa.util.Util;

import com.anim.dialog.MsgSlidingDialog;
import com.anim.dialog.MsgSlidingDialog.OnMsgClickListener;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.update.down.DownDB;
import com.update.down.FileDownUtil;
import com.update.down.MultiThreadDownload;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class UpdateAndDownloadTask extends  AsyncTask<String, Integer, Integer> {
	
	private DataSetList datasetlist;
	private String mApkAddress;
	private String downUrl;
	private String updateDetails = null;
	private File downFile;
	private String mCurrentVersionCode;
	private DownDB mDownDb;
	private MultiThreadDownload mtd;
	private MainActivity mainActivity;
	private MsgSlidingDialog checkUpdateDialog, isUpdateDialog, updatingDialog;
	private int flag;
	
	public UpdateAndDownloadTask(MainActivity mainActivity,int flag) {
		this.mainActivity = mainActivity;
		this.flag = flag;
		checkUpdateDialog = new MsgSlidingDialog(mainActivity, MsgSlidingDialog.PROGRESS_TYPE).setTitleText(
				"正在检查更新…").setTitleTextSize(19);
		checkUpdateDialog.setCancelable(false);
	}
	
	
	@Override
	protected Integer doInBackground(String... params) {
		if ("update".equals(params[0])) {
		try {
			datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from XNGA_APP", "1", "APP_TIME DESC", "",
					"SEARCHYOUNGCONTENT", new DocInfor("", "XNGA_APP"), true, false));
			} catch (Exception e) {
				return -1;
			}
			return 1;
		}
		return 0;
	}

	protected void onPreExecute() {
		if(flag == 1) {
			if (!checkUpdateDialog.isShowing()) {
				checkUpdateDialog.show();
			}
		}
	}

	protected void onPostExecute(Integer result) {
		switch (result) {
		case -1:
			if (Util.isNetworkAvailable(mainActivity)) {
				T.showSnack(mainActivity, R.string.serverFailed);
			} else {
				T.showSnack(mainActivity, R.string.networkerror);
			}
			break;
		case 1:
			if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
				ArrayList<String> nameList = (ArrayList<String>) datasetlist.nameList;
				ArrayList<String> valueList = (ArrayList<String>) datasetlist.valueList;
				ArrayList<String> DOCUMENTID = (ArrayList<String>) datasetlist.DOCUMENTID;
				String mVersionCode = null;
				String mVersionName = null;
				String updateTimes = null;
				mCurrentVersionCode = Util.getVersionCode(mainActivity);
				for (int i = 0; i < nameList.size(); i++) {
					if ("APP_VC".equals(nameList.get(i))) {
						mVersionCode = valueList.get(i);
						mApkAddress = DOCUMENTID.get(0);
					} else if ("APP_VN".equals(nameList.get(i))) {
						mVersionName = valueList.get(i);
					} else if ("APP_DETAIL".equals(nameList.get(i))) {
						updateDetails = valueList.get(i);
					} else if ("APP_TIME".equals(nameList.get(i))) {
						updateTimes = valueList.get(i);
					}
				}
				
				System.out.println("mCurrentVersion "+mCurrentVersionCode);
				System.out.println("mVersionCode "+ mVersionCode);
				//add by fcr for compare versionCode 
				double _mCurrentVersionCode = Double.parseDouble(mCurrentVersionCode);
				double _mVersionCode = Double.parseDouble(mVersionCode);
				if (!TextUtils.isEmpty(mVersionCode) && (_mCurrentVersionCode < _mVersionCode) ) {
					// 如果当前版本号小于服务器返回的版本号就弹出提示对话框
					downUrl = "http://" + Constants.CONNIP + "/IDOC/service/file/" + mApkAddress + "/" + "XNGA_"
							+ mVersionName + ".apk";
					Log.v("XNGA","downloadUrl---"+downUrl);
					showIsUpdateDialog();// 显示更新对话框
				} else {
					if(flag==1) {
						checkUpdateDialog.setCancelable(true);
						checkUpdateDialog.setTitleText("已更新到最新版本!").setTitleTextSize(19)
						.changeAlertType(MsgSlidingDialog.SUCCESS_TYPE);
					}
				}
			}
			break;
		}
		if(checkUpdateDialog != null) {
			if (checkUpdateDialog.isShowing() && checkUpdateDialog.getAlerType() != MsgSlidingDialog.SUCCESS_TYPE) {
				checkUpdateDialog.cancel();
			}
		}
	}
	
	
	public  void showIsUpdateDialog() {
		mDownDb = new DownDB(mainActivity);
		isUpdateDialog = new MsgSlidingDialog(mainActivity, MsgSlidingDialog.NORMAL_TYPE);
		isUpdateDialog.setTitleText("★有新版本发布★").setTitleTextSize(23).setContentText(updateDetails.toString())
				.setConfirmText("立即更新").setCancelText("稍后更新").setConfirmClickListener(new OnMsgClickListener() {

					@Override
					public void onClick(MsgSlidingDialog msgSlidingDialog) {
						System.out.println("url---->" + downUrl);
						// 新建应用文件夹
						File fileDir = new File(Constants.localAPKPath);
						if (!fileDir.exists())
							fileDir.mkdirs();
						downFile = new File(Constants.localAPKPath + FileDownUtil.getFileName(downUrl));
						if (downFile.exists() && FileDownUtil.isFileUsed(mainActivity, downFile)) {
							mDownDb.delete(downUrl);
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.fromFile(downFile), "application/vnd.android.package-archive");
							mainActivity.startActivity(intent);
						} else {
							mtd = new MultiThreadDownload(mainActivity, handler, downUrl, Constants.localAPKPath, 1);
							mtd.start();
						}
					}
				});
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if (!isUpdateDialog.isShowing()) {
					isUpdateDialog.show();
				}
			}
		}, 50);
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FileDownUtil.startDownloadMeg:
				showUpdatingDialog();
				mtd.stoped = false;
				break;
			case FileDownUtil.updateDownloadMeg:
				updatingDialog.setTitleText("正在下载 " + (((int) mtd.getDownloadSize() * 100) / ((int) mtd.getFileSize()))
						+ "%");
				break;
			case FileDownUtil.stopDownloadMeg:
				// T.showShort(mainActivity, "文件已停止下载");
				updatingDialog.cancel();
				mDownDb = new DownDB(mainActivity);
				mDownDb.delete(downUrl);
				break;
			case FileDownUtil.endDownloadMeg:
				updatingDialog.setTitleText("下载完成,是否安装?").setTitleTextSize(19).setConfirmText("安装")
						.setConfirmClickListener(new OnMsgClickListener() {

							@Override
							public void onClick(MsgSlidingDialog msgSlidingDialog) {
								if (FileDownUtil.getFileType(downUrl).equals("apk")) {
									/* apk安装界面跳转 */
									String filename = FileDownUtil.getFileName(downUrl);
									String fileAbsoluteName = Constants.localAPKPath + filename;
									Intent intent = new Intent(Intent.ACTION_VIEW);
									intent.setDataAndType(Uri.fromFile(new File(fileAbsoluteName)),
											"application/vnd.android.package-archive");
									mainActivity.startActivity(intent);
								}
								msgSlidingDialog.cancel();
							}
						}).setCancelText("取消").setCancelClickListener(new OnMsgClickListener() {

							@Override
							public void onClick(MsgSlidingDialog msgSlidingDialog) {
								msgSlidingDialog.cancel();
							}
						}).changeAlertType(MsgSlidingDialog.SUCCESS_TYPE);
				break;
			case FileDownUtil.downloadTimeOut:
				T.showShort(mainActivity, "文件有误，请重新下载");
				updatingDialog.cancel();
				mDownDb = new DownDB(mainActivity);
				mDownDb.delete(downUrl);
				break;
			case FileDownUtil.networkNotAvailable:
				T.showShort(mainActivity, R.string.networkerror);
				updatingDialog.cancel();
				mDownDb = new DownDB(mainActivity);
				mDownDb.delete(downUrl);
				break;
			default:
				break;
			}
		}
	};
	
	private void showUpdatingDialog() {
		updatingDialog = new MsgSlidingDialog(mainActivity, MsgSlidingDialog.PROGRESS_TYPE).setTitleText("正在检查更新…")
				.setTitleTextSize(19).setConfirmText("取消").setConfirmClickListener(new OnMsgClickListener() {

					@Override
					public void onClick(MsgSlidingDialog msgSlidingDialog) {
						mtd.stoped = true;
						msgSlidingDialog.cancel();
					}
				});
		updatingDialog.setCancelable(false);
		isUpdateDialog.cancel();
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if (!updatingDialog.isShowing()) {
					updatingDialog.show();
				}
			}
		}, 50);
	}


}
