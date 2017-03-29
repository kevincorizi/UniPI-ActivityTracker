import java.text.*;
import java.util.*;
import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.time.*;
import java.time.format.*;
import java.util.concurrent.TimeUnit;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.chart.*;

public class RegistroAttività extends Application { //(00)
    private GridPane contenitore;   //(01)
    private TextField matricola;    //(02)
    private ToggleGroup inizioFine; //(03)
    private CheckBox tipi[];        //(04)
    private DatePicker data;        //(05)
    private ComboBox ore;           //(06)
    private ComboBox minuti;        //(07)
    private TextArea descrizione;   //(08)
    private Label messaggi;         //(09)
    
    private final String[] _nomiTipi = {
            "Analisi e specifica",
            "Progetto",
            "Codifica",
            "Prototipazione",
            "Documentazione",
            "Collaudo e verifica"
    };
    private final ArrayList<String> nomiTipi = new ArrayList<>(Arrays.asList(_nomiTipi));
    
    private GestoreCacheLocale gcl;     //(10)
    private MemorizzazioneAttivitaDB mad;   //(11)
    private ConfigurazioneXML cxml;     //(12)
    private EsportazioneAttivitaXML eax;    //(13)
    private Attivita a;     //(14)
    
    public HBox ottieniCampoMatricola(){    //(15)
        HBox campoMatricola = new HBox(10);
        campoMatricola.setPadding(new Insets(25, 10, 0, 0));
        Label mat = new Label("Matricola: ");
        matricola = new TextField();
        campoMatricola.getChildren().addAll(mat, matricola);
        return campoMatricola;
    }
    
    public HBox ottieniCampoInizioFine(){   //(16)
        HBox campoInizioFine = new HBox(10);
        campoInizioFine.setPrefWidth(Double.MAX_VALUE);
        campoInizioFine.setPadding(new Insets(10, 10, 0, 0));
        inizioFine = new ToggleGroup();
        RadioButton inizio = new RadioButton("Inizio ");
        RadioButton fine = new RadioButton("Fine ");
        inizio.setToggleGroup(inizioFine);
        fine.setToggleGroup(inizioFine);
        inizio.setSelected(true);
        campoInizioFine.getChildren().addAll(inizio, fine);
        return campoInizioFine;
    }
    
    public VBox ottieniCampoTipo(){ //(17)
        VBox campoTipo = new VBox();
        campoTipo.setPadding(new Insets(10, 10, 0, 0));
        Label t = new Label("Tipo attività");
        t.setPadding(new Insets(0, 5, 5, 0));
        t.setPrefWidth(Double.MAX_VALUE);
        campoTipo.getChildren().add(t);
        tipi = new CheckBox[nomiTipi.size()];
        for(int i = 0; i < nomiTipi.size(); i++){
            tipi[i] = new CheckBox(nomiTipi.get(i));
            tipi[i].setPrefWidth(Double.MAX_VALUE);
            tipi[i].setPadding(new Insets(0, 5, 5, 0));
            campoTipo.getChildren().add(tipi[i]);
        }
        return campoTipo;
    }
   
    public VBox ottieniCampoOrario(){   //(18)
        VBox campoOrario = new VBox();
        campoOrario.setPadding(new Insets(10, 10, 0, 0));
        Label d = new Label("Orario di riferimento");
        d.setPrefWidth(Double.MAX_VALUE);
        d.setPadding(new Insets(0, 5, 5, 0));
        data = new DatePicker();
        HBox ora = new HBox(10);
        ora.setPadding(new Insets(10, 10, 0, 0));
        Label o = new Label("Ora");
        ore = new ComboBox();
        for(Integer i = 0; i < 24; i++)
            ore.getItems().add(String.format("%02d", i));
        Label m = new Label("Minuti");
        minuti = new ComboBox();
        for(Integer i = 0; i < 60; i++)
            minuti.getItems().add(String.format("%02d", i));
        ora.getChildren().addAll(o, ore, m, minuti);     
        campoOrario.getChildren().addAll(d, data, ora);
        return campoOrario;
    }
   
