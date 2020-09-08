package com.experiments.ai.huddler.data;

import com.experiments.ai.huddler.model.Item;
import com.experiments.ai.huddler.utils.AvroDeserializer;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class FileReadingTask implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(FileReadingTask.class);

    private final BlockingQueue<Item> recordQueue;
    private Set<Long> readRecordIds = new HashSet<>();
    private int numOfRecordsToLoad;
    private String dataFilePath;
    private String schemaFilePath;

    public FileReadingTask(BlockingQueue<Item> recQueue, int numOfRecords, String dFilePath, String sFilePath) {
        this.recordQueue = recQueue;
        this.numOfRecordsToLoad = numOfRecords;
        this.dataFilePath = dFilePath;
        this.schemaFilePath = sFilePath;
    }

    @Override
    public void run() {
        try {
            Schema schema = readSchemaFromFile();
            DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);

            //File dataFile = new File("C:\\Users\\bhupendm\\Development\\Fun\\items.snappy.avro");
            File dataFile = new File(dataFilePath);
            DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(dataFile, datumReader);

            GenericRecord record = null;
            int recordCount = 0;
            while (dataFileReader.hasNext()) {
                record = dataFileReader.next(record);

                Item item = new AvroDeserializer().deSerializeAveoJson(record.toString());

                if (item != null && !readRecordIds.contains(item.getItemId())) {
                    recordQueue.put(item);
                    readRecordIds.add(item.getItemId());
                }

                if (numOfRecordsToLoad != -1 && recordCount >= numOfRecordsToLoad)
                    break;
            }
        } catch (Exception ex) {
            logger.error("Exception while reading file ", ex);
        }
    }

    private Schema readSchemaFromFile() throws IOException {
        //File schemaFile = new File("C:\\Users\\bhupendm\\Development\\Fun\\item.schema.avsc");
        File schemaFile = new File(schemaFilePath);
        Schema schema = new Schema.Parser().parse(schemaFile);
        logger.debug("{}", schema);
        return schema;
    }
}