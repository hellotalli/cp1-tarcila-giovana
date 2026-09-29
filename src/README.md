
># smollang

`smollang` é uma linguagem simples projetada para a demonstração do analisador léxico (checkpoint 1) do compilador.
> https://github.com/hellotalli/cp1-tarcila-giovana/tree/main/src
---

## 1. tabela de especificação de tokens

| categoria| notação  | exemplos | decisões da linguagem |
| :--- | :--- | :--- | :--- |
| **identificador** | `[a-zA-Z_][a-zA-Z0-9_]*` | `total`, `x1`, `contaItens`, `_val` | case sensitive. permite `_`. tamanho ilimitado. |
| **palavra reservada** | padrão do identificador + tabela hash | `int`, `double`, `bool`, `char`, `string`, `if`, `else`, `while`, `return`, `true`, `false` | sensível a maiúsculas/minúsculas. não podem ser usadas como nome de variável. |
| **literal numerico** | `[0-9]+ ("." [0-9]+)?` | `0`, `10`, `3.1415`, `1250.50` | suporta inteiros e decimais (ponto flutuante). notação científica não suportada por enquanto |
| **string** | `\" [^\n\"\\]* \"` | `"olá!"`, `"maior de idade!"` | delimitado por aspas duplas, nao permite quebra de linha interna. |
| **char** | `' [^\'\n\\] '` | `'a'`, `'Z'`, `'1'` | delimitado por aspas simples, contendo exatamente um caractere. |
| **operador** | `+ \| - \| * \| / \| = \| == \| ! \| != \| < \| <= \| > \| >= \| <-` | `+`, `==`, `!=`, `<=`, `<-` | operadores de 1 ou 2 caracteres. |
| **delimitador** | `( \| ) \| { \| } \| ; \| ,` | `(`, `)`, `{`, `}`, `;`, `,` | usados para agrupamento, blocos e separação. |

---

## 2. decisões formais da Linguagem

### alfabeto de entrada
o alfabeto de entrada da linguagem é composto por:
* conjunto de caracteres ASCII imprimíveis (códigos 32 a 126).
* caracteres de controle de espaço em branco: espaço (` `), tabulação (`\t`), retorno de carro (`\r`) e nova linha (`\n`).

### case sensitive
a linguagem é case sensitive.
* exemplo: `idade`, `Idade` e `IDADE` serão distintos
* todas as palavras reservadas devem ser escritas em minúsculas

### espaços em branco e comentários
* **espaços em branco:** espaços, tabulações e quebras de linha funcionam como delimitadores e são descartados pelo scanner sem gerar tokens (exceto para atualizar contadores de linha e coluna).
* **comentário de linha:** Inicia com `//` e consome todos os caracteres até o final da linha (`\n`) ou fim do arquivo (`EOF`).
* **comentário multilinha:** Inicia com `/*` e termina com `*/`. Pode ocupar múltiplas linhas. **Não** é permitido aninhamento de comentários de bloco.

### regra de maximal munch
Para operadores com forma composta (ex: `=` vs `==`, `<` vs `<=` ou `<-`, `!` vs `!=`), o scanner sempre aplica a regra do **prefixo mais longo (*maximal munch*)**. O scanner consome o maior número de caracteres sequenciais válidos antes de determinar a categoria do token.

### palavras reservadas
A lista exata e fechada de palavras reservadas da linguagem inclui:
* **tipos de dados:** `int`, `double`, `bool`, `char`, `string`
* **estruturas de controle:** `if`, `else`, `while`
* **instrução de retorno:** `return`
* **literais booleanos:** `true`, `false`

---

## 3.tokens reconhecidos
```text
// Tipos Básicos e Reservadas
INT, DOUBLE, BOOL, CHAR, STRING_TYPE,
IF, ELSE, WHILE, RETURN, BOOLEANO,

// Identificadores e Literais
IDENTIFICADOR (ex: total, x1)
INTEGER       (ex: 0, 10, 42)
FLOAT         (ex: 3.14, 100.0)
STRING        (ex: "texto")
CHAR_LITERAL  (ex: 'a')

// Operadores
MAIS         (+)
MENOS        (-)
VEZES        (*)
DIVIDIR      (/)
IGUAL        (=)
IGUAL_IGUAL  (==)
NAO          (!)
DIFERENTE    (!=)
MENOR        (<)
MENOR_IGUAL  (<=)
MAIOR        (>)
MAIOR_IGUAL  (>=)
ATRIBUI      (<-)

// Delimitadores
LPAREN       (()
RPAREN       ())
ABRE_CHAVE   ({)
FECHA_CHAVE  (})
PONTO_VIRGULA(;)
VIRGULA      (,)
EOF          (Fim de Arquivo)
```
###### uso de ia:
> a LLM chatgpt foi utilizada para escrever os testes de palavras na Main.java, tirar dúvidas acerca de erros de código e da sintaxe,
> além de tirar dúvidas acerca da necessidade de de dois tokens diferentes para bool e para os tipos booleanos true e false.