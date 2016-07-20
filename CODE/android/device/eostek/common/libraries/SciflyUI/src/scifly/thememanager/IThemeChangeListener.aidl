package scifly.thememanager;

interface IThemeChangeListener{
	void onStatus(String status);
	
	void onSucceed();
	
	void onFailed(String msg);
}