package it.danielebonaldo.filamentdemo

import android.content.Context
import com.google.android.filament.Engine
import com.google.android.filament.View
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.FilamentAsset
import com.google.android.filament.gltfio.ResourceLoader
import com.google.android.filament.utils.Float3
import com.google.android.filament.utils.Mat4
import com.google.android.filament.utils.max
import com.google.android.filament.utils.rotation
import com.google.android.filament.utils.scale
import com.google.android.filament.utils.translation
import com.google.android.filament.utils.transpose
import java.nio.Buffer
import java.nio.ByteBuffer

fun setupModelViewer(viewer: ModelViewer) {
    val options = viewer.view.dynamicResolutionOptions
    options.enabled = true
    viewer.view.dynamicResolutionOptions = options

    viewer.view.antiAliasing = View.AntiAliasing.FXAA
    viewer.view.sampleCount = 4

    val bloom = viewer.view.bloomOptions
    bloom.enabled = true
    viewer.view.bloomOptions = bloom
}

fun readCompressedAsset(context: Context, assetName: String): ByteBuffer {
    val input = context.assets.open(assetName)
    val bytes = ByteArray(input.available())
    input.read(bytes)
    return ByteBuffer.wrap(bytes)
}

fun loadModelGlb(
    assetLoader: AssetLoader,
    resourceLoader: ResourceLoader,
    buffer: Buffer
): FilamentAsset {
    val asset = assetLoader.createAsset(buffer)
    asset?.apply {
        resourceLoader.asyncBeginLoad(asset)
        asset.releaseSourceData()
    }
    return asset!!
}

fun transformToUnitCube(engine: Engine, asset: FilamentAsset, topDownView: Boolean) {
    val tm = engine.transformManager
    val center = asset.boundingBox.center.let { v -> Float3(v[0], v[1], v[2]) }
    val halfExtent = asset.boundingBox.halfExtent.let { v -> Float3(v[0], v[1], v[2]) }
    val maxExtent = 2.0f * max(halfExtent)
    val scaleFactor = 2.0f / maxExtent
    val rotation = if (topDownView) rotation(Float3(x = 1f), 90f) else Mat4()
    val transform = rotation * scale(Float3(scaleFactor)) * translation(Float3(-center))
    tm.setTransform(tm.getInstance(asset.root), transpose(transform).toFloatArray())
}
