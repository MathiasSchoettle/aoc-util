package org.mschoe.aocutil.lib.transform.impl;

import org.mschoe.aocutil.lib.transform.Transformer;

import java.util.List;

public class StringListTransformer implements Transformer<List<String>> {
    @Override
    public List<String> transform(String input) {
        return input.lines().toList();
    }
}
