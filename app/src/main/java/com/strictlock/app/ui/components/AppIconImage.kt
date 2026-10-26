package com.strictlock.app.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.collection.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.strictlock.app.ui.theme.RegainMint
import com.strictlock.app.ui.theme.RegainSurfaceVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object IconCache {
    private val memoryCache = LruCache<String, ImageBitmap>(200)

    fun get(key: String): ImageBitmap? = memoryCache[key]

    fun put(key: String, bitmap: ImageBitmap) {
        memoryCache.put(key, bitmap)
    }

    fun rasterizeDrawable(drawable: Drawable, targetSize: Int = 96): ImageBitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            val bmp = drawable.bitmap
            if (bmp.width <= targetSize && bmp.height <= targetSize) {
                return bmp.asImageBitmap()
            }
        }
        val w = if (drawable.intrinsicWidth in 1..256) drawable.intrinsicWidth else targetSize
        val h = if (drawable.intrinsicHeight in 1..256) drawable.intrinsicHeight else targetSize
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bmp.asImageBitmap()
    }

    suspend fun loadIconAsync(context: Context, packageName: String): ImageBitmap? = withContext(Dispatchers.IO) {
        val cached = get(packageName)
        if (cached != null) return@withContext cached

        try {
            val drawable = context.packageManager.getApplicationIcon(packageName)
            val imgBitmap = rasterizeDrawable(drawable)
            put(packageName, imgBitmap)
            imgBitmap
        } catch (_: Exception) {
            null
        }
    }
}

@Composable
fun AppIconImage(
    drawable: Drawable?,
    modifier: Modifier = Modifier
) {
    if (drawable != null) {
        val bitmap = remember(drawable) {
            try {
                IconCache.rasterizeDrawable(drawable)
            } catch (_: Exception) {
                null
            }
        }
        if (bitmap != null) {
            Image(bitmap = bitmap, contentDescription = null, modifier = modifier)
        } else {
            PlaceholderIcon(modifier = modifier)
        }
    } else {
        PlaceholderIcon(modifier = modifier)
    }
}

@Composable
fun AppIconImage(
    packageName: String,
    appName: String = "",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var imageBitmap by remember(packageName) {
        mutableStateOf(IconCache.get(packageName))
    }

    if (imageBitmap == null) {
        LaunchedEffect(packageName) {
            val loaded = IconCache.loadIconAsync(context, packageName)
            imageBitmap = loaded
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap!!,
            contentDescription = appName.ifBlank { packageName },
            modifier = modifier
        )
    } else {
        PlaceholderIcon(modifier = modifier)
    }
}

@Composable
private fun PlaceholderIcon(modifier: Modifier) {
    Box(
        modifier = modifier.background(RegainSurfaceVariant, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Android,
            contentDescription = null,
            tint = RegainMint.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}
