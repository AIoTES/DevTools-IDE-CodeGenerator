package es.upm.tfo.lst.ProtegePlugin;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SetXmlFile extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SetXmlFile frame = new SetXmlFile();
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
	public SetXmlFile() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(215, 91, 223, 36);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblXmlCoordinatorPath = new JLabel("XML coordinator path");
		lblXmlCoordinatorPath.setBounds(28, 95, 169, 26);
		contentPane.add(lblXmlCoordinatorPath);
		
		JButton btnSiguiente = new JButton("Siguiente");
		btnSiguiente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(textField.getText().equals("")) {
					JOptionPane.showMessageDialog(null, " empty path not allowed");
				}else {
					VariablesManage vm = new VariablesManage();
					vm.main();
				}
			}
		});
		btnSiguiente.setBounds(305, 160, 110, 25);
		contentPane.add(btnSiguiente);
	}

}
