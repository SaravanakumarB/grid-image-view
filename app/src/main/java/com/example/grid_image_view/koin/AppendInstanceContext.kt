package com.example.grid_image_view.koin

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Kind
import org.koin.core.scope.Scope
import org.koin.core.definition.IndexKey
import org.koin.core.definition.indexKey
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.SingleInstanceFactory

fun Scope.append(appendActionContext: AppendInstanceContext.() -> Unit) {
  AppendInstanceContext(this).appendActionContext()
}

class AppendInstanceContext(val scope: Scope) {

  @OptIn(KoinInternalApi::class)
  inline fun <reified T> scoped(crossinline instance: () -> T): Scope = scope.apply {
    val definition = BeanDefinition(
      scopeQualifier = this.scopeQualifier,
      primaryType = T::class,
      kind = Kind.Singleton,
      definition = { instance() }
    )

    val factory = SingleInstanceFactory(definition)
    val instances = HashMap<IndexKey, InstanceFactory<*>>(this.getKoin().instanceRegistry.instances)
    instances[indexKey(T::class, null, this.scopeQualifier)] = factory
    return this
  }
}