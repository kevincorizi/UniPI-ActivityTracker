import java.util.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.util.converter.*;

public class Attivita{  //(00)
    public SimpleStringProperty dataInizioAttivita;
    public SimpleStringProperty dataFineAttivita;
    public SimpleStringProperty descrizioneAttivita;
    public SimpleIntegerProperty matricola;
    public SimpleListProperty<String> tipoAttivita;
    
    private transient final DateTimeStringConverter dtsc;     //(01)
    
    public Attivita(){  //(02)
        ConfigurazioneXML c = new ConfigurazioneXML();  
        dtsc = new DateTimeStringConverter(c.stile.formatoData);
        dataInizioAttivita = new SimpleStringProperty(""); 
        dataFineAttivita = new SimpleStringProperty("");
        descrizioneAttivita = new SimpleStringProperty("");
        matricola = new SimpleIntegerProperty(0);
        tipoAttivita = new SimpleListProperty<>(FXCollections.emptyObservableList());
    }
    
    public Attivita(Date inizio){    //(03)
        ConfigurazioneXML c = new ConfigurazioneXML();  
        dtsc = new DateTimeStringConverter(c.stile.formatoData);
        dataInizioAttivita = new SimpleStringProperty(dtsc.toString(inizio)); 
        dataFineAttivita = new SimpleStringProperty("");
        descrizioneAttivita = new SimpleStringProperty("");
        matricola = new SimpleIntegerProperty(0);
        tipoAttivita = new SimpleListProperty<>(FXCollections.emptyObservableList());
    }
    
    public Attivita(Date inizio, Date fine, int mat, String desc, ObservableList<String> tipo){  //(04)
        ConfigurazioneXML c = new ConfigurazioneXML();
        dtsc = new DateTimeStringConverter(c.stile.formatoData);
        dataInizioAttivita = new SimpleStringProperty(dtsc.toString(inizio));
        dataFineAttivita = new SimpleStringProperty(dtsc.toString(fine));
        matricola = new SimpleIntegerProperty(mat);
        descrizioneAttivita = new SimpleStringProperty(desc);
        tipoAttivita = new SimpleListProperty<>(tipo);
    }
    
    public Integer getMatricola(){  //(05)
        return matricola.get();
    }
    
    public Date getDataInizioAttivita(){
        return dtsc.fromString(dataInizioAttivita.get());
    }
    
    public Date getDataFineAttivita(){
        return dtsc.fromString(dataFineAttivita.get());
    }
    
    public String getDescrizioneAttivita(){
        return descrizioneAttivita.get();
    }
    
    public ObservableList<String> getTipoAttivita(){
        return tipoAttivita.get();
    }
    
    public void setMatricola(Integer mat){  //(06)
        matricola = new SimpleIntegerProperty(mat);
    }
    
    public void setDataInizioAttivita(Date di){
        dataInizioAttivita = new SimpleStringProperty(dtsc.toString(di));
    }
    
    public void setDataFineAttivita(Date df){
        dataFineAttivita = new SimpleStringProperty(dtsc.toString(df));
    }
    
    public void setTipoAttivita(ArrayList<String> s){
        tipoAttivita = new SimpleListProperty<>(FXCollections.observableArrayList(s));
    }
    
    public void setDescrizioneAttivita(String s){
        descrizioneAttivita = new SimpleStringProperty(s);
    }
}

/*
Note:
(00)
    La classe Attivita è una classe bean che serve a contenere i dati
    relativi a una sessione di lavoro di uno studente.

(01)
    L'oggetto DateTimeStringConverter permette di specificare un formato
    per le date all'interno dell'applicazione e è inizializzato nei 
    costruttori con i parametri ottenuti dal file di configurazione XML.
    https://docs.oracle.com/javase/8/javafx/api/javafx/util/converter/DateTimeStringConverter.html

(02)
    Il costruttore default è utilizzato per il recupero dalla cache locale,
    quando nessuna attività precedente è stata salvata.

(03)
    Il secondo costruttore serve a istanziare un'attività appena iniziata o
    a recuperare dalla cache locale un'attività iniziata ma non terminata.
    A essa viene associata la sola data di inizio, mentre gli altri membri
    sono avvalorati quando lo studente termina la sessione di lavoro.

(04)
    Il terzo costruttore serve a istanziare un'attività appena terminata o
    a recuperare dalla cache locale un'attività iniziata e terminata.
    Gli argomenti coincidono con i membri dell'attività.

(05)
    Metodi GET per la classe bean. Restituiscono l'equivalente non-bean del
    membro della classe Attivita.

(06)
    Metodi SET per la classe bean. Convertono i dati in ingresso in tipi bean
    e li assegnano all'oggetto Attivita.
*/