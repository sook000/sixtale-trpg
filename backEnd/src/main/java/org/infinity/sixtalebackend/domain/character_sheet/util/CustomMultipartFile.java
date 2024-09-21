package org.infinity.sixtalebackend.domain.character_sheet.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLConnection;

public class CustomMultipartFile implements MultipartFile {
    private byte[] input;
    private String filename;
    private String contentType;


    public CustomMultipartFile(byte[] input,String filename) {
        this.input = input;
        this.filename = filename;
        this.contentType = URLConnection.guessContentTypeFromName(filename);  // Content-Type 추측

    }

    @Override
    public String getName() {
        return filename;
    }

    @Override
    public String getOriginalFilename() {
        return filename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return input == null || input.length == 0;
    }

    @Override
    public long getSize() {
        return input.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return input;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(input);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try(FileOutputStream fos = new FileOutputStream(dest)){
            fos.write(input);
        }
    }
}