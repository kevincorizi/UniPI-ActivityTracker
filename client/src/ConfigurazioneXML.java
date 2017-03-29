import com.thoughtworks.xstream.*;
import java.nio.file.*;

public class ConfigurazioneXML{ //(00)
    public StileXML stile;
    public InfoProgettoXML progetto;
    public DatabaseXML database;
    public ServerLogXML server;
    public CacheXML cache;
    
    public ConfigurazioneXML(){    //(01)
        try{
            XStream xs = new XStream();
            ValidazioneXML v = new ValidazioneXML();
            String xml = new String(Files.readAllBytes(Paths.get("./myfiles/xml/config.xml")));   
            if(v.validaXML(xml, "./myfiles/xsd/config.xsd")){
                ConfigurazioneXML c = (ConfigurazioneXML)xs.fromXML(xml);
                this.stile = c.stile;
                this.progetto = c.progetto;
                this.database = c.database;
                this.server = c.server;
                this.cache = c.cache;
            }
        } catch(Exception e){
            new LogUtilizzoInterfacciaXML("Eccezione", e.getMessage());
        }
    }
}

/*
Note:
(00)
    La classe ConfigurazioneXML è preposta alla gestione del file di configurazione
    XML. La classe possiede un membro per ogni tipo di parametro (parametri
    stilistici, di progetto, della base di dati, del server di log e della cache
    locale).

(01)
    Il costruttore si occupa di recuperare i parametri di configurazione dal file
    XML e di utilizzarli per inizializzare i membri della classe. Il costruttore
    preventivamente valida il file XML per escludere utilizzi scorretti o malevoli.
*/