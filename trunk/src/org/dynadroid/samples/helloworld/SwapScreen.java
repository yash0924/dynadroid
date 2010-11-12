package org.dynadroid.samples.helloworld;

import android.view.MenuItem;
import android.view.View;
import org.dynadroid.Screen;


public class SwapScreen extends Screen {

    @Override
    protected void onStart() {
        findViewById(R.id.push).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new PushScreen().push();
            }
        });

        findViewById(R.id.swap).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new HelloWorldScreen().swap();
            }
        });
    }

    @Override
    public boolean onMenuItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.push) {
            new PushScreen().push();
        } else if (item.getItemId() == R.id.swap) {
            new HelloWorldScreen().swap();
        }
        return true;
    }
}
