package ComparePayroll.test;

import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import ComparePayroll.Model.TemplateExcelModel;

public class Teste {

    public static void main(String[] args) {
        testComparePayroll();
        //testCopyExcelFile();
    }

    //function to test copy of excel file
    public static void testCopyExcelFile(){
        try {
            //start ini
            ComparePayroll.ComparePayroll.ini = new Ini(new File("compare_payroll.ini"));

            //get desktop folder as file
            File desktop = new File(System.getProperty("user.home") + "/Desktop");

            TemplateExcelModel excel = new TemplateExcelModel(null,desktop);
            excel.copyTemplate();

        } catch (Exception e) {            
            e.printStackTrace();
        }


        
    }

    //function to test ComparePayroll
    public static void testComparePayroll() {
        String folderPath = "C:/Users/Administrador/Documents/Projetos/Moresco/Java-SCI-ComparePayroll/testes/";
        String filePath_1 = folderPath + "03-2022.csv";
        String filePath_2 = folderPath + "02-2022.csv";

        String[] args = new String[]{"-firstPayroll", filePath_1, "-secondPayroll", filePath_2};

        ComparePayroll.ComparePayroll.main(args);
    }
    
}
