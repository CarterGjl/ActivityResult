package com.carter.jl.activityresultlibrary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class AvoidOnResultFragment extends Fragment {

    @SuppressLint("UseSparseArrays")
    private Map<Integer,PublishSubject<ActivityResultInfo>> mSubjects = new
            HashMap<>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, AvoidOnResult.Callback> mCallbacks = new HashMap<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public Observable<ActivityResultInfo> startForResult(final Intent intent) {
        final PublishSubject<ActivityResultInfo> subject = PublishSubject.create();
        return subject.doOnSubscribe(disposable -> {
            mSubjects.put(subject.toString().length(), subject);
            startActivityForResult(intent, subject.toString().length());
        });
    }

    public void startForResult(Intent intent, AvoidOnResult.Callback callback) {

        mCallbacks.put(callback.toString().length(), callback);
        startActivityForResult(intent, callback.toString().length());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PublishSubject<ActivityResultInfo> subject = mSubjects.remove(requestCode);
        if (subject != null) {
            subject.onNext(new ActivityResultInfo(resultCode, data));
            subject.onComplete();
        }

        //callback方式的处理
        AvoidOnResult.Callback callback = mCallbacks.remove(requestCode);
        if (callback != null) {
            callback.onActivityResult( resultCode, data);
        }
    }
}
