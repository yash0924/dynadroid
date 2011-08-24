package org.dynadroid.samples.helloworld;

import android.view.MenuItem;
import android.view.View;
import org.dynadroid.Navigation;
import org.dynadroid.Screen;


public class PushScreen extends Screen {
    Navigation navigation;

    public PushScreen(Navigation navigation) {
        this.navigation = navigation;
    }

    @Override
    protected void onStart() {
        findViewById(R.id.push).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigation.pushScreen(new PushScreen(navigation));
            }
        });

        final Screen me = this;
        findViewById(R.id.pop).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigation.popScreen(me);
            }
        });
    }

    @Override
    public boolean onMenuItemSelected(MenuItem item) {
        final Screen me = this;
        if (item.getItemId() == R.id.push) {
            navigation.pushScreen(new PushScreen(navigation));
        } else if (item.getItemId() == R.id.pop) {
            navigation.popScreen(me);
        }
        return true;
    }
}
