package org.mschoe.aocutil.lib.transform.impl;

import org.mschoe.aocutil.lib.transform.Transformer;

import java.util.List;

public class IntegerListTransformer implements Transformer<List<Integer>> {
    @Override
    public List<Integer> transform(String input) {
        return input.lines().map(Integer::parseInt).toList();
    }
}
