package com.example.matrixscale

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
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
            addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                updateBaseMatrix(MatrixBasedScaleTypeEnum.END_CROP)

                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.athletic)
                binding.blurred.setImageBitmap(applyGaussianBlur(bitmap))

            }

        }
    }

    private fun updateBaseMatrix(scaleType: MatrixBasedScaleTypeEnum) {
        val drawable = binding.blurred.drawable ?: return

        val viewWidth: Float = getImageViewWidth(binding.blurred).toFloat()
        val viewHeight: Float = getImageViewHeight(binding.blurred).toFloat()
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        val baseMatrix = Matrix()
        //baseMatrix.reset() // only needed when matrix is a global property
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

    private fun applyGaussianBlur(src: Bitmap): Bitmap? {
        val blurConfig = arrayOf(
            doubleArrayOf(1.0, 2.0, 1.0),
            doubleArrayOf(2.0, 4.0, 2.0),
            doubleArrayOf(1.0, 2.0, 1.0)
        )
        val convMatrix = ConvolutionMatrix(3)
        convMatrix.applyConfig(blurConfig)
        convMatrix.factor = 30f
        convMatrix.offset = 0f
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix)
    }

}