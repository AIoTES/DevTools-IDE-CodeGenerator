package es.upm.tfo.lst.codegenerator.plugin.protege;
/*
 * 
 * 
 * ut
 * comprobar que la ruta de los ficheros no esten fuera de la ruta de outp
 * 
 * */

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;

import es.upm.tfo.lst.CodeGenerator.GenerateProject;
import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;
import es.upm.tfo.lst.codegenerator.plugin.protege.models.CodeGenerationVariableTable;



public class GenerationConfiguration extends JFrame  {

	private JPanel contentPane;
	private JTable variableTable;
	private JComboBox sourceTextField;
	private JTextField outputTextfield;
	private TableModel generateTable;
	private GenerateProject proj;
	private OWLModelManager owlModelManager;
	private TemplateDataModel mainModel;
	private XmlParser parser;
	private boolean checkValue;
	private Boolean flag=null;
    private static SwingWorker<Integer, Void> swingWorker;

	/**
	 * Create the frame.
	 */
	public GenerationConfiguration(OWLModelManager owlModelManager) {
		 this.owlModelManager=owlModelManager;
		setBackground(Color.LIGHT_GRAY);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 570, 444);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		variableTable = new JTable();
		
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
		
		

		sourceTextField = new JComboBox();
		sourceTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				parser = new XmlParser();
				parser.generateXMLCoordinator(sourceTextField.getEditor().getItem().toString());
				mainModel = parser.getXmlCoordinatorDataModel();
				generateTable= new CodeGenerationVariableTable(mainModel);
				variableTable.setModel(generateTable);
				variableTable.repaint();
				
				
			}
		});
		sourceTextField.setEditable(true);
		//variableTable.setModel(generateTable);
		outputTextfield = new JTextField();
		outputTextfield.setColumns(10);
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(sourceTextField.getEditor().getItem().toString().equals("") || outputTextfield.getText().equals("")) {
					System.out.println(sourceTextField.getEditor().getItem().toString());
					JOptionPane.showMessageDialog(null, " empty path not allowed, check if output directory or template directory are empty");
				}else {
					OWLOntology owlOntology = owlModelManager.getActiveOntology();
					System.out.println(owlOntology.getClassesInSignature().size());
					proj = new GenerateProject();
					proj.addOntology(owlOntology, checkValue);
					proj.setMainModel(mainModel);
					System.out.println(new File(sourceTextField.getEditor().getItem().toString()).getParentFile().getPath()+"/");
					proj.setLocalBaseLoaderPath(new File(sourceTextField.getEditor().getItem().toString()).getParentFile().getPath()+"/");
					proj.setOutputFolder(outputTextfield.getText()+"/");
					System.out.println(owlOntology.getOntologyID().getDefaultDocumentIRI().get().getShortForm());
					ProgressBar p = new ProgressBar();
					p.main();
					asyncProcess();
					
					//JOptionPane.showMessageDialog(null, "Generating source code ...");
				}
			}
		});

		JLabel lblTemplateSource = new JLabel("Template source");

		JLabel lblOutput = new JLabel("Output");

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		JScrollPane scrollPane = new JScrollPane(variableTable);

		JButton btnTemplateFileChooser = new JButton("...");
		btnTemplateFileChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return "Corrdinator XML file";
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
			}
		});

		
		
		JButton btnOutputFileChooser = new JButton("...");
		btnOutputFileChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser fc = new JFileChooser(outputTextfield.getText());
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnVal = fc.showOpenDialog(GenerationConfiguration.this);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            outputTextfield.setText(file.getAbsolutePath());
		        }
			}
		});
		
		JCheckBox checkRecursive = new JCheckBox("Recursive");
		checkRecursive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkValue=checkRecursive.isSelected();
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(25)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(224)
									.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnGenerate, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblTemplateSource, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(sourceTextField, 0, 357, Short.MAX_VALUE)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnTemplateFileChooser, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addGap(29)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(outputTextfield, GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(110)
									.addComponent(checkRecursive)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnOutputFileChooser, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTemplateSource, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addComponent(sourceTextField, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnTemplateFileChooser))
					.addGap(18)
					.addComponent(checkRecursive)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 239, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(outputTextfield, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnOutputFileChooser))
					.addPreferredGap(ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnGenerate, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
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
}
