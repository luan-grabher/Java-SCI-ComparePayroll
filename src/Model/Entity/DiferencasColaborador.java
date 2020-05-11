package Model.Entity;

import java.util.ArrayList;
import java.util.List;

public class DiferencasColaborador {
    private String colaborador;
    private int codigoColaborador; 
    
    public List<Diferenca> diferencas = new ArrayList<>();

    public DiferencasColaborador(String colaborador, int codigoColaborador) {
        this.colaborador = colaborador;
        this.codigoColaborador = codigoColaborador;
    }

    public String getColaborador() {
        return colaborador;
    }

    public int getCodigoColaborador() {
        return codigoColaborador;
    }
    
    
}
