package main;

import Control.Comparar_Control;
import java.io.File;
import SimpleView.View;

public class CompararFolhasDePagamento {
    private static File resumo_1_arquivo = null;
    private static File resumo_2_arquivo = null;
    private static File local_salvar = null;
    
    public static void main(String[] args) {
        if(pegaArquivos()){
            //Define Controle Comparar
            Comparar_Control controleComparar = new Comparar_Control(resumo_1_arquivo, resumo_2_arquivo, local_salvar);
        }
    }
    
    private static boolean pegaArquivos(){
        //Pega 1º Arquivo
        View.render("Escolha a seguir a primeira folha a ser comparada...", "question");
        resumo_1_arquivo = Selector.Arquivo.selecionar("C:/Users", "CSV (Separado por vírgulas)", "csv");
        if(Selector.Arquivo.verifica(resumo_1_arquivo.getAbsolutePath(), "csv")){
            
            //Pega 2º Arquivo
            View.render("Escolha a seguir a segunda folha a ser comparada...", "question");
            resumo_2_arquivo = Selector.Arquivo.selecionar("C:/Users", "CSV (Separado por vírgulas)", "csv");
            if(Selector.Arquivo.verifica(resumo_2_arquivo.getAbsolutePath(), "csv")){
                
                //Pega pasta para salvar
                View.render("Escolha onde você deseja salvar as diferenças...","question");
                local_salvar = Selector.Pasta.selecionar();
                return Selector.Pasta.verifica(local_salvar.getAbsolutePath());
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    
}
