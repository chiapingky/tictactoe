$(document).ready(function() {

    $("#difficulty").change(function() {
        var normalDescription = "Your normal tic tac toe game. Make a continuous line of three cells to win the game";
        var hardDescription = "Same as normal tic tac toe game, but you have to make a continuous line from edge to edge. Challenging for grid larger than 3";

        var difficultySelection = document.getElementById("difficulty");
        var selected = difficultySelection.options[difficultySelection.selectedIndex].value;
        var span = document.getElementById("difficulty-description");
        if (selected == "normal") {
            span.textContent = normalDescription;
        } else {
            span.textContent = hardDescription;
        }
    })

    $("#start-button").click(function() {
        $("#start-button").disabled = true;
        var gridInput = document.getElementById("modifier");
        var gridSize = gridInput.value;
        if (gridSize == null || gridSize == "" || gridSize < 3 || gridSize > 10) {
            alert("Grid size must be selected between 3 - 10");
            $("#start-button").disabled = false;
            return;
        }
        var difficultySelection = document.getElementById("difficulty");
        var difficulty = difficultySelection.options[difficultySelection.selectedIndex].value;
        if (difficulty == null || difficulty == "" || difficulty == difficultySelection.options[0].value) {
            alert("Select difficulty");
            $("#start-button").disabled = false;
            return;
        }
        $.ajax({
            url: "",
            dataType: "json",
            method: "POST",
            data: {
                "gridSize": gridSize,
                "difficulty": difficulty 
            },
            success: function(result) {
                window.location.href = "/game/?id="+result.id+"&name="+name;
            }
        })
    })
})