package com.monkeyteam.chimpagne.model.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.UiComposable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm
import com.google.maps.android.clustering.view.ClusterRenderer
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.rememberClusterManager

const val DIST_CLUSTERING = 50
const val MIN_CLUSTERED_ITEMS = 2

@Composable
@GoogleMapComposable
@MapsComposeExperimentalApi
fun <T : ClusterItem> ChimpagneClustering(
    items: Collection<T>,
    onClusterClick: (Cluster<T>) -> Boolean = { false },
    onClusterItemClick: (T) -> Boolean = { false },
    onClusterItemInfoWindowClick: (T) -> Unit = {},
    onClusterItemInfoWindowLongClick: (T) -> Unit = {},
    clusterContent:
        @[UiComposable Composable]
        ((Cluster<T>) -> Unit)? =
        null,
    clusterItemContent:
        @[UiComposable Composable]
        ((T) -> Unit)? =
        null,
) {
  val clusterManager = rememberClusterManager<T>()
  val customAlgorithm = remember { CustomAlgorithm<T>() }
  val renderer = rememberCustomClusterRenderer(clusterContent, clusterItemContent, clusterManager)

  SideEffect {
    if (clusterManager?.renderer != renderer) {
      clusterManager?.renderer = renderer ?: return@SideEffect
    }
  }

  SideEffect {
    clusterManager ?: return@SideEffect

    clusterManager.algorithm = customAlgorithm
    clusterManager.setOnClusterClickListener(onClusterClick)
    clusterManager.setOnClusterItemClickListener(onClusterItemClick)
    clusterManager.setOnClusterItemInfoWindowClickListener(onClusterItemInfoWindowClick)
    clusterManager.setOnClusterItemInfoWindowLongClickListener(onClusterItemInfoWindowLongClick)
  }

  if (clusterManager != null) {
    com.google.maps.android.compose.clustering.Clustering(
        items = items,
        clusterManager = clusterManager,
    )
  }
}

@Composable
@GoogleMapComposable
@MapsComposeExperimentalApi
fun <T : ClusterItem> rememberCustomClusterRenderer(
    clusterContent: @Composable ((Cluster<T>) -> Unit)?,
    clusterItemContent: @Composable ((T) -> Unit)?,
    clusterManager: ClusterManager<T>?
): ClusterRenderer<T>? {
  val context = LocalContext.current
  val clusterContentState = rememberUpdatedState(clusterContent)
  val clusterItemContentState = rememberUpdatedState(clusterItemContent)
  val clusterRendererState: MutableState<ClusterRenderer<T>?> = remember { mutableStateOf(null) }

  clusterManager ?: return null
  MapEffect(context) { map ->
    val renderer =
        CustomClusterRenderer(
            context, map, clusterManager, clusterContentState.value, clusterItemContentState.value)
    clusterRendererState.value = renderer
  }
  return clusterRendererState.value
}

class CustomAlgorithm<T : ClusterItem> : NonHierarchicalDistanceBasedAlgorithm<T>() {
  init {
    maxDistanceBetweenClusteredItems = DIST_CLUSTERING
  }
}

class CustomClusterRenderer<T : ClusterItem>(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<T>,
    private val clusterContent: @Composable ((Cluster<T>) -> Unit)?,
    private val clusterItemContent: @Composable ((T) -> Unit)?
) : DefaultClusterRenderer<T>(context, map, clusterManager) {

  override fun shouldRenderAsCluster(cluster: Cluster<T>): Boolean {
    return cluster.size >= MIN_CLUSTERED_ITEMS
  }
}
