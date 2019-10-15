package com.openfarmanager.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.openfarmanager.android.filesystempanel.Panel
import com.openfarmanager.android.filesystempanel.vm.BottomBarVM
import com.openfarmanager.android.filesystempanel.vm.MainViewVM
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.main_one_panel.*
import java.text.FieldPosition
import javax.inject.Inject

class Main : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mainVM: MainViewVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.main_one_panel)

        mainVM = ViewModelProviders.of(this, viewModelFactory).get(MainViewVM::class.java)

        val leftPanelSelection = leftPanelSelector
        val rightPanelSelection = rightPanelSelector

        panels.adapter = TabsAdapter(supportFragmentManager)
        panels.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val isLeftPanel = position == 0

                mainVM.requestFocus(if (isLeftPanel) Panel.POSITION_LEFT else Panel.POSITION_RIGHT)
                leftPanelSelection.setBackgroundResource(if (isLeftPanel) R.color.yellow else R.color.main_grey);
                rightPanelSelection.setBackgroundResource(if (!isLeftPanel) R.color.yellow else R.color.main_grey);
            }
        })

        mainVM.requestFocus(Panel.POSITION_LEFT)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private inner class TabsAdapter(fm: FragmentManager) :
            FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount() = 2

        override fun getItem(position: Int): Fragment {
            return if (position == 0) Panel.newInstance(Panel.POSITION_LEFT) else Panel.newInstance(Panel.POSITION_RIGHT)
        }

    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1024
    }
}