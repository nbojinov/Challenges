import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;



public class Preparer {
	
	private HashMap<String,Function> functions;
	
	public Preparer(HashMap<String,Function> functions){
		this.functions=functions;
	}
	
	public ArrayList<Integer> prepare(ArrayList<String> statements) throws SyntaxException{
		ArrayList<Integer> lineTabs=checkBlocks(statements);
		functions.clear();
		HashMap<String,Integer> functionDefinitions=getFunctions(statements);
		for(String key:functions.keySet()){
			checkFunctionSyntax(functions.get(key),functionDefinitions);
		}
		return lineTabs;
	}

	private HashMap<String,Integer> getFunctions(ArrayList<String> st) throws SyntaxException {
		ArrayList<String> statements=new ArrayList<String>(st);
		HashMap<String,Integer> functionDefinitions=new HashMap<String,Integer>();
		int depth=0;
		while(!statements.isEmpty()){
			
			String statement=statements.remove(0);
			ArrayList<String> wordsInStatement=getWordsInStatement(statement);
			if(wordsInStatement==null)
				continue;
			
			if (wordsInStatement.get(0).equals("def")){
				checkFunctionDefinition(wordsInStatement,statement,functionDefinitions);
				depth++;
			} else if (wordsInStatement.get(0).equals("end")){
				checkSize(1, 1, wordsInStatement.size(), statement);
				depth--;
			} else if (wordsInStatement.get(0).equals("while") || wordsInStatement.get(0).equals("if")){
				depth++;
			} else if (wordsInStatement.get(0).equals("//")){
				continue;
			}else {
				if (depth<=0)
					throw new SyntaxException("Statements outside functions aren't allowed at line:\n"+statement);
			}
		}
		
		FunctionDevider.findFunctions(st, functions);
		
		return functionDefinitions;
	}
	
	private ArrayList<String> getWordsInStatement(String statement){
		StringTokenizer wordTokenizer=new StringTokenizer(statement);
		ArrayList<String> wordsInStatement=new ArrayList<String>();
		
		if(!wordTokenizer.hasMoreTokens())
			return null;
		while(wordTokenizer.hasMoreTokens()){
			wordsInStatement.add(wordTokenizer.nextToken());
		}
		return wordsInStatement;
	}

	private void checkFunctionSyntax(Function func,HashMap<String,Integer> functionDefinitions) throws SyntaxException{
		
		ArrayList<String> variables=new ArrayList<String>(func.getParameters());
		ArrayList<String> statements=new ArrayList<String>(func.getStatements());
		
		while(!statements.isEmpty()){
			
			String statement=statements.remove(0);
			ArrayList<String> wordsInStatement=getWordsInStatement(statement);
			if(wordsInStatement==null)
				continue;
			
			if(wordsInStatement.get(0).equals("incr") || wordsInStatement.get(0).equals("decr") || wordsInStatement.get(0).equals("clear")){
				
				checkIncrementDecrementClear(wordsInStatement,statement,variables);
				
			} else if (wordsInStatement.get(0).equals("while") || wordsInStatement.get(0).equals("if")){
				
				checkIfAndWhile(wordsInStatement,statement,variables);
				
			} else if (wordsInStatement.get(0).equals("end") || wordsInStatement.get(0).equals("else")){
				
				checkSize(1,1,wordsInStatement.size(),statement);
				
			}	else if (wordsInStatement.get(0).equals("copy")){
				
				checkCopy(wordsInStatement,statement,variables);
				
			} else if (wordsInStatement.get(0).equals("add") || wordsInStatement.get(0).equals("sub") || 
					wordsInStatement.get(0).equals("mul")  || wordsInStatement.get(0).equals("div") ||
					wordsInStatement.get(0).equals("mod")) {
				
				checkArithmetic(wordsInStatement,statement,variables);
				
			} else if (wordsInStatement.get(0).equals("call")){
				
				if(!functionDefinitions.containsKey(wordsInStatement.get(1)))
					throw new SyntaxException("Undefined function call at line:\n"+statement);
				if(wordsInStatement.size()-2!=functionDefinitions.get(wordsInStatement.get(1)))
					throw new SyntaxException("Wrong number of arguments for function call at line:\n"+statement);
				for(int i=2;i<wordsInStatement.size();i++){
					if(!variables.contains(wordsInStatement.get(i)))
						throw new SyntaxException("Undefined variable "+wordsInStatement.get(i)+" at line:\n"+statement);
				}
				
			} else if (wordsInStatement.get(0).equals("//")){
				
				continue;
				
			} else {
				
				throw new SyntaxException("Couldn't recognize "+wordsInStatement.get(0)+" at line:\n"+statement);
				
			}
		}
	}
	
	private void checkFunctionDefinition(ArrayList<String> wordsInStatement,
			String statement, HashMap<String,Integer> functionDefinitions) throws SyntaxException {
		checkProperVariableName(wordsInStatement.get(1), statement);
		if(functionDefinitions.containsKey(wordsInStatement.get(1)))
			throw new SyntaxException("Redefinition of function "+wordsInStatement.get(1)+" at line:\n"+statement);
		if(!wordsInStatement.get(2).equals("(") || !wordsInStatement.get(wordsInStatement.size()-1).equals(")"))
			throw new SyntaxException("Improper bracketting in "+wordsInStatement.get(1)+" function definition at line:\n"+statement);
		for(int i=3;i<wordsInStatement.size()-1;i++){
			checkProperVariableName(wordsInStatement.get(i),statement);
			if(i!=wordsInStatement.lastIndexOf(wordsInStatement.get(i)))
				throw new SyntaxException("Repetitive use of "+wordsInStatement.get(i)+" at line:\n"+statement);
		}
		functionDefinitions.put(wordsInStatement.get(1),wordsInStatement.size()-4);
	}

