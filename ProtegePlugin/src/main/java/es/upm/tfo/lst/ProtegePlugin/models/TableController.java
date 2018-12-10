package es.upm.tfo.lst.ProtegePlugin.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;





public class TableController extends AbstractTableModel {
	private List <Variable> arrayVars= new ArrayList<>();
	private String  columns [] = {"Name","Required","Default"};

	public TableController() {
		this.arrayVars.add(new Variable("OntologyCount", false, "/defaultRoute"));
		this.arrayVars.add(new Variable("DirectoryOutput", true, "/"));
		this.arrayVars.add(new Variable("imgResPath", false, "/output"));
		this.arrayVars.add(new Variable("templatesAuxiliarDirectory", true, ""));
		this.arrayVars.add(new Variable("exampleVariable1", true, "/CodeGenerator/testExample"));
	}
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return this.arrayVars.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columns.length;
	}
	
    
	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return this.columns[column];
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String retorno="";
		Variable v = this.arrayVars.get(rowIndex);
		switch (columnIndex) {
			case 0:{
				retorno =v.getName();
				break;
			}
			case 1:{
				retorno=String.valueOf(v.isRequired());
				break;
			}
			case 2:{
				retorno = v.getDefaultValue();
				break;
			}
		}
		return retorno;
	}
    
}
