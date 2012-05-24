package com.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import android.app.Activity;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class WakeOnLanActivity extends Activity {
    private static final int PORT = 9;    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
//        android.provider.Settings.System.getString(getContentResolver(), android.provider.Settings.System.WIFI_USE_STATIC_IP);        
//        android.provider.Settings.System.getString(getContentResolver(), android.provider.Settings.System.WIFI_STATIC_IP);
//        android.provider.Settings.System.getString(getContentResolver(), android.provider.Settings.System.WIFI_STATIC_NETMASK);
//        android.provider.Settings.System.getString(getContentResolver(), android.provider.Settings.System.WIFI_STATIC_DNS1);
//        android.provider.Settings.System.getString(getContentResolver(), android.provider.Settings.System.WIFI_STATIC_GATEWAY);
        
        ((Button) findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String macStr = ((EditText) findViewById(R.id.editText1)).getText().toString();
	            byte[] macBytes = getMacBytes(macStr);
	            byte[] bytes = new byte[6 + 16 * macBytes.length];
	            for (int i = 0; i < 6; i++) {
	                bytes[i] = (byte) 0xff;
	            }
	            for (int i = 6; i < bytes.length; i += macBytes.length) {
	                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
	            }
	            
//	    		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//	    		if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
//	    			return;
//	    		}
//				InetAddress address = InetAddress.getByName(Formatter.formatIpAddress(wim.getConnectionInfo().getIpAddress()));
 
				try {
					InetAddress address = InetAddress.getByName(getLocalIpAddress());

		            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
		            DatagramSocket socket = new DatagramSocket();
		            socket.send(packet);
		            socket.close();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
    }
    
    public String getLocalIpAddress() throws SocketException {
//      1.using getHostAddress : ***** IP=fe80::65ca:a13d:ea5a:233d%rmnet_sdio0
//      2.using hashCode and Formatter : ***** IP=238.194.77.212
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress()) {
                    String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                    //Log.i(TAG, "***** IP="+ ip);
                    return ip;
                }
            }
        }
        return null;
    }
    
    
    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }
    
    private InetAddress getBroadcastAddress() throws IOException {
    	WifiManager myWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    	DhcpInfo myDhcpInfo = myWifiManager.getDhcpInfo();
    	if (myDhcpInfo == null) {
    		System.out.println("Could not get broadcast address");
    		return null;
    	}
    	int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask)
    				| ~myDhcpInfo.netmask;
    	byte[] quads = new byte[4];
    	for (int k = 0; k < 4; k++)
    	quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
    	return InetAddress.getByAddress(quads);
    }
}