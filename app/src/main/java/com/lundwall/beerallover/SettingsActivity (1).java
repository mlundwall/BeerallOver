/**
 * Created by Mads Lundwall on 30-05-16.
 */

package com.lundwall.beerallover;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import static com.lundwall.beerallover.CommonData.*;
import static com.lundwall.beerallover.BeerAllOverMain.gemData;

public class SettingsActivity extends Activity implements View.OnClickListener {

    private Button gemknap;
    private Button fortrydknap;
    private CheckBox gpscheck;
    private CheckBox lydcheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

        setContentView(R.layout.setup);

        gemknap = (Button) findViewById(R.id.gemKnap);
        gemknap.setOnClickListener(this);
        gemknap.setTag("gem");

        fortrydknap = (Button) findViewById(R.id.fortrydKnap);
        fortrydknap.setOnClickListener(this);
        fortrydknap.setTag("fortryd");

        // GPS disablet
        /* gpscheck=(CheckBox) findViewById(R.id.gpscheck);
        gpscheck.setChecked(setup_fragps);
*/
        lydcheck=(CheckBox) findViewById(R.id.lydcheck);
        lydcheck.setChecked(setup_skaldytte);
    }

    public void onClick(View v) {
        if (v.getTag()=="gem") {
//          setup_fragps=gpscheck.isChecked(); // GPS Disablet
            setup_skaldytte=lydcheck.isChecked();
            gemData();
        }
        finish();
    }

}
