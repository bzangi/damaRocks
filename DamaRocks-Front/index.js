//Variável global que armazena a conexão
var ws;

function startGame() {
    //Recupera o nome digitado pelo player
    const ign = document.getElementById('ign').value;

    if (ign == '') {
        alert('Insira um nome.')
        return false;
    }

    //Valida se já existe conexão
    if (ws) {
        alert('Já conectado à partida')
        return false;
    }

    //Inicia uma conexão com o WS
    ws = new WebSocket('ws://localhost:8080/game');

    ws.moveStarted = false;
    ws.clickedPiece = '';

    ws.onmessage = function (data) {
        let responseObject = JSON.parse(data.data)
        console.log(JSON.parse(data.data))
        //Partida em andamento {code, message}
        if (responseObject.code == '0') {
            alert(responseObject.message)

            //Aguardando oponente {code, message}
        } else if (responseObject.code == '2') {
            alert(responseObject.message)
            ws.send(ign);

            //Partida iniciando {code, message}
        } else if (responseObject.code == '3') {
            ws.send(ign);
            document.getElementById('login-area').style.display = 'none';
            document.getElementById('game-area').style.display = 'flex';
            document.getElementById('list').style.position = 'relative';
            alert(responseObject.message)

            //Define a ordem dos jogadores {code, message}
        } else if (responseObject.code == '4') {
            localStorage.setItem('player', responseObject.message);
            if (responseObject.message == 'j1') {
                alert('Você é o player 1')
            } else {
                alert('Você é o player 2')
            }

            //Recebe o nome dos jogadores na ordem {j1, j2, code}
        } else if (responseObject.code == '5') {
            document.getElementById('j1').innerText = 'Player 1: ' + responseObject.j1;
            document.getElementById('j2').innerText = 'Player 2: ' + responseObject.j2;

            //Recebe a tabela e o turno serializados {table, turn, code}
        } else if (responseObject.code == '6') {

            let pieces = [...document.querySelectorAll('div[class^="piece"]')];

            if (pieces.length > 0) {
                pieces.forEach(piece => {
                    piece.parentElement.removeChild(piece);
                })
            }

            responseObject.table.spots.forEach(spot => {
                let piece = document.createElement('div');
                if (spot.state == 'pj1') {
                    piece.className = 'piece-j1';
                    document.getElementById(spot.location).append(piece);
                } else if (spot.state == 'pj2') {
                    piece.className = 'piece-j2';
                    document.getElementById(spot.location).append(piece);
                } else {
                    // sem peças (pj0)
                }

            });

            //Separa os elementos clicáveis para cada jogador
            let spotBlackElements = [...document.getElementsByClassName('board-spot-black')];
            let emptyBlackSpotsElements = spotBlackElements.filter(element => {
                return !element.hasChildNodes();
            })

            let playerPieceElements = [...document.getElementsByClassName('piece-' + localStorage.getItem('player'))];

            let allClickableElements = emptyBlackSpotsElements.concat(playerPieceElements);

            allClickableElements.forEach(element => {
                element.addEventListener('click', (e) => {
                    e.bubbles = false;
                    e.cancelBubble = true;
                    e.stopPropagation();

                    //Evitando a seleção do tabuleiro
                    if (e.target.id == 'board') {
                        return false
                    }

                    if (responseObject.turn.player == localStorage.getItem('player')) {
                        //Removendo classe do elemento selecionado
                        let elements = [...document.getElementsByClassName('selected-spot')]
                        elements.forEach(element => {
                            element.classList.remove('selected-spot')
                        });

                        //Selecionando o elemento clicado
                        e.target.classList.add('selected-spot')
                    }

                    if (e.target.className.includes('piece-' + localStorage.getItem('player'))) {
                        this.moveStarted = true;
                        this.clickedPiece = e.target.parentElement.id;
                    }

                    if (this.moveStarted && e.target.className.includes('board-spot-black')) {
                        this.moveStarted = false;
                        this.send('MOVE@' + this.clickedPiece + '/' + 'p' + localStorage.getItem('player') + '@' + e.target.id + '/pj0');
                        this.clickedPiece = '';
                        e.target.classList.remove('selected-spot')
                    }
                })
            });
        }
    }

}