package com.skdirect.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityImageEditorBinding;
import com.skdirect.filter.BitmapUtils;
import com.skdirect.filter.SpacesItemDecoration;
import com.skdirect.filter.ThumbnailsAdapter;
import com.skdirect.utils.Utils;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageEditorActivity extends AppCompatActivity implements ThumbnailsAdapter.ThumbnailsAdapterListener, SeekBar.OnSeekBarChangeListener {
    ImageEditorActivity activity;
    ActivityImageEditorBinding mBinding;
    Uri ImageUri;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String IMAGE_NAME = "abc.png";
    public static final int SELECT_GALLERY_IMAGE = 101;
    Bitmap originalImage;
    Bitmap filteredImage;
    Bitmap finalImage;

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;
    private CommonClassForAPI commonClassForAPI;
    ThumbnailsAdapter mAdapter;
    List<ThumbnailItem> thumbnailItemList;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_editor);
        activity = this;
        commonClassForAPI = CommonClassForAPI.getInstance();
        initViews();

    }

    private void initViews() {
        Intent i = getIntent();
        if (i != null) {
            ImageUri = Uri.parse(i.getStringExtra("ImageUri"));
        }


        thumbnailItemList = new ArrayList<>();
        mAdapter = new ThumbnailsAdapter(activity, thumbnailItemList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        mBinding.recyclerViewFilter.setLayoutManager(mLayoutManager);
        mBinding.recyclerViewFilter.setItemAnimator(new DefaultItemAnimator());
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getResources().getDisplayMetrics());
        mBinding.recyclerViewFilter.addItemDecoration(new SpacesItemDecoration(space));
        mBinding.recyclerViewFilter.setAdapter(mAdapter);

        originalImage = BitmapUtils.getBitmapFromAssets(this, IMAGE_NAME, 300, 300);
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        mBinding.imagePreview.setImageBitmap(originalImage);
        openImageFromGallery(ImageUri);


        mBinding.seekbarBrightness.setMax(200);
        mBinding.seekbarBrightness.setProgress(100);

        // keeping contrast value b/w 1.0 - 3.0
        mBinding.seekbarContrast.setMax(20);
        mBinding.seekbarContrast.setProgress(0);

        // keeping saturation value b/w 0.0 - 3.0
        mBinding.seekbarSaturation.setMax(30);
        mBinding.seekbarSaturation.setProgress(10);

        mBinding.seekbarBrightness.setOnSeekBarChangeListener(this);
        mBinding.seekbarContrast.setOnSeekBarChangeListener(this);
        mBinding.seekbarSaturation.setOnSeekBarChangeListener(this);

        mBinding.imBack.setOnClickListener(view -> {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        });
        mBinding.tvSaveImage.setOnClickListener(view -> {
            finalImage = Bitmap.createScaledBitmap(finalImage, 500, 500, true);
            uploadMultipart(SavedImages(finalImage));
        });
    }

    private String SavedImages(Bitmap bm) {
        String fileName = System.currentTimeMillis() + "_profile.jpg";
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Direct");
        myDir.mkdirs();
        File file = new File(myDir, fileName);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root + "/Direct/" + fileName;
    }


    @Override
    public void onFilterSelected(Filter filter) {
        resetControls();
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        mBinding.imagePreview.setImageBitmap(filter.processFilter(filteredImage));
        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    private void resetControls() {
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_GALLERY_IMAGE) {
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 300, 300);

            // clear bitmap memory
            originalImage.recycle();
            finalImage.recycle();
            finalImage.recycle();

            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            mBinding.imagePreview.setImageBitmap(originalImage);
            bitmap.recycle();
            // render selected image thumbnails
            prepareThumbnail(originalImage);
        }
    }

    private void openImageFromGallery(Uri uri) {
        try {
            //Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, uri, 800, 800);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 3;
            File file = new File(new URI(String.valueOf(uri)));
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            // clear bitmap memory
            originalImage.recycle();
            finalImage.recycle();
            finalImage.recycle();

            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            mBinding.imagePreview.setImageBitmap(originalImage);
            bitmap.recycle();
            // render selected image thumbnails
            prepareThumbnail(originalImage);
        /*Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_GALLERY_IMAGE);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("CheckResult")
    private void uploadMultipart(String path) {
        final File fileToUpload = new File(path);
        new Compressor(this)
                .compressToFileAsFlowable(fileToUpload)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::uploadImagePath, throwable -> {
                    throwable.printStackTrace();
                    showError(throwable.getMessage());
                });
    }

    private void showError(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void uploadImagePath(File file) {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("Image", file.getName(), requestFile);
        Utils.showProgressDialog(this);
        commonClassForAPI.uploadImage(imageObserver, body);
    }

    // upload image
    private final DisposableObserver<String> imageObserver = new DisposableObserver<String>() {
        @Override
        public void onNext(@NotNull String response) {
            try {
                Utils.hideProgressDialog();
                if (response == null) {
                    Log.e("Failed", "Failed ####  " + response);
                    Utils.setToast(getApplicationContext(), "Image Not Uploaded");
                } else {
                    Log.e("Uploaded - ", "" + response);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("image", response);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            Utils.hideProgressDialog();
        }

        @Override
        public void onComplete() {
            Utils.hideProgressDialog();
        }
    };

    public void prepareThumbnail(final Bitmap bitmap) {
        Runnable r = new Runnable() {
            public void run() {
                Bitmap thumbImage;
                if (bitmap == null) {
                    thumbImage = BitmapUtils.getBitmapFromAssets(activity, ImageEditorActivity.IMAGE_NAME, 300, 300);
                } else {
                    thumbImage = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
                }
                if (thumbImage == null)
                    return;
                ThumbnailsManager.clearThumbs();
                thumbnailItemList.clear();
                // add normal bitmap first
                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thumbImage;
                thumbnailItem.filterName = "Filter Normal";
                ThumbnailsManager.addThumb(thumbnailItem);
                List<Filter> filters = FilterPack.getFilterPack(activity);
                for (Filter filter : filters) {
                    ThumbnailItem tI = new ThumbnailItem();
                    tI.image = thumbImage;
                    tI.filter = filter;
                    tI.filterName = filter.getName();
                    ThumbnailsManager.addThumb(tI);
                }

                thumbnailItemList.addAll(ThumbnailsManager.processThumbs(activity));

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        };

        new Thread(r).start();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if (seekBar.getId() == R.id.seekbar_brightness) {
            brightnessFinal = progress - 100;
            Filter myFilter = new Filter();
            myFilter.addSubFilter(new BrightnessSubFilter(progress - 100));
            mBinding.imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
        }
        if (seekBar.getId() == R.id.seekbar_contrast) {
            progress += 10;
            float floatVal = .10f * progress;
            contrastFinal = floatVal;
            Filter myFilter = new Filter();
            myFilter.addSubFilter(new ContrastSubFilter(floatVal));
            mBinding.imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
        }
        if (seekBar.getId() == R.id.seekbar_saturation) {
            float floatVal = .10f * progress;
            saturationFinal = floatVal;
            Filter myFilter = new Filter();
            myFilter.addSubFilter(new SaturationSubfilter(floatVal));
            mBinding.imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalImage = myFilter.processFilter(bitmap);
    }
}