package com.beballer.beballer.utils

import com.beballer.beballer.ui.interfacess.UploadProgressListener

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.buffer
import java.io.IOException

class ProgressRequestBodyBite(
    private val requestBody: RequestBody,
    private val progressListener: UploadProgressListener?
) : RequestBody() {

    private var lastUploaded: Long = 0

    @Throws(IOException::class)
    override fun contentLength(): Long = requestBody.contentLength()

    override fun contentType(): MediaType? = requestBody.contentType()

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val totalSize = contentLength()
        var uploaded: Long = 0

        val countingSink = object : CountingSink(sink) {
            override fun onBytesWritten(bytesWritten: Long) {
                uploaded += bytesWritten
                if (progressListener != null && uploaded != lastUploaded) {
                    progressListener.onProgressUpdate(uploaded, totalSize)
                    lastUploaded = uploaded
                }
            }
        }

        // Use the extension function instead of deprecated Okio.buffer()
        val progressSink: BufferedSink = countingSink.buffer()
        requestBody.writeTo(progressSink)
        progressSink.flush()
    }
}
