package main;

public class Teste {

    public static void main(String[] args) {
        String texto = "pis: 126.88970.71.4   CTPS: 88753   CBO: 513405   Função: Garcom";               
        
        String regex = "(?i).*?PIS.*?";
        System.out.println("Match: " + texto.matches(regex));
        System.out.println("Replace: " + texto.replaceAll(regex,""));
    }
    
}
