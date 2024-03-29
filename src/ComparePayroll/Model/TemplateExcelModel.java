package ComparePayroll.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ini4j.Profile.Section;

import ComparePayroll.ComparePayroll;
import ComparePayroll.Model.Entity.Diferenca;
import ComparePayroll.Model.Entity.DiferencasColaborador;
import JExcel.JExcel;
import fileManager.FileManager;
import fileManager.StringFilter;

public class TemplateExcelModel {
    private final Comparar_Model model;
    private final File saveFile;
    private Map<String, StringFilter> ignores =  new HashMap<>();
    private CellStyle paintedCellStyle = null;

    private SXSSFWorkbook wb;


    public TemplateExcelModel(Comparar_Model model, File saveFolder) {
        this.model = model;
        generateIgnoreMap();

        //Set saveFile 'Comparação de Folhas de Pagamento.xlsx' in desktop
        saveFile = new File(saveFolder.getAbsolutePath() + "/Comparação de Folhas de Pagamento.xlsx");
    }

    //generate ignore map - with section ignored_differences in ini, put in map all ignored differences as StringFilter
    public void generateIgnoreMap() {
        //get section ignored_differences in ComparePayroll.ini
        Section ignored_differences = ComparePayroll.ini.get("ignored_differences");

        //for each ignored difference, put in map as StringFilter
        for (String key : ignored_differences.keySet()) {
            ignores.put(key, new StringFilter(ignored_differences.get(key)));
        }
    }

    //Copy excel 'compare_payroll_template.xlsx' to 'compare_payroll_template_result.xlsx'
    public void copyTemplate(){
        //from ini get 'template.path'
        String templatePath = ComparePayroll.ini.get("template","path").toString();

        //open template as workbook
        try {
            File template = FileManager.getFile(templatePath);
            FileInputStream templateFile = new FileInputStream(template);

            wb = new SXSSFWorkbook(new XSSFWorkbook(templateFile),-1);

            //create cell style for painted cells
            createPaintedCellStyle();
        } catch (Exception e) {            
            e.printStackTrace();
            throw new Error("Ocorreu o seguinte erro: " + e.getMessage());
        }
    }

    //create painted cell style with background color red
    public void createPaintedCellStyle(){
        paintedCellStyle = wb.createCellStyle();
        paintedCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
    }
    
    //evaluate formulas
    public void evaluateFormulas(){
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        for (Sheet sheet : wb) {
            for (Row r : sheet) {
                for (Cell c : r) {
                    if (c.getCellType() == CellType.FORMULA) {
                        evaluator.evaluateFormulaCell(c);
                    }
                }
            }
        }
        
    }

    //Save workbook
    public void save(){
        //save workbook
        try {
            //calculate excel formulas
            evaluateFormulas();

            //save workbook with filestream of saveFile            
            JExcel.saveWorkbookAs(saveFile, wb);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Ocorreu o seguinte erro: " + e.getMessage());
        }
    }

    //function getColMap to generate Map with column name and column index by iniSection and String Array with cols without 'col_' in name, but in ini that have 'col_'
    public Map<String, Integer> getColMap(String iniSection, String[] cols){
        Map<String, Integer> colMap = new HashMap<>();

        //for each col in cols
        for(String col : cols){
            String col_letter = ComparePayroll.ini.get(iniSection, "col_" + col).toString();
            Integer col_int = JExcel.Cell(col_letter);

            //add col_int and col to colMap
            colMap.put(col, col_int);
        }

        return colMap;
    }

    //function nextRow return next row of Sheet
    public SXSSFRow nextRow(SXSSFSheet sh, Integer row){
        //get row
        SXSSFRow r = sh.getRow(row);

        //if row not exists, create row
        if(r == null){
            r = sh.createRow(row);
        }
        return r;
    }

    //putDemitidos - put demitidos in sheet 'Sheet Demitidos' on excel
    public void putDemitidos(){
        //call putDiferentsEmployees with ini section 'Sheet Admitidos' and model.admitidos as employees
        putDiferentsEmployees("Sheet Demitidos", model.demitidos);
    }

    //putAdmitidos - put admitidos in sheet 'Sheet Admitidos' on excel
    public void putAdmitidos(){
        //call putDiferentsEmployees with ini section 'Sheet Admitidos' and model.admitidos as employees
        putDiferentsEmployees("Sheet Admitidos", model.admitidos);
    }

    //putDiferentsEmployees
    public void putDiferentsEmployees(String iniSection, List<Map<String,String>> employees){
        //get sheet by ini section 'Sheet Admitidos' and key 'name'
        SXSSFSheet sh = wb.getSheet(ComparePayroll.ini.get(iniSection,"name").toString());

        //Integer row = ini section 'Sheet Admitidos' and key 'start_row'
        Integer row = Integer.parseInt(ComparePayroll.ini.get(iniSection,"start_row").toString()) - 1;

        //get col map
        Map<String, Integer> cols = getColMap(
            iniSection, 
            new String[]{
                "code", "name", "function", "salary"
            }
        );
        
        //for each admitido in model, add +1 in row, create row, if not exists, put values
        for(Map<String,String> employee : employees){
            //get row
            SXSSFRow r = sh.getRow(row);

            //if row not exists, create row
            if(r == null){
                r = sh.createRow(row);
            }
            
            //put value in col_code
            r.createCell(cols.get("code")).setCellValue(employee.get("code"));
            //put value in col_name
            r.createCell(cols.get("name")).setCellValue(employee.get("name"));
            //put value in col_function
            r.createCell(cols.get("function")).setCellValue(employee.get("function"));
            //put value in col_salary
            r.createCell(cols.get("salary")).setCellValue(employee.get("salary"));
            //add +1 in row
            row++;
        }
    }

