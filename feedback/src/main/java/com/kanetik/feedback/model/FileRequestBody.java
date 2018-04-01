package com.kanetik.feedback.model;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class FileRequestBody extends RequestBody {
    private static final int SEGMENT_SIZE = 2048; // okio.Segment.SIZE

    private final File file;
    private final String contentType;

    public FileRequestBody(File file, String contentType) {
        this.file = file;
        this.contentType = contentType;
    }

    @Override
    public long contentLength() {
        return file.length();
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(contentType);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(file);
            long total = 0;
            long read;

            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                total += read;
                sink.flush();

            }
        } finally {
            Util.closeQuietly(source);
        }
    }
}
