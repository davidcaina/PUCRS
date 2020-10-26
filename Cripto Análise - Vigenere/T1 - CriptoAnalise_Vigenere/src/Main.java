import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Main {

    private static final double FREQ_INGLES = 0.065;
    private static final double FREQ_PORTUGUES = 0.072723;

    // OK
    public static int teste_Kasiski(String texto_cifrado, int tamanho_minimo, int tamanho_maximo) {

        // GUARDA OS POSSIVEIS TAMANHOS QUE A CHAVE POSSA TER.
        HashMap<Integer, Integer> POSSIVEIS_TAMANHOS = new HashMap<>();

        // SEPARA TOD0 TEXTO CIFRADO EM PARTES DE 3 LETRAS.
        HashMap<String, SETs> substringMap = aux_substrings(texto_cifrado);

        // TIRA TODOS SUBS DE 3 LETRAS QUE FOREM UNICOS.
        ArrayList<SETs> substrings = SETs.removeSetsUnicos(new ArrayList<>(substringMap.values()));

        // # RESUMO:
        //   PRA CADA SUBSTRING, CALCULA TODAS AS DISTANCIAS ENTRE OCORRENCIAS IGUAIS
        //   PARA PEGAR O MAIOR NUMERO DE OCORRENCIAS IGUAIS, PARA ENTÃO CALCULAR O
        //   TAMANHO DA CHAVE.
        for (SETs substr : substrings) {

            ArrayList<Integer> lista_distancias = substr.calcula_distancia_entre_Substrings(true);

            for (Integer distancia : lista_distancias) {

                ArrayList<Integer> lista_possiveis_tamanhos = aux_calcula_possiveis_tamanhos(distancia);

                for (Integer tamanho : lista_possiveis_tamanhos) {

                    if (POSSIVEIS_TAMANHOS.containsKey(tamanho)) {
                        Integer temp = POSSIVEIS_TAMANHOS.get(tamanho);
                        POSSIVEIS_TAMANHOS.put(tamanho, ++temp);
                    } else {
                        POSSIVEIS_TAMANHOS.put(tamanho, 1);
                    }
                }
            }
        }

        // MANDA TODOS OS POSSIVEIS TAMANHOS PARA VER QUAL POSSUI O MAIOR NUMERO DE OCORRENCIAS.
        return estima_tamanho_chave(POSSIVEIS_TAMANHOS, tamanho_minimo, tamanho_maximo);
    }

    // OK
    public static int estima_tamanho_chave(HashMap<Integer, Integer> lista_tamanhos_possiveis, int tamanho_minimo, int tamanho_maximo) {
        Set<Integer> keys = lista_tamanhos_possiveis.keySet();


        // "MAIOR" DENTRO DOS LIMITES ESTABELICIDOS.
        Integer maior_frequencia = 0;
        Integer maior_chave = 0;


        for (Integer chave : keys) {

            Integer freq = lista_tamanhos_possiveis.get(chave);

            if(chave > tamanho_maximo || chave < tamanho_minimo) continue;

            if ((chave >= tamanho_minimo) && (chave <= tamanho_maximo) && (freq >= maior_frequencia)){
                maior_frequencia = freq; maior_chave = chave;
            }
        }

        if (maior_chave < tamanho_minimo) {return tamanho_minimo;}
        else if (maior_chave > tamanho_maximo) {return tamanho_maximo;}

        return maior_chave;
    }

    // OK
    public static String estima_chave(String texto_cifrado, int tamanho_chave) {

        String key = "";
        String texto_pelaChave[] = new String[tamanho_chave];


        for (int i = 0; i < tamanho_chave; i++)
            texto_pelaChave[i] = "";

        // SEPARA DE ACORDO COM A CHAVE
        for (int i = 0; i < texto_cifrado.length(); i++) {
            texto_pelaChave[i % tamanho_chave] += texto_cifrado.charAt(i);
        }


        for (int i = 0; i < tamanho_chave; i++) {
            int[] freq = calcula_frequencia(texto_pelaChave[i]);

            // SE SAIR DO ESCOPO DO ALFABETO, RETORNA OS EXTREMOS.
            if(((getMaiorFrequencia(freq) - 4) + 'A') < 65)
                key += (char) 65;
            else if(((getMaiorFrequencia(freq) - 4) + 'A') > 90)
                key += (char) 90;
            else
                key += (char) ((getMaiorFrequencia(freq) - 4) + 'A');
        }

        return key;
    }

    public static String descriptografar(String texto_cifrado, String chave) {

        texto_cifrado = SET_UPPERCASE(texto_cifrado);
        chave = SET_UPPERCASE(chave);
        String res = "";

        for (int i = 0; i < texto_cifrado.length(); i++) {

            int pos_charTexto = texto_cifrado.charAt(i) - 'A';
            int charCorrespondente = chave.charAt(i % chave.length()) - 'A';
            int shift = ((pos_charTexto - charCorrespondente) % 26);

            if (shift < 0) {
                shift += 26;
            }

            shift += 'A';
            res += (char) shift;
        }
        return res;
    }

    // ===================== CLASSE AUXILIAR =======================

    /*
      === CLASSE DE AUXILIO ====

     GUARDA AS POSIÇÕES ONDE FORAM ENCONTRADAS OCORRENCIAS IGUAIS
     E, CONSEQUENTEMENTE,
     GUARDA/CALCULA AS DISTANCIAS ENTRE AS OCORRENCIAS IGUAIS.
    */
    private static class SETs {
        private ArrayList<Integer> pos_encontrado;
        private ArrayList<Integer> distancias;
        private String message;


        public SETs(String msg, int pos) {
            message = msg;
            pos_encontrado = new ArrayList<>();
            distancias = new ArrayList<>();
            pos_encontrado.add(pos);
        }

        // # REMOVE SETS COM OCORRENCIAS UNICAS.
        public static ArrayList<SETs> removeSetsUnicos(ArrayList<SETs> subs) {
            Iterator<SETs> it = subs.iterator();
            while (it.hasNext()) {
                SETs substr = it.next();

                // SE SÓ EXISTIR UMA SUBSTRING DESSA "ORDEM", REMOVE DA LISTA.
                if (substr.pos_encontrado.size() <=1)
                    it.remove();
            }
            return subs;
        }

        // # CALCULA DISTANCIA ENTRE SETS.
        public ArrayList<Integer> calcula_distancia_entre_Substrings(boolean recalc) {

            if (recalc) {

                // CALCULA/SALVA A DISTANCIA ENTRE DOIS ENCONTROS DE "SET's" IGUAIS.
                if (pos_encontrado.size() > 1) {
                    for (int i = 1; i < pos_encontrado.size(); i++) {
                        distancias.add(pos_encontrado.get(i) - pos_encontrado.get(i - 1));
                    }
                } else {
                    distancias.add(0);
                }
            }

            ArrayList<Integer> diff = new ArrayList<Integer>();
            for (Integer n : distancias) {
                diff.add(n);
            }
            return diff;
        }

        // MARCA A POSIÇÃO A ONDE FOI ENCONTRADO UM "SET" IGUAL.
        public void addOccurance(int pos){
            pos_encontrado.add(pos);
        }
    }

    // ===================== METODOS AUXILIARES =====================

    // RETORNA A POSIÇÃO DE MAIOR FREQUENCIA
    private static int getMaiorFrequencia(int[] theArray) {
        int maxPos = 0;
        for (int i = 1; i < theArray.length; i++) {
            if (theArray[i] > theArray[maxPos]) {
                maxPos = i;
            }
        }
        return maxPos;
    }

    // # CALCULA FREQUENCIA / NUMERO DE OCORRENCIAS NO TEXTO
    private static int[] calcula_frequencia(String text) {
        int[] freq = new int[26];

        text = SET_UPPERCASE(text);

        for (int i = 0; i < text.length(); i++) {
            freq[text.charAt(i) - 'A']++;
        }
        return freq;
    }

    // # CALCULA TODOS OS POSSIVEIS TAMANHOS/"FACTORS" Kasiski default).
    private static ArrayList<Integer> aux_calcula_possiveis_tamanhos(int num) {
        ArrayList<Integer> tamanhos = new ArrayList<>();
        int n = num;
        for (int i = 1; i <= (int) Math.sqrt(n); i++) {
            if (n % i == 0) {
                tamanhos.add(i);
            }
        }
        int size = tamanhos.size();
        for (int i = size - 1; i >= 0; i--) {
            tamanhos.add(num / tamanhos.get(i));
        }

        return tamanhos;
    }

    // # SEPARA TEXTO CIFRADO EM PEDAÇOS DE 3 LETRAS (Kasiski default).
    private static HashMap<String, SETs> aux_substrings(String texto_cifrado){
        String sub;
        HashMap<String, SETs> subs = new HashMap<>();

        texto_cifrado = SET_UPPERCASE(texto_cifrado);

        for (int i = 0; i < texto_cifrado.length() - 2; i++) {
            sub = texto_cifrado.substring(i, i + 3);
            if (subs.containsKey(sub)) {
                subs.get(sub).addOccurance(i);
            } else {
                subs.put(sub, new SETs(sub, i));
            }
        }
        return subs;
    }

    // # REALIZA LEITURA DO INPUT.
    private static String getInput() throws IOException {

        InputStream is = new FileInputStream("src/input.txt");
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();

        while(line != null){
            sb.append(line).append("\n");
            line = buf.readLine();
        }

        String fileAsString = sb.toString();
        return fileAsString;
    }

    // # FORMATA TOD0 TEXTO PARA MAISCULO -> (operações com char).
    private static String SET_UPPERCASE(String texto_cifrado) {

        return texto_cifrado.toUpperCase().replaceAll("[^\\p{L}]", "");
    }

    // =============================================================

    public static void main(String[] args) throws IOException {

        // TAMANHO MAXIMO DA CHAVE.
        final int MAX = 10;

        // TODO 1: Realizar a leitura do Arquivo de entrada.
        String TEXTO_CIFRADO = getInput();

        System.out.println("TEXTO INPUT: ");
        System.out.println(TEXTO_CIFRADO);
        System.out.println();

        // # IGNORA CHAVES COM TAMANHO = 1.
        for(int i = 2; i < MAX; i++) {

            // TODO 2: Realizar a estimativa do tamanho da chave.
            int tamanho_chave = teste_Kasiski(TEXTO_CIFRADO, i, MAX);

            System.out.println("TAMANHO DA CHAVE: " + tamanho_chave);

            // TODO 3: Realizar a estimativa de qual é a chave.
            String chave = estima_chave(TEXTO_CIFRADO, tamanho_chave);

            System.out.println("CHAVE: " + chave);

            // TODO 4: Realizar a descifração do texto.
            String texto = descriptografar(TEXTO_CIFRADO, chave);

            System.out.println("TEXTO DESCIFRADO: \n");
            System.out.println(texto);
            System.out.println();
        }

        // CHAVE ENCOTRADA: "=VELINK", provavelmente é "AVELINO"

        String texto = descriptografar(TEXTO_CIFRADO, "AVELINO");

        System.out.println(" ====================================== ");
        System.out.println("Chave: AVELINO ");
        System.out.println("TEXTO DESCIFRADO (com chave \"analisada\"): \n");
        System.out.println(texto);
        System.out.println();
    }
}

