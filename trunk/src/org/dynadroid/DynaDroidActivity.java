package org.dynadroid;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.view.*;
import org.dynadroid.utils.ErrorReporter;
import org.dynadroid.utils.Inflector;
import org.dynadroid.utils.Debug;

import java.lang.reflect.Field;


public abstract class DynaDroidActivity extends Activity {
    int currentOrientation = -1;
    ErrorReporter errorReporter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application);
        showTitle(true);
        enableCustomOrientationHandling(false);
        Application.init(this);
    }

    protected void enableCustomOrientationHandling(boolean enable) {
        if (!enable) return;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setOrientationToPortrait();
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setOrientationToLandscape();
        }
        if (!isRunningInEmulator()) {
            new OrientationEventListener(this) {
                @Override
                public void onOrientationChanged(int i) {
                    Screen topScreen = Application.getTopScreen();
                    if ((topScreen != null && topScreen.onForcePortraitOrientation()) || (i > 315) || (i < 45) || ((i > 135) && (i < 225))) {
                        setOrientationToPortrait();
                    } else {
                        setOrientationToLandscape();
                    }
                }
            }.enable();
        }
    }

    protected void setErrorReporter(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    protected void showTitle(boolean show) {
        if (show) return;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public boolean isRunningInEmulator() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID) == null;
    }

    boolean isOrientationPortrait() {
        return currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    boolean isOrientationLandscape() {
        return currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    void setOrientationToPortrait() {
        if (isOrientationPortrait()) return;
        currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    void setOrientationToLandscape() {
        if (isOrientationLandscape()) return;
        currentOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        Screen topScreen = Application.getTopScreen();
        if (topScreen != null) {
            Class clazz = topScreen.getClass();
            do {
                String className = clazz.getSimpleName();
                int endIndex = className.indexOf("Screen");
                if (endIndex != -1) {
                    String menuName = Inflector.underscore(className.substring(0, endIndex));
                    try {
                        Field field = R.menu.class.getField(menuName);
                        MenuInflater inflater = getMenuInflater();
                        inflater.inflate(field.getInt(null), menu);
                        break;
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } while ((clazz = clazz.getSuperclass()) != null);
            topScreen.onPrepareMenu(menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Screen topScreen = Application.getTopScreen();
        if (topScreen != null) {
            return topScreen.onMenuItemSelected(item);
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Screen topScreen = Application.getTopScreen();
        if (topScreen != null) {
            topScreen.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Screen topScreen = Application.getTopScreen();
        if (topScreen != null) {
            return topScreen.onContextItemSelected(item);
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onDestroy() {
        super.onDestroy();
        Debug.println("***************** Activity onDestroy killing process");
        //android.os.Process.killProcess(android.os.Process.myPid());
        kill();
    }

    protected void kill() {
        ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).restartPackage(getPackageName());
    }

    public void onBackPressed() {
        if (Application.screenCount() == 1) {
            showExitDialog();
        } else {
            Application.popTopScreen();
        }
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}