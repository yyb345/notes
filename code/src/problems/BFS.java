package problems;

import java.util.*;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangyibo
 * Date: 2018/12/9
 * Time: 下午11:16
 */
public class BFS {

	class Employee {
		// It's the unique id of each node;
		// unique id of this employee
		public int id;
		// the importance value of this employee
		public int importance;
		// the id of direct subordinates
		public List<Integer> subordinates;
	};

	public int getImportance(List<Employee> employees, int id) {

		List<Employee> employeesCopy=new ArrayList<>(2001);
		for(int i=0;i<employees.size();i++){
			employeesCopy.set(i,null);
		}

		for(Employee e:employees){
			employeesCopy.set(e.id,e);
		}

		int sumValue=0;
		Queue<Integer> allSubordinates=new LinkedBlockingQueue<>();

		int currentId;
		allSubordinates.add(id);

		while(allSubordinates.size()>0 ){
			currentId=allSubordinates.poll();
			List<Integer> subordinates = employees.get(currentId).subordinates;
			for(int s:subordinates){
				allSubordinates.add(s);
			}
			sumValue+=employees.get(currentId).importance;


		}





		return sumValue;
	}



	//problem 496


	public static int[] nextGreaterElement(int[] nums1, int[] nums2) {

		int []result=new int[nums1.length];

		Stack<Integer> stack =new Stack<>();
		Map<Integer,Integer> map=new HashMap<>();
		if(nums2.length>1)
			stack.push(nums2[0]);

		for(int i=1;i<nums2.length;i++){
			while(stack.size()>0){
				int popValue=stack.peek();
				if(popValue<nums2[i]){
					stack.pop();
					map.put(popValue,nums2[i]);
				}else{
					break;
				}
			}
			stack.push(nums2[i]);
		}

		while(stack.size()>0){
			map.put(stack.pop(),-1);
		}

		for(int j=0;j<nums1.length;j++){
			result[j]=map.get(nums1[j]);
		}

		return result;
	}


	//
	public static int countSubstrings(String s) {


		int result=0;
		boolean [][]value=new boolean[s.length()][s.length()];


		for(int i=0;i<s.length();i++)
			for(int j=0;j<s.length();j++)
				if(i>j)
					value[i][j]=true;

		for(int i=s.length()-1;i>=0;i--){
			for(int j=i;j<s.length();j++){
				if(i!=j) {
					if (value[i + 1][j - 1] && s.charAt(i) == s.charAt(j)) {
						value[i][j] = true;
					} else {
						value[i][j] = false;
					}
				}else{
					value[i][j]=true;
				}

				if(value[i][j])
					result++;

			}
		}

		return result;

	}

	public static int numberOfArithmeticSlices(int[] A) {

		int result=0;
		boolean [][]value=new boolean[A.length][A.length];
		for(int i=A.length-3;i>=0;i--){
			for(int j=i+1;j<A.length;j++){
				if(value[i][j]){

					if((j+1)<A.length && (A[j+1]-A[j])==(A[j]-A[j-1])){
						value[i][j+1]=true;
						//result++;
					}
					if((i-1)>=0 && (A[i]-A[i-1])==(A[i+1]-A[i])){
						value[i-1][j]=true;

					}
				}else{
					if((j+1)<A.length && (A[j+1]-A[j])==(A[j]-A[j-1])){
						value[j-1][j+1]=true;
						//result++;
					}
					if((i-1)>=0 && (A[i]-A[i-1])==(A[i+1]-A[i])){
						value[i-1][i+1]=true;

					}

				}

			}



		}
		for(int i=0;i<A.length;i++){
			for(int j=0;j<A.length;j++){
				if(value[i][j])
					result++;
			}
		}
		return result;
	}


//	//
//	public static int integerBreak(int n) {
//		int []value=new int[n+1];
//		value[1]=1;
//		for(int i=2;i<=n;i++){
//			int max=0;
//			for(int m=i-1;m>=(i/2);m--){
//				int sub=Math.max((value[m]*value[i-m]),m*(i-m));
//				if(sub>max)
//					max=sub;
//			}
//			value[i]=max;
//		}
//
//		return value[n];
//	}

	public static int maxSubArray(int[] nums) {


		int []value=new int[nums.length];
		int max;
		value[nums.length-1]=nums[nums.length-1];
		if(nums.length>1){
			for(int i=nums.length-1;i>=0;i--){
				if((i-1)>=0){
					if(value[i]>0){
						value[i-1]=Math.max((value[i]+nums[i-1]),nums[i-1]);
					}

					else{
						value[i-1]=Math.max(0,nums[i-1]);
					}
				}
			}

		}
		max= Arrays.stream(value).max().getAsInt();



		return max;
	}



	//
	public static int minSteps(int n) {

		int []value=new int[n+1];
		value[1]=1;

		for(int i=2;i<=n;i++){
			int min=Integer.MAX_VALUE;
			for(int past=1;past<i;past++){
				if(i%past==0 && !(value[i]>0)){
					int step=value[past]+(i-past)/past+1;
					if(step<min){
						min=step;
					}
				}
			}
			value[i]=min;
		}

		return value[n];
	}


	public static int minPathSum(int[][] grid) {


		int [][]value=new int[grid.length][grid[0].length];
		//value[0][0]=grid[0][0];

		int row=0,column=0;
		while(row<grid.length && column<grid[0].length){



			for(int i=row;i<grid.length;i++){
				int j=column;
				if(j>=1 && i>=1){
					value[i][j]=Math.min(value[i-1][j],value[i][j-1])+grid[i][j];
				}else if(j>=1 && i<1){
					value[i][j]=value[i][j-1]+grid[i][j];
				}else if(j<1 && i<1 ){
					value[i][j]=grid[i][j];
				}else if(j<1 && i>=1){
					value[i][j]=value[i-1][j]+grid[i][j];

				}
			}


			for(int j=column;j<grid[0].length;j++){
				int i=row;
				if(j>=1 && i>=1){
					value[i][j]=Math.min(value[i-1][j],value[i][j-1])+grid[i][j];
				}else if(j>=1 && i<1){
					value[i][j]=value[i][j-1]+grid[i][j];
				}else if(j<1 && i<1 ){
					value[i][j]=grid[i][j];
				}else if(j<1 && i>=1){
					value[i][j]=value[i-1][j]+grid[i][j];

				}
			}



			if(row<grid.length)
				row++;
			if(column<grid.length)
				column++;
		}

		return value[grid.length-1][grid[0].length-1];
	}


	public static int findTargetSumWays(int[] nums, int S) {

		int sum1=0;
		for(int i=0;i<nums.length;i++){
			sum1+=nums[i];
		}
		int sum=sum1*2;
		int [][]value=new int[nums.length][sum+1];

		if(nums[0]==0) value[0][0]=2;
		for(int j=1;j<sum+1;j++){
			if(nums[0]==j){
				value[0][j]=1;
			}else{
				value[0][j]=0;
			}
		}

		for(int i=1;i<nums.length;i++){
			for(int j=0;j<sum+1;j++){
				if((j+nums[i])<sum+1){
					value[i][j]=value[i-1][j+nums[i]];
				}

			}
		}

		return value[nums.length-1][S+sum1];
	}




		public static void main(String []args){


//		int a[]=new int[]{4,1,2};

			int []data=new int[]{0,0,0,0,0,1};


			System.out.println(findTargetSumWays(data,1));

	}


}
