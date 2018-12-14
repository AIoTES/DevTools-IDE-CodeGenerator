package es.upm.tfo.lst.codegenerator.plugin.protege;
/*
 * 
 * 
 * ut
 * comprobar que la ruta de los ficheros no esten fuera de la ruta de outp
 * 
 * */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.protege.editor.owl.ProtegeOWL;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;

import es.upm.tfo.lst.CodeGenerator.GenerateProject;
import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;
import es.upm.tfo.lst.codegenerator.plugin.protege.models.CodeGenerationVariableTable;



public class GenerationConfiguration extends JFrame{

	private JPanel contentPane;
	private JTable variableTable;
	private JComboBox sourceTextField;
	private JComboBox outputTextfield;
	private TableModel generateTable;
	private GenerateProject proj;
	private OWLModelManager owlModelManager;
	private TemplateDataModel mainModel=null;
	private XmlParser parser;
	private boolean checkValue;
	private Boolean flag=null;
    private static SwingWorker<Integer, Void> swingWorker;
    private List<String> templateArrayOptions;
    private List<String> outputArrayOptions;
    private File templateFileOptions=null;
    private File outputFileOptions=null;
    
	/**
	 * Create the frame.
	 */
	public GenerationConfiguration(OWLModelManager owlModelManager) {
		 this.owlModelManager=owlModelManager;
		
			try
			{ 
				templateFileOptions=new File(ProtegeOWL.getBundleContext().getDataFile("codegenerator.history.templates").getAbsolutePath());
				outputFileOptions=new File(ProtegeOWL.getBundleContext().getDataFile("codegenerator.history.output").getAbsolutePath());
				if(!templateFileOptions.exists())
					templateFileOptions.createNewFile();
				if(!outputFileOptions.exists())
					outputFileOptions.createNewFile();
				
			}catch (Exception e) {
				
			}
		 readFile();
		 
		
		setBackground(Color.LIGHT_GRAY);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 570, 444);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		variableTable = new JTable();
		variableTable.setMinimumSize(new Dimension(500, 200));
		
		
		//receive a project
		proj=null;
		generateTable = new AbstractTableModel() {
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getColumnName(int column) {

				return CodeGenerationVariableTable.COLS[column];
			}

			@Override
			public int getColumnCount() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		
	
		sourceTextField = new JComboBox(templateArrayOptions.toArray());
		
		sourceTextField.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				parser = new XmlParser();
				parser.generateXMLCoordinator(sourceTextField.getEditor().getItem().toString());
				System.out.println(sourceTextField.getEditor().getItem().toString());
				mainModel = parser.getXmlCoordinatorDataModel();
				if(mainModel!=null) {
					generateTable= new CodeGenerationVariableTable(mainModel);
					variableTable.setModel(generateTable);
					
					variableTable.repaint();
				}else {
					JOptionPane.showMessageDialog(null, "The selected file couldn't be loaded");
					parser = new XmlParser();
				}
				
				
			}
		});
		sourceTextField.setEditable(true);
		
		outputTextfield = new JComboBox(outputArrayOptions.toArray());
		outputTextfield.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
			}
		});
		outputTextfield.setEditable(true);
		
		//button to generate code
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(sourceTextField.getEditor().getItem().toString().equals("") || outputTextfield.getSelectedItem().toString().equals("")) {
					System.out.println(sourceTextField.getEditor().getItem().toString());
					JOptionPane.showMessageDialog(null, " empty path not allowed, check if output directory or template directory are empty");
				}else {
					
					
					
					OWLOntology owlOntology = null; 
					owlOntology = owlModelManager.getActiveOntology();
					proj = new GenerateProject();
					proj.addOntology(owlOntology, checkValue);
					proj.setMainModel(mainModel);
					proj.setLocalBaseLoaderPath(new File(sourceTextField.getEditor().getItem().toString()).getParentFile().getPath()+"/");
					String aux = outputTextfield.getEditor().getItem().toString();
					if(!aux.endsWith("/")) aux += "/";
					proj.setOutputFolder(aux);
					System.out.println("actual ontology name "+owlOntology.getOntologyID().getDefaultDocumentIRI().get().getShortForm());
					writeFile(outputFileOptions, outputTextfield.getEditor().getItem().toString());
					writeFile(templateFileOptions, sourceTextField.getEditor().getItem().toString());
					asyncProcess();
				}
			}
		});

		JLabel lblTemplateSource = new JLabel("Template source");
		// button to close panel
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		JScrollPane scrollPane = new JScrollPane(variableTable);
		//button to deploy file chooser to XML
		JButton btnTemplateFileChooser = new JButton("...");
		btnTemplateFileChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				
				fc.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return "Coordinator XML file";
					}

					@Override
					public boolean accept(File f) {
						return f.getName().toLowerCase().endsWith(".xml");
					}
				});
				int returnVal = fc.showOpenDialog(GenerationConfiguration.this);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            sourceTextField.setSelectedItem(file.getAbsolutePath());
		        }
		        //
			}
		});

		
		//button to select output directory
		JButton btnOutputFileChooser = new JButton("...");
		
		btnOutputFileChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
							
				fc.setFileFilter(new FileFilter() {
				
					@Override
					public String getDescription() {
						return "Coordinator XML file";
					}
					
					@Override
					public boolean accept(File f) {
						return f.getName().toLowerCase().endsWith(".xml");
						}
				});

				
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnVal = fc.showOpenDialog(GenerationConfiguration.this);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            outputTextfield.setSelectedItem(file.getAbsolutePath());
		        }
			}
		});
		
		JLabel lblOutput = new JLabel("Output");
		JCheckBox checkRecursive = new JCheckBox("Recursive");
		checkRecursive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkValue=checkRecursive.isSelected();
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(25)
					.addComponent(lblTemplateSource, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
					.addGap(12)
					.addComponent(sourceTextField, GroupLayout.PREFERRED_SIZE, 360, GroupLayout.PREFERRED_SIZE)
					.addGap(6)
					.addComponent(btnTemplateFileChooser, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(139)
					.addComponent(checkRecursive))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(29)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 466, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(29)
					.addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(outputTextfield, GroupLayout.PREFERRED_SIZE, 356, GroupLayout.PREFERRED_SIZE)
					.addGap(6)
					.addComponent(btnOutputFileChooser, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(249)
					.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
					.addGap(12)
					.addComponent(btnGenerate, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(2)
							.addComponent(lblTemplateSource, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
						.addComponent(sourceTextField, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(1)
							.addComponent(btnTemplateFileChooser)))
					.addGap(18)
					.addComponent(checkRecursive)
					.addGap(2)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 239, GroupLayout.PREFERRED_SIZE)
					.addGap(12)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(2)
							.addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
						.addComponent(outputTextfield, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(1)
							.addComponent(btnOutputFileChooser)))
					.addGap(17)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnCancel)
						.addComponent(btnGenerate)))
		);
		contentPane.setLayout(gl_contentPane);
	}

	private void saveComboContent(String t){
			if(new File(t).exists()) {
				//save data
			}
	}
	private void asyncProcess() {
		 
		
		swingWorker = new SwingWorker<Integer, Void>(){

		
			@Override
			protected void done() {
				System.out.println("Done!");
				
			}

			@Override
			protected Integer doInBackground() throws Exception {
				System.out.println("doInBackground...");
				try {
					while(flag==null) {
						flag=proj.process();	
					}

				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
				return null;
			}
			
		};
		swingWorker.execute();
				
		
	}

	private void readFile() {

		 this.templateArrayOptions = new ArrayList<>();
		 this.outputArrayOptions= new ArrayList<>();
		try {
			  BufferedReader br = new BufferedReader(new FileReader(this.templateFileOptions)); 
			  String st; 
			  while ((st = br.readLine()) != null) { 
				  templateArrayOptions.add(st);
			  } 
			  br.close();
			  
			  st=null;
			  
			  br = new BufferedReader(new FileReader(this.outputFileOptions)); 
			  while ((st = br.readLine()) != null) { 
				  outputArrayOptions.add(st);
			  } 
			  br.close();
			  
		}catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
				
		}
	}
	private void writeFile( File f,String dataToWrite) {
		 System.out.println("write file "+dataToWrite);
		boolean aux=true;
		 
		try {
			 BufferedReader br = new BufferedReader(new FileReader(f)); 
			  String st; 
			  while ((st = br.readLine()) != null) { 
				  if(st.equals(dataToWrite)) {
					  aux =false;
					  break;
				  }
			  }
			br.close();
			if(aux) {
				FileWriter fr = new FileWriter(f, true);
				fr.write(dataToWrite);
				fr.write("\n");
				fr.close();
			}
			
			  
		}catch (Exception e) {
			System.out.println("writeFile "+e.getMessage());
				
		}
	}
	

}
