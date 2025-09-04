package com.beballer.beballer.ui.interfacess

interface UploadProgressListener {
    fun onProgressUpdate(uploadedBytes: Long, totalBytes: Long)
}