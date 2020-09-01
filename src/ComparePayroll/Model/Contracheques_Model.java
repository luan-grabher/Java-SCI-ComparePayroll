package ComparePayroll.Model;

import ComparePayroll.Model.Entity.Contracheque;
import ComparePayroll.Model.Entity.Evento;
import SimpleView.Loading;
import fileManager.FileManager;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.ini4j.Ini;

public class Contracheques_Model {    

    private final File arquivo;
    private final Map<String, Contracheque> payrolls = new TreeMap<>();
    private List<Contracheque> contracheques = new ArrayList<>();

    public Contracheques_Model(File arquivo) {
        this.arquivo = arquivo;

        criarListaContracheques();
    }

    public List<Contracheque> getContracheques() {
        return contracheques;
    }
    
    private void criarListaContracheques() {
        //Pega texto do arquivo
        String textoArquivo = FileManager.getText(arquivo.getAbsolutePath());

        //Pega linhas do arquivo, 0 limite de "-1" diz que irá pegar as linhas após o ultimo valor, mesmo que estejam vazias
        String[] linhas = textoArquivo.split("\r\n", -1);
        int max = linhas.length - 1;

        /*Loading*/
        Loading loading = new Loading("Criando lista de contracheques", 0, max);

        //Define a variável que irá controlar o contracheque atual
        String payrollNow = "";
        Contracheque contrachequeAtual = null;
        for (int i = 0; i <= max; i++) {
            try {
                //Atualiza a barra de loading
                loading.updateBar(i + " de " + max, i);
                
               

                //Pega colunas, 0 limite de "-1" diz que irá pegar as colunas após o ultimo valor, mesmo que estejam vazias
                String[] colunas = linhas[i].split(";", -1);

                //Se tiver numero nas 3 primeiras colunas, é novo contracheque
                if (isInteger(colunas[0]) && isInteger(colunas[1]) && isInteger(colunas[2])) {
                    int codigoColaborador = Integer.valueOf(colunas[2]);
                    String colaborador = colunas[3];
                    BigDecimal salarioBase = Contracheque.getSalarioBaseFromString(colunas[27]);

                    contracheques.add(new Contracheque(codigoColaborador, colaborador, salarioBase));
                    contrachequeAtual = contracheques.get(contracheques.size() - 1);

                    //Se não for novo e o contracheque não for nulo
                } else if (contrachequeAtual != null) {
                    //Se for a linha da função
                    if (colunas[3].contains("Função:")) {
                        String funcao = Contracheque.getFuncaoFromString(colunas[3]);
                        contrachequeAtual.setFuncao(funcao);

                        //Se tiver proventos ou descontos
                    } else if ((isInteger(colunas[0]) && !"".equals(colunas[1]) && colunas[25].matches("[0-9,.]+")) //proventos
                            || (isInteger(colunas[33]) && !"".equals(colunas[37]) && colunas[62].matches("[0-9,.]+")) //descontos
                            ) {
                        
                        //Define provento e desconto
                        Evento provento = getEvento(colunas, 0, 1, 25, 17);
                        Evento desconto = getEvento(colunas, 33, 37, 62, 57);
                        
                        //se tiver provento
                        if(provento != null){
                            contrachequeAtual.proventos.add(new Evento(provento.getCodigo(), provento.getNome(), provento.getReferencia(), provento.getValor()));
                        }
                        
                        //se tiver desconto
                        if(desconto != null){
                            contrachequeAtual.descontos.add(new Evento(desconto.getCodigo(), desconto.getNome(), desconto.getReferencia(), desconto.getValor()));
                        }
                        
                    //Se tiver bases 
                    }else if(colunas[0].equals("Folha")){
                        contrachequeAtual.setBaseInss(getBigDecimal(colunas[4]));
                        contrachequeAtual.setValorInss(getBigDecimal(colunas[9]));
                        contrachequeAtual.setBaseFgts(getBigDecimal(colunas[14]));
                        contrachequeAtual.setValorFgts(getBigDecimal(colunas[21]));
                        contrachequeAtual.setBaseIrrf(getBigDecimal(colunas[28]));
                        contrachequeAtual.setBaseRais(getBigDecimal(colunas[35]));
                        contrachequeAtual.setBaseSalarioFamilia(getBigDecimal(colunas[39]));
                    }
                }
            } catch (Exception e) {
            }
        }
        
        //Fecha carregamento
        loading.dispose();
    }
    
    private Evento getEvento(String[] colunas, int colunaCodigo, int colunaNome, int colunaValor, int colunaReferencia){
        try{
            if(isInteger(colunas[colunaCodigo]) && !"".equals(colunas[colunaNome]) && colunas[colunaValor].matches("[0-9,.]+")){
                int codigo = Integer.valueOf(colunas[colunaCodigo]);
                String evento = colunas[colunaNome].replaceAll("[^a-zA-ZáàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ ]", "").trim();
                BigDecimal valor = new BigDecimal(colunas[colunaValor].replaceAll("\\.", "").replaceAll(",", "."));
                String referencia =  colunas[colunaReferencia];

                return new Evento(codigo, evento, referencia, valor);
            }else{
                return null;
            }
        }catch(Exception e){
            return null;
        }
    }

    private BigDecimal getBigDecimal(String str){
        try {
            return new BigDecimal(str.replaceAll("[^0-9.,]", "").replaceAll("\\.", "").replaceAll(",", "."));
        } catch (Exception e) {
            return new BigDecimal(0);
        }
    }
    private boolean isInteger(String str) {
        return str.matches("[0-9]+");
    }
}
