package Model.Entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Contracheque {
    private final int codigoColaborador;
    private final String colaborador;
    private final BigDecimal salarioBase;
    private String funcao = "";
    
    private BigDecimal baseInss = new BigDecimal(0);
    private BigDecimal valorInss = new BigDecimal(0);
    private BigDecimal baseFgts = new BigDecimal(0);
    private BigDecimal valorFgts = new BigDecimal(0);
    private BigDecimal baseIrrf = new BigDecimal(0);
    private BigDecimal baseRais = new BigDecimal(0);
    private BigDecimal baseSalarioFamilia = new BigDecimal(0);
    
    public List<Evento> proventos = new ArrayList<>();
    public List<Evento> descontos = new ArrayList<>();

    public Contracheque(int codigoColaborador, String colaborador, BigDecimal salarioBase) {
        this.codigoColaborador = codigoColaborador;
        this.colaborador = colaborador;
        this.salarioBase = salarioBase;
    }
    
    public BigDecimal getBig(String nomeBig){
        switch(nomeBig){
            case "Salário Base":
                return this.salarioBase;
            case "Base INSS":
                return this.baseInss;
            case "Valor INSS":
                return this.valorInss;
            case "Base FGTS":
                return this.baseFgts;
            case "Valor FGTS":
                return this.valorFgts;
            case "Base IRRF":
                return this.baseIrrf;
            case "Base Rais":
                return this.baseRais;
            case "Base Salário Família":
                return this.baseSalarioFamilia;
            default:
                return BigDecimal.ZERO;
        }
        
    }
    
    public String getString(String nomeString){
        switch(nomeString){
            case "Nome":
                return this.colaborador;
            case "Função":
                return this.funcao;
            default:
                return "";
        }
        
    }

    public int getCodigoColaborador() {
        return codigoColaborador;
    }

    public String getColaborador() {
        return colaborador;
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }
        
    public static BigDecimal getSalarioBaseFromString(String str){
        try{
            String primeiroIndex = "Salário base";
            String segundoIndex = "Horas mensais:";

            String valorStr = str.substring(str.indexOf(primeiroIndex), str.indexOf(segundoIndex));
            valorStr = valorStr.replaceAll("[^0-9,]", "").replaceAll(",", ".");
        
            return new BigDecimal(valorStr);
        }catch(Exception e){
            return new BigDecimal(0);
        }
    }
    
    public static String getFuncaoFromString(String str){
        try{
            String index = "Função:";
            
            String valorFuncao = str.substring(str.indexOf(index)+ index.length()).replaceAll("[^a-zA-ZáàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ ]", "").trim();
            
            return valorFuncao;
        }catch(Exception e){
            return "";
        }
    }
    
    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public BigDecimal getBaseInss() {
        return baseInss;
    }

    public void setBaseInss(BigDecimal baseInss) {
        this.baseInss = baseInss;
    }

    public BigDecimal getValorInss() {
        return valorInss;
    }

    public void setValorInss(BigDecimal valorInss) {
        this.valorInss = valorInss;
    }

    public BigDecimal getBaseFgts() {
        return baseFgts;
    }

    public void setBaseFgts(BigDecimal baseFgts) {
        this.baseFgts = baseFgts;
    }

    public BigDecimal getValorFgts() {
        return valorFgts;
    }

    public void setValorFgts(BigDecimal valorFgts) {
        this.valorFgts = valorFgts;
    }

    public BigDecimal getBaseIrrf() {
        return baseIrrf;
    }

    public void setBaseIrrf(BigDecimal baseIrrf) {
        this.baseIrrf = baseIrrf;
    }

    public BigDecimal getBaseRais() {
        return baseRais;
    }

    public void setBaseRais(BigDecimal baseRais) {
        this.baseRais = baseRais;
    }

    public BigDecimal getBaseSalarioFamilia() {
        return baseSalarioFamilia;
    }

    public void setBaseSalarioFamilia(BigDecimal baseSalarioFamilia) {
        this.baseSalarioFamilia = baseSalarioFamilia;
    }

    
}
