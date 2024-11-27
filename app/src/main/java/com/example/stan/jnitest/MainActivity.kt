package com.example.stan.jnitest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.stan.jnitest.databinding.ActivityMainBinding
import com.example.stan.jnitest.jni.Encryptor
import com.example.stan.jnitest.jni.JniBitmapAction
import com.example.stan.jnitest.jni.JniNative
import com.example.stan.jnitest.utils.AssetsFileUtils
import com.example.stan.jnitest.utils.AudioUtils
import java.io.File
import java.util.*
import java.util.regex.Pattern
import android.net.Uri
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.annotation.RequiresApi
import com.example.stan.jnitest.utils.LightSettingUtil
import com.example.stan.jnitest.utils.TestUtils
import java.io.ObjectStreamClass

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var BASE_URL: String
    private val baseFileUrl by lazy {
        filesDir?.path.toString()
    }
    private val file_name = "testJni.txt"
    private lateinit var encryptPath: String
    private lateinit var lightSettingUtil: LightSettingUtil

    //多个权限
    private val requestPermissionsLauncher =
        registerForActivityResult(RequestMultiplePermissions()) {
            if (it[Manifest.permission.READ_MEDIA_IMAGES] != null && it[Manifest.permission.POST_NOTIFICATIONS] != null) {
                if (Objects.requireNonNull(it[Manifest.permission.READ_MEDIA_IMAGES]) == true) {
                    Log.d("MainActivity", "READ_MEDIA_IMAGES is get..")
                } else {
                    Log.d("MainActivity", "READ_MEDIA_IMAGES is not get..")
                }
                if (Objects.requireNonNull(it[Manifest.permission.POST_NOTIFICATIONS]) == true) {
                    Log.d("MainActivity", "POST_NOTIFICATIONS is get..")
                } else {
                    Log.d("MainActivity", "POST_NOTIFICATIONS is not get..")
                }
            }
        }

    //单个权限
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                // User allow the permission.
            } else {
                // User deny the permission.
            }
        }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n", "SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "isTaskRoot=$isTaskRoot")
        lightSettingUtil = LightSettingUtil(this)
        BASE_URL = getExternalFilesDir("file")!!.absolutePath
        Log.d("MainActivity", "BASE_URL=${BASE_URL}")
        encryptPath = BASE_URL + "encryption_" + file_name
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setPermission()
        Log.d("MainActivity", ((7000.00.toDouble() / 1000000)).toString())
        Log.d("MainActivity", "androidId=${getUniqueId(this)}")
        val pattern: Pattern = Pattern.compile("^[0|-]+$")
        if (pattern.matcher("0").matches()) {
            Log.d("MainActivity", "------------------")
        } else {
            Log.d("MainActivity", "not matches------------------")
        }
        // Example of a call to a native method
