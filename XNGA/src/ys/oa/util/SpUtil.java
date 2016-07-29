package ys.oa.util;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SpUtil {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SpUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	// 在menuList中序号为index的菜单项是否在警用主页显示
	public void setMenuInMain(int index) {
		editor.putBoolean("menuListId" + index, true);
		editor.commit();
	}

	public void setMenuOutMain(int index) {
		editor.putBoolean("menuListId" + index, false);
		editor.commit();
	}

	public boolean isMenuInMain(int index) {
		return sp.getBoolean("menuListId" + index, false);
	}

	// 在menuList中序号为index的菜单项是否在民用主页显示
	public void setMenuInMainCivil(int index) {
		editor.putBoolean("menuCivilListId" + index, true);
		editor.commit();
	}

	public void setMenuOutMainCivil(int index) {
		editor.putBoolean("menuCivilListId" + index, false);
		editor.commit();
	}

	public boolean isMenuInMainCivil(int index) {
		return sp.getBoolean("menuCivilListId" + index, false);
	}

	// account
	public void setAccount(String account) {
		editor.putString("account", account);
		editor.commit();
	}

	public String getAccount() {
		return sp.getString("account", "");
	}

	// password
	public void setPwd(String account) {
		editor.putString("pwd", account);
		editor.commit();
	}

	public String getPwd() {
		return sp.getString("pwd", "");
	}
	
	// authority
	public void setAuth(String account) {
		editor.putString("auth", account);
		editor.commit();
	}
	
	public String getAuth() {
		return sp.getString("auth", "");
	}

	// isAutoLogin
	public void setIsAutoLogin(boolean isAutoLogin) {
		editor.putBoolean("isAutoLogin", isAutoLogin);
		editor.commit();
	}

	public boolean getIsAutoLogin() {
		return sp.getBoolean("isAutoLogin", false);
	}

	// isKeepAccount
	public void setIsKeepAccount(boolean isKeepAccount) {
		editor.putBoolean("isKeepAccount", isKeepAccount);
		editor.commit();
	}

	public boolean getIsKeepAccount() {
		return sp.getBoolean("isKeepAccount", true);
	}

	// accountCivil
	public void setAccountCivil(String accountCivil) {
		editor.putString("accountCivil", accountCivil);
		editor.commit();
	}

	public String getAccountCivil() {
		return sp.getString("accountCivil", "");
	}

	// passwordCivil
	public void setPwdCivil(String pswCivil) {
		editor.putString("pwdCivil", pswCivil);
		editor.commit();
	}

	public String getPwdCivil() {
		return sp.getString("pwdCivil", "");
	}

	// isAutoLoginCivil
	public void setIsAutoLoginCivil(boolean isAutoLoginCivil) {
		editor.putBoolean("isAutoLoginCivil", isAutoLoginCivil);
		editor.commit();
	}

	public boolean getIsAutoLoginCivil() {
		return sp.getBoolean("isAutoLoginCivil", false);
	}

	// isKeepAccountCivil
	public void setIsKeepAccountCivil(boolean isKeepAccountCivil) {
		editor.putBoolean("isKeepAccountCivil", isKeepAccountCivil);
		editor.commit();
	}

	public boolean getIsKeepAccountCivil() {
		return sp.getBoolean("isKeepAccountCivil", true);
	}
	
	public void setGestureLock(List list){
		editor.putString("lock", list.toString());
		editor.commit();
	}
	
	public String getGestureLock() {
		return sp.getString("lock", "");
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }
	
	public boolean putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        return editor.commit();
    }
	
	public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }
	
	public boolean putString(String key, String value) {
        editor.putString(key, value);
        return editor.commit();
    }
	
	public long getLong(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }
	
	public boolean putLong(String key, long value) {
        editor.putLong(key, value);
        return editor.commit();
    }
	
	//add  by fcr at 2016-01-29
	public void putAppVersion(int defaultValue) {
		 editor.putInt("appVersion", defaultValue);
		 editor.commit();
    }
	
	public int getAppVersion() {
		if(!sp.contains("appVersion")) {
			return 0;
		}
		return sp.getInt("appVersion", 0);
    }
	//add end 
	//add by fcr at 2016-02-24
	public void putLastLoginDate(String defaultValue) {
		editor.putString("lastLoginDate",defaultValue);
		editor.commit();
	}
	
	public String getLastLoginDate() {
		if(!sp.contains("lastLoginDate")) {
			return null;
		}
		return sp.getString("lastLoginDate", "");
	}
	
	public void putLastRegTime(String defaultValue) {
		editor.putString("lastRegTime",defaultValue);
		editor.commit();
	}
	
	public String getLastRegTime() {
		if(!sp.contains("lastRegTime")) {
			return "";
		}
		return sp.getString("lastRegTime", "");
	}

}
