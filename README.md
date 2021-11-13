# damaRocks
## Jogo de damas para processo seletivo da Qulture.Rocks

Jogo de damas tradicional em tabuleiro 10x10 com 20 peças para cada jogador.

=======================
### Pré-requisitos
Necessário ter instalado em sua máquina Java Development Kit 8 ou superior e uma IDE que suporte o Java (recomendo Eclipse ou IntelliJ)
com gestão de dependências feita pelo Maven

### Rodando o Servidor
- Clone este repositório via Git (https://github.com/bzangi/damaRocks)
- Inicie a IDE e espere o Maven instalar as dependências
- Em DamaRocks-Back/src/main/java no package com.qulture.draughts acesse a Classe DraughtsAplication e rode a aplicação
- O servidor iniciará na porta <http://localhost:8080>
- Abra o arquivo index.html da pasta DamaRocks-Front para visualizar a página do jogo

### Jogando
- Abra 2 navegadores distintos
- Insira um nome na caixa e clique em 'Iniciar Jogo'
- Feito isso em um dos navegadores, o WebSocket irá aguardar a conexão de outro jogador para iniciar o jogo

### 🛠 Tecnologias
Jogo desenvolvido em:
- [Java] (https://www.oracle.com/java/technologies/downloads/)
- [JavaScript]

### Autor
---
Feito por Bruno Zangirolami
