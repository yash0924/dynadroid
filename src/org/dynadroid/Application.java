package org.dynadroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

    protected static ViewFlipper viewFlipper;
    static List<Screen> screens;
    static DynaDroidActivity activity;
    public static View applicationView;
    protected static ProgressDialog busyDialog;
    static List<String> trackedScreensMap;
    static List<Integer> trackedScreens;

    public synchronized static void init(DynaDroidActivity dorindaActivity) {
        screens = new ArrayList();
        activity = dorindaActivity;
        Screen.setActivity(dorindaActivity);
        viewFlipper = (ViewFlipper) activity.findViewById(R.id.flipper);
        Debug.println("******viewF=" + viewFlipper);
        applicationView = activity.findViewById(R.id.application);
        trackedScreensMap = new ArrayList();
        trackedScreens = new ArrayList();
    }

    public synchronized static int screenCount() {
        return screens.size();
    }

    public synchronized static void swapScreen(Screen screen, boolean wait) {
        pushScreen(screen, false, wait);
        popScreenBelow(screen);
    }

    public synchronized static void pushScreen(Screen screen, boolean wait) {
        pushScreen(screen, true, wait);
    }

    public synchronized static void pushScreen(Screen screen, boolean animate, boolean wait) {
        try {
            if (screens.contains(screen)) {
                Debug.println("*****cancelling pushScreen, screen already on stack");
                return;
            }
            if (!screen.startCalled()) {
                screen.start();
                if (screen.onForcePortraitOrientation()) {
                    activity.setOrientationToPortrait();
                }
            }
            viewFlipper.addView(screen.getView(), screens.size());
            if (animate) {
                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(activity, R.anim.push_left_out));
                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(activity, R.anim.push_left_in));
            } else {
                Debug.println("no animation");
                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_out));
                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in));
            }
            if (!wait) {
                screen.update();
            }
            viewFlipper.showNext();
            Debug.println("***pushing screen " + screen);
            screens.add(screen);
            trackScreen(screen);
        } catch (IllegalStateException e) {
            Debug.println("*****IllegalStateException cancelling pushScreen");
        }
    }

    public synchronized static void trackScreen(Screen screen) {
        String trackingName = screen.trackingName();
        int key = trackedScreensMap.indexOf(trackingName);
        if (key == -1) {
            trackedScreensMap.add(trackingName);
            key = trackedScreensMap.size() - 1;
        }
        trackedScreens.add(key);
    }

    synchronized static String pullScreenAnalytics() {
        String analytics = "screens="+ StringUtils.join(trackedScreens,",") + "&map=" + StringUtils.join(trackedScreensMap, ",");
        trackedScreens = new ArrayList();
        trackedScreensMap = new ArrayList();
        return analytics;
    }

    public synchronized static void popTopScreen(boolean animate, boolean wait) {
        if (screens.size() <= 1) {
            return;
        }
        popScreen(screens.get(screens.size() - 1), animate, wait);
    }

    public synchronized static void popScreenBelow(Screen screen) {
        if (screens.size() <= 1) {
            return;
        }
        popScreen(screens.get(screens.indexOf(screen) - 1), false, false);
    }

    public synchronized static void popScreenAndAllAbove(Screen screen) {
        int index = screens.indexOf(screen);
        while (screens.size() > index) {
            popScreen(screens.get(index), false, index + 1 != screens.size());
        }
    }

    public synchronized static void popAllAbove(Screen screen) {
        int index = screens.indexOf(screen) + 1;
        while (screens.size() > index) {
            popScreen(screens.get(index), false, index + 1 != screens.size());
        }
    }

    synchronized static void popScreen(Screen screen, boolean animate, boolean wait) {
        int indexOfScreen = screens.indexOf(screen);
        if (indexOfScreen != -1) {
            if (animate) {
                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(activity, R.anim.push_right_out));
                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(activity, R.anim.push_right_in));
            } else {
                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_out));
                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in));
            }

            if (indexOfScreen - 1 > 0) {
                Screen screenBelow = screens.get(indexOfScreen - 1);
                if (screen.isTopScreen() && !wait) {
                    screenBelow.update();
                }
                if (screenBelow.onForcePortraitOrientation()) {
                    activity.setOrientationToPortrait();
                }
            }
            viewFlipper.showPrevious();
            viewFlipper.removeView(screen.view);
            screens.remove(screen);
        }
    }

    public synchronized static void popAllButTop() {
        while (screens.size() > 1) {
            Screen scrn = screens.get(0);
            // popScreen() itself will modify the screens object and remove the first popped screen from itself.
            popScreen(scrn, false, false);
        }
    }

    public synchronized static Screen getTopScreen() {
        return screens.size() == 0 ? null : screens.get(screens.size() - 1);
    }

    public static void popTopScreen() {
        popTopScreen(true, false);
    }

    public static View inflate(int layout) {
        LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return vi.inflate(layout, null);
    }

    public static void showMessage(String title, final String message) {
        String button1String = "OK";
        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });
        ad.show();
    }

    public static synchronized void showBusy() {
        Debug.println("***showBusy and isShowing = " + isShowingBusy());
        if (busyDialog != null && busyDialog.isShowing()) {
            return;
        }
        busyDialog = new ProgressDialog(activity, R.style.busy) {
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
                                getTopScreen().onCancelBusy();
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
                try {
                    Field field = R.layout.class.getField(layoutName);
                    return inflate(field.getInt(null));
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        return view;
    }

    public static boolean isMetric() {
        String countryCode = Locale.getDefault().getCountry();
        return !("US".equalsIgnoreCase(countryCode) || "LR".equalsIgnoreCase(countryCode) || "MM".equalsIgnoreCase(countryCode));
    }
}
