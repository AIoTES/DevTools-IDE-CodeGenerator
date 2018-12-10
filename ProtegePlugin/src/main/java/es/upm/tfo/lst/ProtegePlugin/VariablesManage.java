package es.upm.tfo.lst.ProtegePlugin;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import es.upm.tfo.lst.ProtegePlugin.models.TableController;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.JTable;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class VariablesManage extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VariablesManage frame = new VariablesManage();
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
	public VariablesManage() {
		setBackground(Color.LIGHT_GRAY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		//contentPane.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE)
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		table = new JTable();
		table.setModel(new TableController());
		table.setBounds(30, 30, 408, 113);
		contentPane.add(table);
		
		textField = new JTextField();
		textField.setBounds(215, 155, 223, 28);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(215, 194, 223, 28);
		contentPane.add(textField_1);
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(textField.getText().equals("") || textField.getText().equals("")) {
					JOptionPane.showMessageDialog(null, " empty path not allowed");
				}else {
					JOptionPane.showMessageDialog(null, "Generating source code ...");
				}
			}
		});
		btnGenerate.setBounds(328, 234, 110, 25);
		contentPane.add(btnGenerate);
		
		JLabel lblTemplateSource = new JLabel("Template source");
		lblTemplateSource.setBounds(40, 161, 138, 22);
		contentPane.add(lblTemplateSource);
		
		JLabel lblOutput = new JLabel("Output");
		lblOutput.setBounds(50, 200, 110, 22);
		contentPane.add(lblOutput);
	}
}
