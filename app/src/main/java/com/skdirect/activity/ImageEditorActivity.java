package com.skdirect.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityImageEditorBinding;
import com.skdirect.filter.BitmapUtils;
import com.skdirect.filter.EditImageFragment;
import com.skdirect.filter.FiltersListFragment;
import com.skdirect.utils.Utils;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

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

public class ImageEditorActivity extends AppCompatActivity implements FiltersListFragment.FiltersListFragmentListener, EditImageFragment.EditImageFragmentListener {
    com.skdirect.activity.ImageEditorActivity activity;
    ActivityImageEditorBinding mBinding;
    Uri ImageUri;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String IMAGE_NAME = "abc.png";
    public static final int SELECT_GALLERY_IMAGE = 101;
    Bitmap originalImage;
    Bitmap filteredImage;
    Bitmap finalImage;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;
    Bitmap bitmap;
    private CommonClassForAPI commonClassForAPI;

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
            //mBinding.imagePreview.setImageURI(ImageUri);
        }

       /* try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImageUri);
        }catch (Exception e){
            e.printStackTrace();
        }*/

        originalImage = BitmapUtils.getBitmapFromAssets(this, IMAGE_NAME, 300, 300);
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        mBinding.imagePreview.setImageBitmap(originalImage);
        setupViewPager(mBinding.viewpager);
        mBinding.tabs.setupWithViewPager(mBinding.viewpager);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openImageFromGallery(ImageUri);
            }
        }, 3000);

        mBinding.tvSaveImage.setOnClickListener(view -> {
            //final String path = BitmapUtils.insertImage(getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
            /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
            finalImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String a = new String(byteArray, StandardCharsets.UTF_8);
            webView.loadUrl("javascript:getImageFile(\"" + a + "\")");*/
            finalImage = Bitmap.createScaledBitmap(finalImage,500, 500, true);
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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // adding filter list fragment
        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        // adding edit image fragment
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment, "Tab Filter");
        adapter.addFragment(editImageFragment, "Edit");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls
        resetControls();

        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        mBinding.imagePreview.setImageBitmap(filter.processFilter(filteredImage));

        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        mBinding.imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        mBinding.imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        mBinding.imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        // once the editing is done i.e seekbar is drag is completed,
        // apply the values on to filtered image
        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalImage = myFilter.processFilter(bitmap);
    }

    /**
     * Resets image edit controls to normal when new filter
     * is selected
     */
    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
            filtersListFragment.prepareThumbnail(originalImage);
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
            filtersListFragment.prepareThumbnail(originalImage);
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

}