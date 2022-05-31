package org.swa.model;

import java.io.InputStream;

public class Image {
    private InputStream imageData;
    private String imageType;
    private long imageSize;
    private String fileName;
    private Disco disco;

    public Image(InputStream imageData, String imageType, long imageSize, String fileName) {
        this.imageData = imageData;
        this.imageType = imageType;
        this.imageSize = imageSize;
        this.fileName = fileName;
    }

    public Image() {
        this.imageData = null;
        this.imageType = "";
        this.imageSize = 0;
        this.fileName = "";
    }

    
    public InputStream getImageData() {
        return imageData;
    }

    
    public void setImageData(InputStream imageData) {
        this.imageData = imageData;
    }

    
    public String getImageType() {
        return imageType;
    }

    
    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    
    public long getImageSize() {
        return imageSize;
    }

    
    public void setImageSize(long imageSize) {
        this.imageSize = imageSize;
    }

    
    public String getFileName() {
        return fileName;
    }

    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    
    public Disco getDisco() {
        return this.disco;
    }

    
    public void setDisco(Disco disco) {
        this.disco = disco;
    }
}