    public VBox ottieniCampoDescrizione(){  //(19)
        VBox campoDescrizione = new VBox();
        campoDescrizione.setPadding(new Insets(0, 10, 0, 0));
        Label ld = new Label("Descrizione attività");
        ld.setPrefWidth(Double.MAX_VALUE);
        ld.setPadding(new Insets(0, 5, 5, 0));
        descrizione = new TextArea();
        descrizione.setWrapText(true);
        descrizione.setPrefWidth(Double.MAX_VALUE);
        descrizione.setPromptText("Che cosa hai fatto?");
        campoDescrizione.getChildren().addAll(ld, descrizione);
        return campoDescrizione;
    }
    
    public HBox ottieniCampoPulsanti(){ //(20)
        HBox campoPulsanti = new HBox(10);
        campoPulsanti.setPadding(new Insets(10, 10, 0, 0));
        Button invia = new Button("Invia");
        invia.setOnAction((ActionEvent event) ->{
            inviaAttivita();
            new LogUtilizzoInterfacciaXML("Bottone", "Invia");
        });
        Button reset = new Button("Reset");
        reset.setOnAction((ActionEvent event) ->{
            resetCampi();
            new LogUtilizzoInterfacciaXML("Bottone", "Reset");
        });
        Button esporta = new Button("Esporta");
        esporta.setOnAction((ActionEvent event) -> {
            esportaAttivita();
            new LogUtilizzoInterfacciaXML("Bottone", "Esporta");
        });
        campoPulsanti.getChildren().addAll(invia, reset, esporta);
        return campoPulsanti;
    }
    
    public Label ottieniCampoMessaggi(){    //(21)
        messaggi = new Label("Non ci sono messaggi");
        messaggi.setPrefWidth(Double.MAX_VALUE);
        messaggi.setPadding(new Insets(10, 10, 0, 0));
        messaggi.setWrapText(true);
        return messaggi;
    }
    
    public LineChart ottieniGrafico(){  //(22)
        NumberAxis asseX = new NumberAxis();
        asseX.setLabel("Ore di lavoro"); asseX.setAutoRanging(false);
        asseX.setLowerBound(0); asseX.setUpperBound(cxml.progetto.numeroMassimoOre);
        asseX.setTickUnit(1); asseX.setMinorTickCount(2);
        NumberAxis asseY = new NumberAxis();
        asseY.setLabel("Attività svolte");  asseY.setAutoRanging(false);
        asseY.setLowerBound(0); asseY.setUpperBound(13);
        asseY.setTickUnit(2); asseY.setMinorTickCount(1); asseY.setTickLabelsVisible(false);   
        Integer offset[] = {11, 9, 7, 5, 3, 1};    
        LineChart<Number, Number> grafico = new LineChart<>(asseX, asseY);
        grafico.setTitle("Andamento");
        grafico.setPrefWidth(Double.MAX_VALUE);
        grafico.setCreateSymbols(false);    
        ArrayList<XYChart.Series> componenti = new ArrayList<>();       
        for (String s : nomiTipi) {
            XYChart.Series linea = new XYChart.Series();
            linea.setName(s);
            componenti.add(linea);
            grafico.getData().add(linea);
        }
        ObservableList<Attivita> attivita = mad.recuperaAttivitaDB();  
        int lun = (attivita.size() % 2) == 0 ? attivita.size() : (attivita.size() - 1);
        double inizio = 0.0;
        double durata = 0.0;
        for (int i = 0; i < lun - 1; i += 2) {
            Attivita a1 = attivita.get(i);
            Attivita a2 = attivita.get(i + 1);
            inizio += durata;
            durata = Math.ceil(((TimeUnit.MILLISECONDS.toMinutes(a2.getDataFineAttivita().getTime() - a1.getDataInizioAttivita().getTime())) / 60.0) * 2) / 2;
            for (int j = 0; j < nomiTipi.size(); j++) {
                if(a2.getTipoAttivita().contains(nomiTipi.get(j))){
                    componenti.get(j).getData().add(new XYChart.Data(inizio, offset[j] + 1));
                    componenti.get(j).getData().add(new XYChart.Data(inizio + durata, offset[j] + 1));
                }
                else{
                    componenti.get(j).getData().add(new XYChart.Data(inizio, offset[j]));
                    componenti.get(j).getData().add(new XYChart.Data(inizio + durata, offset[j]));
                }
            }
        } 
        return grafico;
    }
    
