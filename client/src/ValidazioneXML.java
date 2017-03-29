import java.io.*;
import javax.xml.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;
import org.xml.sax.*;

public class ValidazioneXML {   //(00)
    public Boolean validaXML(String xml, String percorsoXsd){   //(01)
        try{
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema s = sf.newSchema(new StreamSource(new File(percorsoXsd)));
            s.newValidator().validate(new StreamSource(new StringReader(xml)));
            return true;
        } catch (SAXException | IOException e){
            new LogUtilizzoInterfacciaXML("Eccezione", e.getMessage());
            return false;
        }
    }
}

/*
Note:
(00)
    La classe ValidazioneXML è preposta alla validazione dei file XML con i 
    relativi file XSD.

(01)
    Il metodo validaXML è utilizzato da tutte le classi che necessitino di
    validare una stringa XML a fronte di un file XSD. Il metodo restituisce
    un Boolean che indica se la validazione è avvenuta correttamente o meno.
*/