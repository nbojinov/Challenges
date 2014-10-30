import java.util.ArrayList;
import java.util.StringTokenizer;



public class SyntaxChecker {
	
	ArrayList<String> variables;
	ArrayList<Integer> lineTabs;
	
	public ArrayList<Integer> checkSyntax(String text) throws SyntaxException{
		
		variables=new ArrayList<String>();
		lineTabs=new ArrayList<Integer>();
		
		StringTokenizer tokenizer=new StringTokenizer(text,";");
		
		while(tokenizer.hasMoreTokens()){
			String statement=tokenizer.nextToken();
			StringTokenizer wordTokenizer=new StringTokenizer(statement);
			ArrayList<String> wordsInStatement=new ArrayList<String>();
			if(!wordTokenizer.hasMoreTokens())
				continue;
			while(wordTokenizer.hasMoreTokens()){
				wordsInStatement.add(wordTokenizer.nextToken());
			}
			if(wordsInStatement.get(0).equals("incr") || wordsInStatement.get(0).equals("decr") || wordsInStatement.get(0).equals("clear")){
				if(wordsInStatement.get(0).equals("decr")){
					if(!variables.contains(wordsInStatement.get(1)))
						throw new SyntaxException("Variable "+wordsInStatement.get(1)+" isn't initialised at:\n"+statement);
				}
				checkIncrementDecrementClear(wordsInStatement,statement);
				if(wordsInStatement.get(0).equals("incr") || wordsInStatement.get(0).equals("clear")){
					variables.add(wordsInStatement.get(1));
				}
			} else if (wordsInStatement.get(0).equals("while")){
				checkWhile(wordsInStatement,statement);
			} else if (wordsInStatement.get(0).equals("end")){
				if(wordsInStatement.size()>1){
					throw new SyntaxException("Too many arguments at line:\n"+statement);
				}
			}	else if (wordsInStatement.get(0).equals("copy")){
				checkCopy(wordsInStatement,statement);
			} else {
				throw new SyntaxException("Couldn't recognize "+wordsInStatement.get(0)+" at line:\n"+statement);
			}
		}
		checkLoops(new StringTokenizer(text,";"));
		return lineTabs;
	}
	
	private void checkIncrementDecrementClear(ArrayList<String> wordsInStatement,String statement) throws SyntaxException {
		// TODO Auto-generated method stub
		checkSize(2,wordsInStatement.size(),statement);
		checkVariable(wordsInStatement.get(1), statement);
	}
	
	private void checkWhile(ArrayList<String> wordsInStatement,String statement) throws SyntaxException {
		// TODO Auto-generated method stub
		checkSize(5,wordsInStatement.size(),statement);
		if(!(wordsInStatement.get(2).equals("not") && wordsInStatement.get(4).equals("do"))){
			throw new SyntaxException("Improper while statement at line:\n"+statement);
		}
		checkVariable(wordsInStatement.get(1),statement);
		if(!variables.contains(wordsInStatement.get(1)))
			throw new SyntaxException("Variable "+wordsInStatement.get(1)+" isn't initialised at:\n"+statement);
		if(!wordsInStatement.get(3).equals("0")){
			throw new SyntaxException("You can't compare a variable with anything except 0 at line:\n"+statement);
		}
	}
	
	private void checkCopy(ArrayList<String> wordsInStatement,String statement) throws SyntaxException {
		// TODO Auto-generated method stub
		checkSize(4,wordsInStatement.size(),statement);
		if(!wordsInStatement.get(2).equals("to")){
			throw new SyntaxException("Improper copy statement at line:\n"+statement);
		}
		checkVariable(wordsInStatement.get(1),statement);
		if(!variables.contains(wordsInStatement.get(1)))
			throw new SyntaxException("Variable "+wordsInStatement.get(1)+" isn't initialised at:\n"+statement);
		checkVariable(wordsInStatement.get(3),statement);
		variables.add(wordsInStatement.get(3));
	}
	
	private void checkVariable(String word,String statement) throws SyntaxException {
		char startingCharacter=word.charAt(0);
		if(!Character.isLetter(startingCharacter))
			throw new SyntaxException("Improper variable definition for word:"+word+" at line:\n"+statement);
		for(char c: word.toCharArray())
			if(!(Character.isLetterOrDigit(c) || c=='_'))
				throw new SyntaxException("Improper variable definition for word:"+word+" at line:\n"+statement);
	}
	
	private void checkSize(int size,int statementSize,String statement) throws SyntaxException{
		if(statementSize<size){
			throw new SyntaxException("Too few arguments at line:\n"+statement);
		} else if (statementSize>size){
			throw new SyntaxException("Too many arguments at line:\n"+statement);
		}
	}

	private void checkLoops(StringTokenizer tokenizer) throws SyntaxException {
		// TODO Auto-generated method stub
		int openLoops=0;
		
		while(tokenizer.hasMoreTokens()){
			
			String statement=tokenizer.nextToken();
			String firstWordOfStatement=new StringTokenizer(statement).nextToken();
			if(firstWordOfStatement.equals("while")){
				lineTabs.add(openLoops);
				openLoops++;
				continue;
			} else if (firstWordOfStatement.equals("end")){
				openLoops--;
				if(openLoops<0){
					throw new SyntaxException("Too many end statements");
				}
			}
			lineTabs.add(openLoops);
		}
		if(openLoops>0){
			throw new SyntaxException("While statement(s) not closed");
		}
	}
}
