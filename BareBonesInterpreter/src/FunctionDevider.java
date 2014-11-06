import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;


public class FunctionDevider {
	public static void findFunctions(ArrayList<String> statements,HashMap<String,Function> functions) {
		// TODO Auto-generated method stub
		functions.clear();
		ArrayList<String> temporaryStatements=new ArrayList<String>(statements);
		
		while(!temporaryStatements.isEmpty()){
			String statement=temporaryStatements.remove(0);
			StringTokenizer tokenizer=new StringTokenizer(statement);
			if(tokenizer.nextToken().equals("def")){
				ArrayList<String> functionStatements=getInnerStatements(temporaryStatements);
				ArrayList<String> parameters=new ArrayList<String>();
				String functionName=tokenizer.nextToken();
				tokenizer.nextToken();
				String token=tokenizer.nextToken();
				while(!token.equals(")")){
					parameters.add(new String(token));
					token=tokenizer.nextToken();
				}
				Function func=new Function(functionStatements,parameters);
				functions.put(functionName, func);
			}
		}
	}
	
	public static ArrayList<String> getInnerStatements(ArrayList<String> statements) {
		int depth=1;
		ArrayList<String> innerStatements=new ArrayList<String>();
		while(depth!=0){
			String innerStatement=statements.remove(0);
			StringTokenizer innerTokenizer=new StringTokenizer(innerStatement);
			String innerToken=innerTokenizer.nextToken();
			if(innerToken.equals("while") || innerToken.equals("if")){
				depth++;
			} else if(innerToken.equals("end")){
				depth--;
			}
			if(depth!=0){
				innerStatements.add(innerStatement);
			}
		}
		return innerStatements;
	}
}