    //putWarnings - put warnings in sheet 'Sheet Warnings' on excel
    public void putWarnings(Map<String,String> warnings){
        //set ini section 'Sheet Warnings'
        String iniSection = "Sheet Warnings";

        //get sheet by ini section 'Sheet Warnings' and key 'name'
        SXSSFSheet sh = wb.getSheet(ComparePayroll.ini.get(iniSection,"name").toString());

        //Integer row = ini section 'Sheet Warnings' and key 'start_row'
        Integer row = Integer.parseInt(ComparePayroll.ini.get(iniSection,"start_row").toString()) - 1;

        //get col map
        Map<String, Integer> cols = getColMap(
            iniSection, 
            new String[]{
                "file", "warning"
            }
        );

        //for each warning in warnings, add +1 in row, create row, if not exists, put values
        for(Map.Entry<String,String> warning : warnings.entrySet()){
            //get row
            SXSSFRow r = sh.getRow(row);

            //if row not exists, create row
            if(r == null){
                r = sh.createRow(row);
            }
            
            //put value in col_file
            r.createCell(cols.get("file")).setCellValue(warning.getKey());
            //put value in col_warning
            r.createCell(cols.get("warning")).setCellValue(warning.getValue());
            //add +1 in row
            row++;
        }
    }

    //is ignore - check if any ignore in ignores is filter of string
    public boolean isIgnore(String filter){
        //for each map in ignores
        for(Map.Entry<String,StringFilter> ignore : ignores.entrySet()){
            //if is filter of string, return true
            if(ignore.getValue().filterOfString(filter)){
                return true;
            }
        }

        //return false
        return false;
    }

    //putDifferences - put differences in sheet 'Sheet Diferencas' on excel
    public void putDifferences(String file1name, String file2name){
        //set ini section 'Sheet Diferencas'
        String iniSection = "Sheet Diferencas";

        //get sheet by ini section 'Sheet Diferencas' and key 'name'
        SXSSFSheet sh = wb.getSheet(ComparePayroll.ini.get(iniSection,"name").toString());

        //Integer row = ini section 'Sheet Diferencas' and key 'start_row'
        Integer row = Integer.parseInt(ComparePayroll.ini.get(iniSection,"start_row").toString()) - 1;

        //get col map
        Map<String, Integer> cols = getColMap(
            iniSection, 
            new String[]{
                "code", "name", "lcto", "difference", "current_period", "last_period", "resolved"
            }
        );

        //for each model.diferencas, add +1 in row, create row, if not exists, put values
        for(DiferencasColaborador difference : model.diferencas){
            //get row
            SXSSFRow r = nextRow(sh, row);
            
            //PUT HEADERS
            //put value in col_code
            r.createCell(cols.get("code")).setCellValue(difference.getCodigoColaborador());
            //set painted cell style in col_code
            r.getCell(cols.get("code")).setCellStyle(paintedCellStyle);
            //put value in col_name
            r.createCell(cols.get("name")).setCellValue(difference.getColaborador());
            //set painted cell style in col_name
            r.getCell(cols.get("name")).setCellStyle(paintedCellStyle);
            //put value in col_lcto
            r.createCell(cols.get("lcto")).setCellValue("Diferença");
            //set painted cell style in col_lcto
            r.getCell(cols.get("lcto")).setCellStyle(paintedCellStyle);
            //put value in col_difference
            r.createCell(cols.get("difference")).setCellValue("Valor Diferença");
            //set painted cell style in col_difference
            r.getCell(cols.get("difference")).setCellStyle(paintedCellStyle);
            //put value in col_current_period
            r.createCell(cols.get("current_period")).setCellValue(file1name);
            //set painted cell style in col_current_period
            r.getCell(cols.get("current_period")).setCellStyle(paintedCellStyle);
            //put value in col_last_period
            r.createCell(cols.get("last_period")).setCellValue(file2name);  
            //set painted cell style in col_last_period
            r.getCell(cols.get("last_period")).setCellStyle(paintedCellStyle);

            //add +1 in row
            row++;

            //For each diferenca in differrence.diferencas, add +1 in row, create row, if not exists, put values
            for(Diferenca diferenca : difference.diferencas){
                //if descricao of diferenca is not ignore, put values
                if(!isIgnore(diferenca.getDescricao())){
                    //get row
                    r = nextRow(sh, row);
                    
                    //PUT DIFFERENCES
                    //put value in col_lcto
                    r.createCell(cols.get("lcto")).setCellValue(diferenca.getDescricao());
                    //put value in col_difference
                    r.createCell(cols.get("difference")).setCellValue(diferenca.getDiferenca().toPlainString());
                    //put value in col_current_period
                    r.createCell(cols.get("current_period")).setCellValue(diferenca.getValor_1().toPlainString());
                    //put value in col_last_period
                    r.createCell(cols.get("last_period")).setCellValue(diferenca.getValor_2().toPlainString());
                    //add +1 in row
                    row++;
                }
            }

            row++;
        }

    }
}
