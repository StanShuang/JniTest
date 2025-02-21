package com.example.stan.jnitest.aidl;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stan.jnitest.R;

import java.util.List;

public class BookManagerActivity extends AppCompatActivity {
    private static final String TAG = "aidl##BookManagerActivity";
    private IBookManager mRemoteBookManager;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("LongLogTag")
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    Log.d(TAG, "receive new book:" + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    private ServiceConnection mConnection = new ServiceConnection() {
        @SuppressLint("LongLogTag")
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //将binder对象转换为AIDL接口
            IBookManager bookManager = IBookManager.Stub.asInterface(service);
            try {
                mRemoteBookManager = bookManager;
                List<Book> books = bookManager.getBookList();
                Log.d(TAG, "query book list, list type:" + books.getClass().getCanonicalName());
                Book newBook = new Book(3, "The Black");
                bookManager.addBook(newBook);
                List<Book> newBooksList = bookManager.getBookList();
                for (Book book : newBooksList) {
                    Log.d(TAG, "query book list:" + book.toString());
                }
                bookManager.registerListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private final IOnNewBookArriveListener mOnNewBookArrivedListener = new IOnNewBookArriveListener.Stub() {
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            mHandler.obtainMessage(1, newBook).sendToTarget();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        Intent intent = new Intent(this, BookManagerService.class);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        new Thread(this::doWork).start();
    }

    @SuppressLint("LongLogTag")
    private void doWork() {
        BinderPool binderPool = BinderPool.getInstance(this);
        IBinder securityBinder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
        ISecurityCenter mSecurityCenter = SecurityCenterImpl.asInterface(securityBinder);
        Log.d(TAG, "visit ISecurityCenter");
        String msg = "helloworld-安卓";
        try {
            String passWorld = mSecurityCenter.encrypt(msg);
            Log.d(TAG, "decrypt:" + mSecurityCenter.decypt(passWorld));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        IBinder computeBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
        ICompute mCompute = ComputeImpl.asInterface(computeBinder);
        Log.d(TAG, "visit ICompute");
        try {
            Log.d(TAG, "3+d=" + mCompute.add(3, 5));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onDestroy() {
        if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
            try {
                Log.d(TAG, "un register listener:" + mOnNewBookArrivedListener);
                mRemoteBookManager.unregisterListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}