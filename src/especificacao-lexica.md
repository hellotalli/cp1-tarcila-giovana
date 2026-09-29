### automato finito determinístico - identificador e palavra reservada
necessário plugin mermaid do intellij
```mermaid
stateDiagram-v2
    [*] --> q0
    q0 --> q_id : letra / '_'
    q_id --> q_id : letra / dígito / '_'
    q_id --> TabelaBusca : fim_do_lexema
    TabelaBusca --> PalavraReservada : se estiver no mapa
    TabelaBusca --> Identificador : caso contrário
```

### Autômato Finito Determinístico - String

```mermaid
stateDiagram-v2
    [*] --> q0
    q0 --> q_str : '"'
    q_str --> q_str : caractere ≠ '"' e ≠ '\n'
    q_str --> q_fim : '"'
    q_str --> q_erro : '\n' ou EOF
```

#### AFD de Identificador: 
    Estado q0 -> estado inicial no switch (c) em scanToken().
    Estado q_id -> laço while no método identificador().
    TabelaBusca -> consulta ao palavrasReservadas.get(texto).
#### AFD de String:
    Estado q0 -> case '"' no switch (c).
    Estado q_str -> laço while no método string().
    Estado q_erro -> chamada do método erroEm(...) ao detetar \n ou EOF.