package com.drbrosdev.studytextscan.service.pdfExport

import android.graphics.text.LineBreaker
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics

import android.text.TextPaint
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.Q)
class Pagination(
    private var mIncludePad: Boolean = false,
    private var mWidth: Int = 0,
    private var mHeight: Int = 0,
    private var mSpacingMultiple: Float = 1f,
    private var mSpacingAdd: Float = 0F,
    private var mText: CharSequence? = null,
    private var mPaint: TextPaint? = null,
    private var mPages: MutableList<CharSequence>? = null,
) {

    init {
        layout()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun layout() {

        val layout = StaticLayout.Builder
            .obtain(mText!!, 0, mText!!.length, mPaint!!, mWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setTextDirection(TextDirectionHeuristics.LTR)
            .setLineSpacing(mSpacingAdd, mSpacingMultiple)
            .setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD)
            .setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE)
            .setIncludePad(mIncludePad)
            .build()

        val lines = layout.lineCount
        val text = layout.text
        var startOffset = 0
        var height = mHeight
        for (i in 0 until lines) {
            if (height < layout.getLineBottom(i)) {
                // When the layout height has been exceeded
                addPage(text.subSequence(startOffset, layout.getLineStart(i)))
                startOffset = layout.getLineStart(i)
                height =
                    layout.getLineTop(i) + mHeight - 35 // remove from height the new line where title fits
            }
            if (i == lines - 1) {
                // Put the rest of the text into the last page
                addPage(text.subSequence(startOffset, layout.getLineEnd(i)))
                return
            }
        }
    }

    private fun addPage(text: CharSequence) {
        mPages!!.add(text)
    }

    fun getNumberOfPages(): Int {
        return mPages!!.size
    }

    operator fun get(index: Int): CharSequence? {
        return if (index >= 0 && index < mPages!!.size) mPages!![index] else null
    }
}