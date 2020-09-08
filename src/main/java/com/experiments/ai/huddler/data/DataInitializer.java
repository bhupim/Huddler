package com.experiments.ai.huddler.data;

import com.experiments.ai.huddler.model.EItem;
import com.experiments.ai.huddler.model.Item;
import com.experiments.ai.huddler.service.BatchProcessor;
import com.experiments.ai.huddler.utils.AvroClassGenerator;
import com.experiments.ai.huddler.utils.AvroDeserializer;
import com.experiments.ai.huddler.utils.FileReader;
import com.experiments.ai.huddler.utils.FloatingNumberUtils;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataInitializer {

    private final static Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    AppCache appCache;

    @Value( "${partial.record.count}" )
    private int numOfRecordsToLoad;

    @Value( "${data.file.path}" )
    private String dataFilePath;

    @Value( "${schema.file.path}" )
    private String schemaFilePath;

    public void init() throws IOException {
        logger.info("begin init");

        Instant start = Instant.now();
        logger.info("begin init {}", Timestamp.from(start));

        /*AvroClassGenerator classGenerator = new AvroClassGenerator();
        classGenerator.generateAvroClasses();*/

        BlockingQueue<Item> sharedQueue = new LinkedBlockingDeque<>(); //new ArrayBlockingQueue<>(1000);

        Thread fileReaderThread = new Thread(new FileReadingTask(sharedQueue, numOfRecordsToLoad, dataFilePath, schemaFilePath), "FileReadingTask");
        fileReaderThread.start();

        int cpus = Runtime.getRuntime().availableProcessors();
        int maxThreads = cpus/2;
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(maxThreads);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(maxThreads, maxThreads, 10000, TimeUnit.MILLISECONDS, blockingQueue);

        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                executor.execute(r);
            }
        });

        executor.prestartAllCoreThreads();
        for (int threadCounter=0; threadCounter < maxThreads; threadCounter++) {
            Thread recordProcessor = new Thread(new RecordProcessingTask(sharedQueue, appCache), "RecordProcessor#" + threadCounter);
            executor.execute(recordProcessor);
        }

        try {
            executor.awaitTermination(20, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error("Exception while waiting for threads to terminate", e);
        }

        Instant end = Instant.now();

        logger.info("end init. time taken {}", Duration.between(start, end).toMillis());
    }

    private void startBatch() {
        logger.info("begin startBatch");

        Map<Long, List<EItem>> eItemsMapByCategory = appCache.getCategorizedItems();

        BatchProcessor batchProcessor = new BatchProcessor();

        for (long key : eItemsMapByCategory.keySet()) {
            batchProcessor.startBatch(eItemsMapByCategory.get(key));
        }
        logger.info("end startBatch");
    }
}
