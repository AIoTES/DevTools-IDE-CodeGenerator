package es.upm.tfo.lst.codegenerator.plugin.protege;
/*
 * 
 * 
 * ut
 * comprobar que la ruta de los ficheros no esten fuera de la ruta de outp
 * 
 * */

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import es.upm.tfo.lst.CodeGenerator.GenerateProject;
import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.xmlparser.XmlParser;
import es.upm.tfo.lst.codegenerator.plugin.protege.models.CodeGenerationVariableTable;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.LayoutStyle.ComponentPlacement;

public class GenerationConfiguration extends JFrame {

	private JPanel contentPane;
	private JTable variableTable;
	private JTextField sourceTextField;
	private JTextField outputTextfield;
	private TableModel generateTable;
	private GenerateProject proj;

	/**
	 * Create the frame.
	 */
	public GenerationConfiguration() {
		setBackground(Color.LIGHT_GRAY);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 563, 355);
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
		
		//variableTable.setModel(generateTable);

		sourceTextField = new JTextField();
		sourceTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						System.out.println("entered");
						XmlParser parser = new XmlParser();
						parser.generateXMLCoordinator(sourceTextField.getText().toString());
						TemplateDataModel mainModel = parser.getXmlCoordinatorDataModel();
						System.out.println("main model "+mainModel.toString());
						proj = new GenerateProject(mainModel);
						generateTable = new CodeGenerationVariableTable(proj);
						variableTable.setModel(generateTable);				
						variableTable.repaint();
					}
				});
			}
		});
	
		sourceTextField.setColumns(10);
		//variableTable.setModel(generateTable);
		outputTextfield = new JTextField();
		outputTextfield.setColumns(10);

		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(sourceTextField.getText().equals("") || sourceTextField.getText().equals("")) {
					JOptionPane.showMessageDialog(null, " empty path not allowed");
				}else {
					JOptionPane.showMessageDialog(null, "Generating source code ...");
				}
			}
		});

		JLabel lblTemplateSource = new JLabel("Template source");

		JLabel lblOutput = new JLabel("Output");

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});

		JScrollPane scrollPane = new JScrollPane(variableTable);

		JButton btnTemplateFileChooser = new JButton("...");
		btnTemplateFileChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(sourceTextField.getText());
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
		            sourceTextField.setText(file.getAbsolutePath());
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
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addGap(25)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
							.addComponent(lblTemplateSource, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(sourceTextField, GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
						.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
							.addGap(224)
							.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnGenerate, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
							.addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(outputTextfield, GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnTemplateFileChooser, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnOutputFileChooser, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTemplateSource, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addComponent(sourceTextField, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnTemplateFileChooser))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(outputTextfield, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnOutputFileChooser))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnGenerate, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
	}
}
