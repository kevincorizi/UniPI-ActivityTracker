import java.sql.*;
import java.util.*;
import javafx.collections.*;
import javafx.util.converter.*;

public class MemorizzazioneAttivitaDB {     //(00) 
    private final String conn;
    private final String utente;
    private final String password;
    
    private final DateTimeStringConverter dtsc; //(01)
    
    public MemorizzazioneAttivitaDB(){  //(02)
        ConfigurazioneXML c = new ConfigurazioneXML();
        dtsc = new DateTimeStringConverter(c.stile.formatoData);
        conn = c.database.nomeConnessione;
        utente = c.database.nomeUtente;
        password = c.database.password;
    }
    
    public String salvaAttivitaDB(Attivita a){    //(03)
        String esito = "Attività memorizzata con successo";
        try(
           Connection co = DriverManager.getConnection(conn, utente, password);
           PreparedStatement ps = co.prepareStatement("INSERT INTO attivita (matricola, dataInizioAttivita, dataFineAttivita, descrizioneAttivita, tipoAttivita) VALUES (?, ?, ?, ?, ?)");
        ){    
            ObservableList<Attivita> tutte = recuperaAttivitaDB();
            if(!tutte.isEmpty()){
                Attivita ultima = tutte.get(tutte.size() - 1);
                if(ultima.getDataFineAttivita() != null && a.getDataFineAttivita() != null)
                    esito = "Non hai attività in sospeso da terminare";
                else if(ultima.getDataInizioAttivita() != null && a.getDataInizioAttivita() != null)
                    esito = "Hai già un'attività in sospeso";
                else if(ultima.getDataInizioAttivita() != null && a.getDataFineAttivita().compareTo(ultima.getDataInizioAttivita()) < 0)
                    esito = "Inserisci una data di fine attività valida";
                else if(ultima.getDataFineAttivita() != null && a.getDataInizioAttivita().compareTo(ultima.getDataFineAttivita()) < 0)
                    esito = "Inserisci una data di inizio attività valida";
                if(esito.compareTo("Attività memorizzata con successo") != 0)
                    return esito;
            }
            java.sql.Timestamp di, df;
            if(a.getDataInizioAttivita() != null)
                di = new java.sql.Timestamp(a.getDataInizioAttivita().getTime());
            else
                di = new java.sql.Timestamp(0);
            if(a.getDataFineAttivita() != null)
                df = new java.sql.Timestamp(a.getDataFineAttivita().getTime());
            else
                df = new java.sql.Timestamp(0);
            String s = Arrays.deepToString(a.getTipoAttivita().toArray(new String[0]));
            ps.setInt(1, a.getMatricola());
            ps.setTimestamp(2, di);
            ps.setTimestamp(3, df);
            ps.setString(4, a.getDescrizioneAttivita());
            ps.setString(5, s);
            ps.executeUpdate();
        } catch (SQLException e){
            new LogUtilizzoInterfacciaXML("Eccezione", e.getMessage());
            esito = "Errore nell'inserimento dell'attività nella base di dati";
        }
        return esito;
    }
    
    public ObservableList<Attivita> recuperaAttivitaDB(){   //(04)
        ObservableList<Attivita> ol = FXCollections.observableArrayList();
        try(
           Connection co = DriverManager.getConnection(conn, utente, password);
           Statement st = co.createStatement();
        ){
           ResultSet rs = st.executeQuery("SELECT * FROM attivita;");
           while(rs.next()){
               String di = dtsc.toString(rs.getTimestamp("dataInizioAttivita"));
               if(di.compareTo("01/01/1970 01:00") == 0)
                   di = null;
               String df = dtsc.toString(rs.getTimestamp("dataFineAttivita")); 
               if(df.compareTo("01/01/1970 01:00") == 0)
                   df = null;
               ObservableList<String> ols = FXCollections.observableArrayList((rs.getString("tipoAttivita").replaceAll("[\\[\\]]", "")).split(", "));
               ol.add(new Attivita(dtsc.fromString(di), 
                       dtsc.fromString(df), 
                       rs.getInt("matricola"), rs.getString("descrizioneAttivita"), ols));
           }
        } catch (SQLException e){
            new LogUtilizzoInterfacciaXML("Eccezione", e.getMessage());
        }
        return ol;
    }
}

/*
Note:
(00)
    La classe MemorizzazioneAttivitaDB è preposta alla gestione della base
    di dati. Il membro conn mantiene il nome della connessione alla base di dati, 
    comprensivo di protocollo e porta.
    Il membro utente mantiene il nome dell'utente da utilizzare per accedere
    alla base di dati.
    Il membro password mantiene la password da utilizzare per accedere
    alla base di dati.

(01)
    L'oggetto DateTimeStringConverter permette di specificare un formato
    per le date all'interno dell'applicazione e è inizializzato nel 
    costruttore con i parametri ottenuti dal file di configurazione XML.

(02)
    Il costruttore default recupera dal file di configurazione XML i parametri
    relativi alla base di dati e li memorizza nei membri della classe.

(03)
    Il metodo salvaAttivitaDB() si occupa del salvataggio nella base di dati
    di un'attività appena conclusa. Il metodo si occupa anche della corretta    
    conversione della data in formato MySQL. Il metodo effettua controlli
    semantici sull'inserimento in corso e restituisce tramite una stringa
    l'esito dell'inserimento.

(04)
    Il metodo recuperaAttivitaDB() si occupa del recupero dalla base di dati
    di tutte le attività inserite in passato. Il metodo si occupa anche della 
    corretta conversione della data in formato applicazione.
*/