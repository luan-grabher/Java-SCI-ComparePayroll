package ComparePayroll.Model;

import ComparePayroll.Model.Entity.Contracheque;
import ComparePayroll.Model.Entity.Diferenca;
import ComparePayroll.Model.Entity.DiferencasColaborador;
import ComparePayroll.Model.Entity.Evento;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Comparar_Model {

    private final List<Contracheque> lista_1;
    private final List<Contracheque> lista_2;

    public List<Map<String,String>> demitidos = new ArrayList<>();
    public List<Map<String,String>> admitidos = new ArrayList<>();

    public List<DiferencasColaborador> diferencas = new ArrayList<>();

    /*
        Usando os contra-cheques de cada folha, monta uma lista de diferenças, admitidos e demitidos.
    */
    public Comparar_Model(List<Contracheque> lista_1, List<Contracheque> lista_2) {
        this.lista_1 = lista_1;
        this.lista_2 = lista_2;

        montarDiferencas();
    }

    //Monta a lista de diferenças, admitidos e demitidos
    private void montarDiferencas() {
        //Montar lista de funcionários demitidos
        definirColaboradoresDiferentes(lista_1, lista_2, demitidos);

        //Montar lista de funcionários admitidos
        definirColaboradoresDiferentes(lista_2, lista_1, admitidos);

        //monta diferencas por colaborador
        montarDiferencasPorColaborador();
    }

    private void montarDiferencasPorColaborador() {
        //Percorre lista 1
        for (int i = 0; i < lista_1.size(); i++) {
            Contracheque cont = lista_1.get(i);

            //Procura contracheque da mesma pessoa
            Optional<Contracheque> cont2 = lista_2.stream().filter(c -> c.getCodigoColaborador() == cont.getCodigoColaborador()).findFirst();

            //Se encontrar, faz comparações
            if (cont2.isPresent()) {
                //pega o contra
                Contracheque contra = cont2.get();

                //Pré cria diferencas
                DiferencasColaborador difColab = new DiferencasColaborador(cont.getColaborador(), cont.getCodigoColaborador());

                //Verifica valores padrões
                //Nome e Funcao
                adicionaDiferencaString(difColab, "Nome", cont, contra);
                adicionaDiferencaString(difColab, "Função", cont, contra);

                //Bigs
                adicionaDiferencaBigDecimals(difColab, "Salário Base", cont, contra);
                adicionaDiferencaBigDecimals(difColab, "Base INSS", cont, contra);
                adicionaDiferencaBigDecimals(difColab, "Valor INSS", cont, contra);
                adicionaDiferencaBigDecimals(difColab, "Base FGTS", cont, contra);
                adicionaDiferencaBigDecimals(difColab, "Valor FGTS", cont, contra);
                adicionaDiferencaBigDecimals(difColab, "Base IRRF", cont, contra);
                adicionaDiferencaBigDecimals(difColab, "Base Rais", cont, contra);
                adicionaDiferencaBigDecimals(difColab, "Base Salário Família", cont, contra);

                //Verifica Eventos
                adicionarDiferencaEventos(difColab, "Desconto" , cont.descontos, contra.descontos);
                adicionarDiferencaEventos(difColab, "Provento" , cont.proventos, contra.proventos);
                
                diferencas.add(difColab);
            }
        }
    }

    private void adicionarDiferencaEventos(DiferencasColaborador difColab, String tipoDiferenca ,List<Evento> ev1, List<Evento> ev2) {
        //Percorre todos eventos 1
        for (int i = 0; i < ev1.size(); i++) {
            Evento evento1 = ev1.get(i);
            Optional<Evento> evento2_OP = ev2.stream().filter(c -> c.getCodigo() == evento1.getCodigo()).findFirst();

            //Se evento existir
            if (evento2_OP.isPresent()) {
                Evento evento2 = evento2_OP.get();
                //Se tiver diferenca
                if (evento1.getValor().compareTo(evento2.getValor()) != 0) {
                    difColab.diferencas.add(
                            new Diferenca(
                                    tipoDiferenca + " - " +
                                    evento1.getNome(),
                                    evento1.getValor().subtract(evento2.getValor()),
                                    evento1.getValor(),
                                    evento2.getValor()
                            )
                    );
                }
            } else {
                difColab.diferencas.add(
                        new Diferenca(
                                tipoDiferenca + " - " +
                                evento1.getNome(),
                                evento1.getValor().multiply(new BigDecimal(-1)),
                                evento1.getValor(),
                                BigDecimal.ZERO
                        )
                );
            }
        }

        //Percorre todos eventos 2
        for (int i = 0; i < ev2.size(); i++) {
            Evento evento2 = ev2.get(i);
            Optional<Evento> evento1_OP = ev2.stream().filter(c -> c.getCodigo() == evento2.getCodigo()).findFirst();

            if (!evento1_OP.isPresent()) {
                difColab.diferencas.add(
                        new Diferenca(
                                tipoDiferenca + " - " +
                                evento2.getNome(),
                                evento2.getValor(),
                                evento2.getValor(),
                                BigDecimal.ZERO
                        )
                );
            }
        }
    }

    private void adicionaDiferencaString(DiferencasColaborador difColab, String nomeDif, Contracheque cont, Contracheque contra) {
        if (!cont.getString(nomeDif).equals(contra.getString(nomeDif))) {
            difColab.diferencas.add(
                    new Diferenca(
                            "Alteração (" + nomeDif
                            + ") de: " + cont.getString(nomeDif)
                            + " para: " + contra.getString(nomeDif),
                            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
                    )
            );
        }
    }

    private void adicionaDiferencaBigDecimals(DiferencasColaborador difColab, String nomeBigDecimal, Contracheque cont, Contracheque contra) {
        if (cont.getBig(nomeBigDecimal).compareTo(contra.getBig(nomeBigDecimal)) != 0) {
            difColab.diferencas.add(
                    new Diferenca(
                            nomeBigDecimal,
                            cont.getBig(nomeBigDecimal).subtract(contra.getBig(nomeBigDecimal)),
                            cont.getBig(nomeBigDecimal),
                            contra.getBig(nomeBigDecimal)
                    )
            );
        }
    }

    /**
     * Adiciona na lista de diferentes passada os colaboradores que estão na lista 1 e não estão na lista 2
     * @param lista1 Lista de colaboradores 1
     * @param lista2 Lista de colaboradores 2
     * @param diferentes Lista de colaboradores diferentes
     */
    private void definirColaboradoresDiferentes(List<Contracheque> lista1, List<Contracheque> lista2, List<Map<String,String>> diferentes) {
        for (int i = 0; i < lista1.size(); i++) {
            Contracheque cont = lista1.get(i);
            long existe = lista2.stream().filter(c -> c.getCodigoColaborador() == cont.getCodigoColaborador()).count();

            if (existe == (long) 0) {
                Map<String,String> dif = new HashMap<>();
                dif.put("code", String.valueOf(cont.getCodigoColaborador()));
                dif.put("name", cont.getColaborador());
                dif.put("function", cont.getFuncao());
                dif.put("salary", cont.getSalarioBase().toString());

                diferentes.add(dif);
            }
        }
    }
}
