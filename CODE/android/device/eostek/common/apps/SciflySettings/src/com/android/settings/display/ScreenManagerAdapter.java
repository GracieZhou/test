
package com.android.settings.display;

import android.content.Context;
import android.os.Build;

/**
 * ScreenManagerAdapter.
 * 
 * @author Davis
 * @date 2015-9-9
 */
public class ScreenManagerAdapter {

    Class<?> mClass;

    Object mObject;

    public ScreenManagerAdapter(Context context) {
        if (Build.DEVICE.equals("scifly_m202_1G") || Build.DEVICE.equals("heran")) {
            try {
                mClass = Class.forName("scifly.middleware.display.ScreenPositionManager");
                mObject = mClass.getConstructor(Context.class).newInstance(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mClass = null;
            mObject = null;
        }
    }

    private Object invokeMethod(Class<?> cls, Object receiver, String methodName, Class<?>[] parameterTypes,
            Object[] args) {

        if (cls == null) {
            return null;
        }

        if ((parameterTypes == null && args == null) || parameterTypes.length == args.length) {
            try {
                return cls.getMethod(methodName, parameterTypes).invoke(receiver, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("The number of parameters is inconsistent");
        }

        return null;
    }

    private Object invokeMethod(Class<?> cls, Object receiver, String methodName) {
        return invokeMethod(cls, receiver, methodName, null, null);
    }

    /*
     * private Method getMethod(Class<?> cls, Object receiver, String name,
     * Class<?>[] parameterTypes, Object[] args) { try { return
     * cls.getMethod(name, parameterTypes); } catch (NoSuchMethodException e) {
     * // TODO Auto-generated catch block e.printStackTrace(); } return null; }
     */

    public void initPostion() {
        invokeMethod(mClass, mObject, "initPostion");
    }

    public String[] getCurrentScreenAxis() {
        return (String[]) invokeMethod(mClass, mObject, "getCurrentScreenAxis");
    }

    public int getmMaxRight() {
        return Integer.parseInt(invokeMethod(mClass, mObject, "getmMaxRight").toString());
    }

    public int getmMaxBottom() {
        return Integer.parseInt(invokeMethod(mClass, mObject, "getmMaxBottom").toString());
    }

    public void setPosition(int left, int top, int right, int bottom, int mode) {
        Class<?>[] parameterTypes = new Class[] {
                int.class, int.class, int.class, int.class, int.class
        };

        Object[] parameters = new Object[] {
                left, top, right, bottom, mode
        };

        invokeMethod(mClass, mObject, "setPosition", parameterTypes, parameters);
    }

    public void savePosition(int left, int top, int width, int height) {
        Class<?>[] parameterTypes = new Class[] {
                int.class, int.class, int.class, int.class
        };

        Object[] parameters = new Object[] {
                left, top, width, height
        };

        invokeMethod(mClass, mObject, "savePosition", parameterTypes, parameters);
    }

    public void zoomByPercent(int percent) {
        Class<?>[] parameterTypes = new Class[] {
            int.class
        };

        Object[] parameters = new Object[] {
            percent
        };

        invokeMethod(mClass, mObject, "zoomByPercent", parameterTypes, parameters);
    }

    public void savePostion() {
        invokeMethod(mClass, mObject, "savePostion");
    }

    public int getRateValue() {
        return Integer.parseInt(invokeMethod(mClass, mObject, "getRateValue").toString());
    }
}
