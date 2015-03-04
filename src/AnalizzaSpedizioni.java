/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.jsoup.nodes.Document;

/**
 * @author Simone
 */
class AnalizzaSpedizioni {

    String[] listaLDV;
    char divisoreSezioni = '#';
    String divisoreSezioniIntero = "";
    int numeroRipetizioniDivisore = 80;

    public AnalizzaSpedizioni() {
        int k = 0;
        while (k < numeroRipetizioniDivisore) {
            divisoreSezioniIntero = divisoreSezioniIntero + divisoreSezioni;
            k++;
        }
    }

    void cercaSpedizioni(MainGUI mainGUI) {
        estraiLDV(mainGUI);
        int[] dim = new int[2];
        String[][] matriceRis = null;
        //Genero un divisore per distinguere le varie ricerche effettuate

        if (listaLDV != null) {
            for (String numeroLDV : listaLDV) {
                mainGUI.outputArea.append(divisoreSezioniIntero + "\n");
                if (numeroLDV.contains(" ")) { //se in una riga c'è almeno uno spazio allora c'è almeno una LDV seguita dalla descrizione
                    String[] LDVconDescrizione = numeroLDV.split("\\s", 2);   //esegue lo split al massimo n-1 volte cioè 2-1 volte= 1 volta
                    mainGUI.outputArea.append("STATO DELLA SPEDIZIONE -" + LDVconDescrizione[0] + "- Descrizione: " + LDVconDescrizione[1] + "\n\n");
                    numeroLDV = LDVconDescrizione[0];
                } else {
                    mainGUI.outputArea.append("STATO DELLA SPEDIZIONE -" + numeroLDV + "-\n\n");
                }
                //System.out.println("\n\n\n\nStato della spedizione " + numeroLDV + "\n\n");//debug code
                matriceRis = new CercaSpedizioneOnline().cerca(numeroLDV, dim);//Cerco online i dati della spedizione

                for (int i = 0; i < dim[0]; i++) {//inizio a leggere la matrice risultato
                    if (i == 2 && matriceRis[0][0].equals("Ora consegna:")) {//Se è stata consegnata dopo le informazioni sulla firma lascio una riga vuota
                        mainGUI.outputArea.append("\n");
                    }
                    for (int j = 0; j < dim[1]; j++) {
                        if (matriceRis[i][j] != null) {
                            // System.out.print(ris[i][j] + "\t");//debug code
                            mainGUI.outputArea.append(matriceRis[i][j] + "\t");
                        }
                    }
                    // System.out.println();//debug code
                    mainGUI.outputArea.append("\n");
                }
                //System.out.println("\n");//debug code
                mainGUI.outputArea.append(divisoreSezioniIntero + "\n\n\n\n");
            }
        }
    }

    private void estraiLDV(MainGUI mainGUI) {
        listaLDV = null;
        String inputText = mainGUI.inputArea.getText();
        // listaLDV = inputText.split("[^a-zA-Z0-9\"]+");//tramite la regex spezzo la stringa ogni volta che la RegEx
        listaLDV = inputText.split("\\n"); //Spezzo le stringhe ogni volta che vado a capo
        //è vera (quando trova un carattere che NON è alfanumerico una o più volte)
        //e le stringe derivate le metto in un array


    }
}