//        binding.sampleText.text = JniNative.stringFromJNI()
        val jniNative = JniNative()
        val jniBitmapAction = JniBitmapAction()
        binding.btJni1.setOnClickListener {
//            jniNative.accessField()
//            binding.sampleText.text = "after:" + jniNative.showText
            Log.d("MainActivity", TestUtils.getStringValue())
//            val intent = TestUtils.createIntentForGooglePlay(this)
//            if (intent.resolveActivity(packageManager) != null) {
//                startActivity(intent)
//            } else {
//                Toast.makeText(this, "请安装google play", Toast.LENGTH_LONG).show()
//            }
//            TestUtils.isMainTread()
//            TestUtils.testFloat()
            val age = getUserAge("500239203006302550")
            Log.d("MainActivity", "age is $age")

        }

        binding.btJni2.setOnClickListener {
            binding.sampleText.text = "authName is " + jniNative.accessMethod()
        }

        binding.btJni3.setOnClickListener {
            binding.sampleText.text = "The random value is " + jniNative.accessStaticMethod(10)
        }

        binding.btJni4.setOnClickListener {
            jniNative.arrayTest()
        }

        binding.btEncrypt.setOnClickListener {
            encryptFile()
        }
        binding.btDecrypt.setOnClickListener {
            decryptFile()
        }

        binding.btSplit.setOnClickListener {
            Encryptor.splitFile(baseFileUrl)
        }

        binding.btMerge.setOnClickListener {
            Encryptor.mergeFile(baseFileUrl)
        }

        binding.btJni5.setOnClickListener {
            Encryptor.listDirAllFile(baseFileUrl)
        }

        binding.btJni6.setOnClickListener {
            Encryptor.getPassBitmap()
        }

        binding.btJni7.setOnClickListener {
            val path = "test"
            val filename = "config.json"
            if (AssetsFileUtils.assetsFileIsExit(this, "$path/$filename")) {
                Toast.makeText(this, "$filename is exits", Toast.LENGTH_LONG).show()
                val fileStr = AssetsFileUtils.readFiles(this, "$path/$filename")
                Log.d("MainActivity", fileStr)
            }
        }

        binding.btJni8.setOnClickListener {
            val localIp = AssetsFileUtils.getIPAddress(this)
            Log.d("MainActivity", "本地ip为:$localIp")
        }
        val bitmap = AssetsFileUtils.loadBitmap(this)
        binding.ivBitmap.setImageBitmap(bitmap)
        binding.btJni9.setOnClickListener {
            val bitProcess = bitmap.copy(
                Bitmap.Config.ARGB_8888, true
            )
            jniBitmapAction.nativeProcessBitmap(bitProcess)
            binding.ivBitmapProcess.setImageBitmap(bitProcess)
        }

        binding.btAudio.setOnClickListener {
            AudioUtils.audioCheck(this)
        }

        binding.btAndroid.setOnClickListener {
            val intent = Intent(this, AndroidActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 需要权限的API才可以使用。
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // 在教育用户界面中，向用户解释为什么您的应用程序需要这个
                    // 允许特定功能按预期运行，以及什么
                    // 如果被拒绝，功能将被禁用。在此 UI 中，包括
                    // 让用户继续的“取消”或“不用了”按钮
                    // 在未授予权限的情况下使用您的应用程序。
                    Log.d("MainActivity", "向用户解释为什么您的应用程序需要这个")
                }

                else -> {
                    requestPermissionsLauncher.launch(
                        arrayOf(
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.READ_MEDIA_IMAGES
                        )
                    )
                }
            }


        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0x1024
                    )
                    return
                }
            }
        }
    }

    private fun decryptFile() {
        if (!File(encryptPath).exists()) {
            Log.d("Encryptor", "解密文件不存在")
            return
        }
        val decryptPath = encryptPath.replace("encryption_", "decryption_")
        Encryptor.decryption(encryptPath, decryptPath)
        Log.d("Encryptor", "decryption success")
    }

    private fun encryptFile() {
        val normalPath = BASE_URL + file_name
        val file = File(normalPath)
        if (!file.exists()) {
            Encryptor.createFile(normalPath)
        }
        Encryptor.encryption(normalPath, encryptPath)
        Log.d("Encryptor", "encryption success")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume")
        lightSettingUtil.startSleepTask(lightSettingUtil.danyTime)
        lightSettingUtil.setLastTimeOnTouch()
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause")
        lightSettingUtil.stopSleepTask()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("MainActivity", "dispatchTouchEvent")
        lightSettingUtil.setLastTimeOnTouch()
        if (lightSettingUtil.currentLight.toInt() == 1) {
            lightSettingUtil.startSleepTask(lightSettingUtil.danyTime)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        Log.d("MainActivity", "onNewIntent:${intent.toString()}")
        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent!!
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data
        if (appLinkData != null) {
            val recipeId = appLinkData.lastPathSegment
        }
    }

    private var pseudoId: String? = null
    fun getUniqueId(context: Context): String? {
        if (pseudoId != null) {
            return pseudoId
        }
        val sb = StringBuilder()
        sb.append(Build.BOARD.length % 10)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sb.append(Arrays.deepToString(Build.SUPPORTED_ABIS).length % 10)
        } else {
            // noinspection deprecation
            sb.append(Build.CPU_ABI.length % 10)
        }
        sb.append(Build.DEVICE.length % 10)
        sb.append(Build.DISPLAY.length % 10)
        sb.append(Build.HOST.length % 10)
        sb.append(Build.ID.length % 10)
        sb.append(Build.MANUFACTURER.length % 10)
        sb.append(Build.BRAND.length % 10)
        sb.append(Build.MODEL.length % 10)
        sb.append(Build.PRODUCT.length % 10)
        sb.append(Build.BOOTLOADER.length % 10)
        sb.append(Build.HARDWARE.length % 10)
        sb.append(Build.TAGS.length % 10)
        sb.append(Build.TYPE.length % 10)
        sb.append(Build.USER.length % 10)
        pseudoId = UUID(sb.hashCode().toLong(), Build.FINGERPRINT.hashCode().toLong()).toString()
        return pseudoId
    }

    private fun getUserAge(idCard: String): Int? {
        return try {
            // 检查身份证号码长度是否正确
            if (idCard.length != 18) {
                throw IllegalArgumentException("Invalid ID card number.")
            }

            // 提取出生年、月、日
            val year = idCard.substring(6, 10).toInt()
            val month = idCard.substring(10, 12).toInt() - 1 // Calendar月份从0开始
            val day = idCard.substring(12, 14).toInt()

            // 获取当前日期
            val today = Calendar.getInstance()

            // 设置出生日期
            val birthDate = Calendar.getInstance()
            birthDate.set(year, month, day)

            // 计算年龄
            var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
                age-- // 如果今天的日期在生日之前，则年龄减1
            }
            age
        } catch (e: Exception) {
            e.printStackTrace()
            null // 返回null表示出现错误，如身份证号格式不正确或其他异常
        }
    }

}