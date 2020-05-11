package Model.Entity;

import java.math.BigDecimal;

public class Diferenca {
    private final String descricao;
    
    private final BigDecimal diferenca;
    private final BigDecimal valor_1;
    private final BigDecimal valor_2;

    public Diferenca(String descricao, BigDecimal diferenca, BigDecimal valor_1, BigDecimal valor_2) {
        this.descricao = descricao;
        this.diferenca = diferenca;
        this.valor_1 = valor_1;
        this.valor_2 = valor_2;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public BigDecimal getValor_1() {
        return valor_1;
    }

    public BigDecimal getValor_2() {
        return valor_2;
    }
    
    
}
