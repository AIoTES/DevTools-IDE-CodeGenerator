/*******************************************************************************
 * Copyright 2018 Universidad Politécnica de Madrid UPM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package es.upm.tfo.lst.codegenerator.plugin.protege;
/*
 *
 *
 *
 * comprobar que la ruta de los ficheros no esten fuera de la ruta de output
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
import java.util.Arrays;
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
import javax.swing.SwingConstants;
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




public class GenerationConfiguration extends JFrame implements GenerateProject.ProgessCallbackPublisher {

	private JPanel contentPane;
	private JTable variableTable;
	private JComboBox sourceTextField;
	private JComboBox outputTextfield;
	private TableModel generateTable;
	private GenerateProject proj;
	private OWLModelManager owlModelManager;
	private TemplateDataModel mainModel=null;
	private XmlParser parser;
	private boolean recursiveValue;
	private Boolean flag=null;
    private static SwingWorker<Integer, Void> swingWorker;
    private List<String> templateArrayOptions;
    private List<String> outputArrayOptions;
    private File templateFileOptions=null,outputFileOptions=null;

    private int progress;
    private ProgressBar pb;
    private String xmlParentDir=null;
    //---------messages

    private final String MISSINGTAG="Seems in the xml coodinator some fields is missing";
    private final String SINTAXERROR="Seems in the xml coodinator are error in xml sintax";
	/**
	 * Create the frame.
	 */
	public GenerationConfiguration(OWLModelManager owlModelManager) {
		setTitle("CodeGenerator plugin");
		 this.owlModelManager=owlModelManager;
		 proj = new GenerateProject();
		 this.proj.GenConf = this;

			try
			{
				templateFileOptions=new File(ProtegeOWL.getBundleContext().getDataFile("codegenerator.history.templates").getAbsolutePath());
				outputFileOptions=new File(ProtegeOWL.getBundleContext().getDataFile("codegenerator.history.output").getAbsolutePath());
				if(!templateFileOptions.exists())
					templateFileOptions.createNewFile();
				if(!outputFileOptions.exists())
					outputFileOptions.createNewFile();


			}catch (Exception e1) {
				String mainMessage = "Message: " + e1.getMessage()
				+ "\nStackTrace: " + Arrays.toString(e1.getStackTrace());
				String title = e1.getClass().getName();
				JOptionPane.showMessageDialog(null, mainMessage, title, JOptionPane.ERROR_MESSAGE);
			}
		 readFile();


		setBackground(Color.LIGHT_GRAY);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 656, 514);
		setMinimumSize( new Dimension(570, 444));
		contentPane = new JPanel();

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		variableTable = new JTable();
		variableTable.setMinimumSize(new Dimension(500, 200));



		// initial model: empty table with the columns
		generateTable = new AbstractTableModel() {

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return null;
			}

			@Override
			public int getRowCount() {
				return 0;
			}

			@Override
			public String getColumnName(int column) {

				return CodeGenerationVariableTable.COLS[column];
			}

			@Override
			public int getColumnCount() {
				return CodeGenerationVariableTable.COLS.length;
			}
		};


		sourceTextField = new JComboBox(templateArrayOptions.toArray());

		sourceTextField.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				parser = new XmlParser();
				try {
					mainModel=	parser.generateXMLCoordinator(sourceTextField.getEditor().getItem().toString());
				} catch (Exception e1) {
					String message="";
					if(e1 instanceof NullPointerException)
						message = MISSINGTAG;
					else
						message=SINTAXERROR;

					JOptionPane.showMessageDialog(null, message);
					e1.printStackTrace();
				}

				//parser.setOutput(tempWebTemplate.getPath());
				if(mainModel!=null) {
					generateTable= new CodeGenerationVariableTable(mainModel);
					variableTable.setModel(generateTable);
					variableTable.repaint();
				}else {
					//JOptionPane.showMessageDialog(null, "The selected file couldn't be loaded");
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
					//System.out.println(sourceTextField.getEditor().getItem().toString());
					JOptionPane.showMessageDialog(null, " empty path not allowed, check if output directory or template directory are empty");
				}else {

					OWLOntology owlOntology = null;
					owlOntology = owlModelManager.getActiveOntology();

					proj.addOntology(owlModelManager.getActiveOntology(), recursiveValue);
					proj.setMainModel(mainModel);
					proj.setOutputFolder(sourceTextField.getEditor().toString());

					String aux = outputTextfield.getEditor().getItem().toString();
					if(!aux.endsWith("/")) aux += "/";
					proj.setOutputFolder(aux);

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
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnVal = fc.showOpenDialog(GenerationConfiguration.this);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            outputTextfield.setSelectedItem(file.getAbsolutePath());
		        }
			}
		});

		JLabel lblOutput = new JLabel("Output");
		JCheckBox checkRecursive = new JCheckBox("Load Imported Ontologies recursively");
		checkRecursive.setHorizontalTextPosition(SwingConstants.LEFT);
		checkRecursive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				recursiveValue=checkRecursive.isSelected();
			}
		});

		JLabel lblVariablesInXml = new JLabel("Variables in XML file");
		
		JButton btnNewButton = new JButton("Template Information");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String template_info ="Description: "+mainModel.getTemplateDescription()+"\n";
				JOptionPane.showMessageDialog(null, template_info);
				//TODO show alert with template information
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(29)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnNewButton)
							.addContainerGap())
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_contentPane.createSequentialGroup()
								.addComponent(lblVariablesInXml)
								.addContainerGap())
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
									.addContainerGap())
								.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
									.addComponent(checkRecursive)
									.addPreferredGap(ComponentPlacement.RELATED, 129, Short.MAX_VALUE)
									.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
									.addGap(12)
									.addComponent(btnGenerate, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))
								.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
									.addComponent(lblTemplateSource, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(sourceTextField, 0, 375, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnTemplateFileChooser, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
									.addContainerGap())
								.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
									.addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(outputTextfield, 0, 385, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnOutputFileChooser, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE))))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_contentPane.createSequentialGroup()
								.addGap(2)
								.addComponent(lblTemplateSource, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_contentPane.createSequentialGroup()
								.addGap(1)
								.addComponent(btnTemplateFileChooser)))
						.addComponent(sourceTextField, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnNewButton)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblVariablesInXml)
					.addGap(18)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 270, GroupLayout.PREFERRED_SIZE)
					.addGap(12)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(outputTextfield, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(1)
							.addComponent(btnOutputFileChooser)))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnCancel)
						.addComponent(btnGenerate)
						.addComponent(checkRecursive))
					.addGap(8))
		);
		contentPane.setLayout(gl_contentPane);
	}



	private void saveComboContent(String t){
			if(new File(t).exists()) {
				//save data
			}
	}
	private void asyncProcess() {

		pb = new ProgressBar(this.proj.getTotal2Process());
		swingWorker = new SwingWorker<Integer, Void>(){


			@Override
			protected void done() {
				System.out.println("Done!");
				pb.setVisible(false);
				if(proj.getErrors().isEmpty())
					JOptionPane.showMessageDialog(null, "Code successfully generated");

				dispose();
			}

			@Override
			protected Integer doInBackground() throws Exception {
			    pb.setVisible(true);
				System.out.println("doInBackground...");
				try {
					proj.process();
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

	@Override
	public void updateProgress(int done) {
		System.out.println("done "+done);
		this.pb.progressBar.setValue(done);
	}
}
