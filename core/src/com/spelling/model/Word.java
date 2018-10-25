package com.spelling.model;

import com.spelling.viewmodel.ScreenManager;

/**
 * A Word has an English and Hmong spelling. The ID associated with a Word is the english spelling. A hmong word has
 * a certain amount of game spaces that differ from character count.
 */
public class Word implements Comparable<String> {
    private String englishSpelling;
    private String hmongSpelling;
    private int hmongSpaceLength;

    public Word(String englishSpelling, String hmongSpelling, int hmongSpaceLength) {
        this.englishSpelling = englishSpelling;
        this.hmongSpelling = hmongSpelling;
        this.hmongSpaceLength = hmongSpaceLength;
    }

    public String getWordId() {
        return englishSpelling;
    }

    /**
     * Simply returns a string based on what language
     */
    public String getSpelling(ScreenManager.Language language) {
        switch (language) {
            case ENGLISH:
                return englishSpelling;
            case HMONG:
                return hmongSpelling;
            default:
                return null;
        }
    }

    /**
     * Returns the number of spaces based on language.
     */
    public int getSpaceLength(ScreenManager.Language language) {
        switch (language) {
            case ENGLISH:
                return englishSpelling.length();
            case HMONG:
                return hmongSpaceLength;
            default:
                return 0;
        }
    }

    @Override
    public int compareTo(String o) {
        return o.compareTo(this.englishSpelling);
    }
}
