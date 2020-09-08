package com.experiments.ai.huddler.service;

import com.experiments.ai.huddler.model.EItem;
import com.experiments.ai.huddler.utils.HammingDistanceCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

public class ItemMatcher {

    private final static Logger logger = LoggerFactory.getLogger(ItemMatcher.class);

    public void matchItems(EItem baseItem, List<EItem> categorizedItems) {
        logger.info("begin matchItems");
        int numOfProcessors = Runtime.getRuntime().availableProcessors() / 2;

        BlockingQueue blockingQueue = new ArrayBlockingQueue(numOfProcessors);

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(numOfProcessors, numOfProcessors,
                20, TimeUnit.SECONDS, blockingQueue);

        threadPoolExecutor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
               @Override
               public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                   logger.warn("Thread was Rejected. Queue must be full");
                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException ex) {
                        logger.error("Thread was interrupted {}", ex);
                   }
                   logger.debug("Retrying rejected thread. {}", r.hashCode());
                   threadPoolExecutor.execute(r);
               }
           }
        );

        threadPoolExecutor.prestartAllCoreThreads();

        //Submit all items to be processed
        for (EItem item: categorizedItems) {
            threadPoolExecutor.execute(new HammingDistanceCalculator(baseItem, item));
        }

        //Lets wait for all threads to be finished

        try {
            threadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            logger.error("Execution was interrupted {}", ex);
        }
        logger.info("end matchItems");
    }



}
