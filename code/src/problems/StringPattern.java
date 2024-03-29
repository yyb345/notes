package problems;

/**
 * Created by yangyibo
 * Date: 2019/4/5
 * Time: 下午2:29
 */
public class StringPattern {

	public String replaceSpace(StringBuffer str) {


		int p1=str.length()-1;
		for(int i=0;i<str.length();i++){
			if(str.charAt(i)==' ')
				str.append("  ");
		}

		int p2=str.length()-1;

		while(p1>=0  && p2>p1){
			char c=str.charAt(p1--);
			if(c==' '){
				str.setCharAt(p2--,'0');
				str.setCharAt(p2--,'2');
				str.setCharAt(p2--,'%');
			}else{
				str.setCharAt(p2--,c);
			}

		}

		return str.toString();
	}

	public static void main(String[] args){

		StringBuffer buffer=new StringBuffer("a b c");
		new StringPattern().replaceSpace(buffer);

//		String patters="^[a-z]?\\d{3,}$";
//		String content="q234222";
//		boolean matches = Pattern.matches(patters, content);
//		System.out.println(matches);
	}
}
