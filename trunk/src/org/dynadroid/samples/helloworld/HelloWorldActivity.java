package org.dynadroid.samples.helloworld;

import android.os.Bundle;
import org.dynadroid.DynaDroidActivity;

public class HelloWorldActivity extends DynaDroidActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new HelloWorldScreen().push();
    }
}
