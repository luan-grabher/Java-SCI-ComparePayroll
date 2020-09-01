package ComparePayroll;

import ComparePayroll.Control.Comparar_Control;
import java.io.File;
import SimpleView.View;
import fileManager.Args;
import fileManager.Selector;

public class ComparePayroll {
    private static File resumo_1_arquivo = null;
    private static File resumo_2_arquivo = null;
    private static File local_salvar = null;
    
    public static void main(String[] args) {
        String firstPayroll = Args.get(args, "firstPayroll");
        String secondPayroll = Args.get(args, "secondPayroll");
        
        if(firstPayroll == null || secondPayroll == null && pegaArquivos()){
            Comparar_Control controleComparar = new Comparar_Control(resumo_1_arquivo, resumo_2_arquivo, local_salvar);
        }else{
            if(Selector.verifyFile(firstPayroll) && Selector.verifyFile(secondPayroll)){
                Comparar_Control control = new Comparar_Control(new File(firstPayroll), new File(secondPayroll), new File(System.getProperty("user.home") + "/Desktop"));
            }
        }
    }
    
    private static boolean pegaArquivos(){
        //Pega 1º Arquivo
        View.render("Escolha a seguir a primeira folha a ser comparada...", "question");
        resumo_1_arquivo = Selector.selectFile("C:/Users", "CSV (Separado por vírgulas)", "csv");
        if(Selector.verifyFile(resumo_1_arquivo.getAbsolutePath(), "csv")){
            
            //Pega 2º Arquivo
            View.render("Escolha a seguir a segunda folha a ser comparada...", "question");
            resumo_2_arquivo = Selector.selectFile("C:/Users", "CSV (Separado por vírgulas)", "csv");
            if(Selector.verifyFile(resumo_2_arquivo.getAbsolutePath(), "csv")){
                
                //Pega pasta para salvar
                View.render("Escolha onde você deseja salvar as diferenças...","question");
                local_salvar = Selector.selectFolder("");
                return Selector.verifyFile(local_salvar.getAbsolutePath());
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    
}
