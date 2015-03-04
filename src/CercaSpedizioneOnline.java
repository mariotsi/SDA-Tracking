/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Simone
 */
public class CercaSpedizioneOnline {

    String codice;
    Document doc = null;
    Connection.Response resp = null;
    String[][] risultato;

    String[][] cerca(String codice, int[] dim) {
        this.codice = codice;
        //SDA ha lettere di vettura con 7,9 o 13 caratteri alafanumerici. Altrimenti la LDV non è valida
        if (codice.length() == 7 || codice.length() == 9 || codice.length() == 13 || codice.length() == 12) {
            //Mi connetto alla prima pagina di SDA e faccio una richiesta per una lettera di vettura in modo da ottinere un session_id, codice
            //sicurezza e un cookie valido
            try {
                resp = Jsoup.connect("http://wwww.sda.it/SITO_SDA-WEB/dispatcher?id_ldv=" + codice + "&invoker=home&LEN=&execute2=ActionTracking.doGetTrackingHome&button=Vai").timeout(15000).execute();
            } catch (IOException ex) {
                risultato = new String[1][1];
                risultato[0][0] = "Impossibile connetersi al sito SDA.\nErrore: " + ex.getMessage().toString();
                dim[0] = 1;
                dim[1] = 1;
                return risultato;
            }
            //Faccio una mappa e ci memorizzo tutti i cookies
            Map<String, String> cookies = resp.cookies();
            //Sezione Debug Cookies
        /*for (Entry<String, String> ent : cookies.entrySet()) {
             System.out.println(ent.getKey() + " " + ent.getValue());
             }*/

            //faccio il parse del response per poter lavorare sui tag
            try {
                doc = resp.parse();
            } catch (IOException ex) {
                risultato = new String[1][1];
                risultato[0][0] = "Errore durante il parsing dei dati ricevuti da SDA.\nErrore: " + ex.getMessage().toString();
                dim[0] = 1;
                dim[1] = 1;
                return risultato;
            }

            Element cod_sicurezza = doc.select("input[name=codice_sicurezza]").first();
            Element ses_id = doc.select("input[name=ses_id]").first();
            Element execute2 = doc.select("input[name=execute2]").first();
            //System.out.println("cod sic " + cod_sicurezza.val() + " ses id " + ses_id.val() + " execute2 " + execute2.val());


            try {
                //Richiesta GET per validare la sessione
                Jsoup.connect("http://wwww.sda.it/SITO_SDA-WEB/dispatcherHol?&execute2=ActionTracking.doInsSecurityCode&ses_id=" + ses_id.val() + "&codice_sicurezza=" + cod_sicurezza.val() + "&codice_sicurezza_request=" + cod_sicurezza.val() + "&firstAcs=").cookies(cookies).get();
                //Richiedo i dati della spedizione
                doc = Jsoup.connect("http://wwww.sda.it/SITO_SDA-WEB/dispatcher?id_ldv=" + codice + "&execute2=" + execute2.val() + "&ses_id=" + ses_id.val() + "&code=ldv&codice_sicurezza=" + cod_sicurezza.val() + "&codice_sicurezza_request=" + cod_sicurezza.val() + "&firstAcs=&codice_sicurezza=" + cod_sicurezza.val() + "&codice_sicurezza_request=&firstAcs=").cookies(cookies).get();
            } catch (IOException ex) {
                risultato = new String[1][1];
                risultato[0][0] = "Impossibile connetersi al sito SDA.\nErrore: " + ex.getMessage().toString();
                dim[0] = 1;
                dim[1] = 1;
                return risultato;
            }
            //Prendo la tabella con i dati di tracciatura e la scompongo in righe
            Elements tabTracciatura = doc.select("table").select("[cellpadding=2]").select("[cellspacing=2]").select("[width=600]").select("tbody");
            Elements righeTracciatura = tabTracciatura.select("tr");
            //Se la tabTracciatura è vuota non è possibile tracciare la spedizione
            if (tabTracciatura.isEmpty()) {
                //System.out.println("Non è stata trovata nessuna spedizione con codice " + codice);
                risultato = new String[1][1];
                risultato[0][0] = "Non è stata trovata nessuna spedizione con questo codice.";
                dim[0] = 1;
                dim[1] = 1;
                return (risultato);
            } else {
                //conto righe e colonne di tabTracciatura
                int nRighe = 0, nColonne = 0;
                for (Element riga : righeTracciatura) {
                    nRighe++;
                    nColonne = 0;
                    for (Element td : riga.select("td")) {
                        nColonne++;
                    }
                }
                //Se è stato consegnato è presente la tabella con la film di chi ha ritirato
                Elements tabFirma = doc.select("table").select("[cellpadding=2]").select("[cellspacing=2]").select("[nowrap]");
                if (tabFirma.size()==3) {
                    tabFirma.remove(0);     //Rimuovo il primo elemento Che è un duplicato
                    tabFirma.remove(1);     //rimuovo il secondo elemento che è una tabella inutile
                tabFirma=tabFirma.select("tbody").select("tr").select("td"); //prendo i td dentro alla tabella della firma (4)
                    //dato che è presente la firma aumento di 2+1 il numero delle righe, creo la matrice e inizio a rimpirla con i dati sulla firma, il 1 è per lasciare un riga bianca
                    dim[0] = nRighe = nRighe + 3;
                    risultato = new String[nRighe][nColonne];
                    //Inserisco i dati nella matrice
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            risultato[i][j] = tabFirma.first().text().replace("\u00a0","");//inserisco il primo elemento di tabFirma nella matrice togliendo i caratteri &nbsp; che jSoup vede come \u00a0
                            tabFirma.remove(0);//e successivamente lo cancello da tabFirma
                        }
                    }
                    nRighe = 2+1;//dato che ti sono le due righe cone le informazioni sulla firma e vogliamo lasciare una riga vuota
                    //i dati di spedizione andranno inseriti dalla riga 3
                } else {
                    //Se non esiste la firma le righe non vanno aumentate e la matrice si può inizare a rimpire dalla riga 0
                    dim[0] = nRighe;
                    risultato = new String[nRighe][nColonne];
                    nRighe = 0;
                }
                dim[1] = nColonne;
                //riempo la matrice con i dati della tracciatura
                for (Element tr : righeTracciatura) {
                    nColonne = 0;
                    for (Element td : tr.select("td")) {
                        risultato[nRighe][nColonne++] = td.text();
                    }
                    nRighe++;
                }
            }
            return risultato;
        } else {//Se il codice non è di 7,9,12 o 13 caratteri restituisco un errore
            dim[0] = 1;
            dim[1] = 1;
            risultato = new String[1][1];
            risultato[0][0] = "Il codice di tracciamento inserito non è valido.";
            return risultato;
        }
    }
}
