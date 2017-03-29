import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.converters.basic.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class LogUtilizzoInterfacciaXML{ //(00)
    public String tipo;
    public String evento;
    public Date data;
    
    public LogUtilizzoInterfacciaXML(String tipo, String evento){   //(01)
        this.tipo = tipo;
        this.evento = evento;
        this.data = new Date();
        
        XStream xs = new XStream();
        ConfigurazioneXML c = new ConfigurazioneXML();
        xs.useAttributeFor(LogUtilizzoInterfacciaXML.class, "tipo");
        xs.registerConverter(new DateConverter("yyyy-MM-dd'T'HH:mm:ss", null));
        String s = xs.toXML(this);
        System.out.println(s);
        try(
            DataOutputStream dos = new DataOutputStream((new Socket(c.server.serverIP, c.server.portaServer).getOutputStream()));
        ){
            dos.writeUTF(s);
        } catch (Exception e){
            System.out.println("Errore nell'invio del log XML al server");
            System.out.println(e.getMessage());
        }
    }
}

/*
Note:
(00)
    La classe LogUtilizzoInterfacciaXML è preposta alla creazione e all'invio
    dei log di utilizzo dell'applicazione. I membri coincidono con le informazioni
    riportate nel log.

(01)
    Il costruttore accetta come parametri i valori di tipo e evento dell'azione 
    che ha generato il log, ai quali aggiunge la data corrente per confezionare
    un log completo, che poi è serializzato e inviato al server di log. I parametri
    di connessione sono reperiti dal file di configurazione XML. Il costruttore
    invia un log per ogni evento che si verifica durante l'esecuzione dell'app,
    in particolare per l'apertura e la chiusura, la pressione di un pulsante
    o il verificarsi di eccezioni.
*/