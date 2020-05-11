package Control;

import Model.Comparar_Model;
import Model.Contracheques_Model;
import java.io.File;
import main.Arquivo;
import SimpleView.View;

public class Comparar_Control {

    private final Contracheques_Model resumo_1_contracheques_model;
    private final Contracheques_Model resumo_2_contracheques_model;

    public Comparar_Control(File resumo_1_arquivo, File resumo_2_arquivo, File local_salvar) {

        resumo_1_contracheques_model = new Contracheques_Model(resumo_1_arquivo);
        resumo_2_contracheques_model = new Contracheques_Model(resumo_2_arquivo);

        Comparar_Model modelo =  new Comparar_Model(
                resumo_1_contracheques_model.getContracheques(),
                resumo_2_contracheques_model.getContracheques()
        );
        
        
        /*Monta texto de restorno*/
        String textoCsv = modelo.getDemitidos() + "\r\n" 
                + modelo.getAdmitidos() 
                + modelo.renderDiferencas(resumo_1_arquivo.getName().replaceAll(".csv", ""), resumo_2_arquivo.getName().replaceAll(".csv", ""));
        
        
        String save_path = local_salvar.getAbsolutePath()
                + "\\Diferencas folha " 
                + resumo_1_arquivo.getName().replaceAll(".csv", "")
                + " - "
                + resumo_2_arquivo.getName().replaceAll(".csv", "") + ".csv";
        Arquivo.salvar( save_path , textoCsv);
        
        View.render("Programa terminado!\nArquivo salvo em:\n" + save_path);
    }
}
