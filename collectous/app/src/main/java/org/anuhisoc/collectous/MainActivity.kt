package org.anuhisoc.collectous

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.launch
import org.anuhisoc.collectous.databinding.ActivityMainBinding
import timber.log.Timber
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel:MainViewModel by viewModels()

    private val navDrawerProfileTarget: CustomTarget<Drawable>
        get() = object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                Timber.d("Loading Nav Icon onto toolbar")
                val imageView = binding.navigationView.getHeaderView(0).findViewById<ImageView>(R.id.profile_image_view)
                imageView.setImageDrawable(resource)
            }
            override fun onLoadCleared(placeholder: Drawable?) {
                Timber.d("Nav  Icon OnLoadCleared called")
            }
        }


    private val appBarProfileTarget: CustomTarget<Drawable>
        get() = object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                Timber.d("Loading App bar Icon onto toolbar")
                binding.topMaterialToolBar.navigationIcon = resource
            }
            override fun onLoadCleared(placeholder: Drawable?) {
                Timber.d("App bar Icon OnLoadCleared called")
                binding.topMaterialToolBar.navigationIcon = placeholder
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userInterfaceSetup()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment?
        navHostFragment?.run {
            val appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
            NavigationUI.setupWithNavController(
                    binding.topMaterialToolBar, navController, appBarConfiguration)
            /*Due to some weird default elevation*/
            binding.topMaterialToolBar.elevation = 0f
        }
        binding.topMaterialToolBar.setNavigationOnClickListener { binding.drawerLayout.open() }
    }


    private fun userInterfaceSetup() {
        loadIcon(appBarProfileTarget,resources.getInteger(R.integer.nav_icon_size))
        loadIcon(navDrawerProfileTarget,((resources.getInteger(R.integer.nav_icon_size)*1.75).toInt()))
        loadNavBarName()
    }


    private fun loadNavBarName() {
        val nameTextView = binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.name_text_view)
        lifecycleScope.launch { nameTextView.text = mainViewModel.name.await() }
    }


    private fun loadIcon(target: CustomTarget<Drawable>,iconSizeDp:Int) {
        val file = File(applicationContext.filesDir, getString(R.string.filename_profile_picture))
        if (file.exists()) {
            val dpi = resources.displayMetrics.densityDpi
            val iconSize = iconSizeDp.toPx(dpi)
            Glide.with(this)
                    .load(file)
                    .circleCrop()
                    .placeholder(R.drawable.ic_navigation_icon)
                    .apply(RequestOptions().override(iconSize, iconSize))
                    .into(target)
        }
    }
}