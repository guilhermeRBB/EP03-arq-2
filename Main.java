import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    // Valores atuais de X e Y
    static int X = 0;
    static int Y = 0;

    // Indica se X e Y já receberam valores
    static boolean xDefinido = false;
    static boolean yDefinido = false;

    public static void main(String[] args) {

        String arquivoEntrada = "testeula.ula";
        String arquivoSaida = "testeula.hex";

        boolean houveErro = false;

        try {

            BufferedReader leitor =
                    new BufferedReader(new FileReader(arquivoEntrada));

            String linha;

            int numeroLinha = 0;

            // Guardaremos temporariamente o programa gerado
            String programaHex = "";

            while ((linha = leitor.readLine()) != null) {

                numeroLinha++;

                // -------------------------------------------------
                // LINHA EM BRANCO
                // -------------------------------------------------

                if (linha.length() == 0) {

                    mostrarErro(
                            numeroLinha,
                            linha,
                            "Linha em branco encontrada."
                    );

                    houveErro = true;

                    continue;
                }


                // -------------------------------------------------
                // NÃO PERMITE ESPAÇOS
                // -------------------------------------------------

                if (possuiEspaco(linha)) {

                    mostrarErro(
                            numeroLinha,
                            linha,
                            "Nao sao permitidos espacos."
                    );

                    houveErro = true;

                    continue;
                }


                // -------------------------------------------------
                // INICIO:
                // -------------------------------------------------

                if (linha.equals("inicio:")) {

                    continue;
                }


                // -------------------------------------------------
                // FIM.
                // -------------------------------------------------

                if (linha.equals("fim.")) {

                    continue;
                }


                // -------------------------------------------------
                // X = valor
                // -------------------------------------------------

                if (linha.startsWith("X=")) {

                    if (!validarAtribuicaoNumero(linha, 'X')) {

                        mostrarErro(
                                numeroLinha,
                                linha,
                                "Sintaxe incorreta para X."
                        );

                        houveErro = true;

                        continue;
                    }

                    String numero =
                            linha.substring(2, linha.length() - 1);

                    int valor;

                    try {

                        valor = Integer.parseInt(numero);

                    } catch (NumberFormatException e) {

                        mostrarErro(
                                numeroLinha,
                                linha,
                                "Valor de X invalido."
                        );

                        houveErro = true;

                        continue;
                    }


                    // ULA trabalha com 4 bits
                    // valores permitidos: 0 ate 15

                    if (valor < 0 || valor > 15) {

                        mostrarErro(
                                numeroLinha,
                                linha,
                                "X deve estar entre 0 e 15."
                        );

                        houveErro = true;

                        continue;
                    }


                    X = valor;

                    xDefinido = true;

                    continue;
                }

                // Y = valor

                if (linha.startsWith("Y=")) {

                    if (!validarAtribuicaoNumero(linha, 'Y')) {

                        mostrarErro(
                                numeroLinha,
                                linha,
                                "Sintaxe incorreta para Y."
                        );

                        houveErro = true;

                        continue;
                    }

                    String numero =
                            linha.substring(2, linha.length() - 1);

                    int valor;

                    try {

                        valor = Integer.parseInt(numero);

                    } catch (NumberFormatException e) {

                        mostrarErro(
                                numeroLinha,
                                linha,
                                "Valor de Y invalido."
                        );

                        houveErro = true;

                        continue;
                    }


                    if (valor < 0 || valor > 15) {

                        mostrarErro(
                                numeroLinha,
                                linha,
                                "Y deve estar entre 0 e 15."
                        );

                        houveErro = true;

                        continue;
                    }


                    Y = valor;

                    yDefinido = true;

                    continue;
                }


                // W = operacao

                if (linha.startsWith("W=")) {

                    if (!linha.endsWith(";")) {

                        mostrarErro(
                                numeroLinha,
                                linha,
                                "A instrucao W deve terminar com ;"
                        );

                        houveErro = true;

                        continue;
                    }


                    if (!xDefinido || !yDefinido) {

                        mostrarErro(
                                numeroLinha,
                                linha,
                                "X e Y precisam estar definidos antes de W."
                        );

                        houveErro = true;

                        continue;
                    }


                    String mnemonico =
                            linha.substring(2, linha.length() - 1);


                    char codigoOperacao =
                            converterMnemonico(mnemonico);


                    // Código '?' significa mnemônico inválido
                    if (codigoOperacao == '?') {

                        mostrarErro(
                                numeroLinha,
                                linha,
                                "Mnemonico invalido: " + mnemonico
                        );

                        houveErro = true;

                        continue;
                    }


                    // SÓ AQUI É GERADA UMA SAÍDA

                    char xHex = decimalParaHex(X);

                    char yHex = decimalParaHex(Y);


                    String instrucao =
                            "" + xHex + yHex + codigoOperacao;


                    programaHex += instrucao + "\n";


                    System.out.println(
                            "Instrucao gerada: " + instrucao
                    );


                    continue;
                }


                // SE NÃO ENTROU EM NENHUM CASO

                mostrarErro(
                        numeroLinha,
                        linha,
                        "Instrucao desconhecida."
                );

                houveErro = true;
            }


            leitor.close();


            // SE EXISTIR ALGUM ERRO

            if (houveErro) {

                System.out.println();
                System.out.println(
                        "========================================"
                );

                System.out.println(
                        "O programa possui erros."
                );

                System.out.println(
                        "O arquivo testeula.hex NAO foi gerado."
                );

                System.out.println(
                        "Corrija testeula.ula e execute novamente."
                );

                System.out.println(
                        "========================================"
                );

                return;
            }


            // GRAVA O ARQUIVO HEX

            BufferedWriter escritor =
                    new BufferedWriter(new FileWriter(arquivoSaida));


            escritor.write(programaHex);


            escritor.close();


            System.out.println();

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "Programa convertido com sucesso."
            );

            System.out.println(
                    "Arquivo gerado: " + arquivoSaida
            );

            System.out.println(
                    "========================================"
            );


        } catch (IOException e) {

            System.out.println(
                    "Erro ao abrir ou gravar arquivo."
            );

            System.out.println(
                    e.getMessage()
            );
        }
    }


    // VERIFICA SE EXISTE ESPAÇO

    public static boolean possuiEspaco(String linha) {

        for (int i = 0; i < linha.length(); i++) {

            char c = linha.charAt(i);

            if (c == ' ' || c == '\t') {

                return true;
            }
        }

        return false;
    }


    // VALIDA X=numero; OU Y=numero;

    public static boolean validarAtribuicaoNumero(
            String linha,
            char variavel
    ) {

        // Deve começar como X= ou Y=
        if (linha.length() < 4) {

            return false;
        }


        if (linha.charAt(0) != variavel) {

            return false;
        }


        if (linha.charAt(1) != '=') {

            return false;
        }


        if (linha.charAt(linha.length() - 1) != ';') {

            return false;
        }


        // Verifica se o valor possui apenas números

        for (int i = 2; i < linha.length() - 1; i++) {

            char c = linha.charAt(i);

            if (c < '0' || c > '9') {

                return false;
            }
        }


        return true;
    }


    // CONVERTE MNEMONICO PARA HEXADECIMAL

    public static char converterMnemonico(String m) {

        if (m.equals("nA")) {
            return '0';
        }

        if (m.equals("AoBn")) {
            return '1';
        }

        if (m.equals("nAeB")) {
            return '2';
        }

        if (m.equals("zeroL")) {
            return '3';
        }

        if (m.equals("AeBn")) {
            return '4';
        }

        if (m.equals("nB")) {
            return '5';
        }

        if (m.equals("AxB")) {
            return '6';
        }

        if (m.equals("AenB")) {
            return '7';
        }

        if (m.equals("nAoB")) {
            return '8';
        }

        if (m.equals("AxBn")) {
            return '9';
        }

        if (m.equals("copiaB")) {
            return 'A';
        }

        if (m.equals("AeB")) {
            return 'B';
        }

        if (m.equals("umL")) {
            return 'C';
        }

        if (m.equals("AonB")) {
            return 'D';
        }

        if (m.equals("AoB")) {
            return 'E';
        }

        if (m.equals("copiaA")) {
            return 'F';
        }


        return '?';
    }


    // CONVERTE DECIMAL DE 0 A 15 PARA HEX

    public static char decimalParaHex(int valor) {

        if (valor >= 0 && valor <= 9) {

            return (char) ('0' + valor);
        }


        return (char) ('A' + (valor - 10));
    }


    // MOSTRA ERRO

    public static void mostrarErro(
            int numeroLinha,
            String linha,
            String mensagem
    ) {

        System.out.println();

        System.out.println(
                "ERRO NA LINHA " + numeroLinha
        );

        System.out.println(
                "Conteudo: [" + linha + "]"
        );

        System.out.println(
                mensagem
        );
    }
}