    public VBox ottieniColonnaSinistra(){   //(23)
        VBox colonnaSinistra = new VBox();
        colonnaSinistra.getChildren().addAll(
                ottieniCampoMatricola(), 
                ottieniCampoInizioFine(), 
                ottieniCampoTipo(),
                ottieniCampoOrario()
        );
        return colonnaSinistra;
    }
    
    public VBox ottieniColonnaDestra(){     //(24)
        VBox colonnaDestra = new VBox();
        colonnaDestra.getChildren().addAll(
                ottieniCampoDescrizione(), 
                ottieniCampoPulsanti(), 
                ottieniCampoMessaggi());
        return colonnaDestra;
    }
    
    public GridPane ottieniInterfaccia(){   //(25)
        contenitore = new GridPane();
        contenitore.setHgap(10);
        contenitore.setVgap(10);
        contenitore.setPadding(new Insets(10, 10, 10, 10));
        contenitore.setStyle("-fx-font-family: " + cxml.stile.font + "; -fx-font-size: " + cxml.stile.dimensioneFont);
        RowConstraints rigaSopra = new RowConstraints();
        RowConstraints rigaSotto = new RowConstraints();
        rigaSopra.setPercentHeight(50);
        rigaSotto.setPercentHeight(50);
        ColumnConstraints colonnaSinistra = new ColumnConstraints();
        ColumnConstraints colonnaDestra = new ColumnConstraints();
        colonnaSinistra.setPercentWidth(50);
        colonnaDestra.setPercentWidth(50);
        contenitore.getRowConstraints().addAll(rigaSopra, rigaSotto);
        contenitore.getColumnConstraints().addAll(colonnaSinistra, colonnaDestra);
        contenitore.add(ottieniColonnaSinistra(), 0, 0);
        contenitore.add(ottieniColonnaDestra(), 1, 0);
        contenitore.add(ottieniGrafico(), 0, 1, 2, 1);
        return contenitore;
    }

