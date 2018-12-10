package es.upm.tfo.lst.ProtegePlugin;

import java.awt.event.ActionEvent;

import org.protege.editor.owl.ui.action.ProtegeOWLAction;

public class DeployXmlMenu extends ProtegeOWLAction {

	@Override
	public void initialise() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SetXmlFile sxf = new SetXmlFile();
		sxf.main();
		
	}

}
