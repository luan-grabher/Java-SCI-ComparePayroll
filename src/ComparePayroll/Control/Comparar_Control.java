package ComparePayroll.Control;

import ComparePayroll.Model.Comparar_Model;
import ComparePayroll.Model.Contracheques_Model;
import ComparePayroll.Model.TemplateExcelModel;
import Entity.Executavel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import SimpleView.View;
import fileManager.FileManager;

public class Comparar_Control {
    //private static File payroll_1, File payroll_2 and File saveFolder;
    private final File payroll_1, payroll_2, saveFolder;
    
    //Contracheques_Model  for payroll_1 and payroll_2
    private final Contracheques_Model payroll_1_model, payroll_2_model;

    public static final Map<String,String> warnings = new HashMap<>();

    private Comparar_Model model = null;
    private TemplateExcelModel excel = null;

    //Constructor, recebe os arquivos e o local de salvar, inicializa os modelos
    public Comparar_Control(File payroll_1, File payroll_2, File saveFolder) {
        //Set payroll_1 and payroll_2
        this.payroll_1 = payroll_1;
        this.payroll_2 = payroll_2;
        this.saveFolder = saveFolder;

        //Set files in payroll_1_model_ and payroll_2_model
        payroll_1_model = new Contracheques_Model(payroll_1);
        payroll_2_model = new Contracheques_Model(payroll_2);
    }


    //public class extends Executavel to createListOfModels
    public class createListOfModels extends Executavel{
        @Override
        public void run() {
            //Create list of models
            payroll_1_model.criarListaContracheques();
            payroll_2_model.criarListaContracheques();
            
            //generate warnings for payrolls
            generatePayrollFindedWarning(payroll_1_model, payroll_1);
            generatePayrollFindedWarning(payroll_2_model, payroll_2);
        }
    }

    //function to generate warning for a payroll if payrollsFinded is diferent of payrolls.size()
    public void generatePayrollFindedWarning(Contracheques_Model payroll_model, File payroll){
        //In payroll_2_model if payrollsFinded is diferent of payrolls.size()
        if(payroll_model.payrollsFinded != payroll_model.payrolls.size()){
            //Add warning with file name, and Foram encontradas X folhas de pagamentos, mas considerei Y folhas de pagamentos nos cálculos.
            warnings.put(payroll.getName(), "Foram encontradas " + payroll_model.payrollsFinded + " folhas de pagamentos, mas considerei " + payroll_model.payrolls.size() + " folhas de pagamentos nos cálculos.");
        }
    }
    
    //public class extends Executavel to comparePayrolls
    public class comparePayrolls extends Executavel{
        @Override
        public void run() {
            //init Comparar_Model, and set diferences
            model = new Comparar_Model(payroll_1_model.getContracheques(), payroll_2_model.getContracheques());
        }
    }

    //public class extends Executavel to generateReport
    public class generateReport extends Executavel{
        @Override
        public void run() {
            //Start excel model
            excel = new TemplateExcelModel(model, saveFolder);
            
            //copy template to saveFile
            excel.copyTemplate();
            //put warnings
            excel.putWarnings(warnings);
            //put demitidos
            excel.putDemitidos();
            //put admitidos
            excel.putAdmitidos();
            //put putDifferences
            excel.putDifferences(payroll_1.getName().replaceAll(".csv", ""), payroll_2.getName().replaceAll(".csv", ""));

            //save excel
            excel.save();

            /*        
                log.append(modelo.getDemitidos()).append("\r\n");
                log.append(modelo.getAdmitidos());
                log.append(modelo.renderDiferencas(resumo_1_arquivo.getName().replaceAll(".csv", ""), resumo_2_arquivo.getName().replaceAll(".csv", "")));        
                
                String save_path = local_salvar.getAbsolutePath()
                        + "\\Diferencas folha " 
                        + resumo_1_arquivo.getName().replaceAll(".csv", "")
                        + " - "
                        + resumo_2_arquivo.getName().replaceAll(".csv", "") + ".csv";
                FileManager.save(save_path , log.toString());
                
                View.render("Programa terminado!\nArquivo salvo em:\n" + save_path);
            */
        }
    }


}
