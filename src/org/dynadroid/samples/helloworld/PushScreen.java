package org.dynadroid.samples.helloworld;

import android.view.MenuItem;
import android.view.View;
import org.dynadroid.R;
import org.dynadroid.Screen;


public class PushScreen extends Screen {

    @Override
    protected void onStart() {
        findViewById(R.id.push).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new PushScreen().push();
            }
        });

        findViewById(R.id.pop).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop();
            }
        });
    }

    @Override
    public boolean onMenuItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.push) {
            new PushScreen().push();
        } else if (item.getItemId() == R.id.pop) {
            pop();
        }
        return true;
    }
}
