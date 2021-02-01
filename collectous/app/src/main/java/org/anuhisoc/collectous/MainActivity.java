package org.anuhisoc.collectous;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
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

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            AppBarConfiguration appBarConfiguration =
                    new AppBarConfiguration.Builder(navController.getGraph()).build();
            NavigationUI.setupWithNavController(
                    binding.topMaterialToolBar, navController, appBarConfiguration);

            /*Due to some weird default elevation*/
            binding.topMaterialToolBar.setElevation(0);
        }



    }

    private void loadNavigationIcon(){
        File file = new File(getApplicationContext().getFilesDir(),getString(R.string.filename_profile_picture));
        if(file.exists()) {
            CustomTarget<Drawable> target = new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    Timber.d("Loading Nav Icon onto toolbar");
                    binding.topMaterialToolBar.setNavigationIcon(resource);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    Timber.d("Nav Icon OnLoadCleared called");
                    binding.topMaterialToolBar.setNavigationIcon(placeholder);
                }
            };

            int dpi = getResources().getDisplayMetrics().densityDpi;
            int iconSize = UtilKt.toPx(getResources().getInteger(R.integer.nav_icon_size),dpi);
            Glide.with(this)
                    .load(file)
                    .circleCrop()
                    .placeholder(R.drawable.ic_navigation_icon)
                    .apply(new RequestOptions().override(iconSize,iconSize))
                    .into(target);
        }
    }



}
