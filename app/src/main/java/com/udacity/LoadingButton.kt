package com.udacity

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class  ButtonState {
        Loading ,
         Completed ,
    }

    private var widthSize = 0
    private var heightSize = 0

    private var bgColor: Int = Color.BLACK
    private var textColor: Int = Color.BLACK
    private var buttonText:String =""

    @Volatile
    private var progress: Double = 0.0


    private var valueAnimator = ValueAnimator()

//    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
//
//    }
private var buttonState = ButtonState.Completed

    private val updateListener = ValueAnimator.AnimatorUpdateListener {
        progress = (it.animatedValue as Float).toDouble()

        invalidate()
        requestLayout()
    }


    init {

        isClickable = true

        valueAnimator = AnimatorInflater.loadAnimator(
            context, R.animator.download_animator
        ) as ValueAnimator

        valueAnimator.addUpdateListener(updateListener)
        valueAnimator.addListener(object:AnimatorListenerAdapter(){
            override fun onAnimationStart(animation: Animator?)
            {
                super.onAnimationStart(animation)
                this@LoadingButton.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?)
            {
                super.onAnimationEnd(animation)
                this@LoadingButton.isEnabled = true

            }
        })

        val attr = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,
            0
        )
        try {
            bgColor = attr.getColor(
                R.styleable.LoadingButton_bgColor,
                ContextCompat.getColor(context, R.color.colorPrimary)
            )

            textColor = attr.getColor(
                R.styleable.LoadingButton_textColor,
                ContextCompat.getColor(context, R.color.white)
            )
        } finally {
            attr.recycle()
        }
    }



    fun isDownloadFinished() {
        // cancel the animation when file is downloaded
        valueAnimator.cancel()

        buttonState = ButtonState.Completed
        Log.e(null, "isDownloadFinished: entered" )
      invalidate()
        requestLayout()
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)

    }

    private val rect = RectF(
        740f,
        50f,
        810f,
        110f
    )

    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState == ButtonState.Completed) buttonState = ButtonState.Loading

        animation()

        return true
    }

    private fun animation() {
        if (isNetworkConnected(this.context))
        {
            valueAnimator.start()
        }else
        {
          Snackbar.make(this,"Please connect to the internet",Snackbar.LENGTH_SHORT).show()
        }



    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.strokeWidth = 0f
        paint.color = bgColor
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)


        if (buttonState == ButtonState.Loading) {
            paint.color = Color.parseColor("#004349")
            canvas?.drawRect(
                0f, 0f,
                (width * (progress / 100)).toFloat(), height.toFloat(), paint
            )
            paint.color = Color.parseColor("#F9A825")
            canvas?.drawArc(rect, 0f, (360 * (progress / 100)).toFloat(), true, paint)
            buttonText =resources.getString(R.string.loading)
        }

            if (buttonState == ButtonState.Completed)
            {
                buttonText = resources.getString(R.string.download)
            }

        paint.color = textColor
        canvas?.drawText(buttonText, (width / 2).toFloat(), ((height + 30) / 2).toFloat(),
            paint)



    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }


}