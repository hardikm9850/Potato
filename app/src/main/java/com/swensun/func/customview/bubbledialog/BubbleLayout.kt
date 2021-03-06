package com.swensun.func.customview.bubbledialog

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.swensun.potato.R
import com.swensun.swutils.ui.dp2px

class BubbleLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val paint: Paint
    val path: Path
    private var mLook = Gravity.BOTTOM
    private var mBubblePadding = 0
    private var mWidth = 0
    private var mHeight = 0
    private var mLeft = 0
    private var mTop = 0
    private var mRight = 0
    private var mBottom = 0
    private var mLookPosition = 0
    var lookWidth = 0
    private var mLookLength = 0
    var shadowColor = 0
    var shadowRadius = 0
    var shadowX = 0
    var shadowY = 0
    var bubbleRadius = 0
    var bubbleColor = 0
    private var mListener: OnClickEdgeListener? = null
    private val mRegion = Region()

//    /**
//     * 箭头指向
//     */
//    enum class Look(var value: Int) {
//        /**
//         * 坐上右下
//         */
//        LEFT(1), TOP(2), RIGHT(3), BOTTOM(4);
//
//        companion object {
//            fun getType(value: Int): Look {
//                var type = BOTTOM
//                when (value) {
//                    1 -> type = LEFT
//                    2 -> type = TOP
//                    3 -> type = RIGHT
//                    4 -> type = BOTTOM
//                }
//                return type
//            }
//        }
//    }

    /**
     * 初始化参数
     */
    private fun initValues() {
        mLook = Gravity.BOTTOM
        mLookPosition = dp2px(0f)
        lookWidth = dp2px(10f)
        mLookLength = dp2px(10f)
        shadowRadius = dp2px(3f)
        shadowX = dp2px(0f)
        shadowY = dp2px(0f)
        bubbleRadius = dp2px(3f)
        mBubblePadding = dp2px(5f)
        shadowColor = Color.GRAY
        bubbleColor = Color.BLACK
    }

    private fun initPadding() {
        val p = mBubblePadding * 2
        when (mLook) {
            Gravity.BOTTOM -> setPadding(p, p, p, mLookLength + p)
            Gravity.TOP -> setPadding(p, p + mLookLength, p, p)
            Gravity.LEFT -> setPadding(p + mLookLength, p, p, p)
            Gravity.RIGHT -> setPadding(p, p, p + mLookLength, p)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        initData()
    }

    override fun invalidate() {
        initData()
        super.invalidate()
    }

    override fun postInvalidate() {
        initData()
        super.postInvalidate()
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        paint.pathEffect = CornerPathEffect(bubbleRadius.toFloat())
        paint.setShadowLayer(
            shadowRadius.toFloat(),
            shadowX.toFloat(),
            shadowY.toFloat(),
            shadowColor
        )
        mLeft = mBubblePadding + if (mLook == Gravity.LEFT) mLookLength else 0
        mTop = mBubblePadding + if (mLook == Gravity.TOP) mLookLength else 0
        mRight = mWidth - mBubblePadding - if (mLook == Gravity.RIGHT) mLookLength else 0
        mBottom = mHeight - mBubblePadding - if (mLook == Gravity.BOTTOM) mLookLength else 0
        paint.color = bubbleColor
        path.reset()
        var topOffset = mLookPosition
        if (mLookPosition + mLookLength > mBottom) {
            topOffset = mBottom - lookWidth
        }
        topOffset = if (topOffset > mBubblePadding) topOffset else mBubblePadding
        var leftOffset = mLookPosition
        if (mLookPosition + mLookLength > mRight) {
            topOffset = mRight - lookWidth
        }
        leftOffset = if (leftOffset > mBubblePadding) leftOffset else mBubblePadding
        when (mLook) {
            Gravity.LEFT -> {
                path.moveTo(mLeft.toFloat(), topOffset.toFloat())
                path.rLineTo(-mLookLength.toFloat(), lookWidth / 2.toFloat())
                path.rLineTo(mLookLength.toFloat(), lookWidth / 2.toFloat())
                path.lineTo(mLeft.toFloat(), mBottom.toFloat())
                path.lineTo(mRight.toFloat(), mBottom.toFloat())
                path.lineTo(mRight.toFloat(), mTop.toFloat())
                path.lineTo(mLeft.toFloat(), mTop.toFloat())
            }
            Gravity.TOP -> {
                path.moveTo(leftOffset.toFloat(), mTop.toFloat())
                path.rLineTo(lookWidth / 2.toFloat(), -mLookLength.toFloat())
                path.rLineTo(lookWidth / 2.toFloat(), mLookLength.toFloat())
                path.lineTo(mRight.toFloat(), mTop.toFloat())
                path.lineTo(mRight.toFloat(), mBottom.toFloat())
                path.lineTo(mLeft.toFloat(), mBottom.toFloat())
                path.lineTo(mLeft.toFloat(), mTop.toFloat())
            }
            Gravity.RIGHT -> {
                path.moveTo(mRight.toFloat(), topOffset.toFloat())
                path.rLineTo(mLookLength.toFloat(), lookWidth / 2.toFloat())
                path.rLineTo(-mLookLength.toFloat(), lookWidth / 2.toFloat())
                path.lineTo(mRight.toFloat(), mBottom.toFloat())
                path.lineTo(mLeft.toFloat(), mBottom.toFloat())
                path.lineTo(mLeft.toFloat(), mTop.toFloat())
                path.lineTo(mRight.toFloat(), mTop.toFloat())
            }
            Gravity.BOTTOM -> {
                path.moveTo(leftOffset.toFloat(), mBottom.toFloat())
                path.rLineTo(lookWidth / 2.toFloat(), mLookLength.toFloat())
                path.rLineTo(lookWidth / 2.toFloat(), -mLookLength.toFloat())
                path.lineTo(mRight.toFloat(), mBottom.toFloat())
                path.lineTo(mRight.toFloat(), mTop.toFloat())
                path.lineTo(mLeft.toFloat(), mTop.toFloat())
                path.lineTo(mLeft.toFloat(), mBottom.toFloat())
            }
            else -> {
            }
        }
        path.close()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val r = RectF()
            path.computeBounds(r, true)
            mRegion.setPath(
                path,
                Region(
                    r.left.toInt(),
                    r.top.toInt(),
                    r.right.toInt(),
                    r.bottom.toInt()
                )
            )
            if (!mRegion.contains(event.x.toInt(), event.y.toInt()) && mListener != null) {
                mListener!!.edge()
            }
        }
        return super.onTouchEvent(event)
    }

    var look: Int
        get() = mLook
        set(mLook) {
            this.mLook = mLook
            initPadding()
        }

    var lookPosition: Int
        get() = mLookPosition
        set(mLookPosition) {
            this.mLookPosition = mLookPosition
            invalidate()
        }

    var lookLength: Int
        get() = mLookLength
        set(mLookLength) {
            this.mLookLength = mLookLength
            initPadding()
        }


    fun lookView(refView: View) {
        refView.post {
            // 显示位置
            val l1 = IntArray(2)
            refView.getLocationOnScreen(l1)
            val l2 = IntArray(2)
            getLocationOnScreen(l2)
            val width = refView.width
            lookPosition = l1[0] - l2[0] + width / 2 - mLookLength / 2
        }
    }

    public override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        bundle.putInt("mLookPosition", mLookPosition)
        bundle.putInt("mLookWidth", lookWidth)
        bundle.putInt("mLookLength", mLookLength)
        bundle.putInt("mShadowColor", shadowColor)
        bundle.putInt("mShadowRadius", shadowRadius)
        bundle.putInt("mShadowX", shadowX)
        bundle.putInt("mShadowY", shadowY)
        bundle.putInt("mBubbleRadius", bubbleRadius)
        bundle.putInt("mWidth", mWidth)
        bundle.putInt("mHeight", mHeight)
        bundle.putInt("mLeft", mLeft)
        bundle.putInt("mTop", mTop)
        bundle.putInt("mRight", mRight)
        bundle.putInt("mBottom", mBottom)
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val bundle = state
            mLookPosition = bundle.getInt("mLookPosition")
            lookWidth = bundle.getInt("mLookWidth")
            mLookLength = bundle.getInt("mLookLength")
            shadowColor = bundle.getInt("mShadowColor")
            shadowRadius = bundle.getInt("mShadowRadius")
            shadowX = bundle.getInt("mShadowX")
            shadowY = bundle.getInt("mShadowY")
            bubbleRadius = bundle.getInt("mBubbleRadius")
            mWidth = bundle.getInt("mWidth")
            mHeight = bundle.getInt("mHeight")
            mLeft = bundle.getInt("mLeft")
            mTop = bundle.getInt("mTop")
            mRight = bundle.getInt("mRight")
            mBottom = bundle.getInt("mBottom")
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"))
            return
        }
        super.onRestoreInstanceState(state)
    }

    fun setOnClickEdgeListener(l: OnClickEdgeListener?) {
        mListener = l
    }

    /**
     * 触摸到气泡的边缘
     */
    interface OnClickEdgeListener {
        fun edge()
    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_guide_bubble, this)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        setWillNotDraw(false)
        initValues()
        paint =
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        paint.style = Paint.Style.FILL
        path = Path()
        initPadding()
    }
}