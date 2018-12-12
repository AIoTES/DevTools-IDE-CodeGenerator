package es.upm.tfo.lst.codegenerator.plugin.protege;

import java.awt.event.ActionEvent;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;

public class DeployXmlMenu extends ProtegeOWLAction {

	private GenerationConfiguration sxf;

	@Override
	public void initialise() throws Exception {
		// TODO Auto-generated method stub

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
			sxf.setVisible(true);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

}
