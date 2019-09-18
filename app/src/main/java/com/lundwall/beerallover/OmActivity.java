/**
 * Created by Mads Lundwall on 30-05-16.
 */
package com.lundwall.beerallover;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OmActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        findViewById(R.id.returKnap).setOnClickListener(this);
    }

    public void onClick(View v) {

        finish();
    }

}