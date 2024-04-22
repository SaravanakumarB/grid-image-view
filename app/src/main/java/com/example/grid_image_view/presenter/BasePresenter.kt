package com.redkenko.health.presenter

import android.app.Activity
import android.content.ContentResolver
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.example.grid_image_view.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.core.component.KoinComponent


abstract class BasePresenter : KoinComponent {

  private val compositeDisposable = CompositeDisposable()

  abstract fun unsubscribe()

  abstract fun logout()

  fun Activity.showToast(
    message: String,
    duration: Int = Toast.LENGTH_LONG,
  ) {
    Toast.makeText(this, message, duration).show()
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

  fun View.showSnackBar(
    stringRes: Int,
    duration: Int = Snackbar.LENGTH_LONG,
    doOnDismissed: () -> Unit = { }
  ) {
    Snackbar.make(this, stringRes, duration).apply {
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

  fun ViewDataBinding.showSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    doOnDismissed: () -> Unit = { }
  ) {
    root.showSnackBar(message, duration, doOnDismissed)
  }

  fun ViewDataBinding.showSnackBar(
    stringRes: Int,
    duration: Int = Snackbar.LENGTH_LONG,
    doOnDismissed: () -> Unit = { }
  ) {
    root.showSnackBar(stringRes, duration, doOnDismissed)
  }

  fun AppCompatActivity.showSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    doOnDismissed: () -> Unit = { }
  ) {
    findViewById<View>(android.R.id.content).showSnackBar(message, duration, doOnDismissed)
  }

  fun AppCompatActivity.showLongSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    doOnDismissed: () -> Unit = { }
  ) {
    findViewById<View>(android.R.id.content).showLongSnackBar(message, duration, doOnDismissed)
  }

  fun View.showLongSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    doOnDismissed: () -> Unit = { }
  ) {
    Snackbar.make(this, message, duration).apply {
      val snackBarTextView = view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
      snackBarTextView.maxLines = 5
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

  fun AppCompatActivity.getMimeType(uri: Uri) = contentResolver.getType(uri)

  fun Uri.getMimeType(context: AppCompatActivity): String? {
    return when (scheme) {
      ContentResolver.SCHEME_CONTENT -> context.getMimeType(this)
      ContentResolver.SCHEME_FILE -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(
        MimeTypeMap.getFileExtensionFromUrl(toString()).lowercase()
      )
      else -> null
    }
  }

  fun addRxCall(disposable: Disposable) {
    compositeDisposable.add(disposable)
  }

  fun clearAllCalls() {
    if (!compositeDisposable.isDisposed) {
      compositeDisposable.clear()
    }
  }
}
