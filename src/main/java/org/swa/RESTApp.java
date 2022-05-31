package org.swa;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.swa.jackson.ObjectMapperContextResolver;
import org.swa.security.AppExceptionMapper;
import org.swa.security.CORSFilter;
import org.swa.security.LoggedFilter;


@ApplicationPath("rest")
public class RESTApp extends Application {

    private final Set<Class<?>> classes;

    public RESTApp() {
        HashSet<Class<?>> c = new HashSet<>();
        //aggiungiamo tutte le *root resurces* (cioè quelle
        //con l'annotazione Path) che vogliamo pubblicare

        /*c.add(FattureResource.class);
        c.add(ProdottiResource.class);
        c.add(AutenticazioneResource.class);*/

        //aggiungiamo il provider Jackson per poter
        //usare i suoi servizi di serializzazione e 
        //deserializzazione JSON
        c.add(JacksonJsonProvider.class);

        //necessario se vogliamo una (de)serializzazione custom di qualche classe
        c.add(ObjectMapperContextResolver.class);

        //esempio di autenticazione
        c.add(LoggedFilter.class);

        //aggiungiamo il filtro che gestisce gli header CORS
        c.add(CORSFilter.class);

        //esempio di exception mapper, che mappa in Response eccezioni non già derivanti da WebApplicationException
        c.add(AppExceptionMapper.class);

        classes = Collections.unmodifiableSet(c);
    }

    //l'override di questo metodo deve restituire il set
    //di classi che Jersey utilizzerà per pubblicare il
    //servizio. Tutte le altre, anche se annotate, verranno
    //IGNORATE
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}