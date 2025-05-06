package auditoria;

import java.io.*;

public class Versao1 {
    public static void main(String[] args) throws Exception {
        long inicio = System.currentTimeMillis();

        File pasta = new File("dados");
        File[] arquivos = pasta.listFiles((dir, name) -> name.endsWith(".txt"));

        if (arquivos == null || arquivos.length == 0) {
            System.err.println("Nenhum arquivo .txt encontrado na pasta 'dados'.");
            return;
        }

        FileProcessador processor = new FileProcessador(mergeFiles(arquivos));
        Thread t = new Thread(processor);
        t.start();
        t.join(); 
        
        long fim = System.currentTimeMillis();
        long tempo = fim - inicio;

        System.out.println("CPFs válidos: " + processor.getValidos());
        System.out.println("CPFs inválidos: " + processor.getInvalidos());

        File pastaResultados = new File("resultados");
        if (!pastaResultados.exists()) {
            boolean criada = pastaResultados.mkdir();
            if (!criada) {
                System.err.println("Erro ao criar a pasta 'resultados'.");
                return;
            }
        }

        File resultado = new File(pastaResultados, "versao_1_thread.txt");
        salvarResultado(resultado, tempo);
        System.out.println("Tempo de execução salvo em " + resultado.getAbsolutePath());
    }

    static File mergeFiles(File[] arquivos) throws IOException {
        File temp = new File("merged.txt");
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

    static void salvarResultado(File arquivo, long tempo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            writer.write("Tempo de execução (ms): " + tempo);
        }
    }
}


