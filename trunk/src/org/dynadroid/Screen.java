package org.dynadroid;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import org.dynadroid.utils.Debug;

public abstract class Screen {

    public static DynaDroidActivity activity;
    protected View view;
    private boolean startCalled = false;

    public synchronized static void setActivity(DynaDroidActivity _activity) {
        activity = _activity;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String trackingName() {
        return this.getClass().getSimpleName();
    }

    public boolean startCalled() {
        return startCalled;
    }

    public void start() {
        Debug.println("calling start");
        startCalled = true;
        setView(Application.viewForClass(this.getClass()));
        onStart();
        Debug.println("end start");
    }

    public void update() {
        Debug.println("@#$@#$#@$#@$@#$@# updating : " + this.getClass().getSimpleName());
        onUpdate();
    }

    protected void onStart() {
    }

    protected void onUpdate() {
    }

    protected void onCancelBusy() {

    }

    public void onPrepareMenu(Menu menu) {
    }

    public boolean onMenuItemSelected(MenuItem item) {
        return false;
    }

    public boolean onForcePortraitOrientation() {
        return false;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    }

    public boolean onContextItemSelected(MenuItem item) {
        return true;
    }

    public static boolean isOrientationPortrait() {
        return activity.isOrientationPortrait();
    }

    public static boolean isOrientationLandscape() {
        return activity.isOrientationLandscape();
    }

    public Screen pushAndWait() {
        Application.pushScreen(this, true);
        return this;
    }

    public Screen push() {
        Application.pushScreen(this, false);
        return this;
    }

    public Screen swapAndWait() {
        Application.swapScreen(this, true);
        return this;
    }

    public Screen swap() {
        Application.swapScreen(this, false);
        return this;
    }

    public void pop() {
        Application.popScreen(this, true, false);
    }

    public void popAndWait() {
        Application.popScreen(this, true, true);
    }

    public void pop(boolean animate) {
        Application.popScreen(this, animate, false);
    }

    public void popAndWait(boolean animate) {
        Application.popScreen(this, animate, true);
    }

    public boolean isTopScreen() {
        return Application.getTopScreen() == this;
    }

    protected View findViewById(int id) {
        return view.findViewById(id);
    }

    public View inflate(int layout) {
        return Application.inflate(layout);
    }

    public void showBusy() {
        Application.showBusy();
    }

    public void hideBusy() {
        Application.hideBusy();
    }

    public boolean isShowingBusy() {
        return Application.isShowingBusy();
    }

    public void showMessage(String title, final String message) {
        Application.showMessage(title, message);
    }
}

