package org.swa.collectorsite.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class JavaLocalDateSerializer extends JsonSerializer<LocalDate> {
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String dateAsString = formatter.format(localDate);
        jsonGenerator.writeString(dateAsString);
    }
}
