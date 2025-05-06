package auditoria;

import java.io.*;

public class FileProcessador implements Runnable {
    private File file;
    private int validos = 0;
    private int invalidos = 0;

    public FileProcessador(File file) {
        this.file = file;
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (CPFValidador.validaCPF(linha)) {
                    validos++;
                } else {
                    invalidos++;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler " + file.getName() + ": " + e.getMessage());
        }
    }

    public int getValidos() { return validos; }
    public int getInvalidos() { return invalidos; }
}


