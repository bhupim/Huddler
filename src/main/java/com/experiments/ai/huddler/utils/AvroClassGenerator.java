package com.experiments.ai.huddler.utils;

import org.apache.avro.Schema;
import org.apache.avro.compiler.specific.SpecificCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class AvroClassGenerator {

    Logger logger = LoggerFactory.getLogger(AvroClassGenerator.class);

    public void generateAvroClasses() throws IOException {
        File schemaFile = new File("C:\\Users\\bhupendm\\Development\\Fun\\item.schema.avsc");
        Schema schema = new Schema.Parser().parse(schemaFile);
        logger.debug("{}", schema);

        SpecificCompiler compiler = new SpecificCompiler(schema);
        compiler.compileToDestination(new File("src/main/resources"), new File("src/main/java"));
    }
}
