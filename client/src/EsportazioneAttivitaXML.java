import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.converters.basic.*;
import java.nio.file.*;
import java.util.*;
import javafx.collections.*;

public class EsportazioneAttivitaXML {
    public String esportaListaAttivitaXML(ObservableList<Attivita> lista){       
        String esito = "Non ci sono attività da esportare";
        if(lista.isEmpty()){
            return esito;
        }
        XStream xs = new XStream();
        xs.registerConverter(new DateConverter("yyyy-MM-dd'T'HH:mm:ss", null));
        xs.alias("Attivita", AttivitaSerializzabile.class);
        ArrayList<AttivitaSerializzabile> listaEsportazione = new ArrayList<>();
        lista.forEach((Attivita a) -> {
            listaEsportazione.add(new AttivitaSerializzabile(a));
        });
        String s = xs.toXML(listaEsportazione);
        System.out.println(s);
        try{
            ValidazioneXML v = new ValidazioneXML();
            if(v.validaXML(s, "./myfiles/xsd/xsd_lista_attivita.xsd")){
                Files.write(Paths.get("./myfiles/xml/lista_attivita.xml"), s.getBytes(), StandardOpenOption.CREATE);
                esito = "Lista delle attività esportata con successo";
            }
        } catch (Exception e){
            new LogUtilizzoInterfacciaXML("Eccezione", e.getMessage());
            return esito;
        }
        return esito;
    }
}
