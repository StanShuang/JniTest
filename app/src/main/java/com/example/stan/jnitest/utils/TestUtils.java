package com.example.stan.jnitest.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import androidx.core.content.PackageManagerCompat;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;
import java.security.Key;
import java.util.UUID;

/**
 * @Author Stan
 * @Description
 * @Date 2023/8/10 11:15
 */
public class TestUtils {
    // 加密算法RSA
    public static final String KEY_ALGORITHM = "RSA";
    // 获取公钥的key
    private static final String PUBLIC_KEY = "RSAPublicKey";
    // 获取私钥的key
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    public static double getDouble() {
        double val = 18990000 / 100000.0f;
        return val;
    }

    // 生成密钥对(公钥和私钥)
    public static Map<String, Object> genKeyPair() {
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            SecureRandom secureRandom = new SecureRandom();
            keyPairGen.initialize(1024, secureRandom);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            keyMap.put(PUBLIC_KEY, publicKey);
            keyMap.put(PRIVATE_KEY, privateKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return keyMap;
    }

    public static String getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return base64Encode(key.getEncoded());
    }

    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return base64Encode(key.getEncoded());
    }


    // base64编码
    private static String base64Encode(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }

    public static String getStringValue() {
        double value = 0.000035;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("0.000000");
        decimalFormat.setDecimalFormatSymbols(symbols);
        return new BigDecimal(decimalFormat.format(value)).stripTrailingZeros().toPlainString();
    }

    private static final String GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=";
    private static final String GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending";

    public static Intent createIntentForGooglePlay(Context context) {
        String packageName = "com.lbhk.idlesurvivorfortresstycoon";
        Intent intent = new Intent(Intent.ACTION_VIEW, getGooglePlay(packageName));
        intent.setPackage(GOOGLE_PLAY_PACKAGE_NAME);
        return intent;
    }

    static Uri getGooglePlay(String packageName) {
        return packageName == null ? null : Uri.parse(GOOGLE_PLAY + packageName);
    }

    public static boolean isPackageExists(Context context, String targetPackage) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo info = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                info = packageManager.getPackageInfo(targetPackage, PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES));
            } else {
                info = packageManager.getPackageInfo(targetPackage, PackageManager.GET_ACTIVITIES);
            }
            Log.d("MainActivity", info.packageName);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void isMainTread() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                    Log.d("MainActivity", "当前是主线程-----------------");
                } else {
                    Log.d("MainActivity", Thread.currentThread().getName() + "-----------");
                }
            }
        }, 10000);
    }

    public static void testFloat() {
        long v1 = 2990000;
        float v2 = v1 / 1000000.0f;
        Log.d("MainActivity", "long-float=" + v2);
    }

    public static int getDrawable(Context context) {
        return context.getResources().getIdentifier("bitmap_hot", "drawable", context.getPackageName());
    }

    public static String generateDeviceIdentifiers() {
        int abisLength;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abisLength = Build.SUPPORTED_ABIS.length;
        } else {
            abisLength = Build.CPU_ABI.length();
        }

        String devIdShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                abisLength % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        return new UUID(devIdShort.hashCode(), Build.FINGERPRINT.hashCode()).toString();
    }

}
