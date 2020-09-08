package com.experiments.ai.huddler.utils;

import com.experiments.ai.huddler.model.EItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.BitSet;
import java.util.List;

public class HammingDistanceCalculator implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(HammingDistanceCalculator.class);

    private EItem baseItem;
    private EItem otherItem;

    public HammingDistanceCalculator(EItem bItem, EItem other) {
        this.baseItem = bItem;
        this.otherItem = other;
    }

    @Override
    public void run() {
        Instant start = Instant.now();
        logger.debug("Execution started for {}", this.baseItem.getItemId());

        if (isDifferentAndNewItem(baseItem, otherItem)) {

            int distance = hammingDistance(baseItem.getEmbedding(), otherItem.getEmbedding());
            baseItem.getHammingDistanceMap().put(otherItem.getItemId(), distance);
        }

        Instant end = Instant.now();
        logger.debug("Execution finished for {} {}", this.baseItem.getItemId(), Duration.between(start, end));
    }

    private boolean isDifferentAndNewItem(EItem baseItem, EItem item2) {
        return (!baseItem.equals(item2)
                && !baseItem.getHammingDistanceMap().containsKey(item2.getItemId()));
    }

    private int hammingDistance (BitSet[] data1, BitSet[] data2) {
        //Bit XOR to get difference of bits
        int distance = 0;
        if (data1.length != data2.length) {
            throw new IllegalArgumentException(String.format("Arrays have different length: x[%d], y[%d]", data1.length, data2.length));
        } else {
            for (int i = 0; i < data1.length; i++) {
                distance = distance + hammingDistance(data1[i], data2[i]);
            }
            return distance;
        }
    }

    private int hammingDistance (BitSet data1, BitSet data2) {
        //Bit XOR to get difference of bits
        data1.xor(data2);

        int difference = data1.cardinality();
        return difference;
    }

    @Deprecated
    private long hammingDistance (byte[][] data1, byte[][] data2) {
        long distance = 0;
        Instant start = Instant.now();
        logger.debug("hammingDistance started {}", this.baseItem.getItemId());

        if (data1.length != data2.length) {
            throw new IllegalArgumentException(String.format("Arrays have different length: x[%d], y[%d]", data1.length, data2.length));
        } else {
            for (int i = 0; i < data1.length; i++) {
                distance = distance + hammingDistance(data1[i], data2[i]);
            }
            Instant end = Instant.now();
            logger.debug("hammingDistance finished {}", Duration.between(start, end));
            return distance;
        }
    }

    private int hammingDistance (byte[] data1, byte[] data2) {
        if (data1.length != data2.length) {
            throw new IllegalArgumentException(String.format("Arrays have different length: x[%d], y[%d]", data1.length, data2.length));
        } else {
            int distance = 0;

            for(int i = 0; i < data1.length; i++) {
                if (data1[i] != data2[i]) {
                    distance++;
                }
            }
            return distance;
        }
    }
}
