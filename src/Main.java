import java.util.List;

public class Main {

    public static void main(String[] args) {

        testar("comentarios",
                """
                // Declaração de variáveis
                int idade <- 20;
                double salario = 1250.50; /* comentário
                                             de bloco */
                char opcao = 'A';
                
                if (idade >= 18 == true) {
                    return "de maior";
                }
                """);

        testar("operadores compostos maximal munch",
                """
                ==
                !=
                <=
                >=
                <-
                =
                !
                <
                >
                """);

        testar("numeros int e float",
                """
                0
                10
                3.1415
                100.0
                """);

        testar("ERRO: string não fechada ate fim de linha",
                "string s = \"Texto sem fechar\nint x = 10;");

        testar("ERRO: dtring não fechada ate EOF",
                "\"String no final do arquivo sem fechar");

        testar("ERRO: comentario multilinha nao fechado ate EOF",
                "/* Inicio de comentário sem fechar...");

        testar("ERRO: char fora do alfabeto",
                "int valor = 10 @ 20 ~ 5;");

        testar("literais de char",
                "'a' 'Z' '1'");
    }

    public static void testar(String nome, String codigo) {
        System.out.println("TESTE: " + nome);
        System.out.println("codigo fonte:");
        System.out.println(codigo);
        System.out.println("tokens e erros:");

        Scanner scanner = new Scanner(codigo);
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens) {
            System.out.println(token);
        }
        System.out.println();
    }
}