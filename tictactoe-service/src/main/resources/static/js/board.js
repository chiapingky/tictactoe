let stompClient;
let statusMessage;
let socket;

function refreshBoard() {
    // Get the grid container element
    var gridContainer = document.querySelector('.grid-container');
    // Clear the existing grid items
    gridContainer.innerHTML = '';
    // Set grid size
    var board = game.board;
    var gridSize = board.gridSize;
    // Set the grid template rows and columns dynamically
    gridContainer.style.gridTemplateRows = `repeat(${gridSize}, 1fr)`;
    gridContainer.style.gridTemplateColumns = `repeat(${gridSize}, 1fr)`;
    // Generate the grid items
    for (let i = 0; i < gridSize * gridSize; i++) {
        const gridItem = document.createElement('div');
        gridItem.setAttribute("id",i);
        gridItem.classList.add('grid-item');
        gridItem.classList.add('cell');
        gridItem.onclick = function() {
            moveAction(this.id);
        }
        if(board.board[i] === "CROSS") {
            gridItem.classList.add('cross');
        } else if (board.board[i] === "ROUND") {
            gridItem.classList.add('round');
        }else {
            gridItem.classList.add('empty');
        }
        gridContainer.appendChild(gridItem);
    }
}

function initializeStatus() {
    var crossPlayer = game.crossPlayer;
    var roundPlayer = game.roundPlayer;  
    if (crossPlayer != null && crossPlayer !== "" & roundPlayer != null && roundPlayer !== "") {
        var opponentName = "";
        if (crossPlayer === playerName) {
            opponentName = roundPlayer;
            document.getElementById("opponent-role").textContent = "Round";
            document.getElementById("player-role").textContent = "Cross";
        }
        else {
            opponentName = crossPlayer;
            document.getElementById("opponent-role").textContent = "Cross";
            document.getElementById("player-role").textContent = "Round";
        }   
        document.getElementById("opponent-name").textContent = opponentName;
    }
    document.getElementById("player-name").textContent = playerName;
}

function updateStatus(text) {
    document.getElementById("status-text").innerHTML = text;
}

function openConnection() {
    socket = new SockJS("http://localhost:8080/play");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        stompClient.subscribe("/topic/game-progress/" + game.gameId, function(response) {
            let data = JSON.parse(response.body);
            statusMessage = data.statusMessage;
            if (data.game == null) {
                updateStatus(statusMessage);
            } else {
                game = data.game;
                refreshBoard();
                updateStatus(statusMessage);
                initializeStatus();
            }
        })
    })
    if (action === "join" || action === "failLeave") {
        moveAction(-1);
    }
}

function moveAction(id) {
    $.ajax({
        url: "http://localhost:8080/api/v1/game/play",
        type: 'POST',
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            "username": playerName,
            "move": id,
            "gameId": game.gameId
        }),
        success: function (data) {
            statusMessage = data.statusMessage;
            if (data.game == null) {
                updateStatus(statusMessage);
            } else {
                game = data.game;
                refreshBoard();
                updateStatus(statusMessage);
                initializeStatus()
            }
        }
    })
}

function leaveMatch() {
    if (playerName != null && playerName !== "") {
        $.ajax({
            url: "http://localhost:8080/api/v1/game/leave",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "game": game,
                "username": playerName
            }),
            success: function (data) {
                if (data != null) {
                    moveAction(-2);
                    stompClient.disconnect();
                    socket.close();
                    $("#leave-match").submit();
                }
            }
        })
    }
}

openConnection();
refreshBoard();
initializeStatus();
window.setInterval(function(){ // Set interval for keep-alive
    moveAction(-1);
}, 1000 * 90);