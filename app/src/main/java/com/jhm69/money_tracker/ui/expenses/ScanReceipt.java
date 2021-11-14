package com.jhm69.money_tracker.ui.expenses;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.jhm69.money_tracker.R;
import com.jhm69.money_tracker.entities.Category;
import com.jhm69.money_tracker.entities.Expense;
import com.jhm69.money_tracker.interfaces.IExpensesType;
import com.jhm69.money_tracker.utils.RealmManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanReceipt extends AppCompatActivity {
    ProcessCameraProvider cameraProvider;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    TextRecognizer recognizer;
    ExecutorService executor;
    ImageAnalysis imageAnalysis;
    PreviewView previewView;
    int cam_face = CameraSelector.LENS_FACING_BACK;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_receipt);

        previewView = findViewById(R.id.previewView);

        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void cameraBind() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(getApplicationContext());
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException ignored) {

            }
        }, ContextCompat.getMainExecutor(getApplicationContext()));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cam_face)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(400, 600))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //Latest frame is shown
                        .build();

        executor = Executors.newSingleThreadExecutor();
        imageAnalysis.setAnalyzer(executor, imageProxy -> {
            InputImage image = null;
            @SuppressLint({"UnsafeExperimentalUsageError", "UnsafeOptInUsageError"})
            // Camera Feed-->Analyzer-->ImageProxy-->mediaImage-->InputImage(needed for ML kit face detection)
                    Image mediaImage = imageProxy.getImage();
            if (mediaImage != null) {
                image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                System.out.println("Rotation " + imageProxy.getImageInfo().getRotationDegrees());
            }
            recognizer.process(Objects.requireNonNull(image))
                    .addOnSuccessListener(result -> {
                        String resultText = result.getText();
                        String l="";
                        if(resultText.replaceAll("^[ \t]|[\n]+$", "").matches("[a-zA-Z]+")){

                        }
                        Log.d("Scann", "bindPreview: "+ resultText);

                        if(resultText.length()!=0) {

                            int price = Integer.parseInt(resultText.replaceAll("[^0-9]", ""));
                            String[] descriptions = resultText.split(" ");

                            if(price>1) {
                                    Category receipt = new Category("Receipt", IExpensesType.MODE_EXPENSES);

                                    Expense expense = new Expense(descriptions[0], new Date(), IExpensesType.MODE_EXPENSES, receipt, price);
                                    RealmManager.getInstance().save(expense, Expense.class);
                                    finish();
                            }

                        }

                       /* for (Text.TextBlock block : result.getTextBlocks()) {
                            String blockText = block.getText();
                            Point[] blockCornerPoints = block.getCornerPoints();
                            Rect blockFrame = block.getBoundingBox();
                            for (Text.Line line : block.getLines()) {
                                String lineText = line.getText();
                                Point[] lineCornerPoints = line.getCornerPoints();
                                Rect lineFrame = line.getBoundingBox();
                                for (Text.Element element : line.getElements()) {
                                    String elementText = element.getText();
                                    Point[] elementCornerPoints = element.getCornerPoints();
                                    Rect elementFrame = element.getBoundingBox();
                                }
                            }
                        }*/

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    })
                    .addOnFailureListener(
                            e -> {
                                // Task failed with an exception
                                // ...

                            }).addOnCompleteListener(task -> imageProxy.close());
        });
        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {

        super.onStart();
        try {
            showView();
        } catch (Exception e) {
            Log.d("setUserVisibleHint", "setUserVisibleHint: " + e.getLocalizedMessage());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("WrongConstant")
    void showView() {
        previewView.setAlpha(1f);
        cameraBind();
    }

}