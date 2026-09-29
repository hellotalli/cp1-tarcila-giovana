import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    private final String codigo;

    private int atual = 0;
    private int inicio = 0;

    private int linha = 1;
    private int coluna = 1;
    private int inicioColuna = 1;

    private final List<Token> tokens = new ArrayList<>();

    private static final Map<String, TipoToken> palavrasReservadas = new HashMap<>();

    static {
        palavrasReservadas.put("int", TipoToken.INT);
        palavrasReservadas.put("double", TipoToken.DOUBLE);
        palavrasReservadas.put("bool", TipoToken.BOOL);
        palavrasReservadas.put("char", TipoToken.CHAR);
        palavrasReservadas.put("string", TipoToken.STRING_TYPE);
        palavrasReservadas.put("if", TipoToken.IF);
        palavrasReservadas.put("else", TipoToken.ELSE);
        palavrasReservadas.put("while", TipoToken.WHILE);
        palavrasReservadas.put("return", TipoToken.RETURN);
        palavrasReservadas.put("true", TipoToken.BOOLEANO);
        palavrasReservadas.put("false", TipoToken.BOOLEANO);
    }

    public Scanner(String codigo) {
        this.codigo = codigo;
    }

    public List<Token> scanTokens() {
        while (!estaNoFinal()) {
            inicio = atual;
            inicioColuna = coluna;
            scanToken();
        }

        tokens.add(new Token(TipoToken.EOF, "", linha, coluna));
        return tokens;
    }

    private void scanToken() {
        char c = avancar();

        switch (c) {
            // espaços
            case ' ':
            case '\r':
            case '\t':
                break;

            case '\n':
                linha++;
                coluna = 1;
                break;

            // delimitadores
            case '(':
                adicionarToken(TipoToken.LPAREN);
                break;
            case ')':
                adicionarToken(TipoToken.RPAREN);
                break;
            case '{':
                adicionarToken(TipoToken.ABRE_CHAVE);
                break;
            case '}':
                adicionarToken(TipoToken.FECHA_CHAVE);
                break;
            case ';':
                adicionarToken(TipoToken.PONTO_VIRGULA);
                break;
            case ',':
                adicionarToken(TipoToken.VIRGULA);
                break;

            // operadores simples e compostos
            case '+':
                adicionarToken(TipoToken.MAIS);
                break;
            case '-':
                adicionarToken(TipoToken.MENOS);
                break;
            case '*':
                adicionarToken(TipoToken.VEZES);
                break;

            case '/':
                if (corresponde('/')) {
                    // Comentário de linha: consome até o fim da linha ou EOF
                    while (peek() != '\n' && !estaNoFinal()) {
                        avancar();
                    }
                } else if (corresponde('*')) {
                    // Comentário de bloco
                    comentarioBloco();
                } else {
                    adicionarToken(TipoToken.DIVIDIR);
                }
                break;

            case '=':
                if (corresponde('=')) {
                    adicionarToken(TipoToken.IGUAL_IGUAL);
                } else {
                    adicionarToken(TipoToken.IGUAL);
                }
                break;

            case '!':
                if (corresponde('=')) {
                    adicionarToken(TipoToken.DIFERENTE);
                } else {
                    adicionarToken(TipoToken.NAO);
                }
                break;

            case '<':
                if (corresponde('-')) {
                    adicionarToken(TipoToken.ATRIBUI);
                } else if (corresponde('=')) {
                    adicionarToken(TipoToken.MENOR_IGUAL);
                } else {
                    adicionarToken(TipoToken.MENOR);
                }
                break;

            case '>':
                if (corresponde('=')) {
                    adicionarToken(TipoToken.MAIOR_IGUAL);
                } else {
                    adicionarToken(TipoToken.MAIOR);
                }
                break;

            // strings
            case '"':
                string();
                break;

            // chars (aspas simples)
            case '\'':
                caractereLiteral();
                break;

            default:
                if (ehLetra(c)) {
                    identificador();
                } else if (ehDigito(c)) {
                    numero();
                } else {
                    erro("Caractere inesperado '" + c + "'");
                }
                break;
        }
    }

    private void comentarioBloco() {
        int linhaInicio = linha;
        int colunaInicio = inicioColuna;

        while (!estaNoFinal()) {
            if (peek() == '*' && peekProximo() == '/') {
                avancar(); // consome '*'
                avancar(); // consome '/'
                return;
            }

            if (peek() == '\n') {
                linha++;
                coluna = 0; // será incrementado para 1 no avancar()
            }
            avancar();
        }

        erroEm(linhaInicio, colunaInicio, "Comentário de bloco não fechado até o fim do arquivo (EOF).");
    }

    private void identificador() {
        while (!estaNoFinal() && (ehLetra(peek()) || ehDigito(peek()) || peek() == '_')) {
            avancar();
        }

        String texto = codigo.substring(inicio, atual);
        TipoToken tipo = palavrasReservadas.get(texto);

        if (tipo == null) {
            tipo = TipoToken.IDENTIFICADOR;
        }

        adicionarToken(tipo);
    }

    private void numero() {
        while (!estaNoFinal() && ehDigito(peek())) {
            avancar();
        }

        // reconhece float
        if (peek() == '.' && ehDigito(peekProximo())) {
            avancar(); // consome ponto

            while (!estaNoFinal() && ehDigito(peek())) {
                avancar();
            }

            adicionarToken(TipoToken.FLOAT);
        } else {
            adicionarToken(TipoToken.INTEGER);
        }
    }

    private void string() {
        int linhaInicio = linha;
        int colunaInicio = inicioColuna;

        while (!estaNoFinal() && peek() != '"') {
            if (peek() == '\n') {
                // erro strings multilinha
                erroEm(linhaInicio, colunaInicio, "string nao terminada antes do fim da linha");
                return;
            }
            avancar();
        }

        if (estaNoFinal()) {
            erroEm(linhaInicio, colunaInicio, "string nao terminada ate EOF");
            return;
        }

        // donsome as aspa de fechamento
        avancar();
        adicionarToken(TipoToken.STRING);
    }

    private void caractereLiteral() {
        int linhaInicio = linha;
        int colunaInicio = inicioColuna;

        if (estaNoFinal() || peek() == '\'' || peek() == '\n') {
            erroEm(linhaInicio, colunaInicio, "literal de caractere char invalido ou não terminado");
            return;
        }

        avancar(); // consome o caractere interno

        if (corresponde('\'')) {
            adicionarToken(TipoToken.CHAR_LITERAL);
        } else {
            erroEm(linhaInicio, colunaInicio, "literal de caractere char deve conter apenas um caractere");
            // recuperacao de erro consome ate achar as aspas simples de fechamento ou eol
            while (!estaNoFinal() && peek() != '\'' && peek() != '\n') {
                avancar();
            }
            if (peek() == '\'') avancar();
        }
    }

    // metodos auxiliares
    private char avancar() {
        coluna++;
        return codigo.charAt(atual++);
    }

    private char peek() {
        if (estaNoFinal()) return '\0';
        return codigo.charAt(atual);
    }

    private char peekProximo() {
        if (atual + 1 >= codigo.length()) return '\0';
        return codigo.charAt(atual + 1);
    }

    private boolean corresponde(char esperado) {
        if (estaNoFinal()) return false;
        if (codigo.charAt(atual) != esperado) return false;

        atual++;
        coluna++;
        return true;
    }

    private boolean estaNoFinal() {
        return atual >= codigo.length();
    }

    private boolean ehLetra(char c) {
        return Character.isLetter(c);
    }

    private boolean ehDigito(char c) {
        return Character.isDigit(c);
    }

    private void adicionarToken(TipoToken tipo) {
        String lexema = codigo.substring(inicio, atual);
        tokens.add(new Token(tipo, lexema, linha, inicioColuna));
    }

    private void erro(String mensagem) {
        System.out.println("erro na linha " + linha + ", coluna " + inicioColuna + ": " + mensagem);
    }

    private void erroEm(int l, int c, String mensagem) {
        System.out.println("erro na linha " + l + ", coluna " + c + ": " + mensagem);
    }
}