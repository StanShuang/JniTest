package com.example.stan.jnitest

import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stan.jnitest.android.view.MyAppWidgetProvider
import com.example.stan.jnitest.databinding.ActivityAndroidBinding
import com.example.stan.jnitest.mvvm.test.MVVMTestActivity
import com.example.stan.jnitest.recorder.RecorderSetting
import com.example.stan.jnitest.utils.AssetsFileUtils
import com.example.stan.jnitest.utils.TestUtils
import com.example.stan.jnitest.utils.datastore.preferences.EasyDataStore
import org.OpenUDID.OpenUDID_manager
import org.json.JSONException
import org.json.JSONObject
import java.math.BigInteger
import java.security.SecureRandom
import kotlin.concurrent.thread


class AndroidActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAndroidBinding
    private lateinit var myAppWidgetProvider: MyAppWidgetProvider
    private val LOG_TAG = "AndroidActivity"
    private lateinit var recorderSetting: RecorderSetting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAndroidBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recorderSetting = RecorderSetting(this)
        binding.constraint.setBackgroundResource(TestUtils.getDrawable(this))
        binding.btView.setOnClickListener {
//            val intent = Intent(this, CustomizeActivity::class.java)
//            startActivity(intent)
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            val intent: Intent? =
                packageManager.getLaunchIntentForPackage("com.supertapx.lovedots.vivo") //这里参数就是你要打开的app的包名
            intent!!.putExtra("KEY", "")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
//            val intent = Intent(Intent.ACTION_MAIN)
//            /**知道要跳转应用的包命与目标Activity*/
//            val componentName = ComponentName("com.supertapx.lovedots.vivo", "com.libii.privacypolicy.PrivacyPolicyActivity")
//            intent.component = componentName
//            //这里Intent传值
//            val bundle = Bundle()
//            bundle.putString("KEY", "你好")
//            intent.putExtras(bundle)
//            startActivity(intent)
        }

        //启动另一个app


        //注册点击事件
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.example.stan.jnitest.android.view.action.CLICK")
        myAppWidgetProvider = MyAppWidgetProvider()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(myAppWidgetProvider, intentFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(myAppWidgetProvider, intentFilter)
        }
        binding.btAnim.setBackgroundColor(0xFFFF8080.toInt())
        binding.btAnim.setOnClickListener { it ->

//            val colorAnim = ObjectAnimator.ofInt(
//                it,
//                "backgroundColor",
//                0xFFFF8080.toInt(),
//                0xFF8080FF.toInt()
//            )
//            colorAnim.duration = 3000
//            colorAnim.setEvaluator(ArgbEvaluator())
//            colorAnim.repeatCount = ValueAnimator.INFINITE
//            colorAnim.repeatMode = ValueAnimator.REVERSE
////            colorAnim.start()
//            val set = AnimatorSet()
//            set.playTogether(
//                colorAnim,
//                ObjectAnimator.ofFloat(it, "rotationX", 0F, 360F),
//                ObjectAnimator.ofFloat(it, "rotationY", 0F, 180F),
//                ObjectAnimator.ofFloat(it, "rotation", 0F, -90F),
//                ObjectAnimator.ofFloat(it, "translationX", 0F, 90F),
//                ObjectAnimator.ofFloat(it, "translationY", 0F, 90F),
//                ObjectAnimator.ofFloat(it, "scaleX", 1F, 1.5F),
//                ObjectAnimator.ofFloat(it, "scaleY", 1F, 0.5F),
//                ObjectAnimator.ofFloat(it, "alpha", 1F, 0.25F, 1F),
//            )
//            set.setDuration(5 * 1000).start()
            val start = it.width
            val end = 500
            val valueAnimator = ValueAnimator.ofInt(1, 100)
            valueAnimator.addUpdateListener { animate ->
                val currentValue = animate.animatedValue
                val fraction = animate.animatedFraction
                it.layoutParams.width = IntEvaluator().evaluate(fraction, start, end)
                it.requestLayout()
            }
            valueAnimator.setDuration(5000).start()
        }

        // add button in window
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 10, null)
            } else {
//                addButtonInWindow()
            }
        }

        binding.btThreadLocal.setOnClickListener {
            val mBooleanThreadLocal = ThreadLocal<Boolean>()
            mBooleanThreadLocal.set(true)
            thread(name = "Thread#1") {
                Log.i(LOG_TAG, "[Thread#main]mBooleanThreadLocal=" + mBooleanThreadLocal.get())
                mBooleanThreadLocal.set(false)
                Log.i(LOG_TAG, "[Thread#1]mBooleanThreadLocal=" + mBooleanThreadLocal.get())
            }
            thread(name = "Thread#2") {
                Log.i(LOG_TAG, "[Thread#2]mBooleanThreadLocal=" + mBooleanThreadLocal.get())
            }

            thread(name = "Thread#3") {
                Looper.prepare()
                val handle = Handler(Looper.myLooper()!!)
                Looper.loop()
            }


        }

        binding.btCrashTest.setOnClickListener {
            throw RuntimeException("自定义异常:这是自己抛出的异常....")
        }

        binding.btCoroutine.setOnClickListener {
//            CoroutineTest.startForAsync()
            val intent = Intent(this, MVVMTestActivity::class.java)
            startActivity(intent)
        }

        binding.btScopedStorage.setOnClickListener {
            //TODO 代码暂时还有问题
            val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                "${MediaStore.MediaColumns.DATE_ADDED} desc"
            )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    val uri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    Log.i(LOG_TAG, "$uri-----${uri.encodedPath}")
                }
                cursor.close()
            }
        }

        binding.btScopedStorageWrite.setOnClickListener {
            val bitmap = AssetsFileUtils.loadBitmap(this)
            addBitmapToAlbum(bitmap, "w2.jpg")
        }

        binding.btSendEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)/*不带附件发送邮件*/
            emailIntent.type = "plain/text"/*邮件标题*/
