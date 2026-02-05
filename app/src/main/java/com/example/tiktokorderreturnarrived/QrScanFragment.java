package com.example.tiktokorderreturn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.tiktokorderreturn.data.RestApi;
import com.example.tiktokorderreturn.data.RetroFit;
import com.example.tiktokorderreturn.model.ResponseCheckOrderReturn;
import com.example.tiktokorderreturn.model.ResponseOrderReturn;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_qr_scan, container, false);
        mp_start = MediaPlayer.create(getActivity(), R.raw.start);
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
                    Toast.makeText(getActivity(),"フラッシュライトが有効ではありません。",Toast.LENGTH_SHORT).show();
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
                Call<ResponseCheckOrderReturn> checkPesanan = api.checkOrderReturn(barcodeResult.getText());
                checkPesanan.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseCheckOrderReturn> call, @NonNull Response<ResponseCheckOrderReturn> response) {
                        boolean success = Objects.requireNonNull(response.body()).getSuccess();
                        dismissLoadingDialog();
                        if(success) {

                            String orderId         = Objects.requireNonNull(response.body()).getOrderId();
                            String tracking_number = Objects.requireNonNull(response.body()).getTracking_number();
                            String platform        = Objects.requireNonNull(response.body()).getPlatform();

                            Intent intent = new Intent(getActivity().getApplicationContext(), QrReadActivity.class);
                            intent.putExtra("orderId", orderId);
                            intent.putExtra("tracking_number", tracking_number);
                            intent.putExtra("platform", platform);
                            startActivity(intent);

                        } else {
                            String message = Objects.requireNonNull(response.body()).getMessage();
                            showErrorDialog(message);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ResponseCheckOrderReturn> call, @NonNull Throwable t) {
                        dismissLoadingDialog();
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

