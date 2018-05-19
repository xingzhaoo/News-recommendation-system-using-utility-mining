import java.io.IOException;
import java.util.ArrayList;

public class UnsafeArrayList {
	public static void main(String [] arg) throws IOException{
		ArrayList<Integer> a = new ArrayList<Integer>();
		System.out.println(a.size());
		String[] b = new String[Integer.MAX_VALUE - 2];
		for(int i = 0; i <= (Integer.MAX_VALUE - 2)/2; i++ ){
			a.add(i);
		}
		System.out.println(a.size());	
	}
	
	
}

