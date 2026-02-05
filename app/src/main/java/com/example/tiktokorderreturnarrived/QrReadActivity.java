package com.example.tiktokorderreturn;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tiktokorderreturn.adapter.ItemListAdapter;
import com.example.tiktokorderreturn.data.RestApi;
import com.example.tiktokorderreturn.data.RetroFit;
import com.example.tiktokorderreturn.model.ResponseDetailOrder;
import com.example.tiktokorderreturn.model.ResponseOrderReturn;
import com.example.tiktokorderreturn.model.data;
import com.example.tiktokorderreturn.model.itemListOrder;
import com.example.tiktokorderreturn.model.orders;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrReadActivity extends AppCompatActivity {

    private TextView urlTextView;
    private ListView lvItemList;
    private ArrayList<itemListOrder> listItem = new ArrayList<>();
    private ItemListAdapter listItemAdapter;
    private String order_id;
    private String tracking_number;
    private String platform;
    private String item_list;
    private LoadingDialogFragment loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_read);

        urlTextView = findViewById(R.id.urlTextView);
        lvItemList  = findViewById(R.id.lv);

        Intent intent = getIntent();
        if(intent != null) {
            order_id =  intent.getStringExtra("orderId");
            tracking_number = intent.getStringExtra("tracking_number");
            platform =  intent.getStringExtra("platform");
            urlTextView.setText(order_id);

            showLoadingDialog();
            RestApi api = RetroFit.getInstanceRetrofit();
            Call<ResponseDetailOrder> splashCall = api.getDetailOrder(order_id, tracking_number, platform);
            splashCall.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ResponseDetailOrder> call, @NonNull Response<ResponseDetailOrder> response) {

                    int code = Objects.requireNonNull(response.body()).getCode();
                    String message = Objects.requireNonNull(response.body()).getMessage();
                    String requestId = Objects.requireNonNull(response.body()).getRequest_id();
                    data datas = Objects.requireNonNull(response.body()).getData();
                    Log.e("ENTARO", code + " | " + message+ " | " + requestId);

                    ArrayList<orders> listorders = datas.getOrders();
                    listItem = new ArrayList<>();

                    item_list = "";
                    for (orders order: listorders) {
                        ArrayList<itemListOrder> itemList = order.getItemList();
                        for(itemListOrder item: itemList) {
                            listItem.add(item);
                            item_list += (item_list.length() > 0 ? ";" : "") + item.getProduct_id() + "," + item.getSku_id();
                        }
                    }

                    listItemAdapter = new ItemListAdapter(QrReadActivity.this, listItem);
                    lvItemList.setAdapter(listItemAdapter);

                    dismissLoadingDialog();
                }

                @Override
                public void onFailure(@NonNull Call<ResponseDetailOrder> call, @NonNull Throwable t) {
                    showErrorDialogDataNotFound("Error", "Data Tidak ditemukan.");
                }
            });

            // ■ 処理: ボタン QR画像保存
            ImageButton saveImageButton = findViewById(R.id.saveImageButton);
            saveImageButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showConfirmationDialog();

                }
            });
        }

        // ■ 処理: 戻るボタン
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void showSuccessDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Berhasil"); // Set the dialog title
        builder.setMessage(message); // Set the dialog message
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss(); // Close the dialog
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showWarningDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Peringatan"); // Set the dialog title
        builder.setMessage(message); // Set the dialog message
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss(); // Close the dialog
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showErrorDialogDataNotFound(String title, String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            dialog.dismiss(); // Dismiss the dialog
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();

    }
    private void showErrorDialog(String errorMessage) {
        ErrorMessageDialogFragment.newInstance(errorMessage)
                .show(getSupportFragmentManager(), "ErrorMessageDialog");
    }

    public void showLoadingDialog() {
        loadingDialog = new LoadingDialogFragment();
        loadingDialog.show(getSupportFragmentManager(), "LoadingDialogTag");
    }

    public void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isAdded()) {
            loadingDialog.dismiss();
            loadingDialog = null; // Clear the reference
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Confirmation");
        builder.setMessage("Are you sure you want to save?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLoadingDialog();
                RestApi api = RetroFit.getInstanceRetrofit();
                Call<ResponseOrderReturn> splashCall = api.updateReturn(order_id, item_list, platform);
                splashCall.enqueue(new Callback<ResponseOrderReturn>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseOrderReturn> call, @NonNull Response<ResponseOrderReturn> response) {
                        dismissLoadingDialog();

                        boolean success = Objects.requireNonNull(response.body()).getSuccess();
                        String message = Objects.requireNonNull(response.body()).getMessage();
                        if(success) {
                            boolean isReturnUpdateStok = Objects.requireNonNull(response.body()).getIsReturnUpdateStok();
                            if(isReturnUpdateStok) {
                                showSuccessDialog(message);
                            } else {
                                showWarningDialog(message);
                            }
                        } else {
                            showErrorDialog(message);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ResponseOrderReturn> call, @NonNull Throwable t) {
                        showErrorDialog("Proses simpan data gagal. Coba Lagi!");
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User cancelled exit
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}