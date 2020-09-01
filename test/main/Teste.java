package main;

import ComparePayroll.Model.Entity.Contracheque;
import java.math.BigDecimal;

public class Teste {

    public static void main(String[] args) {
        String texto = "PIS: 126.88970.71.4   CTPS: 88753   CBO: 513405   Função: Garcom";
        String teste = Contracheque.getFuncaoFromString(texto);
        
        System.out.println("'" + teste + "'");
    }
    
}