//            emailIntent.type = "image/*"
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Test")
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("contact@libii.com"))
            emailIntent.putExtra(Intent.EXTRA_TEXT, "This is Test Email" + "\n" + "1.0.2") //发送的内容
            emailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(Intent.createChooser(emailIntent, "分享"))
        }

        binding.btArea.setOnClickListener {
            val language = resources.configuration.locale.language
            val country = resources.configuration.locale.country
            Log.i(LOG_TAG, "Language is: $language;Country is:$country")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val language1 = resources.configuration.locales.get(0).language
                val country1 = resources.configuration.locales.get(0).country
                Log.i(LOG_TAG, "Use Locales---Language is: $language1;Country is:$country1")
            }
            val str = "onValidateInAppFailure called: {\"result\":false}"
            val childrenStr = str.substring(str.indexOf(":") + 1)
            val isInvalidReceipt = try {
                JSONObject(childrenStr)
                true
            } catch (e: JSONException) {
                e.printStackTrace()
                false
            }
            Log.i(LOG_TAG, childrenStr + "isInvalidReceipt:$isInvalidReceipt")
            Log.i(LOG_TAG, "double 精度" + TestUtils.getDouble())
        }

        binding.btSave.setOnClickListener {
            EasyDataStore.putData("name", "stan")
        }

        binding.btGet.setOnClickListener {
            val data = EasyDataStore.getData("name", "")
            Toast.makeText(this, "name:$data", Toast.LENGTH_LONG).show()
        }

        binding.btRandom.setOnClickListener {
            val value = BigInteger(64, SecureRandom()).toString(16)
            Log.i(LOG_TAG, "随机码为:$value")
        }

        binding.btKey.setOnClickListener {
            val keyMap = TestUtils.genKeyPair()
            val publicKey = TestUtils.getPublicKey(keyMap)
            val privateKey = TestUtils.getPrivateKey(keyMap)
            Log.i(LOG_TAG, "公钥:$publicKey  ;私钥:$privateKey")
        }

        binding.btStartRecorder.setOnClickListener {
            recorderSetting.startRecorder()
        }

        binding.btStopRecorder.setOnClickListener {
            recorderSetting.stopRecorder()
        }

        binding.btDeviceId.setOnClickListener {
            var deviceId = OpenUDID_manager.getOpenUDID()
            if (deviceId.isEmpty()) {
                Log.i(LOG_TAG, "OpenUDID is null,get DeviceIdentifiers")
                deviceId = TestUtils.generateDeviceIdentifiers()
            }
            Log.i(
                LOG_TAG, "deviceId:${deviceId},android_id:${
                    Settings.System.getString(
                        contentResolver, Settings.System.ANDROID_ID
                    )
                },DeviceIdentifier:${TestUtils.generateDeviceIdentifiers()}"
            )
        }

    }

    private fun addBitmapToAlbum(bitmap: Bitmap, displayName: String) {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        } else {
            values.put(
                MediaStore.MediaColumns.DATA,
                "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName"
            )
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val outputStream = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
            }
        }
        Log.i(LOG_TAG, "write success..")

    }

    private fun addButtonInWindow() {
        val mWindowManager: WindowManager =
            getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val mFloatingButton = Button(this)
        mFloatingButton.text = "通用Window添加的button"
        val mLayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            0,
            0,
            PixelFormat.TRANSLUCENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        mLayoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mLayoutParams.gravity = Gravity.LEFT or Gravity.TOP
        mLayoutParams.x = 100
        mLayoutParams.y = 300
        mWindowManager.addView(mFloatingButton, mLayoutParams)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myAppWidgetProvider)
        recorderSetting.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        recorderSetting.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        recorderSetting.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        Log.i(LOG_TAG, "onResume()")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        Log.i(LOG_TAG, "onNewIntent：${intent.toString()}")
    }
}