package com.aitangba.testproject.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.aitangba.testproject.R
import com.aitangba.testproject.databinding.DialogStandardBinding

/**
 * Created by Fring on 2020/8/24
 * 宽度：为屏幕宽度的 60/75；
 * 高度：自适应，最多为屏幕高度 8/13
 */
open class StandardDialog(context: Context) : Dialog(context, R.style.CustomDialog) {

    private var mBinding: DialogStandardBinding? = null
    private var mEnableCloseIcon = true
    private var mScreenWidth = 0
    private var mScreenHeight = 0

    init {
        val windowManager = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay?.apply {
            val displayMetrics = DisplayMetrics()
            this.getMetrics(displayMetrics)
            mScreenWidth = displayMetrics.widthPixels
            mScreenHeight = displayMetrics.heightPixels

            Log.d("ClosableDialogTest_TAG", String.format("maxWidth = %d, maxHeight = %d", mScreenWidth * 60 / 75, mScreenHeight * 8 / 13))
        }
    }

    override fun show() {
        window?.decorView?.setPadding(0, 0, 0, 0)
        if (mScreenWidth > 0) {
            window?.attributes?.width = mScreenWidth * 60 / 75
        }
        window?.attributes?.height = ViewGroup.LayoutParams.MATCH_PARENT
        window?.attributes?.gravity = Gravity.CENTER
        window?.findViewById<View>(Window.ID_ANDROID_CONTENT)?.setOnClickListener {
            dismiss()
        }
        super.show()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(wrapInBottomSheet(layoutResID, null, null))
    }

    override fun setContentView(view: View) {
        super.setContentView(wrapInBottomSheet(0, view, null))
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.setContentView(wrapInBottomSheet(0, view, params))
    }

    /**
     * 是否需要显示 “x” 关闭按钮
     */
    fun enableCloseIcon(enable: Boolean) {
        mEnableCloseIcon = enable
        mBinding?.apply {
            this.closeImage.visibility = if (mEnableCloseIcon) View.VISIBLE else View.GONE
        }
    }

    private fun wrapInBottomSheet(layoutResId: Int, view: View?, params: ViewGroup.LayoutParams?): View {
        val inflater = LayoutInflater.from(context)
        val binding = DataBindingUtil.inflate<DialogStandardBinding>(inflater, R.layout.dialog_standard, null, false)
        binding.containerView.setOnSizeChangedListener { w, h ->
            if (w > 0 && h > 0) {
                Log.d("ClosableDialogTest_TAG", String.format("containerView.width = %d,containerView.height = %d", w, h))

                val layoutParams = binding.guideline.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.guidePercent = if (w > h) 0.2f else 0.25f
                binding.guideline.requestLayout()
            }
        }
        binding.closeImage.visibility = if (mEnableCloseIcon) View.VISIBLE else View.GONE
        binding.closeImage.setOnClickListener {
            dismiss()
        }

        if (mScreenHeight > 0) {
            val layoutParams = binding.containerView.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.matchConstraintMaxHeight = mScreenHeight * 8 / 13;
        }

        var targetView = view
        if (view == null) {
            if (layoutResId == 0) {
                throw RuntimeException("content view can not  be null !")
            }
            targetView = inflater.inflate(layoutResId, null, false)
        }

        if (params == null) {
            binding.containerView.addView(targetView)
        } else {
            binding.containerView.addView(targetView, params)
        }

        mBinding = binding
        return binding.root
    }
}