package ComparePayroll;

import ComparePayroll.Control.Comparar_Control;
import java.io.File;
import SimpleView.View;
import fileManager.Args;
import fileManager.Selector;
import javax.swing.JOptionPane;
import org.ini4j.Ini;

public class ComparePayroll {

    private static File resumo_1_arquivo = null;
    private static File resumo_2_arquivo = null;

    public static Ini ini;

    public static void main(String[] args) {
        //Define ini
        try {
            String inipath = Args.get(args, "ini_path");
            ini = new Ini(new File(inipath == null?"config.ini":inipath));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Não foi possivel encontrar o arquivo de configuração!");
            System.exit(0);
        }

        String firstPayroll = Args.get(args, "firstPayroll");
        String secondPayroll = Args.get(args, "secondPayroll");

        if (firstPayroll == null || secondPayroll == null && pegaArquivos()) {
            Comparar_Control controleComparar = new Comparar_Control(resumo_1_arquivo, resumo_2_arquivo, new File(System.getProperty("user.home") + "/Desktop"));
        } else {
            if (Selector.verifyFile(firstPayroll, "csv") && Selector.verifyFile(secondPayroll, "csv")) {
                Comparar_Control control = new Comparar_Control(new File(firstPayroll), new File(secondPayroll), new File(System.getProperty("user.home") + "/Desktop"));
            }
        }
    }

    private static boolean pegaArquivos() {
        //Pega 1º Arquivo
        View.render("Escolha a seguir a primeira folha a ser comparada...", "question");
        resumo_1_arquivo = Selector.selectFile("C:/Users", "CSV (Separado por vírgulas)", "csv");
        if (Selector.verifyFile(resumo_1_arquivo.getAbsolutePath(), "csv")) {

            //Pega 2º Arquivo
            View.render("Escolha a seguir a segunda folha a ser comparada...", "question");
            resumo_2_arquivo = Selector.selectFile("C:/Users", "CSV (Separado por vírgulas)", "csv");
            return Selector.verifyFile(resumo_2_arquivo.getAbsolutePath(), "csv");
        } else {
            return false;
        }
    }

}