	private void checkIfAndWhile(ArrayList<String> wordsInStatement, String statement,ArrayList<String> variables) throws SyntaxException {
		checkSize(4,4,wordsInStatement.size(),statement);
		
		try{
			checkVariableOrNumber(wordsInStatement.get(1),variables);
			if(!(wordsInStatement.get(2).equals("==") || wordsInStatement.get(2).equals("!=") || wordsInStatement.get(2).equals("<=") ||
					wordsInStatement.get(2).equals(">=") || wordsInStatement.get(2).equals("<") || wordsInStatement.get(2).equals(">")))
				throw new SyntaxException("Not a known operator for comparison at line:\n"+statement);
			checkVariableOrNumber(wordsInStatement.get(3),variables);
		} catch (NumberFormatException e){
			throw new SyntaxException("Not a properly defined number or uninitialized variable at:\n"+statement);
		}
	}

	private void checkArithmetic(ArrayList<String> wordsInStatement,
			String statement,ArrayList<String> variables) throws SyntaxException {
		checkSize(3,4,wordsInStatement.size(),statement);
		if(!variables.contains(wordsInStatement.get(1)))
			throw new SyntaxException("Variable "+wordsInStatement.get(1)+" isn't initialised at:\n"+statement);
		try{
			checkVariableOrNumber(wordsInStatement.get(2),variables);
			if(wordsInStatement.size()==4){
				checkVariableOrNumber(wordsInStatement.get(3),variables);
			}
		} catch (NumberFormatException e){
			throw new SyntaxException("Not a properly defined number or uninitialized variable at:\n"+statement);
		}
	}

	private void checkIncrementDecrementClear(ArrayList<String> wordsInStatement,String statement,ArrayList<String> variables) throws SyntaxException {
		checkSize(2,2,wordsInStatement.size(),statement);
		if(wordsInStatement.get(0).equals("incr") || wordsInStatement.get(0).equals("clear")){
			checkProperVariableName(wordsInStatement.get(1), statement);
			variables.add(wordsInStatement.get(1));
		} else {
			if(!variables.contains(wordsInStatement.get(1)))
				throw new SyntaxException("Variable "+wordsInStatement.get(1)+" isn't initialised at:\n"+statement);
		}
		
	}

	private void checkProperVariableName(String word,String statement) throws SyntaxException {
		// TODO check for system names
		char startingCharacter=word.charAt(0);
		if(!Character.isLetter(startingCharacter))
			throw new SyntaxException("Improper definition for word:"+word+" at line:\n"+statement);
		for(char c: word.toCharArray())
			if(!(Character.isLetterOrDigit(c) || c=='_'))
				throw new SyntaxException("Improper definition for word:"+word+" at line:\n"+statement);
	}
	
	private void checkCopy(ArrayList<String> wordsInStatement,String statement,ArrayList<String> variables) throws SyntaxException {
		checkSize(4,4,wordsInStatement.size(),statement);
		if(!wordsInStatement.get(2).equals("to")){
			throw new SyntaxException("Improper copy statement at line:\n"+statement);
		}
		if(!variables.contains(wordsInStatement.get(1)))
			throw new SyntaxException("Variable "+wordsInStatement.get(1)+" isn't initialised at:\n"+statement);
		variables.add(wordsInStatement.get(3));
	}
	
	private void checkSize(int minSize,int maxSize,int statementSize,String statement) throws SyntaxException{
		if(statementSize<minSize){
			throw new SyntaxException("Too few arguments at line:\n"+statement);
		} else if (statementSize>maxSize){
			throw new SyntaxException("Too many arguments at line:\n"+statement);
		}
	}

	private ArrayList<Integer> checkBlocks(ArrayList<String> statements) throws SyntaxException {
		int openBlocks=0;
		ArrayList<Integer> lineTabs=new ArrayList<Integer>();
		
		for(String statement:statements){
			String firstWordOfStatement=new StringTokenizer(statement).nextToken();
			if(firstWordOfStatement.equals("while") || firstWordOfStatement.equals("if") || firstWordOfStatement.equals("def")){
				lineTabs.add(openBlocks);
				openBlocks++;
				continue;
			} else if (firstWordOfStatement.equals("end")){
				openBlocks--;
				if(openBlocks<0){
					throw new SyntaxException("Too many end statements "+statement);
				}
			} 
			if(firstWordOfStatement.equals("else"))
				lineTabs.add(openBlocks-1);
			else
				lineTabs.add(openBlocks);
		}
		if(openBlocks>0){
			throw new SyntaxException("While or If statement(s) not closed");
		}
		
		return lineTabs;
	}
	
	private void checkVariableOrNumber(String stringVariable,ArrayList<String> variables) {
		if(!variables.contains(stringVariable)){
			Integer.parseInt(stringVariable);
		} 
	}
}
