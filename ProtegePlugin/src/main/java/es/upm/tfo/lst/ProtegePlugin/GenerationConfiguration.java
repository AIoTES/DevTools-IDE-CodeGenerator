package es.upm.tfo.lst.ProtegePlugin;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import es.upm.tfo.lst.ProtegePlugin.models.TableController;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
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

	/**
	 * Create the frame.
	 */
	public GenerationConfiguration() {
		setBackground(Color.LIGHT_GRAY);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 563, 355);
		contentPane = new JPanel();
		//contentPane.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE)
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		variableTable = new JTable();
		variableTable.setModel(new TableController());

		sourceTextField = new JTextField();
		sourceTextField.setColumns(10);

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
