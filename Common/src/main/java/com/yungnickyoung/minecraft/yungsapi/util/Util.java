package com.yungnickyoung.minecraft.yungsapi.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Random;

public class Util {
    public static <T> void shuffle(ObjectArrayList<T> list, Random random) {
        int size = list.size();

        for(int i = size; i > 1; --i) {
            int n = random.nextInt(i);
            list.set(i - 1, list.set(n, list.get(i - 1)));
        }

    }
}
