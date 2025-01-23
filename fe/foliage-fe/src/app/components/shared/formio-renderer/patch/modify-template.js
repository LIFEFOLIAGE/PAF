import panel from './panel';
import field from './field';
//import file from './file';

export function modifyTemplate(template) {
	template.panel = panel;
	template.field.form = field.form;
	//template.file = file;
}