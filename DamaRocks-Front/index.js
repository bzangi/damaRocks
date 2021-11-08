//Variável global que armazena a conexão
var ws;

function startGame() {
    //Recupera o nome digitado pelo player
    const ign = document.getElementById('ign').value;

    //Valida se já existe conexão
    // if (ws) {
    //     alert('Já conectado à partida')
    //     return false;
    // }

    //Inicia uma conexão com o WS
    ws = new WebSocket('ws://localhost:8080/game');

    ws.onmessage = function (data) {
        let responseObject = JSON.parse(data.data)
        console.log(JSON.parse(data.data))
        if (responseObject.code == '3') {
            document.getElementById('login-area').style.display = 'none';
            document.getElementById('game-area').style.display = 'flex';
            document.getElementById('list').style.position = 'relative';
            alert(responseObject.message)
        } else if (responseObject.code == '0') {
            alert(responseObject.message)
        }
    }

}