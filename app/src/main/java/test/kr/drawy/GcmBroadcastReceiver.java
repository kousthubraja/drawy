package test.kr.drawy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Kousthub on 05-11-2015.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent in = new Intent("custom-event-name");
        in.putExtra("message", intent.getStringExtra("message"));
        LocalBroadcastManager.getInstance(context).sendBroadcast(in);
    }
}
