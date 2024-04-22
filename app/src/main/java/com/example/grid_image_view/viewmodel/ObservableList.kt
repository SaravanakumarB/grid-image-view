package com.redkenko.health.viewmodel

import androidx.lifecycle.MutableLiveData

open class ObservableList<T> : MutableLiveData<List<T>>(emptyList()) {

  val size get() = value?.size ?: 0

  fun add(item: T) {
    value = value?.plus(item)
  }

  fun remove(item: T) {
    value = value?.minus(item)
  }

  operator fun plusAssign(list: List<T>) {
    value = value?.plus(list)
  }

  fun firstItem() = value?.get(0)

  fun isNullOrEmpty() = value?.isNullOrEmpty() ?: true

  operator fun get(index: Int) = value?.get(index)

  fun getIndexOf(item: T) = value?.indexOf(item) ?: -1

  fun toMutableList() = value?.toMutableList() ?: mutableListOf()

  fun clear() {
    value = emptyList()
  }
}
