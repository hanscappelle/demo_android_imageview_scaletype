package com.example.matrixscale

import android.graphics.Matrix
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.matrixscale.MainActivity.Companion.getImageViewHeight
import com.example.matrixscale.MainActivity.Companion.getImageViewWidth
import com.example.matrixscale.databinding.ViewBlurBinding

class BlurEndCropView : AppCompatActivity() {

    private lateinit var binding: ViewBlurBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewBlurBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backdrop

        binding.blurred.apply {
            scaleType = ImageView.ScaleType.MATRIX
            addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                updateBaseMatrix()
                applyBlur(binding.blurred, 16f)
            }
        }
    }

    /**
     * source: https://github.com/elye/demo_android_imageview_scaletype
     */
    private fun updateBaseMatrix() {
        val drawable = binding.blurred.drawable ?: return

        val viewWidth = getImageViewWidth(binding.blurred).toFloat()
        val viewHeight = getImageViewHeight(binding.blurred).toFloat()
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        val baseMatrix = Matrix()
        val widthScale = viewWidth / drawableWidth
        val heightScale = viewHeight / drawableHeight

        // end crop part
        val scale = widthScale.coerceAtLeast(heightScale)
        baseMatrix.postScale(scale, scale)
        baseMatrix.postTranslate(
            (viewWidth - drawableWidth * scale),
            (viewHeight - drawableHeight * scale)
        )

        binding.blurred.imageMatrix = baseMatrix
    }

    /**
     * source: https://developer.android.com/guide/topics/renderscript/migrate#image_blur_on_android_12_into_a_view
     */
    private fun applyBlur(view: View, radius: Float) {
        val blurRenderEffect = RenderEffect.createBlurEffect(
            radius, radius,
            Shader.TileMode.MIRROR
        )
        view.setRenderEffect(blurRenderEffect)
    }

}