import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Map.Entry;


public class Window extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window frame = new Window();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Window() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 456, 370);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		final JButton executeButton = new JButton("Execute");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 12, 426, 122);
		contentPane.add(scrollPane);
		
		final JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				executeButton.setEnabled(false);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				executeButton.setEnabled(false);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 143, 426, 115);
		contentPane.add(scrollPane_1);
		
		final JTextArea textArea_2 = new JTextArea();
		scrollPane_1.setViewportView(textArea_2);
		textArea_2.setEditable(false);
		
		
		executeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HashMap<String,Integer> variables=new HashMap<String,Integer>();
				StringTokenizer tokenizer=new StringTokenizer(textArea.getText().trim(), ";");
				ArrayList<String> statements=new ArrayList<String>();
				while(tokenizer.hasMoreTokens())
					statements.add(tokenizer.nextToken());
				Interpreter interpreter=new Interpreter(variables,statements);
				while(!interpreter.hasFinished()){
						interpreter.interpretLine();
				}
				String bareBonesCode="";
				for(Entry<String, Integer> entry: variables.entrySet())
					bareBonesCode+=entry.getKey()+"="+entry.getValue()+"\n";
				textArea_2.setText(bareBonesCode);
			}
		});
		executeButton.setBounds(321, 270, 117, 25);
		contentPane.add(executeButton);
		
		JButton openButton = new JButton("Open file");
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter filter=new FileNameExtensionFilter("BareBones (bb) files", "bb");
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setFileFilter(filter);
				
				if(fileChooser.showOpenDialog(contentPane)==JFileChooser.APPROVE_OPTION){
					BufferedReader reader=null;
					try {
						reader=new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
						String line,bareBonesCode="";
						while((line=reader.readLine())!=null){
							bareBonesCode+=line+"\n";
						}
						reader.close();
						textArea.setText(bareBonesCode.trim());
						textArea_2.setText("");
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		openButton.setBounds(22, 270, 117, 25);
		contentPane.add(openButton);
		
		JButton btnErrorCheck = new JButton("Error Check");
		btnErrorCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				SyntaxChecker syntaxChecker=new SyntaxChecker();
				try {
					String areaText=textArea.getText();
					areaText=areaText.replace("\n", "").replace(";",";\n").replace("\t","").replaceAll("[ ]+", " ").trim();
					ArrayList<Integer> tabs=syntaxChecker.checkSyntax(areaText);
					String newText="";
					int lineNumber=0;
					for(String line: areaText.split("\n")){
						for(int i=0;i<tabs.get(lineNumber);i++)
							newText+="    ";
						newText+=line+"\n";
						lineNumber++;
					}	
					textArea_2.setText("No errors!");
					textArea.setText(newText);
					executeButton.setEnabled(true);
				} catch (SyntaxException e1) {
					// TODO Auto-generated catch block
					textArea_2.setText(e1.getMessage());
				}
			}
		});
		btnErrorCheck.setBounds(321, 307, 117, 25);
		contentPane.add(btnErrorCheck);
		
		JButton btnSaveFile = new JButton("Save file");
		btnSaveFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				FileNameExtensionFilter filter=new FileNameExtensionFilter("BareBones (bb) files", "bb");
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setFileFilter(filter);
				
				if(fileChooser.showSaveDialog(contentPane)==JFileChooser.APPROVE_OPTION){
					try {
						BufferedWriter writer=new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()));
						writer.write(textArea.getText());
						writer.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnSaveFile.setBounds(22, 307, 117, 25);
		contentPane.add(btnSaveFile);
	}
}
