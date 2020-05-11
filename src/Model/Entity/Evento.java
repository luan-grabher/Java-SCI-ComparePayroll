package Model.Entity;

import java.math.BigDecimal;

public class Evento {
    private final int codigo;
    private final String nome;
    private final String referencia;
    private final BigDecimal valor;

    public Evento(int codigo, String nome, String referencia, BigDecimal valor) {
        this.codigo = codigo;
        this.nome = nome;
        this.referencia = referencia;
        this.valor = valor;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getReferencia() {
        return referencia;
    }

    public BigDecimal getValor() {
        return valor;
    }
    
    
}
