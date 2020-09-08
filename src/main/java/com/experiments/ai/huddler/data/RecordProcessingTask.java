package com.experiments.ai.huddler.data;

import com.experiments.ai.huddler.model.EItem;
import com.experiments.ai.huddler.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class RecordProcessingTask implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(RecordProcessingTask.class);

    private final BlockingQueue<Item> recordQueue;
    private final AppCache appCacheToBeUpdated;


    public RecordProcessingTask(BlockingQueue<Item> recQueue, AppCache appCache) {
        this.recordQueue = recQueue;
        this.appCacheToBeUpdated = appCache;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Item item = recordQueue.take();

                if (item != null) {
                    EItem eItem = new EItem();

                    eItem.setItemId(item.getItemId());
                    eItem.setLeaf_categ_id(item.getLeafCategId());
                    eItem.setGallery_url(item.getGalleryUrl().toString());
                    eItem.setEmbedding(mapEncodingToBitset(item.getEmbedding(), 7, 24));

                    appCacheToBeUpdated.addItemToCache(eItem);
                }

            } catch (InterruptedException ex) {
                logger.error("Exception while processing data ", ex);
            }
        }
    }

    /**
     * This mapping assumes embeddings are float with one digit before decimal.
     * For now assuming precision only up to 7 digits after decimal
     * @param embeddings
     * @return
     */
    private BitSet[] mapEncodingToBitset(List<Float> embeddings, int precision, int bitCount) {
        int len = embeddings.size();
        BitSet[] bitSetArr = new BitSet[len];

        for (int i=0; i<len; i++) {
            float floatVal = embeddings.get(i);

            int shiftedInt = (int) (floatVal * Math.pow(10, precision));

            BitSet bitSet = bitsetFromNumber(shiftedInt, bitCount);

            bitSetArr[i] = bitSet;
        }
        return bitSetArr;
    }

    private BitSet bitsetFromNumber(int number, int numberOfBits) {
        BitSet bitset = new BitSet(numberOfBits);

        String binary = Integer.toBinaryString(number);

        if (binary.length() > numberOfBits) {
            for (int i = numberOfBits - 1; i >= 0; i--) {
                if (binary.charAt(i) == '1') {
                    bitset.set(i);
                }
            }
        } else {
            int bitIndex = numberOfBits - 1;
            for (int i=binary.length()-1; i >= 0; i--) {

                if (binary.charAt(i) == '1') {
                    bitset.set(bitIndex);
                    bitIndex--;
                }
            }
        }

        return bitset;
    }
}
