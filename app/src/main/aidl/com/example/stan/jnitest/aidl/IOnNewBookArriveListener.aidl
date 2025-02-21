// IOnNewBookArriveListener.aidl
package com.example.stan.jnitest.aidl;

// Declare any non-default types here with import statements
import com.example.stan.jnitest.aidl.Book;

interface IOnNewBookArriveListener {
   void onNewBookArrived(in Book newBook);
}