package org.anuhisoc.collectous;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableDecoder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.anuhisoc.collectous.databinding.ActivityMainBinding;

import java.io.File;

import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadNavigationIcon();
    }

    private void loadNavigationIcon(){
        File file = new File(getApplicationContext().getFilesDir(),"img_user_profile_picture.jpg");
        if(file.exists()) {
 /*           Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            binding.topMaterialToolBar.setNavigationIcon(drawable);*/
            Glide
                    .with(this)
                    .load(file)
                    .circleCrop()
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            Timber.d("Loading Nav Icon onto toolbar");
                            binding.topMaterialToolBar.setNavigationIcon(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            Timber.d("Nav Icon OnLoadCleared called");
                            binding.topMaterialToolBar.setNavigationIcon(R.drawable.ic_navigation_icon);
                        }
                    });
        }
    }
}