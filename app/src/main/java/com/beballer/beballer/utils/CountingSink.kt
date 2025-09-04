package com.beballer.beballer.utils



import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import java.io.IOException

abstract class CountingSink(delegate: BufferedSink) : ForwardingSink(delegate) {

    @Throws(IOException::class)
    override fun write(source: Buffer, byteCount: Long) {
        super.write(source, byteCount)
        onBytesWritten(byteCount)
    }

    protected abstract fun onBytesWritten(bytesWritten: Long)
}
