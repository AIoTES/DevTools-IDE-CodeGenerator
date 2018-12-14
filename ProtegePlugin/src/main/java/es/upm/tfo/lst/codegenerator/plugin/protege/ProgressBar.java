package es.upm.tfo.lst.codegenerator.plugin.protege;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.lang.reflect.GenericArrayType;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import es.upm.tfo.lst.CodeGenerator.GenerateProject;

public class ProgressBar extends JFrame {

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public ProgressBar() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 463, 199);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JProgressBar progressBar = new JProgressBar();

		progressBar.setMinimum(0);
        progressBar.setMaximum(100);
		progressBar.setStringPainted(true);

		JLabel lblGeneratingCode = new JLabel("Generating code");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(37)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblGeneratingCode)
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 353, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(63, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(28)
					.addComponent(lblGeneratingCode)
					.addGap(18)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(185, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}





}
