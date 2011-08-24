package org.dynadroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;
import org.dynadroid.utils.Debug;
import org.dynadroid.utils.Inflector;
import org.dynadroid.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Application {

    public static DynaDroidActivity activity;
    public static View applicationView;
    protected static ProgressDialog busyDialog;
    protected static List<Navigation> navigationList;
    private static Navigation topNavigation;
    
    static int layout_application, id_application, style_busy;

    public synchronized static void init(DynaDroidActivity dynaDroidActivity) {
        activity = dynaDroidActivity;
        layout_application = getResourceIdByName("layout","application");
        id_application = getResourceIdByName("id","application");
        style_busy = getResourceIdByName("style","busy");
        dynaDroidActivity.setContentView(layout_application);
        Screen.setActivity(dynaDroidActivity);
        applicationView = activity.findViewById(id_application);
    }

    static void addNavigation(Navigation nav) {
        navigationList.add(nav);
        topNavigation = nav;
    }

    static void markAsNavigationWithTopScreen(Navigation nav) {
        topNavigation = nav;
    }

    static Screen getTopScreen() {
        return topNavigation.getTopScreen();
    }

    public static View inflate(int layout) {
        LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return vi.inflate(layout, null);
    }

    public static void showMessage(String title, final String message) {
        showMessage(title, message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });
    }

    public static void showMessage(String title, final String message, DialogInterface.OnClickListener onclick) {
        String button1String = "OK";
        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(button1String, onclick);
        ad.show();
    }

    public static synchronized void showBusy() {
        Debug.println("***showBusy and isShowing = " + isShowingBusy());
        if (busyDialog != null && busyDialog.isShowing()) {
            return;
        }
        busyDialog = new ProgressDialog(activity, style_busy) {
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    onBackPressed();
                    return true;
                }
                return super.onKeyDown(keyCode, event);
            }

            public void onBackPressed() {
                Debug.println("***Dialog onBackPressed");
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you sure you want to cancel?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                topNavigation.getTopScreen().onCancelBusy();
                                busyDialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

        };
        busyDialog.setTitle(null);
        busyDialog.setMessage("Loading...");
        busyDialog.setCancelable(false);
        busyDialog.show();
    }

    public synchronized static boolean isShowingBusy() {
        return busyDialog != null && busyDialog.isShowing();
    }

    public synchronized static void hideBusy() {
        Debug.println("***hideBusy and isShowing = " + isShowingBusy());
        if (isShowingBusy()) {
            busyDialog.dismiss();
        }
    }

    synchronized static View viewForClass(Class clazz) {
        View view = null;
        do {
            String className = clazz.getSimpleName();
            int endIndex = className.indexOf("Screen");
            if (endIndex != -1) {
                String layoutName = Inflector.underscore(className.substring(0, endIndex));
                return inflate(getResourceIdByName("layout",layoutName));
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        return view;
    }

    public static boolean isMetric() {
        String countryCode = Locale.getDefault().getCountry();
        return !("US".equalsIgnoreCase(countryCode) || "LR".equalsIgnoreCase(countryCode) || "MM".equalsIgnoreCase(countryCode));
    }

    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        Application.activity.startActivity(intent);
    }

    public static int getResourceIdByName(String className, String name) {
        Class r = null;
        int id = 0;
        try {
            r = Class.forName(activity.getPackageName() + ".R");

            Class[] classes = r.getClasses();
            Class desireClass = null;

            for (int i = 0; i < classes.length; i++) {
                if (classes[i].getName().split("\\$")[1].equals(className)) {
                    desireClass = classes[i];
                    break;
                }
            }
            if (desireClass != null) {
                id = desireClass.getField(name).getInt(desireClass);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return id;
    }

    public static String pullScreenAnalytics() {
        StringBuffer analytics = new StringBuffer();
        for (Navigation nav : navigationList) {
            analytics.append(nav.pullScreenAnalytics()).append("&");
        }
        return analytics.toString();
    }

    static void onBackPressed() {
        if (topNavigation.screenCount() == 1) {
            activity.finish();
        } else {
            topNavigation.popTopScreen();
        }
    }
}
