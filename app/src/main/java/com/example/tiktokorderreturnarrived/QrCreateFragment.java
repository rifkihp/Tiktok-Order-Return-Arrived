package com.example.tiktokorderreturn;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tiktokorderreturn.data.RestApi;
import com.example.tiktokorderreturn.data.RetroFit;
import com.example.tiktokorderreturn.model.ResponseCheckOrderReturn;
import com.example.tiktokorderreturn.model.ResponseOrderReturn;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QrCreateFragment extends Fragment {

    View view;
    ImageView qrImageView_qfc;
    TextView urlTextView_qfc;
    LoadingDialogFragment loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_qr_create, container, false);

        // ■ ボタン処理: URLからQRコード生成
        qrImageView_qfc = view.findViewById(R.id.qrImageView_qcf);
        urlTextView_qfc = view.findViewById(R.id.urlEditText_qcf);
        //urlTextView_qfc.setText("JX5428230056");

        Button createButton_qcf = view.findViewById(R.id.createButton_qcf);
        createButton_qcf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // urlEditText_qfcに文字が入力されているか判定
                if (urlTextView_qfc.length() > 0) {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                    // 文字が入っている場合（画像生成）
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(urlTextView_qfc.getText().toString(), BarcodeFormat.QR_CODE,300,300);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmapCreate = barcodeEncoder.createBitmap(bitMatrix);

                        qrImageView_qfc.setImageBitmap(bitmapCreate);
                        showLoadingDialog();
                        RestApi api = RetroFit.getInstanceRetrofit();
                        Call<ResponseCheckOrderReturn> splashCall = api.checkOrderReturn(urlTextView_qfc.getText().toString());
                        splashCall.enqueue(new Callback<ResponseCheckOrderReturn>() {
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

                        // 例外処理
                    }catch(WriterException e){
                        throw new RuntimeException(e);
                    }

                    // 文字が入っていない場合
                }else{
                    Toast.makeText(getActivity(),"テキストを入力してください。",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ■ ボタン処理: QR画像保存
        /*ImageButton saveImageButton_qcf = view.findViewById(R.id.saveImageButton_qcf);
        saveImageButton_qcf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // saveImageView_qfc に画像が生成されているかの判定
                try{
                    Bitmap bitmap = ((BitmapDrawable) qrImageView_qfc.getDrawable()).getBitmap();

                    // 生成されている場合
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) qrImageView_qfc.getDrawable();
                    Bitmap bitmapSave = bitmapDrawable.getBitmap();

                    FileOutputStream fileOutputStream = null;
                    File sdCard = Environment.getExternalStorageDirectory();
                    File Directory = new File(sdCard.getAbsolutePath() + "/Download");
                    Directory.mkdir();

                    @SuppressLint("DefaultLocale") String filename = String.format("%d.jpg",System.currentTimeMillis());
                    File outfile = new File(Directory,filename);

                    // 画像生成
                    try {
                        fileOutputStream = new FileOutputStream(outfile);
                        bitmapSave.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(Uri.fromFile(outfile));
                        getActivity().sendBroadcast(intent);

                        Toast.makeText(getActivity(),"画像を保存しました。",Toast.LENGTH_SHORT).show();

                        // 例外処理
                    } catch(IOException e){
                        e.printStackTrace();
                        Toast.makeText(getActivity(),"画像の保存に失敗しました。",Toast.LENGTH_SHORT).show();
                    }
                }

                // 生成されていない場合
                catch(NullPointerException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"画像が存在しません。",Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        // ■ ボタン処理: テキストコピー
        /*ImageButton copyImageButton_qcf = view.findViewById(R.id.copyImageButton_qcf);
        copyImageButton_qcf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (urlTextView_qfc.length() > 0) {

                    ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("GET_TEXT", urlTextView_qfc.getText().toString());
                    if(clipboardManager == null) {

                        Toast.makeText(getActivity(),"テキストのコピーに失敗しました。",Toast.LENGTH_SHORT).show();
                    }else{
                        clipboardManager.setPrimaryClip(clip);
                        clip.getDescription();

                        Toast.makeText(getActivity(),"テキストをコピーしました。",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getActivity(),"テキストを入力してください。",Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        /*ImageButton openImageBtn_qcf = view.findViewById(R.id.openImageButton_qcf);
        openImageBtn_qcf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Uri uri = Uri.parse(urlTextView_qfc.getText().toString());
                    Intent iUrl = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(iUrl);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "URLの認識に失敗しました。", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        return view;
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

    private void showErrorDialog(String errorMessage) {
        ErrorMessageDialogFragment.newInstance(errorMessage)
                .show(getChildFragmentManager(), "ErrorMessageDialog");
    }
}