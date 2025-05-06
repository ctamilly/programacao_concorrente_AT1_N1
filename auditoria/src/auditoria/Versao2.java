package auditoria;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Versao2 {
    public static void main(String[] args) throws Exception {
        long inicio = System.currentTimeMillis();

        File pasta = new File("dados");
        File[] arquivos = pasta.listFiles((dir, name) -> name.endsWith(".txt"));

        if (arquivos == null || arquivos.length == 0) {
            System.err.println("Nenhum arquivo .txt encontrado na pasta 'dados'.");
            return;
        }

        List<File> listaArquivos = Arrays.asList(arquivos);
        int meio = Math.min(15, listaArquivos.size());
        List<File> grupo1 = listaArquivos.subList(0, meio);
        List<File> grupo2 = listaArquivos.subList(meio, listaArquivos.size());

        File merged1 = mergeFiles(grupo1, "merged1.txt");
        File merged2 = mergeFiles(grupo2, "merged2.txt");

        FileProcessador p1 = new FileProcessador(merged1);
        FileProcessador p2 = new FileProcessador(merged2);

        Thread t1 = new Thread(p1);
        Thread t2 = new Thread(p2);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        long fim = System.currentTimeMillis();
        long tempo = fim - inicio;

        int totalValidos = p1.getValidos() + p2.getValidos();
        int totalInvalidos = p1.getInvalidos() + p2.getInvalidos();

        System.out.println("CPFs válidos: " + totalValidos);
        System.out.println("CPFs inválidos: " + totalInvalidos);

        File pastaResultados = new File("resultados");
        if (!pastaResultados.exists()) {
            pastaResultados.mkdir();
        }

        File resultado = new File(pastaResultados, "versao_2_duas_threads.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultado))) {
            writer.write("Tempo de execução (ms): " + tempo);
        }

        System.out.println("Tempo de execução salvo em " + resultado.getAbsolutePath());
    }

    static File mergeFiles(List<File> arquivos, String nomeTemp) throws IOException {
        File temp = new File(nomeTemp);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            for (File f : arquivos) {
                try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        writer.write(linha);
                        writer.newLine();
                    }
                }
            }
        }
        return temp;
    }
}
