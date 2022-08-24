package com.github.chrisbanes.photoview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Matrix
import android.view.animation.AccelerateDecelerateInterpolator

/**
 * This is a ValueAnimator that animates between a [originalMatrix] and the [finalMatrix].
 */
class PhotoViewAttacherMatrixAnimator(private val photoViewAttacher: PhotoViewAttacher) : ValueAnimator() {

    private lateinit var originalMatrix: Matrix
    private lateinit var finalMatrix: Matrix

    private val originalValuesArray = FloatArray(9)
    private val finalValuesArray = FloatArray(9)
    private var diffArray = FloatArray(9)

    private var listener: PhotoViewAnimationListener? = null

    init {
        setFloatValues(0f, 1f)
        duration = MATRIX_ANIMATION_DURATION_MS
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener {
            val fraction = animatedFraction
            originalMatrix.getValues(originalValuesArray)
            finalMatrix.getValues(finalValuesArray)

            diffArray = originalValuesArray
                .zip(finalValuesArray)
                .map { pair -> (pair.first + ((pair.second - pair.first) * fraction)) }
                .toFloatArray()

            photoViewAttacher.setDisplayMatrix(Matrix().apply { setValues(diffArray) })
        }

        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                listener?.onAnimationEnd()
            }
        })
    }

    fun animate(originalMatrix: Matrix, finalMatrix: Matrix, listener: PhotoViewAnimationListener?) {
        this.originalMatrix = originalMatrix
        this.finalMatrix = finalMatrix
        this.listener = listener

        cancel()
        start()
    }

    companion object {
        private const val MATRIX_ANIMATION_DURATION_MS = 400L
    }
}