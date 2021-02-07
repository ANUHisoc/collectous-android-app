package org.anuhisoc.collectous

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
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

    private lateinit var  navController: NavController
    private  lateinit var  appBarConfiguration :AppBarConfiguration

    private val homeNavIconOnClickListener
    get() =  View.OnClickListener {   binding.drawerLayout.open() }

    private val backIconOnClickListener
    get() = View.OnClickListener {  onBackPressed() }

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
            appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
            NavigationUI.setupWithNavController(
                    binding.topMaterialToolBar, navController, appBarConfiguration)
            /*Due to some weird default elevation*/
            binding.topMaterialToolBar.elevation = 0f
        }

        navHostFragment?.navController?.let {navController = it}

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            NavigationUI.onNavDestinationSelected(menuItem,navController)
            binding.drawerLayout.close()
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.topMaterialToolBar.run {
                Timber.d("destination id : ${destination.id}")
                if(destination.id!= R.id.homeFragment)
                    setNavigationOnClickListener(backIconOnClickListener)
                else {
                    /*Glide loaded icon disappears; need to load a temporary icon or else Text on app bar gets dislocated temporarily */
                    binding.topMaterialToolBar.setNavigationIcon(R.drawable.ic_navigation_icon)
                    loadIcon(appBarProfileTarget,resources.getInteger(R.integer.nav_icon_size))
                    setNavigationOnClickListener(homeNavIconOnClickListener)
                }
            }
        }
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



    private fun loadIcon(target: CustomTarget<Drawable>, iconSizeDp:Int) {
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