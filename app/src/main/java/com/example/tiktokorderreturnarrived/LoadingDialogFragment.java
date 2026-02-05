package com.example.tiktokorderreturn;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class LoadingDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate your custom loading layout here
        return inflater.inflate(R.layout.dialog_loading, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // You can customize the dialog's properties here, e.g., remove title
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false); // Prevent dismissal by back button or outside touch
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}