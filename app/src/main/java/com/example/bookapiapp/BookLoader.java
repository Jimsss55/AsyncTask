package com.example.bookapiapp;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;
//import android.support.v4.content.AsyncTaskLoader;

public class BookLoader extends AsyncTaskLoader<String> {
    private String mQueryString;

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    public BookLoader(@NonNull Context context, String queryString){
        super(context);
        mQueryString = queryString;

    }

    @Nullable
    @Override
    public String loadInBackground() {
        return NetworkUtils.getBookInfo(mQueryString);
    }
}
