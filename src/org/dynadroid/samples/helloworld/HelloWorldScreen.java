package org.dynadroid.samples.helloworld;

import android.view.MenuItem;
import android.view.View;
import org.dynadroid.R;
import org.dynadroid.Screen;

public class HelloWorldScreen extends Screen {

    @Override
    protected void onStart() {
        findViewById(R.id.push).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new PushScreen().push();
            }
        });

        findViewById(R.id.swap).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SwapScreen().swap();
            }
        });

        findViewById(R.id.show_busy).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showBusy();
            }
        });
    }

    @Override
    public boolean onMenuItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.push) {
            new PushScreen().push();
        } else if (item.getItemId() == R.id.swap) {
            new SwapScreen().swap();
        } else if (item.getItemId() == R.id.show_busy) {
            showBusy();
        }
        return true;
    }

}
