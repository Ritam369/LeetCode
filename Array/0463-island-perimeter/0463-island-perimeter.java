class Solution {
    public int islandPerimeter(int[][] grid) {
        int peri = 0;
        int row=grid.length-1, col=grid[0].length-1;

        for(int i=0; i<=row; i++){
            for(int j=0; j<=col; j++){
                if(grid[i][j]==1){ // if land then only
                    if(i==0 || grid[i-1][j]==0) peri++; //top
                    if(i==row || grid[i+1][j]==0) peri++; //bottom
                    if(j==0 || grid[i][j-1]==0) peri++; //left
                    if(j==col || grid[i][j+1]==0) peri++; //right
                }
            }
        }
        return peri;
    }
}