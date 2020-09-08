package com.experiments.ai.huddler.utils;

import com.experiments.ai.huddler.model.Item;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.specific.SpecificDatumReader;
import org.mihkel.avro.io.ExtendedJsonDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AvroDeserializer {

    private static Logger logger = LoggerFactory.getLogger(AvroDeserializer.class);

    public Item deSerializeAveoJson(String data) {
        DatumReader<Item> reader = new SpecificDatumReader<>(Item.class);
        try {
            Decoder decoder = new ExtendedJsonDecoder(Item.getClassSchema(), data);
            return reader.read(null, decoder);
        } catch (IOException e) {
            logger.error("Deserialization error {}", e.getMessage());
        }
        return null;
    }
}


