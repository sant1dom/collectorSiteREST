package org.swa.collectorsite.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class JavaLocalDateDeserializer extends JsonDeserializer<LocalDate> {
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String dateAsString = jsonParser.getText();
        try {
            Date date = formatter.parse(dateAsString);
            return LocalDate.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault());
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }
}
