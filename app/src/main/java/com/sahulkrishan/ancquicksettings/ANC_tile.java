package com.sahulkrishan.ancquicksettings;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;

/**
 * Created by Sahul Krishan on 20/02/2018.
 */

public class ANC_tile extends TileService {

    Runtime rt = Runtime.getRuntime();
    private final String LOG_TAG = "ANC_TILE";
    private final int STATE_OFF = 0;
    private final int STATE_ON = 1;
    private int toggleState = STATE_ON;
    String[] enable_anc = {"su","system/bin/setprop persist.audio.anc.enabled true"};
    String[] disable_anc = {"su","system/bin/setprop persist.audio.anc.enabled false"};

    public String getSystemProperty(String key) {
        String value = null;
        try {
            value = (String) Class.forName("android.os.SystemProperties").getMethod("get", String.class).invoke(null, key);
        } catch (Exception e) {e.printStackTrace();}
        return value;
    }

    @Override
    public void onClick() {
        Tile tile = getQsTile();
        if (toggleState == STATE_ON) {
            toggleState = STATE_OFF;
            tile.setState(Tile.STATE_INACTIVE);
            tile.setLabel(getString(R.string.anc_inactive));
            Log.d(LOG_TAG, "Requesting root permissions and executing commands...");
            try {
                rt.exec(disable_anc);
            } catch (IOException e) {
                e.printStackTrace();
            }
            tile.updateTile();
            Toast.makeText(getApplicationContext(),R.string.reconnect,Toast.LENGTH_LONG).show();
        } else {
            if (toggleState == STATE_OFF) {
                toggleState = STATE_ON;
                tile.setState(Tile.STATE_ACTIVE);
                tile.setLabel(getString(R.string.anc_active));
                Log.d(LOG_TAG, "Requesting root permissions and executing commands...");
                try {
                    rt.exec(enable_anc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tile.updateTile();
                Toast.makeText(getApplicationContext(),R.string.reconnect,Toast.LENGTH_LONG).show();
            }
        }
        Log.d(LOG_TAG, "ANC mode = " + Integer.toString(toggleState));
    }

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        Log.d(LOG_TAG, "Started Listening");
        if (getSystemProperty("persist.audio.anc.enabled").equals("false")){
            Log.d(LOG_TAG, "ANC is disabled, setting tile to inactive...");
            toggleState = STATE_OFF;
            tile.setState(Tile.STATE_INACTIVE);
            tile.setLabel(getString(R.string.anc_inactive));
            tile.updateTile();
        } else {
            Log.d(LOG_TAG, "ANC is enabled, setting tile to active...");
            toggleState = STATE_ON;
            tile.setState(Tile.STATE_ACTIVE);
            tile.setLabel(getString(R.string.anc_active));
            tile.updateTile();
        }
    }

    @Override
    public void onStopListening() {Log.d(LOG_TAG, "Stopped Listening");}
}