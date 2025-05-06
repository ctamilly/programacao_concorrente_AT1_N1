package auditoria;

import java.io.*;
import java.util.*;

public class Versao5 {
    public static void main(String[] args) throws Exception {
        long inicio = System.currentTimeMillis();

        File pasta = new File("dados");
        File[] arquivos = pasta.listFiles((dir, name) -> name.endsWith(".txt"));

        if (arquivos == null || arquivos.length == 0) {
            System.err.println("Nenhum arquivo .txt encontrado na pasta 'dados'.");
            return;
        }

        List<File> listaArquivos = Arrays.asList(arquivos);

        List<List<File>> grupos = dividirEmGrupos(listaArquivos, 5);

        List<FileProcessador> processadores = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        int grupoIndex = 1;
        for (List<File> grupo : grupos) {
            File merged = mergeFiles(grupo, "merged_v5_" + grupoIndex + ".txt");
            FileProcessador fp = new FileProcessador(merged);
            Thread t = new Thread(fp);
            threads.add(t);
            processadores.add(fp);
            t.start();
            grupoIndex++;
        }

        for (Thread t : threads) {
            t.join();
        }

        long fim = System.currentTimeMillis();
        long tempo = fim - inicio;

        int totalValidos = processadores.stream().mapToInt(FileProcessador::getValidos).sum();
        int totalInvalidos = processadores.stream().mapToInt(FileProcessador::getInvalidos).sum();

        System.out.println("CPFs válidos: " + totalValidos);
        System.out.println("CPFs inválidos: " + totalInvalidos);

        File pastaResultados = new File("resultados");
        if (!pastaResultados.exists()) {
            pastaResultados.mkdir();
        }

        File resultado = new File(pastaResultados, "versao_5_seis_threads.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultado))) {
            writer.write("Tempo de execução (ms): " + tempo);
        }

        System.out.println("Tempo de execução salvo em " + resultado.getAbsolutePath());
    }

    static List<List<File>> dividirEmGrupos(List<File> arquivos, int tamanhoGrupo) {
        List<List<File>> grupos = new ArrayList<>();
        for (int i = 0; i < arquivos.size(); i += tamanhoGrupo) {
            grupos.add(arquivos.subList(i, Math.min(i + tamanhoGrupo, arquivos.size())));
        }
        return grupos;
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
