package com.example.grid_image_view.activity

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.grid_image_view.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.example.grid_image_view.authhelper.NetworkConnectionUtil
import com.example.grid_image_view.errorhandling.RetrofitException
import com.example.grid_image_view.koin.setupActivityProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.scope.Scope
import java.util.*

const val CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"

/**
 * An [AppCompatActivity] that facilitates field injection to child activities and constituent fragments that extend
 * [com.redkenko.health.fragment.InjectableFragment].
 */
open class InjectableAppCompatActivity : AppCompatActivity(), AndroidScopeComponent {
  /**
   * The [ActivityComponent] corresponding to this activity. This cannot be used before [onCreate] is called, and can be
   * used to inject lateinit fields in child activities during activity creation (which is recommended to be done in an
   * override of [onCreate]).
   */

  private var alertDialog: AlertDialog? = null

  private var networkChangeListener: INetworkChangeListener? = null
  private var bannerNetworkChangeListener: INetworkChangeListener? = null
  private var networkChangeReceiver: NetworkChangeReceiver? = null
  private var networkConnectionUtil: NetworkConnectionUtil? = null

  lateinit var activityLauncher: InjectableAppCompatActivityResult<Intent, ActivityResult>

  override val scope: Scope by activityRetainedScope()

  override fun onCreate(savedInstanceState: Bundle?) {
    // Note that the activity component must be initialized before onCreate() since it's possible for onCreate() to
    // synchronously attach fragments (e.g. during a configuration change), which requires the activity component for
    // createFragmentComponent(). This means downstream dependencies should not perform any major operations to the
    // injected activity since it's not yet fully created.
    initializeActivityComponent()
    initializeActivityLauncher()
    setupActivityProvider(scope)
    networkChangeReceiver = NetworkChangeReceiver()
    networkConnectionUtil = NetworkConnectionUtil(this)
    super.onCreate(savedInstanceState)
  }

