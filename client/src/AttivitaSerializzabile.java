import java.io.*;
import java.util.*;

public class AttivitaSerializzabile implements Serializable{  //(00)
    public Date dataInizioAttivita;
    public Date dataFineAttivita;
    public String descrizioneAttivita;
    public Integer matricola;
    public ArrayList<String> tipoAttivita;
    
    public AttivitaSerializzabile(Attivita a){    //(01)
        dataInizioAttivita = a.getDataInizioAttivita();
        dataFineAttivita = a.getDataFineAttivita();
        descrizioneAttivita = a.getDescrizioneAttivita();
        matricola = a.getMatricola();
        tipoAttivita = new ArrayList<>(a.getTipoAttivita());
    }
}

/*
Note:
(00)
    La classe AttivitaSerializzabile si rende necessaria per sopperire alla mancata
    implementazione dell'interfaccia Serializable da parte degli oggetti bean.
    Contiene gli stessi membri della classe Attivita, ma convertiti a tipi
    semplici. La classe è Serializable, e viene utilizzata come struttura dati
    di riferimento per il salvataggio e il recupero delle attività dalla cache e
    per l'esportazione della lista delle attività in formato XML.

(02)
    Il costruttore effettua la conversione di una Attivita in una AttivitaSerializzabile
    pronta per la serializzazione.
*/