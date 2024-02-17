package org.chiapingky.tictactoe.board;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "board")
public class Board {
    @Id
    private String boardId;
    private Integer gridSize;
    @Enumerated
    private Shape[] board;

    public Board() {
        gridSize = 3;
        board = new Shape[gridSize*gridSize];
    }

    public Board(Integer gridSize, Shape[] board) {
        this.gridSize = gridSize;
        this.board = board;
    }

    public Board(String boardId, Integer gridSize, Shape[] board) {
        this.boardId = boardId;
        this.gridSize = gridSize;
        this.board = board;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public Integer getGridSize() {
        return gridSize;
    }

    public void setGridSize(Integer gridSize) {
        this.gridSize = gridSize;
    }

    public Shape[] getBoard() {
        return board;
    }

    public void setBoard(Shape[] board) {
        this.board = board;
    }
}