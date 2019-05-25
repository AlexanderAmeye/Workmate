package me.example.paul.Utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.util.Log;

import java.io.UnsupportedEncodingException;

public class NFCHelper {

    public static void enableForegroundDispatchSystem(Context context, NfcAdapter adapter, Class c) {
        Intent intent = new Intent(context, c).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        adapter.enableForegroundDispatch((Activity) context, pendingIntent, intentFilters, null);
    }

    public static void disableForegroundDispatchSystem(Context context, NfcAdapter adapter) {
        adapter.disableForegroundDispatch((Activity) context);
    }

    public static String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }
}
