package com.strictlock.app.ui.components

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.strictlock.app.ui.theme.RegainMint

fun Drawable.toBitmap(defaultWidth: Int = 120, defaultHeight: Int = 120): Bitmap {
    if (this is BitmapDrawable && bitmap != null) {
        return bitmap
    }
    val w = if (intrinsicWidth > 0) intrinsicWidth else defaultWidth
    val h = if (intrinsicHeight > 0) intrinsicHeight else defaultHeight
    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bmp
}

@Composable
fun AppIconImage(
    drawable: Drawable?,
    modifier: Modifier = Modifier
) {
    if (drawable != null) {
        val bitmap = remember(drawable) {
            try {
                drawable.toBitmap().asImageBitmap()
            } catch (_: Exception) {
                null
            }
        }
        if (bitmap != null) {
            Image(bitmap = bitmap, contentDescription = null, modifier = modifier)
        } else {
            Icon(Icons.Default.Android, contentDescription = null, modifier = modifier, tint = RegainMint)
        }
    } else {
        Icon(Icons.Default.Android, contentDescription = null, modifier = modifier, tint = RegainMint)
    }
}

@Composable
fun AppIconImage(
    packageName: String,
    appName: String = "",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val drawable = remember(packageName) {
        try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (_: Exception) {
            null
        }
    }
    AppIconImage(drawable = drawable, modifier = modifier)
}
