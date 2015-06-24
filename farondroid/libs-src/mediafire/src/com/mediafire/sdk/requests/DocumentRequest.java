package com.mediafire.sdk.requests;

/**
 * Request used to fetch a document from mediafire
 */
public class DocumentRequest {

    private final String hash;
    private final String quickKey;
    private final String page;
    private final OptionalParameters parameters;

    public DocumentRequest(String hash, String quickKey, String page, OptionalParameters parameters) {
        this.hash = hash;
        this.quickKey = quickKey;
        this.page = page;
        this.parameters = parameters;
    }
    
    public DocumentRequest(String hash, String quickKey, String page) {
        this(hash, quickKey, page, null);
    }

    public String getHash() {
        return hash;
    }

    public String getQuickKey() {
        return quickKey;
    }

    public String getPage() {
        return page;
    }

    public OptionalParameters getOptionalParameters() {
        return parameters;
    }

    public static class OptionalParameters {
        public static final String OUTPUT_PDF = "pdf";
        public static final String OUTPUT_IMG = "img";
        public static final String OUTPUT_FLASH = "swf";

        private static final String DEFAULT_OUTPUT = "pdf";
        private static final int DEFAULT_SIZE_ID = -1;
        private static final boolean DEFAULT_REQUEST_CONVERSION_ONLY = false;
        private static final boolean DEFAULT_REQUEST_JSON_ENCODED_DATA = false;

        private String output = DEFAULT_OUTPUT;
        private int sizeId = DEFAULT_SIZE_ID;
        private boolean requestingJSONEncodedData;
        private boolean requestingConversionOnly;

        public OptionalParameters() { }
        
        public void setOutputPdf() {
            this.output = "pdf";
        }
        
        public void setOutputFlash() {
            this.output = "swf";
        }
        
        public void setOutputImage() {
            this.output = "img";
        }
        
        public void setOutputImageWithSizeId(int value) {
            this.output = "img";
            this.sizeId = value;
        }
        
        public void setIncludeJSONEncodedData(boolean value) {
            this.requestingJSONEncodedData = value;
        }
        
        public void setRequestConversionOnly(boolean value) {
            this.requestingConversionOnly = value;
        }

        public String getOutput() {
            return output;
        }

        public int getSizeId() {
            return sizeId;
        }

        public boolean isRequestingJSONEncodedData() {
            return requestingJSONEncodedData;
        }

        public boolean isRequestingConversionOnly() {
            return requestingConversionOnly;
        }
    }
}
