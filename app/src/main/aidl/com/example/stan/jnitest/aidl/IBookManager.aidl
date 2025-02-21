// IBookManager.aidl
package com.example.stan.jnitest.aidl;

// Declare any non-default types here with import statements
import com.example.stan.jnitest.aidl.Book;
import com.example.stan.jnitest.aidl.IOnNewBookArriveListener;

    interface IBookManager{
        List<Book> getBookList();
        void addBook(in Book book);
        void registerListener(IOnNewBookArriveListener listener);
        void unregisterListener(IOnNewBookArriveListener listener);
    }
