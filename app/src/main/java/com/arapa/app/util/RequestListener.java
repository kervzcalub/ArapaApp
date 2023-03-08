package com.arapa.app.util;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class RequestListener {

    public interface OnSuccessListener{
        void onSuccess(Task<QuerySnapshot> task);
    }

    public interface OnErrorListener{
        void onError(Exception e);
    }
}
