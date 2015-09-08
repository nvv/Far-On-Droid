package com.mediafire.sdk.response_models.data_models;

import java.util.ArrayList;
import java.util.List;

/**
* Created by Chris on 12/23/2014.
*/
public class ResumableBitmapModel {
    private int count;
    private List<String> words;

    public int getCount() {
        return count;
    }

    public List<Integer> getWords() {
        if (words == null || words.isEmpty()) {
            return new ArrayList<>();
        }
        return convert(words);
    }

    private List<Integer> convert(List<String> words) {
        List<Integer> ret = new ArrayList<>();
        for (String str : words) {
            ret.add(Integer.parseInt(str));
        }

        return ret.size() == words.size() ? ret : new ArrayList<Integer>();
    }
}
