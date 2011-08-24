package org.dynadroid.samples.helloworld;

import android.os.Bundle;
import android.widget.ViewFlipper;
import org.dynadroid.Application;
import org.dynadroid.DynaDroidActivity;
import org.dynadroid.Navigation;

public class HelloWorldActivity extends DynaDroidActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewFlipper viewFlipper = (ViewFlipper)Application.applicationView.findViewById(R.id.flipper);
        Navigation nav = new Navigation(this,viewFlipper);
        nav.pushScreen(new HelloWorldScreen(nav));
    }
}
