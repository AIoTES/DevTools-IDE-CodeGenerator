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
package es.upm.tfo.lst.codegenerator.plugin.protege.models;

import java.util.Map;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import es.upm.tfo.lst.CodeGenerator.GenerateProject;
import es.upm.tfo.lst.CodeGenerator.model.TemplateDataModel;
import es.upm.tfo.lst.CodeGenerator.model.Variable;

/**
 * @author amedrano
 *
 */
public class CodeGenerationVariableTable implements TableModel {

	private GenerateProject project;

	static public String  COLS [] = {"Name","Desciption", "Required","Value"};
/**
 * 
 * @param proj {@link GenerateProject}
 */
	public CodeGenerationVariableTable(GenerateProject proj) {
		project = proj;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		
		return project.getMainModel().getArrayVars().size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return COLS.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Map<String, Variable> vars = project.getVariablesArray();
		String varname = (String) vars.keySet().toArray()[rowIndex];
		switch (columnIndex) {
		case 0:
			return varname;
		case 1:
			return vars.get(varname).getDescription();
		case 2:
			return vars.get(varname).isRequired();
		case 3:
			return vars.get(varname).getValue();
		default:
			break;
		}
		return null;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return COLS[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 2) {
			// required
			return Boolean.class;
		}
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		return columnIndex == 3;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		
		if (columnIndex == 3) {
			project.setVariable(getValueAt(rowIndex, 0).toString(),getValueAt(rowIndex, 1).toString(), aValue.toString());
		}
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		// No background changes

	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		// No background changes

	}
	
	public void initProject(TemplateDataModel model) {
		this.project = new GenerateProject(model);
		
	}

}
