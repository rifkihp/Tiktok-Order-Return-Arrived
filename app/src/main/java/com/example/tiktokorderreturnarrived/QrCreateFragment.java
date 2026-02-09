package com.example.tiktokorderreturnarrived;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tiktokorderreturnarrived.data.RestApi;
import com.example.tiktokorderreturnarrived.data.RetroFit;
import com.example.tiktokorderreturnarrived.model.ResponseSetOrderReturnArrived;
import com.google.zxing.WriterException;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QrCreateFragment extends Fragment {

    View view;
    TextView urlTextView_qfc;
    LoadingDialogFragment loadingDialog;


    MediaPlayer mp_start;
    MediaPlayer mp_error;
    MediaPlayer mp_success;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_qr_create, container, false);


        mp_start = MediaPlayer.create(getActivity(), R.raw.start);
        mp_success = MediaPlayer.create(getActivity(), R.raw.stop);
        mp_error = MediaPlayer.create(getActivity(), R.raw.warn);

        urlTextView_qfc = view.findViewById(R.id.urlEditText_qcf);
        //urlTextView_qfc.setText("JX5428230056");

        Button createButton_qcf = view.findViewById(R.id.createButton_qcf);
        createButton_qcf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (urlTextView_qfc.length() > 0) {

                    mp_start.start();
                    showLoadingDialog();
                    RestApi api = RetroFit.getInstanceRetrofit();
                    Call<ResponseSetOrderReturnArrived> setOrderReturnArrived = api.setOrderReturnArrived(urlTextView_qfc.getText().toString());
                    setOrderReturnArrived.enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseSetOrderReturnArrived> call, @NonNull Response<ResponseSetOrderReturnArrived> response) {
                            boolean success = Objects.requireNonNull(response.body()).getSuccess();
                            String message = Objects.requireNonNull(response.body()).getMessage();
                            dismissLoadingDialog();
                            if(success) {
                                urlTextView_qfc.setText("");
                                mp_success.start();
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            } else {
                                mp_error.start();
                                showErrorDialog(message);
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<ResponseSetOrderReturnArrived> call, @NonNull Throwable t) {
                            dismissLoadingDialog();
                            mp_error.start();
                            showErrorDialog("PROSES GAGAL, COBA LAGI!");
                        }
                    });

                } else {
                    showErrorDialog("No. Resi Belum diisi!");
                }
            }
        });

        return view;
    }

    private void showErrorDialog(String errorMessage) {
        //barcodeViewStart();
        //ErrorMessageDialogFragment.newInstance(errorMessage)
        //        .show(getChildFragmentManager(), "ErrorMessageDialog");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Error");
        alertDialogBuilder.setMessage(errorMessage);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();
    }

    public void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialogFragment();
        }
        // Use getChildFragmentManager() if the dialog should be managed within this Fragment's lifecycle
        // Use getParentFragmentManager() if the dialog should be managed by the parent Activity
        loadingDialog.show(getChildFragmentManager(), "loading_dialog_tag");
    }

    public void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null; // Clear the reference after dismissal
        }
    }
}