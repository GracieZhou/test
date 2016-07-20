package scifly.datacache;

import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class DataCacheListener implements ImageLoadingListener {

    @Override
    public void onLoadingStarted(String requestUri, View view) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onLoadingFailed(String requestUri, View view, FailReason failReason) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onLoadingComplete(String requestUri, View view, Object dataObject) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onLoadingCancelled(String requestUri, View view) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onCheckingComplete(String requestUri, View view, Object dataObject) {
        // TODO Auto-generated method stub
    }
}
