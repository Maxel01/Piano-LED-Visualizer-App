package de.maxiindiestyle.pianoledvisualizer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import com.badlogic.gdx.Gdx;

import java.lang.reflect.Array;
import java.util.Arrays;

public class AndroidAddresses extends Connection.Addresses {

    private Context context;

    public AndroidAddresses(Context context) {
        this.context = context;
    }

    @Override
    public byte[] getIP() {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo connectionInfo = wm.getConnectionInfo();
        int ipAddress = connectionInfo.getIpAddress();
        String ipString = Formatter.formatIpAddress(ipAddress);
        String[] ip = ipString.split("\\.");
        byte[] ipByte = new byte[ip.length];
        for (int i = 0; i < ip.length; i++) {
            ipByte[i] = (byte) Integer.parseInt(ip[i]);
        }
        return ipByte;
    }
}
