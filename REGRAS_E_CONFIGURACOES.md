# Manual de Regras e Configurações - Damas Turcas Java

Este documento descreve detalhadamente as **regras oficiais do jogo de Damas Turcas (*Turkish Draughts / Dama*)** adotadas no desenvolvimento do projeto, as **matrizes espaciais** disponíveis, o **sistema de notação algébrica** e as **configurações de jogo e inteligência artificial**.

---

## Sumário

- [1. Regras do Jogo Adotadas](#1-regras-do-jogo-adotadas)
  - [Conformidade com as Regras de Damas Turcas](#conformidade-com-as-regras-de-damas-turcas)
  - [Movimentação das Peças Comuns (Pedras)](#movimentação-das-peças-comuns-pedras)
  - [Captura pelas Peças Comuns](#captura-pelas-peças-comuns)
  - [Lei da Maioria (Captura Obrigatória do Maior Número)](#lei-da-maioria-captura-obrigatória-do-maior-número)
  - [Capturas Múltiplas e Remoção Imediata](#capturas-múltiplas-e-remoção-imediata)
  - [Promoção e Coroação a Dama Turca](#promoção-e-coroação-a-dama-turca)
  - [A Dama Turca Voadora](#a-dama-turca-voadora)
  - [Condições de Vitória e Empate](#condições-de-vitória-e-empate)
- [2. Matrizes Espaciais do Tabuleiro](#2-matrizes-espaciais-do-tabuleiro)
  - [Matriz 8x8 (Padrão Oficial de Damas Turcas - Recomendado)](#matriz-8x8-padrão-oficial-de-damas-turcas---recomendado)
  - [Matriz 10x10 (Padrão Ampliado)](#matriz-10x10-padrão-ampliado)
  - [Comparativo das Matrizes](#comparativo-das-matrizes)
- [3. Notação Algébrica e Formatos de Entrada](#3-notação-algébrica-e-formatos-de-entrada)
  - [Sistema de Coordenadas Estilo Xadrez](#sistema-de-coordenadas-estilo-xadrez)
  - [Formatos de Entrada Aceitos](#formatos-de-entrada-aceitos)
  - [Exemplos Práticos](#exemplos-práticos)
- [4. Configurações de Jogo e Motores de IA](#4-configurações-de-jogo-e-motores-de-ia)
  - [Seleção dos Motores de Inteligência Artificial](#seleção-dos-motores-de-inteligência-artificial)
  - [Níveis de Dificuldade](#níveis-de-dificuldade)
  - [Simbologia e Guia Visual](#simbologia-e-guia-visual)

---

## 1. Regras do Jogo Adotadas

### Conformidade com as Regras de Damas Turcas
O jogo segue rigorosamente a tradição milenar das **Damas Turcas (*Dama*)**:

1. Todas as casas do tabuleiro são **ativas e jogáveis** (não se joga apenas nas casas escuras).
2. As peças **Brancas** (`●` / `★`) sempre realizam o primeiro movimento da partida.
3. O jogador tem a liberdade de **escolher sua cor** (Brancas ou Pretas):
   - Ao escolher **Brancas**: o jogador joga primeiro.
   - Ao escolher **Pretas**: a IA joga primeiro com as Brancas.

---

### Movimentação das Peças Comuns (Pedras)
* **Deslocamento Simples**: Uma pedra comum pode se mover apenas **1 casa ortogonal** para a **Frente**, para a **Esquerda** ou para a **Direita**, desde que a casa de destino esteja livre.
* **Proibição de Diagonal e Recuo**: Pedras comuns **não se movem na diagonal** e **não podem recuar para trás** em movimentos simples.

---

### Captura pelas Peças Comuns
* **Direções de Captura**: A pedra comum captura saltando ortogonalmente para a **Frente**, para a **Esquerda** ou para a **Direita** sobre uma peça adversária adjacente.
* **Mecânica do Salto**: A pedra aterrissa imediatamente na casa livre seguinte após a peça adversária.
* **Proibição de Captura para Trás**: Pedras comuns não capturam para trás.

---

### Lei da Maioria (Captura Obrigatória do Maior Número)
* A captura é **estritamente obrigatória**. Se houver possibilidade de captura, nenhum movimento simples é permitido.
* **Regra da Maioria**: Caso existam múltiplos caminhos de captura disponíveis para o jogador ou para a IA, é **obrigatório escolher a linha que capture a maior quantidade de peças adversárias**.
* O motor de regras valida automaticamente todas as possibilidades e impede lances ilegais, alertando o jogador.

---

### Capturas Múltiplas e Remoção Imediata
* **Remoção Imediata**: Nas Damas Turcas, a peça adversária capturada é **retirada imediatamente do tabuleiro** no instante em que é saltada. Isso pode abrir novas linhas de passagem durante a mesma jogada.
* **Mudança de Direção**: Durante uma sequência de saltos múltiplos, a peça pode mudar de direção ortogonal a cada salto (por exemplo: salto à frente, seguido de salto à direita).

---

### Promoção e Coroação a Dama Turca
* Quando uma pedra comum atinge a última fileira do tabuleiro:
  * **Brancas**: atingem a fileira 8 (topo do tabuleiro no 8x8 ou fileira 10 no 10x10).
  * **Pretas**: atingem a fileira 1 (base do tabuleiro).
* A peça é imediatamente promovida a **Dama Turca** (`★` para Brancas, `☆` para Pretas), ganhando poderes ampliados de movimentação.

---

### A Dama Turca Voadora
* **Movimentação Livre (Estilo Torre)**: Pode se deslocar por **qualquer número de casas vazias** ao longo das 4 direções ortogonais (Frente, Trás, Esquerda e Direita).
* **Captura à Distância**: Pode capturar uma peça adversária localizada a qualquer distância em linha reta ortogonal, desde que não haja obstáculos intermediários.
* **Escolha do Pouso**: Após saltar a peça adversária, a Dama Turca pode pousar em **qualquer casa livre** imediatamente atrás ou várias casas adiante ao longo da mesma linha ortogonal.
* **Saltos em 90 Graus**: Se a partir da casa de pouso houver outra peça adversária capturável em qualquer direção ortogonal, a Dama deve prosseguir capturando.

---

### Condições de Vitória e Empate

#### Vitória:
Um jogador vence a partida quando:
1. **Eliminação Total**: Capturar todas as peças do adversário.
2. **Bloqueio Total**: O adversário não possuir nenhum movimento legal no seu turno.

#### Empate:
Uma partida termina empatada quando:
1. Atingir **46 rodadas completas** (92 meios-lances) consecutivos sem nenhuma captura de peça ou avanço de pedras comuns.

---

## 2. Matrizes Espaciais do Tabuleiro

O jogo oferece suporte a duas matrizes espaciais configuráveis:

### Matriz 8x8 (Padrão Oficial de Damas Turcas - Recomendado)
* **Estrutura**: Grade com 8 colunas (**A** a **H**) e 8 linhas (**1** a **8**).
* **Casas**: 64 casas totais (todas jogáveis).
* **Peças Iniciais**: 16 peças para cada jogador (2 fileiras completas).
  * Brancas: ocupam as linhas 2 e 3.
  * Pretas: ocupam as linhas 6 e 7.
  * Linhas livres iniciais: 1, 4, 5 e 8.
* **Experiência**: A experiência autêntica das Damas Turcas com dinamismo tático direto.

---

### Matriz 10x10 (Padrão Ampliado)
* **Estrutura**: Grade com 10 colunas (**A** a **J**) e 10 linhas (**1** a **10**).
* **Casas**: 100 casas totais (todas jogáveis).
* **Peças Iniciais**: 20 peças para cada jogador (2 fileiras completas).
  * Brancas: ocupam as linhas 2 e 3.
  * Pretas: ocupam as linhas 8 e 9.
  * Linhas livres iniciais: 1, 4, 5, 6, 7 e 10.
* **Experiência**: Maior profundidade de manobras estratégicas e espaço estendido para atuação de Damas Turcas.

---

### Comparativo das Matrizes

| Característica | Matriz 8x8 (Padrão Oficial) | Matriz 10x10 (Ampliada) |
|---|:---:|:---:|
| **Dimensão Total** | 64 casas | 100 casas |
| **Casas Jogáveis** | 64 (todas) | 100 (todas) |
| **Peças por Jogador** | 16 | 20 |
| **Fileiras Iniciais de Peças** | 2 fileiras | 2 fileiras |
| **Colunas** | A, B, C, D, E, F, G, H | A, B, C, D, E, F, G, H, I, J |
| **Linhas** | 1 a 8 | 1 a 10 |
| **Duração Média de Partida** | 20 a 40 lances | 35 a 65 lances |

---

## 3. Notação Algébrica e Formatos de Entrada

O jogo utiliza o sistema de notação algébrica para mapeamento das casas (coluna de A a H/J e linha numérica de 1 a 8/10):

```
       A   B   C   D   E   F   G   H
    8 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]  8
    7 [○] [○] [○] [○] [○] [○] [○] [○]  7
    6 [○] [○] [○] [○] [○] [○] [○] [○]  6
    5 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]  5
    4 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]  4
    3 [●] [●] [●] [●] [●] [●] [●] [●]  3
    2 [●] [●] [●] [●] [●] [●] [●] [●]  2
    1 [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]  1
       A   B   C   D   E   F   G   H
```

---

### Formatos de Entrada Aceitos

O parser inteligente ([`NotationParser.java`](file:///src/main/java/br/com/damas/turcas/game/NotationParser.java)) e o controlador ([`Main.java`](file:///src/main/java/br/com/damas/turcas/Main.java)) aceitam os seguintes comandos:

1. **Por Extenso em Português / Inglês**:
   * `E3 para E4` ou `e3 para e4`
   * `E3 to E4` ou `e3 to e4`
2. **Direto com Espaço ou Hífen**:
   * `C3 D3` ou `c3 d3`
   * `C3-C4` ou `C3->C4`
3. **Notação de Captura Simples**:
   * `E3 x E5` ou `E3:E5`
   * `E3 para E5` ou `E3 E5`
4. **Notação de Capturas Múltiplas**:
   * `A3:A5:C5` ou `A3 para A5 para C5`
   * `A3 C5` (o motor calcula o trajeto completo da captura)
5. **Guia de Ajuda e Comandos**:
   * `?`, `<?>` ou `ajuda` (exibe todos os formatos aceitos e lista todos os lances legais imediatos)
6. **Limpeza e Redesenho do Tabuleiro**:
   * `cls` ou `limpar` (limpa a tela do terminal e redesenha o tabuleiro e HUD atualizados)
7. **Encerramento da Partida**:
   * `sair`, `exit` ou `q`

---

### Exemplos Práticos

| Situação | Digitação no Terminal | Ação Executada |
|---|---|---|
| Movimento Frontal | `C3 para C4` ou `C3 C4` | Avança a pedra de C3 para C4 |
| Movimento Lateral | `E3 para D3` ou `E3 D3` | Move a pedra lateralmente de E3 para D3 |
| Captura Frontal | `E3 para E5` ou `E3:E5` | Salta sobre a peça em E4 e pousa em E5 |
| Captura em L (Múltipla) | `A3:A5:C5` ou `A3 C5` | Salta sobre A4 (pousa em A5) e salta sobre B5 (pousa em C5) |
| Lance com Dama Turca | `A1 para A8` ou `A1 A8` | Voa pela coluna A de A1 até A8 |
| Consultar Ajuda e Lances Legais | `?` ou `<?>` | Imprime todos os formatos aceitos e lista os lances possíveis |
| Limpar e Redesenhar Tela | `cls` ou `limpar` | Limpa o terminal e reimprime o estado atual do jogo |
| Encerrar Partida | `sair`, `exit` ou `q` | Encerra o jogo e retorna ao terminal |

---

## 4. Configurações de Jogo e Motores de IA

### Seleção da Cor das Peças

```
┌────────────────────────────────────────────────────────┐
│                    ESCOLHA SUA COR                     │
├───────┬──────────────┬─────────────────────────────────┤
│ OPÇÃO │ COR          │ CARACTERÍSTICA                  │
├───────┼──────────────┼─────────────────────────────────┤
│ 1     │ Brancas (●)  │ Você joga primeiro [Padrão]     │
│ 2     │ Pretas (○)   │ A IA joga primeiro (com Brancas)│
└───────┴──────────────┴─────────────────────────────────┘
```

---

### Seleção dos Motores de Inteligência Artificial

```
┌────────────────────────────────────────────────────────────────────────┐
│                      MOTOR DE INTELIGÊNCIA ARTIFICIAL                  │
├───────┬───────────────────────────────────┬────────────────────────────┤
│ OPÇÃO │ MOTOR DE IA                       │ CARACTERÍSTICA             │
├───────┼───────────────────────────────────┼────────────────────────────┤
│ 1     │ Modo Híbrido Mestre [Recomendado] │ Combina A*, MDP e HC       │
│ 2     │ Processo de Decisão de Markov     │ Value Iteration & Softmax  │
│ 3     │ Busca A* (A-Star)                 │ Táticas com PriorityQueue  │
│ 4     │ Hill Climbing com Reinicialização │ Otimização & Restarts      │
└───────┴───────────────────────────────────┴────────────────────────────┘
```

1. **Modo Híbrido Mestre**: Ativa busca A* para variantes táticas e capturas forçadas, MDP para posicionamento estratégico e Hill Climbing para consenso.
2. **Processo de Decisão de Markov (MDP)**: Equação de Bellman com fator de desconto $\gamma = 0.90$ e modelo estocástico de probabilidade Softmax ($\tau = 50.0$).
3. **Busca A\***: Fila de prioridade Min-Heap com heurística admissível para encontrar planos ótimos de ganho material.
4. **Hill Climbing com Reinicialização Aleatória**: Otimização heurística local com múltiplos restarts para mitigar máximos locais.

---

### Níveis de Dificuldade

```
┌────────────────────────────────────────────────────────────────────────┐
│                          NÍVEL DE DIFICULDADE                          │
├───────┬──────────┬─────────────────────────────────────────────────────┤
│ OPÇÃO │ NÍVEL    │ PARÂMETROS DE COMPUTAÇÃO                            │
├───────┼──────────┼─────────────────────────────────────────────────────┤
│ 1     │ Fácil    │ Profundidade = 2 | Nós A* = 250 | Restarts HC = 10  │
│ 2     │ Médio    │ Profundidade = 3 | Nós A* = 600 | Restarts HC = 20  │
│ 3     │ Difícil  │ Profundidade = 4 | Nós A* = 1200 | Restarts HC = 40 │
└───────┴──────────┴─────────────────────────────────────────────────────┘
```

---

### Simbologia e Guia Visual

| Elemento | Símbolo | Cor no Terminal | Descrição |
|---|:---:|---|---|
| **Pedra Branca** | `●` | Ciano Brilhante | Peça comum do jogador |
| **Dama Turca Branca** | `★` | Amarelo Brilhante | Dama coroada do jogador |
| **Pedra Preta** | `○` | Vermelho Brilhante | Peça comum da IA |
| **Dama Turca Preta** | `☆` | Magenta Brilhante | Dama coroada da IA |
| **Casa Clara** | `   ` | Fundo Cinza Claro | Casa do tabuleiro |
| **Casa Escura** | `   ` | Fundo Cinza Escuro | Casa do tabuleiro |
| **Último Lance** | `   ` | Fundo Dourado/Oliva | Destaca a origem e destino da jogada anterior |
