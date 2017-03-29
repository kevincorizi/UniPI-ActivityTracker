import java.io.*;
import javafx.collections.*;

public class GestoreCacheLocale {   //(00)
    private final String percorso; 
    
    public GestoreCacheLocale(){    //(01)
        ConfigurazioneXML c = new ConfigurazioneXML();
        percorso = c.cache.percorso;
    }
    
    public void salvaAttivitaBin(Attivita a){   //(02)
        try(
            FileOutputStream fout = new FileOutputStream(percorso);
            ObjectOutputStream oout = new ObjectOutputStream(fout);
        ){
            if(a != null){
                AttivitaSerializzabile as = new AttivitaSerializzabile(a);
                oout.writeObject(as);
            }
            else{
                oout.writeUTF("");
            }
        }catch (IOException e){
            new LogUtilizzoInterfacciaXML("Eccezione", e.getMessage());
        }
    }
    
    public Attivita recuperaAttivitaBin(){  //(03)
        Attivita a = new Attivita();
        try(
            FileInputStream fin = new FileInputStream(percorso);
            ObjectInputStream oin = new ObjectInputStream(fin);
        ){
            AttivitaSerializzabile as = (AttivitaSerializzabile)(oin.readObject());
            a = new Attivita(as.dataInizioAttivita, 
                    as.dataFineAttivita, as.matricola, 
                    as.descrizioneAttivita, FXCollections.observableArrayList(as.tipoAttivita));
        }catch (Exception e){
            if(!(e instanceof EOFException)){
                new LogUtilizzoInterfacciaXML("Eccezione", e.getMessage());
            }
        }
        return a;
    }
}

/*
Note:
(00)
    La classe GestoreCacheLocale è preposta alla gestione del file di
    cache binario e contiene il percorso del file di cache, ottenuto 
    dal file di configurazione XML nel costruttore.   

(01)
    Il costruttore di default si occupa dell'ottenimento del percorso
    del file di cache locale dal file XML di configurazione.

(02)
    Il metodo salvaAttivitaBin() si occupa del salvataggio in cache locale
    dell'attività più recentemente inserita ma non ancora inviata alla
    base di dati. E' richiamato alla chiusura dell'applicazione.

(03)
    Il metodo recuperaAttivitaBin() si occupa del recupero dalla cache locale
    dell'attività più recentemente salvata ma non ancora inviata alla
    base di dati. E' richiamato all'apertura dell'applicazione.
*/