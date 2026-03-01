import java.io.*;

public class Persistencia {
    public static <T> void salvarDados(String nomeArquivo, T objeto) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
            oos.writeObject(objeto);
        } catch (IOException e) {
            e.printStackTrace();}}

    @SuppressWarnings("unchecked")
    public static <T> T carregarDados(String nomeArquivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nomeArquivo))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null; }}}
