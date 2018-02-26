package com.sahulkrishan.ancquicksettings;

import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
/* import java.util.concurrent.TimeUnit;
import android.view.KeyEvent;
import android.media.AudioManager;
import android.content.Context; */

/**
 * Created by Sahul Krishan on 20/02/2018.
 */

public class ANC_tile extends TileService {

    Runtime rt = Runtime.getRuntime();
    private final String LOG_TAG = "ANC_TILE";
    private final int STATE_OFF = 0;
    private final int STATE_ON = 1;
    private int state = STATE_ON;

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
        if (state == STATE_ON) {
            // set persist.audio.anc.enabled to false and check if it's actually set to false
            try {
                Log.d(LOG_TAG, "Executing commands...");
                // Request root permissions
                /*rt.exec("su");*/
                // Disable ANC
                Process disable_anc = rt.exec("system/bin/setprop persist.audio.anc.enabled false");
                disable_anc.waitFor();
                if (getSystemProperty("persist.audio.anc.enabled").equals("false")){
                    // ANC is disabled, update tile.
                    Log.d(LOG_TAG, "ANC is disabled, setting tile to inactive...");
                    state = STATE_OFF;
                    tile.setState(Tile.STATE_INACTIVE);
                    tile.setLabel(getString(R.string.anc_inactive));
                    tile.setIcon(Icon.createWithResource(this, R.drawable.ic_tile_anc));
                    /* AudioManager mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
                    if (mAudioManager.isMusicActive()) {
                        KeyEvent stop = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_STOP);
                        mAudioManager.dispatchMediaKeyEvent(stop);
                        TimeUnit.MILLISECONDS.sleep(500);
                        KeyEvent play = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
                        mAudioManager.dispatchMediaKeyEvent(play);
                    } */ /** Disable for now, this doesn't automatically restart ANC **/
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
                    // Request root permissions
                    /*rt.exec("su");*/
                    // Enable ANC
                    Process enable_anc = rt.exec("system/bin/setprop persist.audio.anc.enabled true");
                    enable_anc.waitFor();
                    if (getSystemProperty("persist.audio.anc.enabled").equals("true")){
                        // ANC is enabled, update tile.
                        Log.d(LOG_TAG, "ANC is enabled, setting tile to active...");
                        state = STATE_ON;
                        tile.setState(Tile.STATE_ACTIVE);
                        tile.setLabel(getString(R.string.anc_active));
                        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_tile_anc));
                        Toast.makeText(getApplicationContext(),R.string.reconnect,Toast.LENGTH_LONG).show();
                        /* AudioManager mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
                        if (mAudioManager.isMusicActive()) {
                            KeyEvent stop = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE);
                            mAudioManager.dispatchMediaKeyEvent(stop);
                            TimeUnit.MILLISECONDS.sleep(500);
                            KeyEvent play = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
                            mAudioManager.dispatchMediaKeyEvent(play);
                        } */ /** Disable for now, this doesn't automatically restart ANC **/
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
        if (getSystemProperty("persist.audio.anc.enabled").equals("false")){
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