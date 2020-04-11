package com.aib.adapter;

import java.util.List;

public class BaseAdapter implements Adapter {
    private List<String> texts;

    public BaseAdapter(List<String> texts) {
        this.texts = texts;
    }

    @Override
    public int getCount() {
        return texts.size();
    }

    @Override
    public List<String> getData() {
        return texts;
    }
}
