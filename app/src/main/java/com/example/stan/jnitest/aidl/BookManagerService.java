package com.example.stan.jnitest.aidl;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author Stan
 * @Description
 * @Date 2025/2/12 11:31
 */
public class BookManagerService extends Service {
    private static final String TAG = "aidl##BookManagerService";

    private final Binder mBinderPool = new BinderPool.BinderPoolImpl();
    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);
    private final CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();
    private final RemoteCallbackList<IOnNewBookArriveListener> listeners = new RemoteCallbackList<>();
    private final Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @SuppressLint("LongLogTag")
        @Override
        public void registerListener(IOnNewBookArriveListener listener) throws RemoteException {
            listeners.register(listener);
            Log.d(TAG, "registerListener size:" + listeners.beginBroadcast());
            listeners.finishBroadcast();
        }

        @SuppressLint("LongLogTag")
        @Override
        public void unregisterListener(IOnNewBookArriveListener listener) throws RemoteException {

            listeners.unregister(listener);
            Log.d(TAG, "unregisterListener succeed");

            Log.d(TAG, "unregisterListener,current size:" + listeners.beginBroadcast());
            listeners.finishBroadcast();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        return mBinder;
        return mBinderPool;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mBookList.add(new Book(1, "Android"));
//        mBookList.add(new Book(2, "Ios"));
//        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed.set(true);
        super.onDestroy();
    }

    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            while (!mIsServiceDestoryed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int id = mBookList.size() + 1;
                Book newBook = new Book(id, "new book#" + id);

                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("LongLogTag")
    private void onNewBookArrived(Book newBook) throws RemoteException {
        mBookList.add(newBook);
        final int N = listeners.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArriveListener listener = listeners.getBroadcastItem(i);
            Log.d(TAG, "onNewBookArrived,notify listener:" + listener);
            if (listener != null) {
                listener.onNewBookArrived(newBook);
            }
        }
        listeners.finishBroadcast();
    }

}
