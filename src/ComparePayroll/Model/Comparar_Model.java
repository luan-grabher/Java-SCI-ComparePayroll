package ComparePayroll.Model;

import ComparePayroll.Model.Entity.Contracheque;
import ComparePayroll.Model.Entity.Diferenca;
import ComparePayroll.Model.Entity.DiferencasColaborador;
import ComparePayroll.Model.Entity.Evento;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Comparar_Model {

    private final List<Contracheque> lista_1;
    private final List<Contracheque> lista_2;

    private List<List<String>> demitidos = new ArrayList<>();
    private List<List<String>> admitidos = new ArrayList<>();

    private List<DiferencasColaborador> diferencas = new ArrayList<>();

    public Comparar_Model(List<Contracheque> lista_1, List<Contracheque> lista_2) {
        this.lista_1 = lista_1;
        this.lista_2 = lista_2;

        montarDiferencas();
    }

    public String getDemitidos() {
        return renderAdmOuDem("Demitidos", demitidos);
    }

    public String getAdmitidos() {
        return renderAdmOuDem("Admitidos", admitidos);
    }

    private String renderAdmOuDem(String nome, List<List<String>> lista) {
        if (lista.size() > 0) {
            StringBuilder str = new StringBuilder(nome + ";Codigo;Nome;Funcao;Salario\r\n");

            for (int i = 0; i < lista.size(); i++) {
                List<String> vals = lista.get(i);
                for (int j = 0; j < vals.size(); j++) {
                    String val = vals.get(j);
                    str.append(";" + val);
                }
                str.append("\r\n");
            }

            return str.toString();
        } else {
            return "";
        }
    }
    
    public String renderDiferencas(String nomeResumo1, String nomeResumo2){
        if(diferencas.size() > 0){
            StringBuilder str = new StringBuilder("\r\n");
            for (int i = 0; i < diferencas.size(); i++) {
                DiferencasColaborador diferenca = diferencas.get(i);
                str.append("\r\n\r\n(");
                str.append(diferenca.getCodigoColaborador());
                str.append(")");
                str.append(diferenca.getColaborador());
                
                str.append("\r\n;Diferença;Valor Diferença;" + nomeResumo1.replaceAll("\\.", "-") + ";" + nomeResumo2.replaceAll("\\.", "-"));
                
                for (int j = 0; j < diferenca.diferencas.size(); j++) {
                    Diferenca dif = diferenca.diferencas.get(j);
                    
                    str.append("\r\n");
                    str.append(";" + dif.getDescricao());
                    str.append(";" + dif.getDiferenca().toString().replaceAll("\\.", ","));
                    str.append(";" + dif.getValor_1().toString().replaceAll("\\.", ","));
                    str.append(";" + dif.getValor_2().toString().replaceAll("\\.", ","));
                }
            }
            return str.toString();
        }else{
            return "";
        }
    }

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
                                    evento2.getValor().subtract(evento1.getValor()),
                                    evento1.getValor(),
                                    evento2.getValor()
                            )
                    );
                }
            } else {
                difColab.diferencas.add(
                        new Diferenca(
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
                            contra.getBig(nomeBigDecimal).subtract(cont.getBig(nomeBigDecimal)),
                            cont.getBig(nomeBigDecimal),
                            contra.getBig(nomeBigDecimal)
                    )
            );
        }
    }

    private void definirColaboradoresDiferentes(List<Contracheque> lista1, List<Contracheque> lista2, List<List<String>> diferentes) {
        for (int i = 0; i < lista1.size(); i++) {
            Contracheque cont = lista1.get(i);
            long existe = lista2.stream().filter(c -> c.getCodigoColaborador() == cont.getCodigoColaborador()).count();

            if (existe == (long) 0) {
                List<String> demit = new ArrayList<>();
                demit.add(String.valueOf(cont.getCodigoColaborador()));
                demit.add(cont.getColaborador());
                demit.add(cont.getFuncao());
                demit.add(cont.getSalarioBase().toString());
                diferentes.add(demit);
            }
        }
    }
}
