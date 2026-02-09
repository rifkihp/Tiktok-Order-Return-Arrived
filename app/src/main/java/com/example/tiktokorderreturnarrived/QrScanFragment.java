package com.example.tiktokorderreturnarrived;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tiktokorderreturnarrived.data.RestApi;
import com.example.tiktokorderreturnarrived.data.RetroFit;
import com.example.tiktokorderreturnarrived.model.ResponseSetOrderReturnArrived;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrScanFragment extends Fragment {

    View view;

    private CompoundBarcodeView compoundBarcodeView;
    private CameraSettings settings;

    private boolean hasCameraFlash = false;
    private boolean flashOn = false;

    private static int  frontCameraState = 0;
    LoadingDialogFragment loadingDialog;


    MediaPlayer mp_start;
    MediaPlayer mp_error;
    MediaPlayer mp_success;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_qr_scan, container, false);
        mp_start = MediaPlayer.create(getActivity(), R.raw.start);
        mp_success = MediaPlayer.create(getActivity(), R.raw.stop);
        mp_error = MediaPlayer.create(getActivity(), R.raw.warn);

        barcodeViewSetting();
        barcodeViewStart();

        // FlashLight
        hasCameraFlash = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        ImageButton flashImageButton = view.findViewById(R.id.flashImageButton);
        flashImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasCameraFlash) {
                    if(flashOn) {
                        flashOn = false;
                        flashImageButton.setImageResource(R.drawable.icon_flash_off);
                        compoundBarcodeView.setTorchOff();
                    }else{
                        flashOn = true;
                        flashImageButton.setImageResource(R.drawable.icon_flash_on);
                        compoundBarcodeView.setTorchOn();
                    }
                } else {
                    Toast.makeText(getActivity(),"Fungsi tidak tersedia.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // FrontCamera
        ImageButton changeImageButton = view.findViewById(R.id.changeImageButton);
        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(frontCameraState == 0) {
                    frontCameraState = 1;
                    replaceFragment(new QrScanFragment());
                }else{
                    frontCameraState = 0;
                    replaceFragment(new QrScanFragment());
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        compoundBarcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        compoundBarcodeView.resume();
    }

    private void barcodeViewSetting() {

        compoundBarcodeView = (CompoundBarcodeView) view.findViewById(R.id.compaundBarcodeView);
        settings = compoundBarcodeView.getBarcodeView().getCameraSettings();
        settings.setRequestedCameraId(frontCameraState);
        compoundBarcodeView.getBarcodeView().setCameraSettings(settings);
        compoundBarcodeView.setStatusText("");
        compoundBarcodeView.resume();
        System.out.println(frontCameraState);
    }

    public void barcodeViewStart() {
        compoundBarcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult barcodeResult) {
                mp_start.start();
                showLoadingDialog();
                RestApi api = RetroFit.getInstanceRetrofit();
                Call<ResponseSetOrderReturnArrived> setOrderReturnArrived = api.setOrderReturnArrived(barcodeResult.getText());
                setOrderReturnArrived.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseSetOrderReturnArrived> call, @NonNull Response<ResponseSetOrderReturnArrived> response) {
                        boolean success = Objects.requireNonNull(response.body()).getSuccess();
                        String message = Objects.requireNonNull(response.body()).getMessage();
                        dismissLoadingDialog();
                        if(success) {
                            barcodeViewStart();
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
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> list) {
                // 処理なし
            }
        });

    }

    // ■ 設定メソッド: FrontCamera の利用時に、Fragmentを再生成する
    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();

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
                barcodeViewStart();
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

