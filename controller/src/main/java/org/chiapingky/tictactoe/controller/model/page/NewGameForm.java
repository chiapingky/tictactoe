package org.chiapingky.tictactoe.controller.model.page;

public class NewGameForm {
    private String difficulty;
    private Integer gridSize;

    public NewGameForm() {
    }

    public NewGameForm(String difficulty, Integer gridSize) {
        this.difficulty = difficulty;
        this.gridSize = gridSize;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getGridSize() {
        return gridSize;
    }

    public void setGridSize(Integer gridSize) {
        this.gridSize = gridSize;
    }
}
