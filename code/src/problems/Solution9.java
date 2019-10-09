package problems;

/**
 * Created by yangyibo
 * Date: 2019/4/3
 * Time: 下午8:39
 */
public class Solution9 {

	int[][] direction=new int[][]{{0,1},{0,-1},{1,0},{-1,0}};
	int m,n;
	int result=0;
	public int movingCount(int threshold, int rows, int cols)
	{
		m=rows;
		n=cols;
		boolean[][] visited=new boolean[rows][cols];
//		for(int i=0;i<rows;i++)
//			for(int j=0;j<cols;j++)
//				if(!visited[i][j])
					DFS(visited,threshold,0,0);
		return result;
	}

	public void DFS(boolean[][] visited,int threshold, int x,int y){

		if(x<0 || y<0 || x==m || y==n)
			return;
		if(visited[x][y])
			return;
		visited[x][y]=true;
		if(!canMove(threshold,x,y))
			return;

		result++;
		System.out.println(x+","+y);

		for(int[] d:direction){
			DFS(visited,threshold,x+d[0],y+d[1]);
		}

	}


	boolean canMove(int threshold,int xx,int yy){

		int sum=0;
		int x=xx;
		int y=yy;

		while(x>0){
			sum+=(x%10);
			x=x/10;
		}
		while(y>0){
			sum+=(y%10);
			y=y/10;
		}

		if(sum>threshold)
			return false;
		else
			return true;
	}


	public static  void main(String[] args){
		new Solution9().movingCount(10,1,100);
	}
}
