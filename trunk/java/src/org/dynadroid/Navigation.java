package org.dynadroid;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;
import org.dynadroid.utils.Debug;
import org.dynadroid.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: bknorr
 */
public class Navigation {

    protected ViewFlipper viewFlipper;
    protected List<Screen> screens;
    protected List<String> trackedScreensMap;
    protected List<Integer> trackedScreens;
    private DynaDroidActivity activity;
    private int anim_push_left_out, anim_push_left_in, anim_push_right_out, anim_push_right_in, anim_fade_out, anim_fade_in;

    public Navigation(DynaDroidActivity dynaDroidActivity, ViewFlipper viewFlipper) {
        activity = dynaDroidActivity;
        this.viewFlipper = viewFlipper;
        anim_push_left_out = Application.getResourceIdByName("anim", "push_left_out");
        anim_push_left_in = Application.getResourceIdByName("anim", "push_left_in");
        anim_push_right_out = Application.getResourceIdByName("anim", "push_right_out");
        anim_push_right_in = Application.getResourceIdByName("anim", "push_right_in");
        anim_fade_out = Application.getResourceIdByName("anim", "fade_out");
        anim_fade_in = Application.getResourceIdByName("anim", "fade_in");
        screens = new ArrayList();
        trackedScreensMap = new ArrayList();
        trackedScreens = new ArrayList();
    }

    public int screenCount() {
        return screens.size();
    }
    
    public Screen pushScreen(Screen screen) {
        return this.pushScreen(screen, true);
    }

    public Screen popScreen(Screen screen) {
        return this.popScreen(screen, true);
    }

    public Screen swapScreen(Screen screen) {
        pushScreen(screen, false);
        popScreenBelow(screen);
        return screen;
    }

    public Screen pushScreen(Screen screen, boolean animate) {
        Application.markAsNavigationWithTopScreen(this);
        try {
            if (screens.contains(screen)) {
                Debug.println("*****cancelling pushScreen, screen already on stack");
                return screen;
            }
            if (!screen.startCalled()) {
                screen.start();
                if (screen.onForcePortraitOrientation()) {
                    activity.setOrientationToPortrait();
                }
            }
            viewFlipper.addView(screen.getView(), screens.size());
            if (animate) {
                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(activity, anim_push_left_out));
                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(activity, anim_push_left_in));
            } else {
                Debug.println("no animation");
                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(activity, anim_fade_out));
                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(activity, anim_fade_in));
            }
            viewFlipper.showNext();
            Debug.println("***pushing screen " + screen);
            screens.add(screen);
            trackScreen(screen);
        } catch (IllegalStateException e) {
            Debug.println("*****IllegalStateException cancelling pushScreen");
        }
        return screen;
    }

    public void trackScreen(Screen screen) {
        String trackingName = screen.trackingName();
        int key = trackedScreensMap.indexOf(trackingName);
        if (key == -1) {
            trackedScreensMap.add(trackingName);
            key = trackedScreensMap.size() - 1;
        }
        trackedScreens.add(key);
    }

    String pullScreenAnalytics() {
        String analytics = "screens=" + StringUtils.join(trackedScreens, ",") + "&map=" + StringUtils.join(trackedScreensMap, ",");
        trackedScreens = new ArrayList();
        trackedScreensMap = new ArrayList();
        return analytics;
    }

    public Screen popTopScreen(boolean animate) {
        if (screens.size() <= 1) {
            return null;
        }
        return popScreen(screens.get(screens.size() - 1), animate);
    }

    public void popScreenBelow(Screen screen) {
        if (screens.size() <= 1) {
            return;
        }
        popScreen(screens.get(screens.indexOf(screen) - 1), false);
    }

    public Screen popScreenAndAllAbove(Screen screen) {
        int index = screens.indexOf(screen);
        while (screens.size() > index) {
            popScreen(screens.get(index), false);
        }
        return getTopScreen();
    }

    public Screen popAllAbove(Screen screen) {
        int index = screens.indexOf(screen) + 1;
        while (screens.size() > index) {
            popScreen(screens.get(index), false);
        }
        return screen;
    }

    public Screen popScreen(Screen screen, boolean animate) {
        int indexOfScreen = screens.indexOf(screen);
        if (indexOfScreen != -1) {
            if (animate) {
                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(activity, anim_push_right_out));
                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(activity, anim_push_right_in));
            } else {
                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(activity, anim_fade_out));
                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(activity, anim_fade_in));
            }

            if (indexOfScreen - 1 > 0) {
                Screen screenBelow = screens.get(indexOfScreen - 1);
                if (screenBelow.onForcePortraitOrientation()) {
                    activity.setOrientationToPortrait();
                }
            }
            viewFlipper.showPrevious();
            viewFlipper.removeView(screen.view);
            screens.remove(screen);
        }
        return getTopScreen();
    }

    public void popAllButTop() {
        while (screens.size() > 1) {
            Screen scrn = screens.get(0);
            // popScreen() itself will modify the screens object and remove the first popped screen from itself.
            popScreen(scrn, false);
        }
    }

    public Screen getTopScreen() {
        return screens.size() == 0 ? null : screens.get(screens.size() - 1);
    }

    public Screen popTopScreen() {
        return popTopScreen(true);
    }

}
