package com.ln.base.tool.language;

public enum Language {
    AUTO(0), CHINA(1), TAIWAN(2), KOREAN(3), ENGLISH(4), FRENCH(5);
    //AUTO,CHINA,TAIWAN,KOREA,ENGLISH,FRANCE;

    public int code;

    Language(int code) {
        this.code = code;
    }

    public static Language get(int code) {
        switch (code) {
            case 0:
                return AUTO;
            case 1:
                return CHINA;
            case 2:
                return TAIWAN;
            case 3:
                return KOREAN;
            case 4:
                return ENGLISH;
            case 5:
                return FRENCH;
            default:
                return AUTO;
        }
    }
}
