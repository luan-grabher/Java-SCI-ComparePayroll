package ComparePayroll.Model;

import ComparePayroll.ComparePayroll;
import ComparePayroll.Control.Comparar_Control;
import ComparePayroll.Model.Entity.Contracheque;
import ComparePayroll.Model.Entity.Evento;
import SimpleView.Loading;
import fileManager.Args;
import fileManager.FileManager;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Contracheques_Model {

    private static final int STRING_COMPARE_REGEX = 0;
    private static final int STRING_COMPARE_EQUALS = 1;

    private static final int EVENT_TYPE_EARNING = 0;
    private static final int EVENT_TYPE_DISCOUNT = 1;

    private final File arquivo;
    public final Map<String, Contracheque> payrolls = new TreeMap<>();
    public Integer payrollsFinded = 0;

    //Mapeamento de colunas
    private static final Map<String, Integer> mapCols = new HashMap<>();

    public Contracheques_Model(File arquivo) {
        this.arquivo = arquivo;
    }
    

    public List<Contracheque> getContracheques() {
        //Cria uma lista para retornar
        //Essa conversão pra lista só existe porque fiz a alteração no programa para funcioonar as colunas e o resto do programa usa lista e eu nao quero olhar o resto do programa
        List<Contracheque> list = new ArrayList<>();

        //Percorre todas folhas e coloca na lista
        payrolls.entrySet().forEach((entry) -> {
            list.add(entry.getValue());
        });
        return list;
    }

    public void criarListaContracheques() {
        //Pega texto do arquivo
        String textoArquivo = FileManager.getText(arquivo.getAbsolutePath());

        //Pega linhas do arquivo, 0 limite de "-1" diz que irá pegar as linhas após o ultimo valor, mesmo que estejam vazias
        String[] linhas = textoArquivo.split("\r\n", -1);
        int max = linhas.length - 1;

        /*Loading*/
        Loading loading = new Loading("Criando lista de contracheques", 0, max);

        //Define a variável que irá controlar o contracheque atual
        String payrollNow = "";

        //Define para acessar o número da coluna onde ficam os códigos
        Integer column_code = Integer.valueOf(ComparePayroll.ini.get("ComparePayroll", "column_code"));

        //Percorre todas as linhas
        for (int i = 0; i <= max; i++) {
            try {
                //Atualiza a barra de loading
                loading.updateBar(i + " de " + max, i);

                String a = ComparePayroll.ini.get("ComparePayroll", "column_name");

                //Pega colunas, 0 limite de "-1" diz que irá pegar as colunas após o ultimo valor, mesmo que estejam vazias
                String[] colunas = linhas[i].split(";", -1);
                //Lista de colunas
                List<String> cols = new LinkedList<>(Arrays.asList(colunas));
                
                if("3790".equals(cols.get(0)) && "08.csv".equals(arquivo.getName())){
                    System.out.println(i);
                }

                //Atualizar colunas
                updateMapOfColumnIfExists("name", colunas, STRING_COMPARE_EQUALS);

                //Se update a coluna do inss, atualiza todas
                if (updateMapOfColumnIfExists("inss_base", colunas, STRING_COMPARE_EQUALS)) {
                    //Atualiza colunas
                    updateMapOfColumnIfExists("inss_value", colunas, STRING_COMPARE_EQUALS);
                    updateMapOfColumnIfExists("fgts_base", colunas, STRING_COMPARE_EQUALS);
                    updateMapOfColumnIfExists("fgts_value", colunas, STRING_COMPARE_EQUALS);
                    updateMapOfColumnIfExists("irrf_base", colunas, STRING_COMPARE_EQUALS);
                    updateMapOfColumnIfExists("rais_base", colunas, STRING_COMPARE_EQUALS);
                    updateMapOfColumnIfExists("family_salary_base", colunas, STRING_COMPARE_EQUALS);

                }//Se a primeira coluna for "Folha"
                else if (payrollNow != null && colunas[column_code].equals(ComparePayroll.ini.get("ComparePayroll", "column_sheet"))) {
                    //Define bases
                    payrolls.get(payrollNow).setBaseInss(getBigDecimal(colunas[mapCols.get("inss_base")]));
                    payrolls.get(payrollNow).setValorInss(getBigDecimal(colunas[mapCols.get("inss_value")]));
                    payrolls.get(payrollNow).setBaseFgts(getBigDecimal(colunas[mapCols.get("fgts_base")]));
                    payrolls.get(payrollNow).setValorFgts(getBigDecimal(colunas[mapCols.get("fgts_value")]));
                    payrolls.get(payrollNow).setBaseIrrf(getBigDecimal(colunas[mapCols.get("irrf_base")]));
                    payrolls.get(payrollNow).setBaseRais(getBigDecimal(colunas[mapCols.get("rais_base")]));
                    payrolls.get(payrollNow).setBaseSalarioFamilia(getBigDecimal(colunas[mapCols.get("family_salary_base")]));

                    // Exibe alerta se o robô não pegou nenhum provento ou desconto                     
                    if (payrolls.get(payrollNow).proventos.isEmpty() || payrolls.get(payrollNow).descontos.isEmpty()) {
                        Comparar_Control.warnings.put(arquivo.getName(), "Nenhum provento ou desconto encontrado para o contracheque " + payrolls.get(payrollNow).getColaborador());
                    }

                    //Se tiver a coluna da folha
                    payrollNow = null;
                } //Se atualizar a coluna de base salário, quer dizer que é um novo funcionário
                else if (updateMapOfColumnIfExists("base_salary", colunas, STRING_COMPARE_REGEX)) {
                    //Adiciona folha de pagamento encontradas para avisar
                    payrollsFinded++;

                    //Se já tiver definido a coluna de nome
                    if (mapCols.containsKey("name")) {
                        //Adiciona uma nova folha de pagamento
                        payrolls.put(
                                colunas[mapCols.get("name")], //Nome do colaborador
                                new Contracheque(
                                        //Código de colaborador
                                        Integer.valueOf(colunas[column_code]),
                                        colunas[mapCols.get("name")], //Nome do colaborador
                                        Contracheque.getSalarioBaseFromString(
                                                colunas[mapCols.get("base_salary")] //Salário base da coluna salário base
                                        )
                                )
                        );

                        //Define a chave da folha de pagamento atual
                        payrollNow = colunas[mapCols.get("name")];
                    }

                } else if (updateMapOfColumnIfExists("earnings", colunas, STRING_COMPARE_EQUALS)) {//PROVENTOS
                    //Se tiver a coluna de proventos, é a coluna de títulos dos proventos e descontos

                    //Atualiza colunas dos proventos e valor
                    updateMapOfColumnIfExists("earnings_reference", colunas, STRING_COMPARE_REGEX, 0); //REFERÊNCIA
                    updateMapOfColumnIfExists("earnings_value", colunas, STRING_COMPARE_EQUALS, 0); //VALOR

                    updateMapOfColumnIfExists("discounts", colunas, STRING_COMPARE_EQUALS); //DESCONTOS
                    updateMapOfColumnIfExists("discounts_reference", colunas, STRING_COMPARE_REGEX, 1); //REFERENCIA
                    updateMapOfColumnIfExists("discounts_value", colunas, STRING_COMPARE_EQUALS, 1); //VALOR
                } /**
                 * Se tiver uma coluna com apenas numeros(codigo) outra com
                 * somente letras(nome evento) e outra com apenas valor
                 */
                else if (payrollNow != null
                        && Args.indexOf(colunas, Args.INDEX_OF_SEARCH_TYPE_REGEX_EQUALS, "[0-9]+") != -1 //Se Existir uma coluna com apenas números
                        && Args.indexOf(colunas, Args.INDEX_OF_SEARCH_TYPE_REGEX_EQUALS, "[0-9.]+,[0-9]+") != -1 //Se Existir uma coluna com valores
                        && Args.indexOf(colunas, Args.INDEX_OF_SEARCH_TYPE_REGEX_EQUALS, "[a-zA-ZáàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ ]+") != -1 //Se Existir uma coluna com apenas letras
                        ) {
                    /**
                     * Para pegar os proventos e descontos, primeiro devemos,
                     * separar as colunas da primeira coluna, até a coluna de
                     * descontos. Depois, nas colunas de proventos e descontos,
                     * deveremos excluir as colunas vazias. Se tiver 3 colunas,
                     * não existe referência, se existir 4 colunas, tem
                     * referencia
                     *
                     * ID - Será a primeira coluna do conjunto de colunas Nome -
                     * Será a segunda coluna do conjunto de colunas Referência -
                     * Será a 3ª coluna do conjunto de colunas se existirem 4
                     * colunas no conjunto Valor - Será a última coluna do
                     * conjunto de colunas
                     */

                    //Se tiver no minimo 1 coluna para poder diminuir depois e não ser menor do que 0
                    if (mapCols.get("discounts") > 0
                            && mapCols.get("earnings") > 0
                            && mapCols.get("earnings") < mapCols.get("discounts")) {
                        //Sublista para os proventos e descontos
                        List<String> earningsCols = new ArrayList(cols.subList(0, mapCols.get("discounts")));
                        List<String> discountCols = new ArrayList(cols.subList(mapCols.get("discounts"), cols.size()));

                        //Remove espaços em branco
                        earningsCols.removeIf(s -> s.trim().equals(""));
                        discountCols.removeIf(s -> s.trim().equals(""));

                        insertEventIfExistsInColumns(earningsCols, payrollNow, EVENT_TYPE_EARNING);
                        insertEventIfExistsInColumns(discountCols, payrollNow, EVENT_TYPE_DISCOUNT);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro no: " + payrollNow);
                e.printStackTrace();
            }
        }

        //Fecha carregamento
        loading.dispose();
    }

    /**
     * Atualiza o valor inteiro no mapa de colunas se a coluna existir na linha
     *
     * @param columnName Nome da coluna, por exemplo "name" se no arquivo ini
     * está "column_name"
     * @param mapCols O mapa de colunas que está sendo usado
     * @param cols As colunas da linha
     * @return Retorna verdadeiro se o mapa for atualizado, falso se continuar
     * igual
     */
    private boolean updateMapOfColumnIfExists(String columnName, String[] cols, int stringCompareType) {
        return updateMapOfColumnIfExists(columnName, cols, stringCompareType, 0);
    }

    /**
     * Atualiza o valor inteiro no mapa de colunas se a coluna existir na linha
     *
     * @param columnName Nome da coluna, por exemplo "name" se no arquivo ini
     * está "column_name"
     * @param mapCols O mapa de colunas que está sendo usado
     * @param cols As colunas da linha
     * @param skip Número de vezes que irá ignorar os achados
     * @return Retorna verdadeiro se o mapa for atualizado, falso se continuar
     * igual
     */
    private boolean updateMapOfColumnIfExists(String columnName, String[] cols, int stringCompareType, int skip) {
        Integer pos = -1;
        String columnFilter = ComparePayroll.ini.get("ComparePayroll", "column_" + columnName);
        if (stringCompareType == STRING_COMPARE_REGEX) {
            //Se o tipo de comparação for regex, compara por regex
            pos = Args.indexOf(cols, Args.INDEX_OF_SEARCH_TYPE_REGEX_BETWEEN, columnFilter, skip);
        } else if (stringCompareType == STRING_COMPARE_EQUALS) {
            //Se o tipo de comparação dor string igual, procura por iguais
            pos = Args.indexOf(cols, Args.INDEX_OF_SEARCH_TYPE_EQUALS_IGNORE_CASE, columnFilter, skip);
        }

        //Se encontrar alguma posição com aquele valor, retorna o valor
        if (pos > -1) {
            mapCols.put(columnName, pos);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Pega o evento nas colunas informadas. Devem ser 3 ou 4 colunas. 3 colunas
     * sem referencia ou 4 com referencia.
     *
     * @param Lista de strings com valores dos proventos ou descontos, sem
     * strings em branco
     * @return evento nas colunas informadas.
     */
    private boolean insertEventIfExistsInColumns(List<String> cols, String payrollNow, int eventType) {
        /**
         * Para pegar os proventos e descontos, primeiro devemos, separar as
         * colunas da primeira coluna, até a coluna de descontos. Depois, nas
         * colunas de proventos e descontos, deveremos excluir as colunas
         * vazias. Se tiver 3 colunas, não existe referência, se existir 4
         * colunas, tem referencia
         *
         * ID - Será a primeira coluna do conjunto de colunas Nome - Será a
         * segunda coluna do conjunto de colunas Referência - Será a 3ª coluna
         * do conjunto de colunas se existirem 4 colunas no conjunto Valor -
         * Será a última coluna do conjunto de colunas
         */

        try {
            //Se existir 3 ou 4 colunas, então está ok
            int size = cols.size();
            if (size == 3 || size == 4) {
                //Codigo sempre será na primeira coluna e o nome na segunda
                int code = Integer.valueOf(cols.get(0));
                String name = cols.get(1).replaceAll("[^a-zA-ZáàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ ]", "").trim();

                String reference = "";
                //Se existir 4 colunas entao tem referencia
                if (cols.size() == 4) {
                    reference = cols.get(2);
                }

                //Valor sempre será a ultima coluna
                BigDecimal value = new BigDecimal(cols.get(cols.size() - 1).replaceAll("\\.", "").replaceAll(",", "."));

                if (eventType == EVENT_TYPE_EARNING) {
                    payrolls.get(payrollNow).proventos.add(
                            new Evento(code, name, reference, value)
                    );
                } else if (eventType == EVENT_TYPE_DISCOUNT) {
                    payrolls.get(payrollNow).descontos.add(
                            new Evento(code, name, reference, value)
                    );
                }
                return true;
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Converte uma string do csv para bigdecimal. fica apenas com numeros '.' e
     * ','. Troca as virgulas por pontos e remove os pontos de milhar
     *
     * @param str String a ser converetida
     * @return BigDecimal convertido da string ou bugdecimal zero
     */
    private BigDecimal getBigDecimal(String str) {
        try {
            return new BigDecimal(str.replaceAll("[^0-9.,]", "").replaceAll("\\.", "").replaceAll(",", "."));
        } catch (Exception e) {
            return new BigDecimal("0.0");
        }
    }

    private boolean isInteger(String str) {
        return str.matches("[0-9]+");
    }
}
