package org.dynadroid.samples.helloworld;

import android.view.MenuItem;
import android.view.View;
import org.dynadroid.Navigation;
import org.dynadroid.Screen;


public class SwapScreen extends Screen {

    Navigation navigation;

    public SwapScreen(Navigation navigation) {
        this.navigation = navigation;
    }

    @Override
    protected void onStart() {
        findViewById(R.id.push).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigation.pushScreen(new PushScreen(navigation));
            }
        });

        findViewById(R.id.swap).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigation.swapScreen(new HelloWorldScreen(navigation));
            }
        });
    }

    @Override
    public boolean onMenuItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.push) {
            navigation.pushScreen(new PushScreen(navigation));
        } else if (item.getItemId() == R.id.swap) {
            navigation.swapScreen(new HelloWorldScreen(navigation));
        }
        return true;
    }
}
