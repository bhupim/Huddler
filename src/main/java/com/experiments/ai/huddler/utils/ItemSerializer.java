package com.experiments.ai.huddler.utils;

import com.experiments.ai.huddler.model.EItem;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class ItemSerializer extends StdSerializer<EItem> {

    public ItemSerializer() {
        this(null);
    }

    public ItemSerializer(Class<EItem> t) {
        super(t);
    }

    @Override
    public void serialize(EItem eItem, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("itemId", eItem.getItemId());
        jsonGenerator.writeNumberField("item_category", eItem.getLeaf_categ_id());
        jsonGenerator.writeStringField("gallery_url", eItem.getGalleryUrl());
        jsonGenerator.writeEndObject();
    }

}
