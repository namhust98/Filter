package com.example.filter;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.filter.Image.Fragment.EditFragment;
import com.example.filter.Image.Fragment.FilterFragment;
import com.example.filter.Image.Fragment.StickerFragment;
import com.example.filter.Image.FullImageActivity;
import com.example.filter.Image.Thumbnails.ThumbnailCallback;
import com.example.filter.Image.sticker.DrawableSticker;
import com.example.filter.Image.sticker.Sticker;
import com.example.filter.Image.sticker.StickerCallback;
import com.example.filter.Image.sticker.StickerView;
import com.example.filter.Image.sticker.TextSticker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity implements ThumbnailCallback, EditFragment.EditFragmentListener, StickerCallback {

    public static Bitmap bitmap, finalbitmap, rotatebitmap, defaultbitmap;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    int w, h;
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;
    private ImageView imgView, rotateleft, rotateright;
    private Uri image_uri;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private RelativeLayout title;
    private LinearLayout openCameraTitle, chooseImageTitle, cropimage, saveimage, resetdefault, removesticker, fullimage;
    private String timestamp = "";
    private StickerView stickerView;
    private RelativeLayout image;
    private int flagCrop = 0, flagSticker = 0;
    private EditFragment editFragment = new EditFragment();
    private FilterFragment filterFragment = new FilterFragment();
    private StickerFragment stickerFragment = new StickerFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imgView = findViewById(R.id.place_holder_imageview);
        viewPager = findViewById(R.id.myViewPager);
        tabLayout = findViewById(R.id.myTabLayout);
        title = findViewById(R.id.title);
        stickerView = findViewById(R.id.stickerview);
        cropimage = findViewById(R.id.cropImage);
        image = findViewById(R.id.myImage);
        saveimage = findViewById(R.id.saveImage);
        openCameraTitle = findViewById(R.id.openCamera_title);
        chooseImageTitle = findViewById(R.id.chooseImage_title);
        resetdefault = findViewById(R.id.resetDefault);
        removesticker = findViewById(R.id.removeSticker);
        rotateleft = findViewById(R.id.rotateleft);
        rotateright = findViewById(R.id.rotateright);
        fullimage = findViewById(R.id.fullImage);

        fullimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalbitmap != null) {

                    String path = insertImage(getContentResolver(), stickerView.createBitmap(), timestamp);
                    Intent intent = new Intent(ImageActivity.this, FullImageActivity.class);
                    intent.putExtra("path", path);
                    startActivity(intent);

                } else
                    Toast.makeText(ImageActivity.this, "Hiện chưa có ảnh được chọn", Toast.LENGTH_SHORT).show();
            }
        });

        rotateleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalbitmap != null) {
                    rotatebitmap = rotateLeft(finalbitmap);
                    finalbitmap = rotatebitmap;
                    imgView.setImageBitmap(finalbitmap);
                    initViewPager();
                } else
                    Toast.makeText(ImageActivity.this, "Hiện chưa có ảnh được chọn", Toast.LENGTH_SHORT).show();
            }
        });

        rotateright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalbitmap != null) {
                    rotatebitmap = rotateRight(finalbitmap);
                    finalbitmap = rotatebitmap;
                    imgView.setImageBitmap(finalbitmap);
                    initViewPager();
                } else
                    Toast.makeText(ImageActivity.this, "Hiện chưa có ảnh được chọn", Toast.LENGTH_SHORT).show();
            }
        });

        removesticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalbitmap != null) {
                    stickerView.removeAllStickers();
                    flagSticker = 0;
                } else
                    Toast.makeText(ImageActivity.this, "Hiện chưa có ảnh được chọn", Toast.LENGTH_SHORT).show();
            }
        });

        resetdefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalbitmap != null) {
                    stickerView.removeAllStickers();
                    editFragment.resetControls();
                    flagSticker = 0;
                    finalbitmap = Bitmap.createScaledBitmap(defaultbitmap, 1200, 1200, true);
                    imgView.setImageBitmap(finalbitmap);
                    initViewPager();
                } else
                    Toast.makeText(ImageActivity.this, "Hiện chưa có ảnh được chọn", Toast.LENGTH_SHORT).show();
            }
        });

        openCameraTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, 10);
                    } else
                        openCamera();
                }
            }
        });

        chooseImageTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, 10);
                    } else
                        chooseImage();
                }
            }
        });

        cropimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalbitmap != null) {
                    cropImage();
                } else
                    Toast.makeText(ImageActivity.this, "Hiện chưa có ảnh được chọn", Toast.LENGTH_SHORT).show();
            }
        });

        saveimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalbitmap != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission, 10);
                        } else {
                            saveImage();
                        }
                    }
                } else
                    Toast.makeText(ImageActivity.this, "Hiện chưa có ảnh được chọn", Toast.LENGTH_SHORT).show();
            }
        });
        setupSticker();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.opencamera) {
            title.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, 10);
                } else
                    openCamera();
            }
        } else if (item.getItemId() == R.id.chooseimage) {
            title.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, 10);
                } else
                    chooseImage();
            }
        } else if (item.getItemId() == R.id.useimage) {
            Intent intent = new Intent(ImageActivity.this, VideoActivity.class);
            startActivity(intent);
        }
        return true;
    }

    private void initViewPager() {
        editFragment.setListener(this);
        stickerFragment.setStickerCallback(this);

        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainViewPagerAdapter.addFragment(filterFragment, getString(R.string.tab_filters));
        mainViewPagerAdapter.addFragment(editFragment, getString(R.string.tab_edit));
        mainViewPagerAdapter.addFragment(stickerFragment, getString(R.string.tab_sticker));
        viewPager.setAdapter(mainViewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else
                    Toast.makeText(ImageActivity.this, "Từ chối cấp quyền, vui lòng cấp quyền để ứng dụng có thể truy cập và chụp ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 20) && (resultCode == RESULT_OK)) {
            if (bitmap != null) {
                onNewImage();
            }
            visibleView();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), image_uri);
                rotatebitmap = rotateBitmap(bitmap);
                defaultbitmap = rotatebitmap;
                finalbitmap = Bitmap.createScaledBitmap(rotatebitmap, 1200, 1200, true);

            } catch (IOException e) {
                e.printStackTrace();
            }
            imgView.setImageBitmap(finalbitmap);

            initViewPager();

            AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
            builder.setTitle("Kích thước ảnh không đúng!");
            builder.setMessage("Nếu kích thước ảnh không đúng với khung, khi chỉnh sửa sẽ làm ảnh bị méo. Bạn có muốn cắt ảnh?");
            builder.setCancelable(false);
            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setNeutralButton("Cắt Ảnh", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cropImage();
                }
            });
            builder.create().show();

        } else if ((requestCode == 21) && (resultCode == RESULT_OK)) {
            if (bitmap != null) {
                onNewImage();
            }
            visibleView();
            if (data != null) {
                image_uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), image_uri);
                    rotatebitmap = rotateBitmap(bitmap);
                    defaultbitmap = rotatebitmap;
                    finalbitmap = Bitmap.createScaledBitmap(rotatebitmap, 1200, 1200, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgView.setImageBitmap(finalbitmap);

                initViewPager();

                if (bitmap.getWidth() != bitmap.getHeight()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
                    builder.setTitle("Kích thước ảnh không đúng!");
                    builder.setMessage("Nếu kích thước ảnh không đúng với khung, khi chỉnh sửa sẽ làm ảnh bị méo. Bạn có muốn cắt ảnh?");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setNeutralButton("Cắt Ảnh", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cropImage();
                        }
                    });
                    builder.create().show();
                }
            }
        } else if ((requestCode == 30) && (resultCode == RESULT_OK)) {
            if (data != null) {
                image_uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), image_uri);
                    finalbitmap = Bitmap.createScaledBitmap(bitmap, 1200, 1200, true);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                stickerView.removeAllStickers();
                flagSticker = 0;
                imgView.setImageBitmap(finalbitmap);
                initViewPager();
            }
        }
    }

    private void cropImage() {
        flagCrop = 1;
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        if (flagSticker == 1) {
            String path = insertImage(getContentResolver(), stickerView.createBitmap(), timestamp);
            cropIntent.setDataAndType(Uri.parse(path), "image/*");
        } else {
            cropIntent.setDataAndType(image_uri, "image/*");
        }

        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 3);
        cropIntent.putExtra("aspectY", 3);
        cropIntent.putExtra("scaleUpIfNeeded", false);
        cropIntent.putExtra("return-data", true);

        startActivityForResult(cropIntent, 30);
    }

    private void chooseImage() {
        flagCrop = 0;
        Intent chooseIntent = new Intent(Intent.ACTION_PICK);
        chooseIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(chooseIntent, "Chọn ảnh từ: "), 21);
    }

    private void openCamera() {
        flagCrop = 0;
        if (timestamp.equals("")) {
            timestamp = String.valueOf(System.currentTimeMillis());
        }
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp);
        values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp);

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, 20);
    }

    private void saveImage() {

        if (timestamp.equals("")) {
            timestamp = String.valueOf(System.currentTimeMillis());
        }

        final String path = insertImage(getContentResolver(), stickerView.createBitmap(), timestamp);
        View view = findViewById(R.id.main_layout);
        if (!TextUtils.isEmpty(path)) {
            Snackbar snackbar = Snackbar
                    .make(view, "Ảnh đã được lưu!", Snackbar.LENGTH_LONG)
                    .setAction("OPEN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setType("image/*");
                            startActivity(intent);
                        }
                    });

            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar
                    .make(view, "Có lỗi xảy ra, ảnh chưa được lưu!", Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    @Override
    public void onThumbnailClick(Filter filter) {
        if (flagCrop == 1) {
            finalbitmap = Bitmap.createScaledBitmap(bitmap, 1200, 1200, true);
        } else {
            finalbitmap = Bitmap.createScaledBitmap(rotatebitmap, 1200, 1200, true);
        }
        imgView.setImageBitmap(filter.processFilter(finalbitmap));
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        if (flagCrop == 1) {
            finalbitmap = Bitmap.createScaledBitmap(bitmap, 1200, 1200, true);
        } else {
            finalbitmap = Bitmap.createScaledBitmap(rotatebitmap, 1200, 1200, true);
        }
        imgView.setImageBitmap(myFilter.processFilter(finalbitmap));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        if (flagCrop == 1) {
            finalbitmap = Bitmap.createScaledBitmap(bitmap, 1200, 1200, true);
        } else {
            finalbitmap = Bitmap.createScaledBitmap(rotatebitmap, 1200, 1200, true);
        }
        imgView.setImageBitmap(myFilter.processFilter(finalbitmap));
    }

    @Override
    public void onContrastChanged(float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        if (flagCrop == 1) {
            finalbitmap = Bitmap.createScaledBitmap(bitmap, 1200, 1200, true);
        } else {
            finalbitmap = Bitmap.createScaledBitmap(rotatebitmap, 1200, 1200, true);
        }
        imgView.setImageBitmap(myFilter.processFilter(finalbitmap));
    }

    @Override
    public void onEditStarted() {
    }

    @Override
    public void onEditCompleted() {
        if (flagCrop == 1) {
            finalbitmap = Bitmap.createScaledBitmap(bitmap, 1200, 1200, true);
        } else {
            finalbitmap = Bitmap.createScaledBitmap(rotatebitmap, 1200, 1200, true);
        }
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        imgView.setImageBitmap(myFilter.processFilter(finalbitmap));
    }

    protected String insertImage(ContentResolver cr, Bitmap source, String timestamp) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp);
        values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp);

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    protected Bitmap storeThumbnail(ContentResolver cr, Bitmap source, long id, float width, float height, int kind) {

        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND, kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    protected Bitmap rotateLeft(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap rotate = Bitmap.createBitmap(bitmap, 0, 0, 1200, 1200, matrix, true);
        return rotate;
    }

    protected Bitmap rotateRight(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotate = Bitmap.createBitmap(bitmap, 0, 0, 1200, 1200, matrix, true);
        return rotate;
    }

    protected Bitmap rotateBitmap(Bitmap bitmap) {
        w = bitmap.getWidth();
        h = bitmap.getHeight();
        if (w > h) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotate = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
            return rotate;
        } else {
            return bitmap;
        }
    }

    protected void onNewImage() {
        stickerView.removeAllStickers();
        flagSticker = 0;
        flagCrop = 0;
    }

    protected void setupSticker() {

        stickerView.setBackgroundColor(Color.WHITE);
        stickerView.setLocked(false);
        stickerView.setConstrained(true);

        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {
                //stickerView.removeAllSticker();
                if (sticker instanceof TextSticker) {
                    ((TextSticker) sticker).setTextColor(Color.RED);
                    stickerView.replace(sticker);
                    stickerView.invalidate();
                }
            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
            }

            @Override
            public void onStickerTouchedDown(@NonNull Sticker sticker) {
            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {
            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {
            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
            }
        });
    }

    protected void visibleView() {
        title.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        image.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStickerClick(int drawableId) {
        flagSticker = 1;
        Drawable drawable =
                ContextCompat.getDrawable(this, drawableId);
        stickerView.addSticker(new DrawableSticker(drawable));
    }

    @Override
    public void onTextStickerClick(int drawableId) {
        flagSticker = 1;

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ImageActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.text_sticker_dialog, null);

        EditText text = mView.findViewById(R.id.edittext);
        ImageView black = mView.findViewById(R.id.black);
        ImageView white = mView.findViewById(R.id.white);
        ImageView blue = mView.findViewById(R.id.blue);
        ImageView red = mView.findViewById(R.id.red);
        ImageView yellow = mView.findViewById(R.id.yellow);

        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setTextColor(getResources().getColor(R.color.black));
            }
        });

        white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setTextColor(getResources().getColor(R.color.white));
            }
        });

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setTextColor(getResources().getColor(R.color.blue));
            }
        });

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setTextColor(getResources().getColor(R.color.red));
            }
        });

        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setTextColor(getResources().getColor(R.color.yellow));
            }
        });

        mBuilder.setView(mView);
        mBuilder.setTitle("Vui lòng nhập từ bạn muốn viết vào sticker");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String edttext = text.getText().toString();
                Drawable bubble = ContextCompat.getDrawable(ImageActivity.this, drawableId);
                stickerView.addSticker(
                        new TextSticker(getApplicationContext())
                                .setDrawable(bubble)
                                .setText(edttext)
                                .setMaxTextSize(24)
                                .setTextColor(text.getCurrentTextColor())
                                .resizeText()
                        , Sticker.Position.TOP);
                dialog.dismiss();
            }
        });

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.create().show();
    }

    @Override
    public void onTextStickerClick() {
        flagSticker = 1;

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ImageActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.text_sticker_dialog, null);

        EditText text = mView.findViewById(R.id.edittext);
        ImageView black = mView.findViewById(R.id.black);
        ImageView white = mView.findViewById(R.id.white);
        ImageView blue = mView.findViewById(R.id.blue);
        ImageView red = mView.findViewById(R.id.red);
        ImageView yellow = mView.findViewById(R.id.yellow);

        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setTextColor(getResources().getColor(R.color.black));
            }
        });

        white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setTextColor(getResources().getColor(R.color.white));
            }
        });

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setTextColor(getResources().getColor(R.color.blue));
            }
        });

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setTextColor(getResources().getColor(R.color.red));
            }
        });

        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setTextColor(getResources().getColor(R.color.yellow));
            }
        });

        mBuilder.setView(mView);
        mBuilder.setTitle("Vui lòng nhập từ bạn muốn viết");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String edttext = text.getText().toString();
                stickerView.addSticker(
                        new TextSticker(getApplicationContext())
                                .setText(edttext)
                                .setMaxTextSize(120)
                                .setTextColor(text.getCurrentTextColor())
                                .resizeText()
                        , Sticker.Position.TOP);
                dialog.dismiss();
            }
        });

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.create().show();
    }

    class MainViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> arrayFragment = new ArrayList<>();
        private ArrayList<String> arrayTitle = new ArrayList<>();

        private MainViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return arrayFragment.get(position);
        }

        @Override
        public int getCount() {
            return arrayFragment.size();
        }

        private void addFragment(Fragment fragment, String title) {
            arrayFragment.add(fragment);
            arrayTitle.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return arrayTitle.get(position);
        }

    }

}
