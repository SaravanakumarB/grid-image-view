package com.example.grid_image_view.page

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.grid_image_view.R
import com.example.grid_image_view.databinding.AdapterImageDataBinding
import com.example.grid_image_view.domain.response.ImageDataModel
import com.example.grid_image_view.utils.CacheUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.CoroutineContext


class ImageDataListAdapter(private val context: Context, private val cacheUtils: CacheUtils) :
  RecyclerView.Adapter<ImageDataListAdapter.ViewHolder>() {

  private var list: MutableList<ImageDataModel> =
    ArrayList()

  private var onItemClickedListener: ((ImageDataModel, View) -> Unit)? = null

  fun setOnItemClickedListener(listener: (ImageDataModel, View) -> Unit) {
    onItemClickedListener = listener
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding =
      AdapterImageDataBinding.inflate(
        inflater,
        parent,
        /* attachToParent= */ false
      )
    return ViewHolder(binding)
  }

  fun setImageList(marketPlaceCategoryList: MutableList<ImageDataModel>) {
    this.list = marketPlaceCategoryList
    notifyDataSetChanged()
  }

  override fun getItemCount(): Int {
    return list.size
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(context, list[position], position, cacheUtils)
    holder.binding.root.setOnClickListener {
      onItemClickedListener?.invoke(list[position], holder.binding.root)
    }
  }

  class ViewHolder(val binding: AdapterImageDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(context: Context, imageDataModel: ImageDataModel, position: Int, cacheUtils: CacheUtils) {
      val url = imageDataModel.thumbnail.domain + "/" + imageDataModel.thumbnail.basePath + "/0/" +
              imageDataModel.thumbnail.key
      binding.tvImageName.text = imageDataModel.id
      binding.ivImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder))
      val savedImage = cacheUtils.getImageSavedInDiskOrMemory(imageDataModel.thumbnail.id + imageDataModel.thumbnail.key)
      if(savedImage != null) {
        Log.e("Test1234", "loading image from cache")
        binding.ivImage.setImageBitmap(savedImage)
      } else {
        Log.e("Test1234", "saving image to cache")
        Presenter().execute(url, binding.ivImage, imageDataModel.thumbnail.id + imageDataModel.thumbnail.key, cacheUtils)
      }
    }

    class Presenter : CoroutineScope {
      private var job: Job = Job()
      override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job // to run code in Main(UI) Thread

      // call this method to cancel a coroutine when you don't need it anymore,
      // e.g. when user closes the screen
      private fun cancel() {
        job.cancel()
      }

      fun execute(url: String, bmImage: ImageView, fileName: String, cacheUtils: CacheUtils) = launch {
        onPreExecute()
        val result = doInBackground(url) // runs in background thread without blocking the Main Thread
        onPostExecute(result, bmImage, fileName, cacheUtils)
      }

      private suspend fun doInBackground(url: String): Bitmap? = withContext(Dispatchers.IO) { // to run code in Background Thread
        // do async work
        val urldisplay = (url)
        var mIcon11: Bitmap? = null
        try {
          val `in` = URL(urldisplay).openStream()
          mIcon11 = BitmapFactory.decodeStream(`in`)
        } catch (e: Exception) {
          Log.e("Error", e.message!!)
          e.printStackTrace()
        }
        return@withContext mIcon11
      }

      // Runs on the Main(UI) Thread
      private fun onPreExecute() {
        // show progress
      }

      // Runs on the Main(UI) Thread
      private fun onPostExecute(result: Bitmap?, bmImage: ImageView, fileName: String, cacheUtils: CacheUtils) {
        // hide progress
        bmImage.setImageBitmap(result)
        cacheUtils.saveImageInDiskAndMemory(fileName, result)
        cancel()
      }
    }
  }
}
