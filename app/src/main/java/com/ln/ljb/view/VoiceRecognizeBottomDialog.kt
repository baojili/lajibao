package com.ln.ljb.view

import android.content.Context
import android.view.View
import com.ln.base.dialog.BottomDialog
import com.ln.base.network.RequestUiHandler
import com.ln.base.tool.Log
import com.ln.base.tool.StorageUtils
import com.ln.ljb.R
import com.ln.mp3.MP3Recorder
import kotlinx.android.synthetic.main.view_voice_recognize.*
import java.io.File
import java.util.*

open class VoiceRecognizeBottomDialog : BottomDialog, RequestUiHandler {
    private var mP3Recorder: MP3Recorder? = null
    private var mRecordFile: File? = null
    private var timer: Timer? = null
    private var updateTime: Long = 0L

    constructor(context: Context?) : super(context) {
        setContentView(R.layout.view_voice_recognize)
        ll_stop_voice?.setOnClickListener(this)
        ll_resume_voice.setOnClickListener(this)
        iv_dialog_close.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.ll_stop_voice -> {
                stopRecord()
                showRecognizing()
                onFinishRecord(mRecordFile)
            }
            R.id.ll_resume_voice -> {
                Log.e("ll_resume_voice")
                showSpeaking()
                startRecord()
            }
            R.id.iv_dialog_close -> {
                dismiss()
            }
        }
    }

    open fun onFinishRecord(recordFile: File?) {

    }

    open fun onPerSecond(second: Long) {
        //Log.e("second " + second)
        var temp: Long = 5 - second
        if (temp <= 0L) {
            temp = 0L
            stopRecord()
            contentView.post {
                showRecognizing()
                onFinishRecord(mRecordFile)
            }
        }
        contentView.post {
            //Log.e("ui thread " + temp )
            tv_speak_title.text = context.getString(R.string.pls_speak_listening, temp.toString())
        }
    }

    private fun startRecord() {
        updateTime = 0L
        view_voice_Line.clear()
        mRecordFile = File(
            StorageUtils.getCustomCacheDirectory(context, "mp3"),
            UUID.randomUUID().toString() + ".mp3"
        )
        //Log.e(mRecordFile?.absolutePath)
        if (mRecordFile != null) {
            mP3Recorder = MP3Recorder(mRecordFile)
            mP3Recorder?.start()
        }
        if (timer == null) {
            timer = Timer()
        }
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (mP3Recorder != null) {
                    view_voice_Line?.setVolume(mP3Recorder!!.volume)
                }
                //Log.e("updateTime = " + updateTime)
                if (updateTime % 1000 == 0L) {
                    onPerSecond(updateTime / 1000)
                }
                updateTime += 100L
            }
        }, 0, 100)
    }

    private fun stopRecord() {
        timer?.cancel()
        timer = null
        mP3Recorder?.stop()
    }

    private fun showSpeaking() {
        view_voice_Line?.visibility = View.VISIBLE
        ll_speaking?.visibility = View.VISIBLE
        ll_recognize_fail?.visibility = View.GONE
        ll_recognizing?.visibility = View.GONE
    }

    private fun showRecognizeFail() {
        ll_speaking?.visibility = View.GONE
        ll_recognize_fail?.visibility = View.VISIBLE
        view_voice_Line?.visibility = View.INVISIBLE
        ll_recognizing?.visibility = View.GONE
    }

    private fun showRecognizing() {
        view_voice_Line?.visibility = View.INVISIBLE
        ll_speaking?.visibility = View.GONE
        ll_recognize_fail?.visibility = View.GONE
        ll_recognizing?.visibility = View.VISIBLE
    }

    override fun showStable() {
        super.showStable()
        showSpeaking()
        startRecord()
    }

    override fun dismiss() {
        super.dismiss()
        stopRecord()
    }

    override fun onSuccess() {
        dismiss()
        mRecordFile?.delete()
    }

    override fun onError(errcode: Int, errMsg: String?) {
        showRecognizeFail()
        mRecordFile?.delete()
    }

    override fun onStart(hint: String?) {
        showRecognizing()
    }
}