    public void avvaloraInterfaccia(Attivita a){    //(26)
        if(a.getMatricola() != 0){
            matricola.setText(a.getMatricola().toString());
        }  
        if(a.getTipoAttivita() != null){
            for (CheckBox c : tipi) {
                if (a.getTipoAttivita().contains(c.getText())) {
                    c.setSelected(true);
                }
            }
        }
        if(a.getDataInizioAttivita() != null){
            data.setValue(LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(a.getDataInizioAttivita())));
            ore.setValue(new SimpleDateFormat("HH").format(a.getDataInizioAttivita()));
            minuti.setValue(new SimpleDateFormat("mm").format(a.getDataInizioAttivita()));
        }
        if(a.getDataFineAttivita() != null){
            data.setValue(LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(a.getDataFineAttivita())));
            ore.setValue(new SimpleDateFormat("HH").format(a.getDataFineAttivita()));
            minuti.setValue(new SimpleDateFormat("mm").format(a.getDataFineAttivita()));
        }
        descrizione.setText(a.getDescrizioneAttivita());
    }
    
    public void aggiornaAttivita(){     //(27)
        if(matricola.getText().equals("") && data.getValue() == null && ore.getValue() == null && minuti.getValue() == null && descrizione.getText().equals("")){
            a = null;
            return;
        }
        a.setMatricola(0);
        if(!matricola.getText().equals(""))
            if(matricola.getText().matches("[0-9][0-9][0-9][0-9][0-9][0-9]"))
                a.setMatricola(Integer.parseInt(matricola.getText()));  
        String d;
        if(data.getValue() != null)
            d = data.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        else
            d = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        d += " ";
        if(ore.getValue() != null)            
            d += (String)ore.getValue();
        else
            d += LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH"));
        d += ":";
        if(minuti.getValue() != null)
            d += (String)minuti.getValue();
        else
            d += LocalDateTime.now().format(DateTimeFormatter.ofPattern("mm"));
        try{
            if(inizioFine.getSelectedToggle() == inizioFine.getToggles().get(0)){
                a.setDataInizioAttivita(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(d)); 
                a.setDataFineAttivita(null);
            }  
            else{
                a.setDataFineAttivita(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(d));
                a.setDataInizioAttivita(null);
            }
        } catch (ParseException pe) { new LogUtilizzoInterfacciaXML("Eccezione", pe.getMessage()); }
        a.setDescrizioneAttivita(descrizione.getText());
        ArrayList<String> s = new ArrayList<>();
        for(CheckBox c : tipi)
            if(c.isSelected())
               s.add(c.getText());
        a.setTipoAttivita(s);
    }
    
    public void inviaAttivita(){    //(28)
        aggiornaAttivita();
        if(a == null){
            messaggi.setText("Devi compilare i campi");
            return;
        }
        Boolean matricolaOK = (a.getMatricola() != 0);
        Boolean inizio = inizioFine.getToggles().get(0).isSelected();
        Boolean fine = inizioFine.getToggles().get(1).isSelected();
        Boolean descrizioneInizioOK = (fine || (inizio && a.getDescrizioneAttivita().length() == 0));
        Boolean descrizioneFineOK = (inizio || (fine && a.getDescrizioneAttivita().length() > 0 && a.getDescrizioneAttivita().length() <= 500));
        int numeroTipi = 0;
        for(CheckBox c : tipi)
            if(c.isSelected())
                numeroTipi++;
        Boolean tipiInizioOK = (fine || (numeroTipi == 0 && inizio));
        Boolean tipiFineOK = (inizio || (numeroTipi > 0 && fine));
        if(matricolaOK && ((inizio && descrizioneInizioOK && tipiInizioOK) || (fine && descrizioneFineOK && tipiFineOK))){
            String esito = mad.salvaAttivitaDB(a);
            System.out.println(esito);
            if(esito.equals("Attività memorizzata con successo")){
                contenitore.getChildren().remove(2);
                contenitore.add(ottieniGrafico(), 0, 1, 2, 1);
                resetCampi();
            }
            messaggi.setText(esito);
        }
        if(!matricolaOK)
            messaggi.setText("Inserisci una matricola valida");
        if(!(inizio && descrizioneInizioOK && tipiInizioOK)){
            if(!descrizioneInizioOK)
                messaggi.setText("Puoi dire quello che hai fatto solo quando termini l'attività");
            else if(!tipiInizioOK)
                messaggi.setText("Per selezionare un tipo devi terminare l'attività");
        }
        if(!(fine && descrizioneFineOK && tipiFineOK)){
            if(!descrizioneFineOK)
                messaggi.setText("La descrizione non deve essere vuota e non può superare i 500 caratteri");
            else if(!tipiFineOK)
                messaggi.setText("Devi selezionare almeno un tipo di attività");
        }
    }
    
    public void resetCampi(){   //(29)
        matricola.setText("");
        inizioFine.selectToggle(inizioFine.getToggles().get(0)); 
        for(CheckBox c : tipi){
            c.setSelected(false);
        }
        data.setValue(null);
        ore.setValue(null);
        minuti.setValue(null);
        descrizione.setText("");
        messaggi.setText("Non ci sono messaggi");
    }
    
    public void esportaAttivita(){  //(30)
        String esito = eax.esportaListaAttivitaXML(mad.recuperaAttivitaDB());
        messaggi.setText(esito);
    }

    @Override
    public void start(Stage stage) {    //(31)
        cxml = new ConfigurazioneXML();
        gcl = new GestoreCacheLocale();   
        mad = new MemorizzazioneAttivitaDB();
        eax = new EsportazioneAttivitaXML();
        new LogUtilizzoInterfacciaXML("Evento", "Apertura applicazione"); 
        
        a = gcl.recuperaAttivitaBin();
        Scene scene = new Scene(ottieniInterfaccia(), 800, 600);
        stage.setTitle("Registro Attività");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setOnCloseRequest((WindowEvent we) -> {
            aggiornaAttivita();
            gcl.salvaAttivitaBin(a);
            new LogUtilizzoInterfacciaXML("Evento", "Chiusura applicazione");
        });
        stage.show(); 
        if(a != null)
            avvaloraInterfaccia(a);
    }
}

