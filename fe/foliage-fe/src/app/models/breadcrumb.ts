export type BreadcrumbNode = {
	icon?: string,
	label?: string,
	title?: string,
	url: string[]
};
export class BreadcrumbModel {
	public nodes: BreadcrumbNode[];
	public label?: string;
	public icon?: string;
	public title?: string;
	constructor(nodes: BreadcrumbNode[] = [], label: (string|undefined) = undefined, icon: (string|undefined) = undefined) {
		this.nodes = nodes;
		this.label = label;
		this.icon = icon;
	}
	public concat(currUrl: string[], nextTitle: string) : BreadcrumbModel {
		return new BreadcrumbModel(
			[
				...this.nodes, 
				{
					label: this.label,
					url: currUrl
				}
			],
			nextTitle
		);
	}
}