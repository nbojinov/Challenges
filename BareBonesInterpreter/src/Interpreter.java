import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;


public class Interpreter {
	
	
	HashMap<String,Function> functions;
	
	public Interpreter(HashMap<String,Function> functions){
		this.functions=functions;
	}
	
	public HashMap<String,Integer> interpret() throws RunTimeException{
		if(!functions.containsKey("main"))
			throw new RunTimeException("Definition of main not found!");
		else if(functions.get("main").getParameters().size()!=0){
			throw new RunTimeException("main shouldn't have parameters");
		}
		
		HashMap<String,Integer> variables=new HashMap<String,Integer>();
		
		interpretFunction(functions.get("main"),variables);
		
		return variables;
	}

	private void interpretFunction(Function function,HashMap<String,Integer> variables) throws RunTimeException {
		
		ArrayList<String> statements=new ArrayList<String>(function.getStatements());
		while(!statements.isEmpty()){
			interpretLine(statements,variables);
		}
		
	}

	private void interpretLine(ArrayList<String> statements,HashMap<String,Integer> variables) throws RunTimeException{
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
			variables.put(variable,variables.get(variable)-1);
			
		}else if (token.equals("add") || token.equals("sub") || token.equals("mul") || token.equals("div") || token.equals("mod")){
			
			String resultVariable=tokenizer.nextToken();
			String firstVariable=tokenizer.nextToken();
			Integer firstVar=getVariableOrNumber(firstVariable,variables);
			Integer secondVar=null;
			if(tokenizer.hasMoreTokens()){
				secondVar=getVariableOrNumber(tokenizer.nextToken(),variables);
			} else {
				secondVar=firstVar;
				firstVar=variables.get(resultVariable);
			}
			if(token.equals("add")){
				variables.put(resultVariable, firstVar+secondVar);
			} else if(token.equals("sub")){
				variables.put(resultVariable, firstVar-secondVar);
			} else if(token.equals("mul")){
				variables.put(resultVariable, firstVar*secondVar);
			} else if(token.equals("div")){
				if(secondVar==0)
					throw new RunTimeException("Devision by 0 at line:\n"+statement);
				variables.put(resultVariable, firstVar/secondVar);
			} else if(token.equals("mod")){
				if(secondVar==0)
					throw new RunTimeException("Devision by 0 at line:\n"+statement);
				variables.put(resultVariable, firstVar % secondVar);
			}
			
		}else if (token.equals("copy")){
			
			String sourceVariable=tokenizer.nextToken();
			tokenizer.nextToken();
			String destinationVariable=tokenizer.nextToken();
			variables.put(destinationVariable, variables.get(sourceVariable));
			
		} else if (token.equals("while")){
			
			String firstVal=tokenizer.nextToken();
			Integer checkVariable1=getVariableOrNumber(firstVal,variables);
			String compareOperator=tokenizer.nextToken();
			String secondVal=tokenizer.nextToken();
			Integer checkVariable2=getVariableOrNumber(secondVal,variables);
			ArrayList<String> innerStatements=FunctionDevider.getInnerStatements(statements);
			Function function=new Function(innerStatements,new ArrayList<String>());
			
			while(compareOperations(compareOperator,checkVariable1,checkVariable2)){
				interpretFunction(function,variables);
				checkVariable1=getVariableOrNumber(firstVal,variables);
				checkVariable2=getVariableOrNumber(secondVal,variables);
			}
			
		} else if (token.equals("if")){
			
			int checkVariable1=getVariableOrNumber(tokenizer.nextToken(),variables);
			String compareOperator=tokenizer.nextToken();
			int checkVariable2=getVariableOrNumber(tokenizer.nextToken(),variables);
			
			ArrayList<String> innerStatements=FunctionDevider.getInnerStatements(statements);
			ArrayList<String> operateStatements=new ArrayList<String>();
			
			if(compareOperations(compareOperator,checkVariable1,checkVariable2)){
				for(String innerStatement:innerStatements){
					StringTokenizer innerTokenizer=new StringTokenizer(innerStatement);
					if(!innerTokenizer.nextToken().equals("else"))
						operateStatements.add(innerStatement);
				}
			} else {
				boolean elseFound=false;
				for(String innerStatement:innerStatements){
					StringTokenizer innerTokenizer=new StringTokenizer(innerStatement);
					if(elseFound)
						operateStatements.add(innerStatement);
					if(innerTokenizer.nextToken().equals("else"))
						elseFound=true;
				}
			}
			
			Function function=new Function(operateStatements,new ArrayList<String>());
			
			interpretFunction(function,variables);
			
		} else if (token.equals("call")){
			
			HashMap<String,String> functionConnections=new HashMap<String,String>();
			HashMap<String,Integer> functionVariables=new HashMap<String,Integer>();
			Function function=functions.get(tokenizer.nextToken());
			
			int i=0;
			while(tokenizer.hasMoreTokens()){
				String variable=tokenizer.nextToken();
				functionConnections.put(new String(function.getParameters().get(i)),new String(variable));
				functionVariables.put(new String(function.getParameters().get(i)), new Integer(variables.get(variable)));
				i++;
			}
			
			interpretFunction(function,functionVariables);
			
			for(Entry<String,String> entry:functionConnections.entrySet()){
				variables.put(entry.getValue(), functionVariables.get(entry.getKey()));
			}
			
		} else if (token.equals("//")){
			
		}
	}

	private Integer getVariableOrNumber(String stringVariable,HashMap<String,Integer> variables) {
		if(!variables.containsKey(stringVariable)){
			return Integer.parseInt(stringVariable);
		} 
		return variables.get(stringVariable);
	}
	
	public boolean compareOperations(String compareOperator,int checkVariable1,int checkVariable2){
		return (compareOperator.equals("==") && checkVariable1==checkVariable2) || 
		(compareOperator.equals("!=") && !(checkVariable1==checkVariable2)) ||
		(compareOperator.equals("<=") && checkVariable1<=checkVariable2) ||
		(compareOperator.equals(">=") && checkVariable1>=checkVariable2) ||
		(compareOperator.equals("<") && checkVariable1<checkVariable2) ||
		(compareOperator.equals(">") && checkVariable1>checkVariable2);
	}
}
