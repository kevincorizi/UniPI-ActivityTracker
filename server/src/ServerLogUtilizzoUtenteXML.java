import java.io.*;
import java.net.*;
import java.nio.file.*;

public class ServerLogUtilizzoUtenteXML {   //(00)
    private static final Integer porta = 1234;
    
    public static void main (String[] args) {   //(01)
        System.out.println("Server avviato");
        while(true){
            try(
                ServerSocket ss = new ServerSocket(porta);
                Socket sd = ss.accept();
                DataInputStream dis = new DataInputStream(sd.getInputStream());
            ){
                System.out.println("Ricevuto log");
                String s = dis.readUTF();
                ValidazioneXML v = new ValidazioneXML();
                if(v.validaXML(s, "./myfiles/xsd/log.xsd")){
                    Files.write(Paths.get("./myfiles/xml/log.xml"), s.getBytes(), StandardOpenOption.APPEND);
                    Files.write(Paths.get("./myfiles/xml/log.xml"), (System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
                    System.out.println("Log valido");
                    System.out.println(s);
                }
                else{
                    System.out.println("Log non valido");
                }
            } catch (Exception e){
                System.out.println("Errore nella ricezione del log");
                System.out.println(e.getMessage());
            }
        }
    }
}

/*
Note:
(00)
    La classe ServerLogUtilizzoUtenteXML è preposta alla ricezione dei log di
    utilizzo dell'applicazione. 

(01)
    Il metodo main della classe  implementa un server monoprocesso
    monothread ciclico, che ascolta per eventuali connessioni dal client,
    riceve log e dopo averli validati li inserisce in un file di log. Le 
    operazioni svolte dal server sono mostrate nella console.
*/