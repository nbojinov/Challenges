// The code for the word-coloring belongs to: user1803551 at http://stackoverflow.com/questions/23738633/colorizing-certain-part-of-the-text-in-jeditorpane-makes-foreground-colorization

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;


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
		setBounds(100, 100, 709, 592);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 22, 685, 383);
		contentPane.add(scrollPane);
		
		final JEditorPane textArea = new JEditorPane();
		textArea.setEditorKit(new StyledEditorKit());
        textArea.setDocument(new DefaultStyledDocument());
		scrollPane.setViewportView(textArea);
		
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 417, 685, 149);
		contentPane.add(scrollPane_1);
		
		final JTextPane textArea_2 = new JTextPane();
		scrollPane_1.setViewportView(textArea_2);
		textArea_2.setEditable(false);
		
		final HashMap<String,Function> functions=new HashMap<String,Function>();
		final Preparer preparer=new Preparer(functions);
		final Interpreter interpreter=new Interpreter(functions);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 129, 21);
		contentPane.add(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mniOpen = new JMenuItem("Open");
		mniOpen.addActionListener(new ActionListener() {
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
		mniOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		mnFile.add(mniOpen);
		
		JMenuItem mniSave = new JMenuItem("Save");
		mniSave.addActionListener(new ActionListener() {
			
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
		mniSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		mnFile.add(mniSave);
		
		JMenuItem mniExit = new JMenuItem("Exit");
		mniExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		mniExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		mnFile.add(mniExit);
		
		JMenu mnRun = new JMenu("Run");
		menuBar.add(mnRun);
		
		final JMenuItem mniExecute = new JMenuItem("Execute");
		
		JMenuItem mniErrorCheck = new JMenuItem("Error Check");
		mniErrorCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					String areaText=textArea.getText();
					areaText=areaText.replace("\n", "").replace(";",";\n").replace("\t","").replaceAll("[ ]+", " ").trim();
					StringTokenizer tokenizer=new StringTokenizer(areaText,";");
					
					ArrayList<String> statements=new ArrayList<String>();
					while(tokenizer.hasMoreTokens())
						statements.add(tokenizer.nextToken());
					
					ArrayList<Integer> tabs=preparer.prepare(statements);
					
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
					mniExecute.setEnabled(true);
					
				} catch (SyntaxException e1) {
					// TODO Auto-generated catch block
					textArea_2.setText(e1.getMessage());
				}
			}
		});
		mniErrorCheck.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		mnRun.add(mniErrorCheck);
		
		
		mniExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
				
				StringTokenizer tokenizer=new StringTokenizer(textArea.getText().trim(), ";");
				ArrayList<String> statements=new ArrayList<String>();
				while(tokenizer.hasMoreTokens())
					statements.add(tokenizer.nextToken());
				
				HashMap<String,Integer> variables=interpreter.interpret();
				
				String bareBonesCode="";
				for(Entry<String, Integer> entry: variables.entrySet())
					bareBonesCode+=entry.getKey()+"="+entry.getValue()+"\n";
				textArea_2.setText(bareBonesCode);
				} catch (RunTimeException e1) {
					// TODO Auto-generated catch block
					textArea_2.setText(e1.getMessage());
				}
			}
		});
		
		final Pattern pat;
	    
	    String[] words = {"def","//","call","while","def","end","if","else","clear","add","incr","sub","decr","mul","div","mod","do"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            sb.append("("+words[i]+")" + "|");
        }
        sb.deleteCharAt(sb.length()-1);
        pat = Pattern.compile(sb.toString());
		
		mniExecute.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, ActionEvent.CTRL_MASK));
		mnRun.add(mniExecute);
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			
			SimpleAttributeSet black = new SimpleAttributeSet();
			SimpleAttributeSet green = new SimpleAttributeSet();
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				mniExecute.setEnabled(false);
				
				SwingUtilities.invokeLater(new Runnable() {

                public void run() {

                    findAndColor();
                }
            });
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				mniExecute.setEnabled(false);
				
				SwingUtilities.invokeLater(new Runnable() {

	                public void run() {

	                    findAndColor();
	                }
	            });
			}
			
			private void findAndColor() {
				StyleConstants.setForeground(black, Color.BLACK);
				StyleConstants.setForeground(green, Color.MAGENTA);

	            StyledDocument doc = (StyledDocument) textArea.getDocument();
	            doc.setCharacterAttributes(0, doc.getLength(), black, true);

	            Matcher m = pat.matcher(textArea.getText());
	            while (m.find()) {
	                doc.setCharacterAttributes(m.start(), m.end(), green, true);
	                doc.setCharacterAttributes(m.end(), doc.getLength(), black, true);
	            }   
	        }

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