/*
Note:
(00)
    La classe RegistroAttivita fornisce l'interfaccia grafica e permette
    all'utente di interagire con l'applicazione.
(01)
    https://docs.oracle.com/javafx/2/api/javafx/scene/layout/GridPane.html
(02)
    https://docs.oracle.com/javafx/2/api/javafx/scene/control/TextField.html
(03)
    https://docs.oracle.com/javafx/2/api/javafx/scene/control/ToggleGroup.html
(04)
    https://docs.oracle.com/javafx/2/api/javafx/scene/control/CheckBox.html
(05)
    https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/DatePicker.html
(06)(07)
    https://docs.oracle.com/javafx/2/api/javafx/scene/control/ComboBox.html
(08)
    https://docs.oracle.com/javafx/2/api/javafx/scene/control/TextArea.html
(09)
    https://docs.oracle.com/javafx/2/api/javafx/scene/control/Label.html
(10)
    Il membro gcl è utilizzato per l'interazione con la cache locale
(11)
    Il membro mad è utilizzato per l'interazione con la base di dati
(12)
    Il membro cxml è utilizzato per l'ottenimento del file di configurazione XML
(13)
    Il membro eax è utilizzato per l'esportazione della lista delle attività
    in formato XML.
(14)
    Il membro a è utilizzato per tenere traccia dell'attività dell'utente 
    durante l'utilizzo dell'applicazione.
(15)
    Il metodo ottieniCampoMatricola() è utilizzato per ottenere la porzione
    di interfaccia grafica relativa all'inserimento del numero di matricola.
(16)
    Il metodo ottieniCampoInizioFine() è utilizzato per ottenere la porzione
    di interfaccia grafica relativa alla scelta tra inizio o fine attività. Il
    metodo istanzia due oggetti di tipo RadioButton e li assegna al ToggleGroup.
    https://docs.oracle.com/javafx/2/api/javafx/scene/control/RadioButton.html
(17)
    Il metodo ottieniCampoTipo() è utilizzato per ottenere la porzione di 
    interfaccia relativa alla scelta del tipo di attività appena conclusa.
(18)
    Il metodo ottieniCampoOrario() è utilizzato per ottenere la porzione di
    interfaccia relativa all'indicazione dell'orario dell'attività che si vuole 
    registrare. Per la data è utilizzato un DatePicker, mentre per l'orario 
    sono utilizzati due ComboBox, uno per l'ora, uno per i minuti.
(19)
    Il metodo ottieniCampoDescrizione() è utilizzato per ottenere la porzione 
    interfaccia relativa alla descrizione dell'attività appena conclusa.
(20)
    Il metodo ottieniCampoPulsanti() è utilizzato per ottenere la porzione di
    interfaccia relativa ai pulsanti dell'applicazione. Il metodo istanzia
    tre oggetti di tipo Button (Invia, Reset, Esporta) e associa a ciascuno
    uno specifico handler con una lambda expression.
    https://docs.oracle.com/javafx/2/api/javafx/scene/control/Button.html
(21)
    Il metodo ottieniCampoMessaggi() è utilizzato per ottenere la porzione di
    interfaccia relativa ai messaggi visualizzati dall'applicazione come 
    conseguenza delle azioni dell'utente.
(22)
    Il metodo ottieniGrafico() è utilizzato per la realizzazione del grafico 
    delle attività dell'utente. Il grafico è implementato come un LineChart
    in cui ogni tipo di attività è reso con un oggetto di tipo XYChart.Series.
    https://docs.oracle.com/javafx/2/api/javafx/scene/chart/LineChart.html
    https://docs.oracle.com/javafx/2/api/javafx/scene/chart/XYChart.Series.html
(23)
    Il metodo ottieniColonnaSinistra() è utilizzato per la realizzazione della
    parte superiore sinistra dell'interfaccia grafica. Questo metodo invoca
    i metodi ottieniCampoMatricola(), ottieniCampoInizioFine(), ottieniCampoTipo(),
    ottieniCampoOrario() e inserisce gli oggetti da essi restituiti in un VBox.
    https://docs.oracle.com/javafx/2/api/javafx/scene/layout/VBox.html
(24)
    Il metodo ottieniColonnaSinistra() è utilizzato per la realizzazione della
    parte superiore destra dell'interfaccia grafica. Questo metodo invoca i metodi
    ottieniCampoDescrizione(), ottieniCampoPulsanti(), ottieniCampoMessaggi()
    e inserisce gli oggetti da essi restituiti in un VBox.
(25)
    Il metodo ottieniInterfaccia() è utilizzato per la realizzazione dell'intera
    interfaccia. Questo metodo costruisce un oggetto di tipo GridPane, imposta 
    il numero di righe e colonne, assegna lo stile all'interfaccia secondo
    i parametri recuperati dal file di configurazione XML e richiama i metodi
    ottieniColonnaSinistra(), ottieniColonnaDestra() e ottieniGrafico(), grazie
    ai quali ottieni in modo iterativo tutte le componenti dell'interfaccia. 
    L'oggetto GridPane restituito è utilizzato come layout e viene utilizzato
    come parametro per il costruttore dell'oggetto Scene dell'interfaccia.
(26)
    Il metodo avvaloraInterfaccia(Attivita a) è utilizzato per l'inserimento
    nei campi dell'interfaccia dei dati recuperati dalla cache locale di una
    attività inserita in precedenza ma non ancora memorizzata nella base di
    dati.
(27)
    Il metodo aggiornaAttivita() è utilizzato per l'aggiornamento del membro 
    a di tipo Attivita in seguito alla modifica dei campi di input. Il metodo
    è invocato subito prima dell'invio alla base di dati o subito prima della
    chiusura dell'applicazione.
(28)
    Il metodo inviaAttivita() è utilizzato per l'invio alla base di dati di 
    una attività appena inserita dall'utente. Prima di effettuare l'invio tramite
    il membro mad, il metodo esegue dei controlli sintattici e semantici 
    sull'attività inserita. Questi controlli si basano esclusivamente sulla
    compatibilità dei campi inseriti. Controlli più approfonditi (es. sequenzialità
    delle date) sono delegati all'oggetto mad.
(29)
    Il metodo resetCampi() è utilizzato per pulire gli input inseriti dall'utente
    (es. alla pressione del pulsante Reset).
(30)
    Il metodo esportaAttivita() è utilizzato per l'esportazione della lista 
    delle attività in formato XML alla pressione del pulsante Esporta.
(31)
    Il metodo start() è richiamato all'avvio dell'applicazione e si occupa di
    costruire l'interfaccia grafica, mostrarla a video, inviare un log al server
    per l'apertura dell'applicazione e di inizializzare gli oggetti necessari
    all'utilizzo da parte dell'utente.
*/