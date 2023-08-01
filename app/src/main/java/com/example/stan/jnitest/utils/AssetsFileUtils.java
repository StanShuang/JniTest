package com.example.stan.jnitest.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class AssetsFileUtils {

    /**
     * @param context
     * @param path    文件相对路径-比如是assets/bin/config.json只用传bin/config.json
     * @return
     */
    public static boolean assetsFileIsExit(Context context, String path) {
        int index = path.lastIndexOf("/");
        String assetsPath;
        String assetsFilename;
        if (index != -1) {
            assetsPath = path.substring(0, index);
            assetsFilename = path.substring(index + 1);
        } else {
            assetsPath = "";
            assetsFilename = path;
        }

        Log.d("MainActivity", "assetsPath=" + assetsPath + ",assetsFileName=" + assetsFilename);
        AssetManager assetManager = context.getAssets();
        try {
            String[] names = assetManager.list(assetsPath);
            for (String filename : names) {
                if (filename.equals(assetsFilename.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param context
     * @param path    文件相对路径-比如是assets/bin/config.json 只用传bin/config.json
     * @return
     */
    public static String readFiles(Context context, @NotNull String path) {
        InputStream iStream = null;
        BufferedReader bufferedReader = null;
        try {
            iStream = context.getAssets().open(path);
            bufferedReader = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();

        } catch (IOException e) {
            e.printStackTrace();
            try {
                iStream.close();
                bufferedReader.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 用来获取手机拨号上网（包括CTWAP和CTNET）时由PDSN分配给手机终端的源IP地址。
     *
     * @return
     * @author SHANHY
     */
    public static String getPsdnIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }


            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            } else if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {

                //有线网络
                try {
                    // 获取本地设备的所有网络接口
                    Enumeration<NetworkInterface> enumerationNi = NetworkInterface
                            .getNetworkInterfaces();
                    while (enumerationNi.hasMoreElements()) {
                        NetworkInterface networkInterface = enumerationNi.nextElement();
                        String interfaceName = networkInterface.getDisplayName();
                        Log.i("tag", "网络名字" + interfaceName);

                        // 如果是有线网卡
                        if (interfaceName.equals("eth0")) {
                            Enumeration<InetAddress> enumIpAddr = networkInterface
                                    .getInetAddresses();

                            while (enumIpAddr.hasMoreElements()) {
                                // 返回枚举集合中的下一个IP地址信息
                                InetAddress inetAddress = enumIpAddr.nextElement();
                                // 不是回环地址，并且是ipv4的地址
                                if (!inetAddress.isLoopbackAddress()
                                        && inetAddress instanceof Inet4Address) {
                                    Log.i("tag", inetAddress.getHostAddress() + "   ");

                                    return inetAddress.getHostAddress();
                                }
                            }
                        }
                    }

                } catch (SocketException e) {
                    e.printStackTrace();
                }

            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }


    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 从assets中加载图片
     *
     * @return
     */
    public static Bitmap loadBitmap(Context context) {
        Bitmap bm = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open("test.jpg");
            BitmapFactory.Options options = new BitmapFactory.Options();
            bm = BitmapFactory.decodeStream(is, null, options);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bm;
    }
}
