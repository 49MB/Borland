package com.borland.dbswing;

public class SaveCellSelection {
  public int row;
  public int col;
  int rows[];
  int cols[];
  
  public SaveCellSelection(int row, int col, int rows[], int[] cols) {
    this.row = row;
    this.col = col;
    this.rows = rows;
    this.cols = cols;
  }
  
  public int getRow() {
    return row;
  }
  
  public int getCol() {
    return col;
  }
  
  public int[] getRows() {
    return rows;
  }
  
  public int[] getCols() {
    return cols;
  }
}