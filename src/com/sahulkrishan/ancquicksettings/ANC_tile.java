package com.sahulkrishan.ancquicksettings;

import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import android.os.SystemProperties;

/**
 * Created by Sahul Krishan on 20/02/2018.
 */

public class ANC_tile extends TileService {

    Runtime rt = Runtime.getRuntime();
    private final String LOG_TAG = "ANC_TILE";
    private final int STATE_OFF = 0;
    private final int STATE_ON = 1;
    private int state = STATE_ON;

    @Override
    public void onClick() {
        Tile tile = getQsTile();
        if (state == STATE_ON) {
            // set persist.audio.anc.enabled to false and check if it's actually set to false
            try {
                Log.d(LOG_TAG, "Executing commands...");
                
		// Disable ANC
                SystemProperties.set("persist.audio.anc.enabled", "false");
		if (SystemProperties.get("persist.audio.anc.enabled", "false")){
                    // ANC is disabled, update tile.
                    Log.d(LOG_TAG, "ANC is disabled, setting tile to inactive...");
                    state = STATE_OFF;
                    tile.setState(Tile.STATE_INACTIVE);
                    tile.setLabel(getString(R.string.anc_inactive));
                    tile.setIcon(Icon.createWithResource(this, R.drawable.ic_tile_anc));
                } else {
                    // Failed to disable ANC, set tile to unavailable and display a warning.
                    Log.d(LOG_TAG, "Failed to disable ANC, displaying warning...");
                    tile.setState(Tile.STATE_ACTIVE);
                    tile.setLabel(getString(R.string.anc_failed_disable));
                }
            } catch (IOException e) {e.printStackTrace();} catch (InterruptedException e) {e.printStackTrace();}
            tile.updateTile();
        } else {
            if (state == STATE_OFF) {
                state = STATE_ON;
                tile.setState(Tile.STATE_ACTIVE);
                tile.setLabel(getString(R.string.anc_active));
                // set persist.audio.anc.enabled to true and check if it's actually set to true
                try {
                    Log.d(LOG_TAG, "Executing commands...");
                    
		    // Enable ANC
                    SystemProperties.set("persist.audio.anc.enabled", "true");
		    if (SystemProperties.get("persist.audio.anc.enabled", "true")){
                        // ANC is enabled, update tile.
                        Log.d(LOG_TAG, "ANC is enabled, setting tile to active...");
                        state = STATE_ON;
                        tile.setState(Tile.STATE_ACTIVE);
                        tile.setLabel(getString(R.string.anc_active));
                        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_tile_anc));
                        Toast.makeText(getApplicationContext(),R.string.reconnect,Toast.LENGTH_LONG).show();
                    } else {
                        // Failed to enable ANC, set tile to unavailable and display a warning.
                        Log.d(LOG_TAG, "Failed to enable ANC, displaying warning...");
                        tile.setState(Tile.STATE_INACTIVE);
                        tile.setLabel(getString(R.string.anc_failed_enable));
                        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_tile_anc_error));
                    }
                } catch (IOException e) {e.printStackTrace();} catch (InterruptedException e) {e.printStackTrace();}
                tile.updateTile();
            }
        }
        Log.d(LOG_TAG, "ANC mode = " + Integer.toString(state));
    }

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        Log.d(LOG_TAG, "Started Listening");
	if (SystemProperties.get("persist.audio.anc.enabled", "false")){
            Log.d(LOG_TAG, "ANC is disabled, setting tile to inactive...");
            state = STATE_OFF;
            tile.setState(Tile.STATE_INACTIVE);
            tile.setLabel(getString(R.string.anc_inactive));
            tile.setIcon(Icon.createWithResource(this, R.drawable.ic_tile_anc));
            tile.updateTile();
        } else {
            Log.d(LOG_TAG, "ANC is enabled, setting tile to active...");
            state = STATE_ON;
            tile.setState(Tile.STATE_ACTIVE);
            tile.setLabel(getString(R.string.anc_active));
            tile.setIcon(Icon.createWithResource(this, R.drawable.ic_tile_anc));
            tile.updateTile();
        }
    }

    @Override
    public void onStopListening() {Log.d(LOG_TAG, "Stopped Listening");}
}
