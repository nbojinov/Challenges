import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;


public class Interpreter {
	
	
	ArrayList<String> statements;
	HashMap<String, Integer> variables;

	public Interpreter(HashMap<String,Integer> variables,ArrayList<String> statements){
		this.variables=variables;
		this.statements=statements;
	}

	public void interpretLine(){
		// implement error checking
		String statement=statements.remove(0);
		StringTokenizer tokenizer=new StringTokenizer(statement);
		String token=tokenizer.nextToken();
		if(token.equals("clear")){
			variables.put(tokenizer.nextToken(),0);
		} else if (token.equals("incr")){
			String variable=tokenizer.nextToken();
			if(variables.containsKey(variable))
				variables.put(variable,variables.get(variable)+1);
			else
				variables.put(variable, 1);
		} else if (token.equals("decr")){
			String variable=tokenizer.nextToken();
			if(variables.get(variable)==0){
				variables.put(variable, 0);
			} else {
				variables.put(variable,variables.get(variable)-1);
			}
		} else if (token.equals("copy")){
			String sourceVariable=tokenizer.nextToken();
			tokenizer.nextToken();
			String destinationVariable=tokenizer.nextToken();
			variables.put(destinationVariable, variables.get(sourceVariable));
		} else if (token.equals("while")){
			String checkVariable=tokenizer.nextToken();
			int depth=1;
			ArrayList<String> innerStatements=new ArrayList<String>();
			while(depth!=0){
				String innerStatement=statements.remove(0);
				StringTokenizer innerTokenizer=new StringTokenizer(innerStatement);
				String innerToken=innerTokenizer.nextToken();
				if(innerToken.equals("while")){
					depth++;
				} else if(innerToken.equals("end")){
					depth--;
				}
				if(depth!=0){
					innerStatements.add(innerStatement);
				}
			}
			while(variables.get(checkVariable)!=0){
				Interpreter innerInterpreter=new Interpreter(variables,new ArrayList<String>(innerStatements));
				while(!innerInterpreter.hasFinished()){
					innerInterpreter.interpretLine();
				}
			}
		}
	}
	
	public boolean hasFinished(){
		return statements.size()==0;
	}
}
