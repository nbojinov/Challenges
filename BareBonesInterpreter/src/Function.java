import java.util.ArrayList;


public class Function {
	
	private ArrayList<String> statements;
	private ArrayList<String> parameters;

	public Function(ArrayList<String> statements,ArrayList<String> parameters){
		this.statements=statements;
		this.parameters=parameters;
	}

	public ArrayList<String> getStatements() {
		return statements;
	}
	
	public ArrayList<String> getParameters(){
		return parameters;
	}
}
