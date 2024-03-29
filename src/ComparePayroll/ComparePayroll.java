package ComparePayroll;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import SimpleView.View;
import fileManager.Args;
import fileManager.Selector;
import javax.swing.JOptionPane;
import org.ini4j.Ini;

import ComparePayroll.Control.Comparar_Control;
import Entity.Executavel;
import Executor.Execution;

public class ComparePayroll {

    private static File firstPayroll = new File("");
    private static File secondPayroll = new File("");

    public static Ini ini;

    public static void main(String[] args) {
        //Define ini
        try {
            String inipath = args != null ? Args.get(args, "ini_path") : "config.ini";
            ini = new Ini(new File(inipath == null ? "compare_payroll.ini" : inipath));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Não foi possivel encontrar o arquivo de configuração!");
            System.exit(0);
        }

        //Se o arquivo ini existir
        if (args != null && Args.get(args, "firstPayroll") != null && Args.get(args, "secondPayroll") != null) {
            firstPayroll = new File(Args.get(args, "firstPayroll"));
            secondPayroll = new File(Args.get(args, "secondPayroll"));
        }

        //Se os arquivos existirem
        if (firstPayroll != null && secondPayroll != null && !firstPayroll.exists() && !secondPayroll.exists()) {
            //Pega arquivos com usuário e verifica se existem
            if (!getFilesWithUser() || !Selector.verifyFile(firstPayroll.getAbsolutePath(), "csv") && Selector.verifyFile(secondPayroll.getAbsolutePath(), "csv")) {
                JOptionPane.showMessageDialog(null, "Os arquivos escolhidos são inválidos!");
                System.exit(0);
            }
        }

        Comparar_Control controleComparar = new Comparar_Control(
                firstPayroll, //Primeria folha
                secondPayroll, //Segunda folha
                new File(System.getProperty("user.home") + "/Desktop") //Local de Salvar
        );

        //map of executavel
        Map<String, Executavel> execs = new LinkedHashMap<>();
        execs.put("Pegando informações dos arquivos", controleComparar.new createListOfModels());
        execs.put("Comparando os arquivos", controleComparar.new comparePayrolls());
        execs.put("Gerando relatório", controleComparar.new generateReport());

        Execution execution = new Execution("Comparar folhas de pagamento");
        execution.setExecutionMap(execs);
        execution.runExecutables();
        
        execution.endExecution();

        //Show message to user 'Arquivo salvo na área de trabalho'
        JOptionPane.showMessageDialog(null, "Arquivo salvo na área de trabalho");
    }

    /*
        * Pega arquivos com usuário e verifica se existem
        */
    private static boolean getFilesWithUser() {
        //Pega 1º Arquivo
        View.render("Escolha a seguir a primeira folha a ser comparada...", "question");
        firstPayroll = Selector.selectFile("C:/Users", "CSV (Separado por vírgulas)", "csv");
        firstPayroll = firstPayroll == null ? new File("") : firstPayroll;
        if (Selector.verifyFile(firstPayroll.getAbsolutePath(), true, "csv")) {
            //Pega 2º Arquivo
            View.render("Escolha a seguir a segunda folha a ser comparada...", "question");
            secondPayroll = Selector.selectFile("C:/Users", "CSV (Separado por vírgulas)", "csv");
            secondPayroll = secondPayroll == null ? new File("") : secondPayroll;
            return Selector.verifyFile(secondPayroll.getAbsolutePath(), true, "csv");
        } else {
            return false;
        }
    }

}
