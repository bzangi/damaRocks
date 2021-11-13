//Variável global que armazena a conexão
var ws;
var gameStarted = false;

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
    ws.clickedPieceState = '';

    ws.onmessage = function (data) {
        let responseObject = JSON.parse(data.data)
        console.log(JSON.parse(data.data))
        //Partida em andamento {code, message}
        if (responseObject.code == '0') {
            alert(responseObject.message);

            //Oponente desconectado
        } else if (responseObject.code == '1') {
            alert(responseObject.message);
            this.close;
            location.reload();

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


            //limpando tabuleiro
            if (pieces.length > 0) {
                pieces.forEach(piece => {
                    piece.parentElement.removeChild(piece);
                })
            }


            //reposicionando peças de acordo com novo tabuleiro enviado pelo back end
            responseObject.table.spots.forEach(spot => {
                let piece = document.createElement('div');
                if (spot.state == 'pj1') {
                    piece.className = 'piece-j1';
                    document.getElementById(spot.location).append(piece);
                } else if (spot.state == 'qpj1') {
                    piece.className = 'piece-qpj1';
                    document.getElementById(spot.location).append(piece);
                } else if (spot.state == 'pj2') {
                    piece.className = 'piece-j2';
                    document.getElementById(spot.location).append(piece);
                } else if (spot.state == 'qpj2') {
                    piece.className = 'piece-qpj2';
                    document.getElementById(spot.location).append(piece);
                } else {
                    // sem peças (pj0)
                }

            });

            //Separa os elementos clicáveis para cada jogador
            var spotBlackElements = [...document.getElementsByClassName('board-spot-black')];

            //removendo eventListeners antigos
            if (gameStarted) {
                spotBlackElements.forEach(spot => {
                    spot.removeEventListener('click', this.moveFunction);
                })
            }

            let emptyBlackSpotsElements = spotBlackElements.filter(element => {
                return !element.hasChildNodes();
            })

            let playerPieceElements = [...document.getElementsByClassName('piece-' + localStorage.getItem('player'))];
            let playerQueenElements = [...document.getElementsByClassName('piece-qp' + localStorage.getItem('player'))];

            let allClickableElements = emptyBlackSpotsElements.concat(playerPieceElements, playerQueenElements);


            //Movendo a peça e restringindo o jogador que pode movê-la
            this.moveFunction = function (e) {
                e.bubbles = false;
                e.cancelBubble = true;
                e.stopPropagation();

                //Evitando a seleção do tabuleiro
                if (e.target.id == 'board') {
                    return false
                }


                if (responseObject.turn.player == localStorage.getItem('player')) { //AQUI PODE
                    //Removendo classe do elemento selecionado
                    let elements = [...document.getElementsByClassName('selected-spot')]
                    elements.forEach(element => {
                        element.classList.remove('selected-spot')
                    });

                    //Selecionando o elemento clicado
                    e.target.classList.add('selected-spot')

                    console.log(localStorage.getItem('player'))
                    console.log(e.target.className)


                    if (e.target.className.includes('piece-' + localStorage.getItem('player')) || e.target.className.includes('piece-qp' + localStorage.getItem('player'))) {
                        ws.moveStarted = true;
                        ws.clickedPiece = e.target.parentElement.id; //id do spot NAO ESTA PEGANDO O PIECE SIMPLES
                        ws.clickedPieceState = e.target.className.includes('qpj1') ? ws.clickedPieceState = 'qpj1' :
                            e.target.className.includes('qpj2') ? ws.clickedPieceState = 'qpj2' :
                                e.target.className.includes('j1') ? ws.clickedPieceState = 'pj1' :
                                    e.target.className.includes('j2') ? ws.clickedPieceState = 'pj2' : ws.clickedPiece = '';
                    }

                    if (ws.moveStarted && e.target.className.includes('board-spot-black')) {
                        ws.moveStarted = false;
                        ws.send('MOVE@' + ws.clickedPiece + '/' + ws.clickedPieceState + '@' + e.target.id + '/pj0');
                        ws.clickedPiece = '';
                        ws.clickedPieceState = '';
                        e.target.classList.remove('selected-spot');
                    }
                }
            }

            allClickableElements.forEach(element => {
                element.addEventListener('click', this.moveFunction);
            });

            gameStarted = true;

        } else if (responseObject.code == '7') {
            alert(responseObject.message);
        }
    }

}