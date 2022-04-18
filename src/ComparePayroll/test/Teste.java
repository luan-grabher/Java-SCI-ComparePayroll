package ComparePayroll.test;


public class Teste {

    public static void main(String[] args) {
        String folderPath = "C:/Users/Administrador/Documents/Projetos/Moresco/Java-SCI-ComparePayroll/testes/";
        String filePath_1 = folderPath + "03-2022.csv";
        String filePath_2 = folderPath + "02-2022.csv";

        args = new String[]{"-firstPayroll", filePath_1, "-secondPayroll", filePath_2};
        
        ComparePayroll.ComparePayroll.main(args);
    }
    
}