  override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
    super.onCreate(savedInstanceState, persistentState)
    initializeActivityComponent()
    initializeActivityLauncher()
  }

  private fun initializeActivityComponent() {

  }

  private fun initializeActivityLauncher() {
    activityLauncher = InjectableAppCompatActivityResult.registerActivityForResult(this)
  }

  private fun registerNetworkChangeReceiver() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      val intentFilter = IntentFilter()
      intentFilter.addAction(CONNECTIVITY_ACTION)
      registerReceiver(networkChangeReceiver, intentFilter)
    }
  }

  private fun unregisterNetworkChangeReceiver() {
    try {
      unregisterReceiver(networkChangeReceiver)
    } catch (e: IllegalArgumentException) {
      e.printStackTrace()
    }
  }

  //
  inner class NetworkChangeReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      checkConnection()
    }
  }

  private fun checkConnection() {
    if (networkConnectionUtil?.getCurrentConnectionStatus() != NetworkConnectionUtil.ConnectionStatus.NONE) {
      networkChangeListener?.networkConnected()
      bannerNetworkChangeListener?.networkConnected()
      if (alertDialog != null && alertDialog!!.isShowing) {
        alertDialog?.dismiss()
      }
    } else {
      networkChangeListener?.networkDisconnected()
      bannerNetworkChangeListener?.networkDisconnected()
    }
  }

  override fun onResume() {
    super.onResume()
    setBannerConnectivityListener()
    registerNetworkChangeReceiver()
    //    setting app language to english
    val locale = Locale.ENGLISH
    Locale.setDefault(locale)
    val resources = this.resources
    val config = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
  }

  override fun onDestroy() {
    super.onDestroy()
    unregisterNetworkChangeReceiver()
  }

  fun setNetworkChangeListener(listener: INetworkChangeListener?) {
    this.networkChangeListener = listener
  }

  private fun setBannerNetworkChangeListener(listener: INetworkChangeListener?) {
    this.bannerNetworkChangeListener = listener
  }

  interface INetworkChangeListener {
    fun networkConnected()
    fun networkDisconnected()
  }

  private var isConnected = true
  private fun setBannerConnectivityListener() {
    setBannerNetworkChangeListener(object : INetworkChangeListener {
      override fun networkConnected() {
        if (!isConnected) {
          this@InjectableAppCompatActivity.handleInternetStatusBanner(View.GONE)
        }
      }

      override fun networkDisconnected() {
        isConnected = false
        this@InjectableAppCompatActivity.handleInternetStatusBanner(View.VISIBLE)
      }
    })
  }

  fun isNetworkAvailable(): Boolean {
    return networkConnectionUtil?.getCurrentConnectionStatus() != NetworkConnectionUtil.ConnectionStatus.NONE
  }

  fun handleNetworkErrorPage(error: Throwable?): Boolean {
    if (error is RetrofitException) {
      if (error.code == RetrofitException.CODE_1000 || !isNetworkAvailable()) {
        return true
      }
    }
    return false
  }

  fun showNoInternetConnectionDialog(showDialog: Boolean = false) {
    if (showDialog) {
      if (!isFinishing) {
      val builder: AlertDialog.Builder = AlertDialog.Builder(this)
      builder.setMessage(resources.getString(R.string.no_internet_connection_message))
        .setTitle(resources.getString(R.string.no_internet_connection_title))
        .setCancelable(false)
        .setPositiveButton(resources.getString(R.string.retry)) { _, _ ->
          checkConnection()
        }

      if (alertDialog != null && alertDialog!!.isShowing) {
        alertDialog?.dismiss()
      }

        alertDialog = builder.create()
        alertDialog?.show()
      }
    }
  }


  fun hideKeyBoard(activity: AppCompatActivity, view: View) {
    val imm: InputMethodManager =
      activity.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
  }

  companion object {

    const val SNACK_BAR_MESSAGE_CODE = 4001

    const val KEY_SNACK_BAR_MESSAGE = "KEY_SNACK_BAR_MESSAGE"
    const val KEY_SNACK_BAR_ACTION = "KEY_SNACK_BAR_ACTION"

    const val KEY_REDIRECT_ADD_ADDRESS = "KEY_REDIRECT_ADD_ADDRESS"
  }

  fun View.showSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    doOnDismissed: () -> Unit = { }
  ) {
    Snackbar.make(this, message, duration).apply {
      addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
          super.onDismissed(transientBottomBar, event)
          if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
            doOnDismissed()
          }
        }
      })
    }.show()
  }

  fun View.showSnackBarWithAction(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    action: String,
    doOnAction: () -> Unit = { }
  ) {
    Snackbar.make(this, message, duration).apply {
      setAction(action) {
        doOnAction()
      }
    }.show()
  }

  fun AppCompatActivity.showSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    doOnDismissed: () -> Unit = { }
  ) {
    findViewById<View>(android.R.id.content).showSnackBar(message, duration, doOnDismissed)
  }

  fun AppCompatActivity.showSnackBarWithAction(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    action: String,
    doOnAction: () -> Unit = { }
  ) {
    findViewById<View>(android.R.id.content).showSnackBarWithAction(
      message,
      duration,
      action,
      doOnAction
    )
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == RESULT_OK) {
      when (requestCode) {

      }
    }
  }

  fun AppCompatActivity.handleInternetStatusBanner(
    visibility: Int = View.GONE,
    initialVisibility: Boolean = false
  ) {
    try {
      val appCompatTextView = this.findViewById<AppCompatTextView>(R.id.tv_no_internet)
      if (!initialVisibility && visibility == View.VISIBLE) {
        appCompatTextView.setBackgroundColor(
          ResourcesCompat.getColor(
            this.resources,
            R.color.red,
            null
          )
        )
        appCompatTextView.text = this.getString(R.string.no_connection)
        appCompatTextView.visibility = visibility
      } else if (!initialVisibility && visibility == View.GONE) {
        appCompatTextView.setBackgroundColor(
          ResourcesCompat.getColor(
            this.resources,
            R.color.green,
            null
          )
        )
        appCompatTextView.text = this.getString(R.string.internet_back)
        this.lifecycleScope.launch {
          delay(3000)
          appCompatTextView.visibility = visibility
        }
      } else {
        appCompatTextView.visibility = View.GONE
      }
    } catch (e: Exception) {
      Log.i(
        "Internet Exception:",e.localizedMessage ?: "View not found"
      )
    }
  }
}
