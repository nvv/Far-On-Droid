package com.mediafire.sdk.api.responses.data_models;

import java.util.ArrayList;
import java.util.List;

/**
* Created by Chris on 12/23/2014.
*/
public class ResumableBitmap {
    private int count;
    private List<String> words;

    public int getCount() {
        return count;
    }

    public List<Integer> getWords() {
        if (words == null || words.isEmpty()) {
            return new ArrayList<Integer>();
        }
        return convert(words);
    }

    private List<Integer> convert(List<String> words) {
        List<Integer> ret = new ArrayList<Integer>();
        for (String str : words) {
            ret.add(Integer.parseInt(str));
        }

        if (ret.size() == words.size()) {
            return ret;
        } else {
            return new ArrayList<Integer>();
        }
    }
}
