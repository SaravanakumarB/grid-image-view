package com.example.grid_image_view.koin

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.grid_image_view.activity.InjectableAppCompatActivity
import com.example.grid_image_view.fragment.InjectableFragment
import org.koin.androidx.scope.activityRetainedScope
import org.koin.androidx.scope.fragmentScope
import org.koin.core.scope.Scope

fun <ActivityType : InjectableAppCompatActivity> ActivityType.setupActivityProvider(scope: Scope = activityRetainedScope().value) {

  // Append instance to scope
  scope.append {
    scoped<Activity> { this@setupActivityProvider }
    scoped<AppCompatActivity> { this@setupActivityProvider }
    scoped<InjectableAppCompatActivity> { this@setupActivityProvider }
  }
}

fun <FragmentType : InjectableFragment> FragmentType.setupFragmentProvider(scope: Scope = fragmentScope().value) {

  // Append instance to scope
  scope.append {
    scoped<Fragment> { this@setupFragmentProvider }
    scoped<InjectableFragment> { this@setupFragmentProvider }
  }
}

fun Scope.currentInjectableAppCompatActivity(): InjectableAppCompatActivity? = getOrNull<InjectableAppCompatActivity>()
fun Scope.currentActivity(): AppCompatActivity? = getOrNull<AppCompatActivity>()

fun Scope.currentInjectableFragment(): InjectableFragment? = getOrNull<InjectableFragment>()
fun Scope.currentFragment(): Fragment? = getOrNull<Fragment>()
