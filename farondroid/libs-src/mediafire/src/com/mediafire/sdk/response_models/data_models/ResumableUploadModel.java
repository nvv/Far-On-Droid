package com.mediafire.sdk.response_models.data_models;

/**
* Created by Chris on 12/23/2014.
*/
public class ResumableUploadModel {
    private String all_units_ready;
    private int number_of_units;
    private int unit_size;
    private ResumableBitmapModel bitmap;
    private String upload_key;

    public String getAllUnitsReady() {
        return all_units_ready;
    }

    public int getNumberOfUnits() {
        return number_of_units;
    }

    public int getUnitSize() {
        return unit_size;
    }

    public ResumableBitmapModel getBitmap() {
        return bitmap;
    }

    public String getUploadKey() {
        return upload_key;
    }
}
