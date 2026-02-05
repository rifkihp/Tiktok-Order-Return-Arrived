package com.example.tiktokorderreturn;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ErrorMessageDialogFragment extends DialogFragment {

    private static final String ARG_MESSAGE = "message";
    private String errorMessage;

    public static ErrorMessageDialogFragment newInstance(String message) {
        ErrorMessageDialogFragment fragment = new ErrorMessageDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            errorMessage = getArguments().getString(ARG_MESSAGE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Error")
                .setMessage(errorMessage)
                .setPositiveButton("OK", (dialog, id) -> {
                    // Optional: Handle OK button click, e.g., dismiss dialog
                    dialog.dismiss();
                });
        return builder.create();
    }
}