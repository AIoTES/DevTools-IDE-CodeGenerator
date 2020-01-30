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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.JOptionPane;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;

public class DeployXmlMenu extends ProtegeOWLAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private GenerationConfiguration sxf;

	@Override
	public void initialise() throws Exception {
		// Nothing to do.

	}

	@Override
	public void dispose() throws Exception {
		sxf.dispose();
		sxf = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {

			OWLModelManager owlModelManager = getOWLModelManager();
			sxf = new GenerationConfiguration(owlModelManager);
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					sxf.setVisible(true);
				}
			});
		} catch (Exception e1) {
			String mainMessage = "Message: " + e1.getMessage()
                    + "\nStackTrace: " + Arrays.toString(e1.getStackTrace());
            String title = e1.getClass().getName();
            JOptionPane.showMessageDialog(null, mainMessage, title, JOptionPane.ERROR_MESSAGE);
		}

	}

}
