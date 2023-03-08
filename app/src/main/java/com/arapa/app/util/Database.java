package com.arapa.app.util;

import androidx.annotation.NonNull;

import com.arapa.app.util.RequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Database {

    private FirebaseFirestore db;
    private RequestListener.OnSuccessListener successListener;
    private RequestListener.OnErrorListener errorListener;


    public Database() {
        db = FirebaseFirestore.getInstance();
    }

    public void retrieveSchools(RequestListener.OnSuccessListener successListener, RequestListener.OnErrorListener errorListener) {

        this.successListener = successListener;
        this.errorListener = errorListener;
        db.collection("Schools")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        successListener.onSuccess(task);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorListener.onError(e);
                    }
                });
    }

}
