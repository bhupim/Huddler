package com.experiments.ai.huddler.utils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class FloatingNumberUtils {

    public static byte[] floatToByteArray(float value) {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }

    public static float byteArrayToFloat(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getFloat();
    }

    public static byte[][] floatToByteArray(List<Float> floatList) {
        int size = floatList.size();
        byte[][] mappedByteArray = new byte[size][];

        for (int i=0; i<size; i++) {
            byte[] byteArr = floatToByteArray(floatList.get(i));
            mappedByteArray[i] = byteArr;
        }
        return mappedByteArray;
    }

    public static List<Float> byteArrayToFloat(byte[][] mappedByteArr) {
        List<Float> floatList = new ArrayList<>();
        int size = mappedByteArr.length;

        for (int i=0; i<size; i++) {
            float number = byteArrayToFloat(mappedByteArr[i]);
            floatList.add(i, number);
        }
        return floatList;
    }
}
