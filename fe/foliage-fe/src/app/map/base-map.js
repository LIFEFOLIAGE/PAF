import {pointerMove, singleClick} from 'ol/events/condition';
//import {MapLayerType} from "src/app/gis-table/gis-table.component";
//import { get as getProjection } from 'ol/proj';
//import VectorSource from 'ol/source/Vector';
//import VectorLayer from 'ol/layer/Vector';
//import { Select, defaults as defaultInteractions } from 'ol/interaction';
//import {Style, Stroke, Text, Fill } from 'ol/style';
//import  { Map, Collection, Feature, Overlay, View } from 'ol';

import {Map as olMap, Collection as olCollection, Feature as olFeature, Overlay as olOverlay, View as olView, Feature} from 'ol';

import Legend from "ol-ext/legend/Legend";
import FillPattern from "ol-ext/style/FillPattern";
import {
	Style as olStyleStyle,
	Circle as olStyleCircle,
	Fill as olStyleFill,
	Stroke as olStyleStroke,
	Text as olStyleText, Style, RegularShape
} from 'ol/style';
import {Tile as olLayerTile, Vector as olLayerVector} from 'ol/layer';
import {Vector as olSourceVector, XYZ as olSourceXYZ} from 'ol/source';
import {ScaleLine, defaults as olControldefaults} from 'ol/control';

import {
	containsExtent as extentcontainsExtent,
	clone as extentclone,
	scaleFromCenter as extentscaleFromCenter,
	extend as extentExtend
} from 'ol/extent';

//import * as olLayerGroup from 'ol/layer/Group';

import GeoJSON from 'ol/format/GeoJSON';
import GML3 from 'ol/format/GML3';
import OSM from 'ol/source/OSM';

import TileWMS from 'ol/source/TileWMS';
import {createXYZ} from 'ol/tilegrid';
import {bbox, tile} from 'ol/loadingstrategy';

import MapBrowserEventType from 'ol/MapBrowserEventType';
//import * as olProj from 'ol/proj';
import {Select as olInteractionSelect, defaults as olInteractiondefaults} from 'ol/interaction';
//import TileLayer from 'ol/layer/Tile';

//import XYZ from 'ol/source/XYZ';

import WKT from 'ol/format/WKT';

import LayerGroup from 'ol/layer/Group';
import LayerSwitcher from 'ol-ext/control/LayerSwitcher';

import {
	Point,
	LineString,
	LinearRing,
	Polygon,
	MultiPoint,
	MultiLineString,
	MultiPolygon,
	GeometryCollection
} from "ol/geom";

import proj4 from 'proj4';

import {register} from 'ol/proj/proj4';
import VectorSource from "ol/source/Vector";
import VectorImageLayer from 'ol/layer/VectorImage';
import {Layer} from "ol/layer";
import WebGLVectorLayerRenderer from "ol/renderer/webgl/VectorLayer";
import {asArray} from "ol/color";
import {DEVICE_PIXEL_RATIO} from "ol/has";

import {indiceLayers} from "./base-map-layers";
import {map} from 'rxjs';
import { environment } from 'src/environments/environment';
import { rejects } from 'assert';

import { AreaConstraintControl } from './area-constraint';
//import { geom } from 'openlayers';


const format = new WKT();

Object.entries(environment.srids).forEach(
	([srid, def]) => {
		proj4.defs(srid, def);
	}
);
register(proj4);

const viewSrid = environment.mapsSrid;

const vectorBuilder = (feats) => new olSourceVector({wrapX: false, features: feats});



const larghezzaStrada = 8;
const spessoreBordoPoligono = 2;

export function hexToRGB(hex, alpha) {
	let r = parseInt(hex.slice(1, 3), 16),
		g = parseInt(hex.slice(3, 5), 16),
		b = parseInt(hex.slice(5, 7), 16);

	if (alpha) {
		return "rgba(" + r + ", " + g + ", " + b + ", " + alpha + ")";
	} else {
		return "rgb(" + r + ", " + g + ", " + b + ")";
	}
}


function getRandomColor(str) {
	let hash = 0;
	if (str.length === 0) return hash;
	for (let i = 0; i < str.length; i++) {
		hash = str.charCodeAt(i) + ((hash << 5) - hash);
		hash = hash & hash;
	}
	let color = '#';
	for (let i = 0; i < 3; i++) {
		let value = (hash >> (i * 8)) & 255;
		color += ('00' + value.toString(16)).substr(-2);
	}
	return color;
}

function createStripedPattern(lineWidth, spacing, slope, color, backgroundColor) {
	const can = document.createElement('canvas');
	const len = Math.hypot(1, slope);

	const w = can.width = 1 / len + spacing + 0.5 | 0; // round to nearest pixel
	const h = can.height = slope / len + spacing * slope + 0.5 | 0;

	const ctx = can.getContext('2d');

	ctx.fillStyle = backgroundColor;
	ctx.fillRect(0, 0, can.width, can.height);

	ctx.strokeStyle = color;
	ctx.lineWidth = lineWidth;
	ctx.beginPath();

	// Line through top left and bottom right corners
	ctx.moveTo(0, 0);
	ctx.lineTo(w, h);
	// Line through top right corner to add missing pixels
	ctx.moveTo(0, -h);
	ctx.lineTo(w * 2, h);
	// Line through bottom left corner to add missing pixels
	ctx.moveTo(-w, 0);
	ctx.lineTo(w, h * 2);

	ctx.stroke();
	return ctx.createPattern(can, 'repeat');
}

function createStripedPatterTrattegg(lineWidth, spacing, slope, color, backgroundColor) {
	const can = document.createElement('canvas');
	const len = Math.hypot(1, slope);

	const w = can.width = 1 / len + spacing + 0.5 | 0; // round to nearest pixel
	const h = can.height = slope / len + spacing * slope + 0.5 | 0;

	const ctx = can.getContext('2d');

	ctx.fillStyle = backgroundColor;
	ctx.fillRect(0, 0, can.width, can.height);

	ctx.strokeStyle = color;
	ctx.lineWidth = lineWidth;
	ctx.lineCap = "butt";
	ctx.beginPath();

	ctx.moveTo(w * 0.2, h * 0.2);
	ctx.lineTo(w * 0.8, h * 0.8);

	ctx.stroke();
	return ctx.createPattern(can, 'repeat');
}

export function stripeStyle(borderColor, backgroundColor, stripeColor) {
	return new olStyleStyle({
		stroke: new olStyleStroke({
			color: borderColor,
			width: spessoreBordoPoligono,
		}),
		fill: new olStyleFill({
			color: createStripedPattern(10, 20, 3, stripeColor, backgroundColor)
		})
	});
}

export function stripeStyleTrattegg(borderColor, backgroundColor, stripeColor) {
	return new olStyleStyle({
		stroke: new olStyleStroke({
			color: borderColor,
			width: spessoreBordoPoligono,
		}),
		fill: new olStyleFill({
			color: createStripedPatterTrattegg(10, 20, 3, stripeColor, backgroundColor)
		})
	});
}

export function bordoTratteggiatoStripeStyle(borderColor, backgroundColor, stripeColor) {
	return new olStyleStyle({
		stroke: new olStyleStroke({
			color: borderColor,
			width: spessoreBordoPoligono,
			lineDash: [4, 8],
			lineDashOffset: 6
		}),
		fill: new olStyleFill({
			color: createStripedPattern(10, 20, 3, stripeColor, backgroundColor)
		})
	});
}

export function squareStyle(borderColor, backgroundColor, squareColor) {
	const paddingPercent = 0.5;
	const can = document.createElement('canvas');
	const ctx = can.getContext('2d');
	can.width = 50;
	can.height = 50;
	ctx.fillStyle = backgroundColor;
	ctx.fillRect(0, 0, can.width, can.height);

	ctx.fillStyle = squareColor;
	ctx.fillRect(
		can.width * paddingPercent,
		can.height * paddingPercent,
		can.width - (can.width * paddingPercent),
		can.height - (can.height * paddingPercent)
	);

	const canvasResult = ctx.createPattern(can, 'repeat');

	return new olStyleStyle({
		stroke: new olStyleStroke({
			color: borderColor,
			width: spessoreBordoPoligono,
		}),
		fill: new olStyleFill({
			color: canvasResult
		})
	});
}

export function lineeOrizVertStyle(borderColor, backgroundColor, lineColor) {
	const can = document.createElement('canvas');
	const ctx = can.getContext('2d');
	can.width = 50;
	can.height = 50;
	ctx.fillStyle = backgroundColor;
	ctx.fillRect(0, 0, can.width, can.height);

	ctx.lineWidth = 8;
	ctx.strokeStyle = lineColor;

	ctx.beginPath();
	ctx.moveTo(can.width / 2, 0);
	ctx.lineTo(can.width / 2, can.height);
	ctx.moveTo(0, can.height / 2);
	ctx.lineTo(can.width, can.height / 2);
	ctx.stroke();

	const canvasResult = ctx.createPattern(can, 'repeat');

	return new olStyleStyle({
		stroke: new olStyleStroke({
			color: borderColor,
			width: spessoreBordoPoligono,
		}),
		fill: new olStyleFill({
			color: canvasResult
		})
	});
}

export function puntiStyle(borderColor, backgroundColor, circleColor) {
	const can = document.createElement('canvas');
	const ctx = can.getContext('2d');
	can.width = 50;
	can.height = 50;
	ctx.fillStyle = backgroundColor;
	ctx.fillRect(0, 0, can.width, can.height);

	ctx.fillStyle = circleColor;
	ctx.arc(can.width / 2,
		can.height / 2,
		can.width / 4,
		0,
		2 * Math.PI);
	ctx.fill();

	const canvasResult = ctx.createPattern(can, 'repeat');

	return new olStyleStyle({
		stroke: new olStyleStroke({
			color: borderColor,
			width: spessoreBordoPoligono,
		}),
		fill: new olStyleFill({
			color: canvasResult
		})
	});
}

export function triangoliStyle(borderColor, backgroundColor, triangleColor) {
	const padding = 10;

	const can = document.createElement('canvas');
	const ctx = can.getContext('2d');
	can.width = 50;
	can.height = 50;
	ctx.fillStyle = backgroundColor;
	ctx.fillRect(0, 0, can.width, can.height);

	ctx.fillStyle = triangleColor;

	ctx.beginPath();
	ctx.moveTo(padding, can.height - padding);
	ctx.lineTo((can.width / 2), padding);
	ctx.lineTo(can.height - padding, can.width - padding);
	ctx.lineTo(padding, can.height - padding);

	ctx.fill();

	const canvasResult = ctx.createPattern(can, 'repeat');

	return new olStyleStyle({
		stroke: new olStyleStroke({
			color: borderColor,
			width: spessoreBordoPoligono,
		}),
		fill: new olStyleFill({
			color: canvasResult
		})
	});
}

export function lineeOrizStyle(borderColor, backgroundColor, lineColor) {
	const can = document.createElement('canvas');
	const ctx = can.getContext('2d');
	can.width = 50;
	can.height = 50;
	ctx.fillStyle = backgroundColor;
	ctx.fillRect(0, 0, can.width, can.height);

	ctx.lineWidth = 8;
	ctx.strokeStyle = lineColor;

	ctx.beginPath();
	ctx.moveTo(0, can.height / 2);
	ctx.lineTo(can.width, can.height / 2);
	ctx.stroke();

	const canvasResult = ctx.createPattern(can, 'repeat');

	return new olStyleStyle({
		stroke: new olStyleStroke({
			color: borderColor,
			width: spessoreBordoPoligono,
		}),
		fill: new olStyleFill({
			color: canvasResult
		})
	});
}


//costa mare
export const style501 = new olStyleStyle(
	{
		stroke: new olStyleStroke({
			color: '#0b2078',
			width: spessoreBordoPoligono,
		}),
		fill: new olStyleFill({
			color: '#304fd4'
		})
	}
);

//costa laghi
export const style502 = style501;

//linea blu
export const style503 = new olStyleStyle(
	{
		stroke: new olStyleStroke({
			color: '#213795FF',
			width: larghezzaStrada,
		})
	}
);


// poligono senza riempimento e con bordo blu
export const style504 = new olStyleStyle(
	{
		stroke: new olStyleStroke({
			color: '#213795FF',
			width: spessoreBordoPoligono,
		})
	}
);

export const style505 = stripeStyle('#964b00ff', 'transparent', '#964b007f');

// Poligono trasparente con bordo tratteggiato arancione. Il riempimento è a linee oblique.
export const style506 = new olStyleStyle(
	{
		stroke: new olStyleStroke({
			color: '#ff8000',
			width: spessoreBordoPoligono,
			lineDash: [4, 8],
			lineDashOffset: 6
		}),
		fill: new FillPattern({
			pattern: "hatch",
			ratio: 1,
			color: 'rgba(255,130,0,0.5)',
			offset: 3,
			scale: 1,
			size: 5,
			spacing: 10,
			angle: -45
		})
	}
);

// polygon con bordo blu, riempimento blu con dei quadrati
export const style507 = new olStyleStyle(
	{
		stroke: new olStyleStroke({
			color: '#213795',
			width: spessoreBordoPoligono,
		}),
		fill: new FillPattern({
			pattern: "tile",
			image: undefined,
			ratio: 1,
			icon: undefined,
			color: "rgb(14,24,71)",
			offset: 0,
			scale: 1,
			fill: new olStyleFill({color: "rgb(47,76,203)"}),
			size: 8,
			spacing: 16,
			angle: 0
		})
	}
);


// linee viola
export const style508 = new olStyleStyle(
	{
		stroke: new olStyleStroke({
			color: '#891fd5',
			width: larghezzaStrada,
		})
	}
);

// poligono viola, riempimento a linee orizzontali e verticali
// export const style509 = lineeOrizVertStyle('#891fd5ff', 'transparent', '#891fd57f');
export const style509 = lineeOrizVertStyle('#891fd500', '#891fd5', '#310b4c');// TODO: test no alpha

// poligono viola, riempimento a punti
// export const style510 = puntiStyle('#891fd5ff', 'transparent', '#891fd57f');
export const style510 = puntiStyle('#891fd500', '#891fd5', '#310b4c');// TODO: test no alpha

// punti viola TODO
export const style511 = new olStyleStyle({
	image: new olStyleCircle({
		radius: 10,
		fill: new olStyleFill({
			color: '#891FD5FF',
		}),
	})
});

// poligono vuoto con bordo viola
export const style512 = new olStyleStyle(
	{
		stroke: new olStyleStroke({
			color: '#891fd5',
			width: spessoreBordoPoligono,
		})
	}
);

// Triangoli marroni TODO da testare
export const style513 = new olStyleStyle(
	{
		image: new RegularShape({
			fill: new olStyleFill({color: '#964b00ff'}),
			stroke: new olStyleStroke({color: '#964b00ff', width: 2}),
			points: 3,
			radius: 10,
			// rotation: Math.PI / 4,
			angle: 0,
		})
	}
);


// poligono rosso, riempimento puntinato
// export const style514 = puntiStyle('#BF0909FF', 'transparent', '#BF09097f');
export const style514 = puntiStyle('#BF090900', '#BF0909', '#520505');// TODO: test no alpha


//TODO: Triangoli verde TODO da testare
export const style515 = new olStyleStyle(
	{
		image: new RegularShape({
			fill: new olStyleFill({color: '#114c04'}),
			stroke: new olStyleStroke({color: '#114c04', width: 2}),
			points: 3,
			radius: 10,
			// rotation: Math.PI / 4,
			angle: 0,
		})
	}
);

// poligono verde, riempimento puntinato
// export const style516 = puntiStyle('#2ABF09FF', 'transparent', '#2ABF097F');
export const style516 = puntiStyle('#2ABF09FF', '#2ABF09FF', '#114c04');// TODO: test no alpha

export const style517 = lineeOrizStyle('#2ABF0900', '#2abf09', '#124e04'); // TODO: test no alpha

//poligono rosso, riempimento quadrettato
// export const style518 = squareStyle('#d00303ff', '#D003031A', '#D0030380');
export const style518 = new olStyleStyle(
	{
		fill: new FillPattern({
			pattern: "tile",
			image: undefined,
			ratio: 1,
			icon: undefined,
			color: "#D00303ff",
			offset: 0,
			scale: 1,
			size: 8,
			spacing: 16,
			angle: 0
		})
	}
);


//poligono rosso, riempimento con linee tratteggiate oblique
// export const style519 = stripeStyleTrattegg('#d00303ff', '#D003031A', '#D0030380');
export const style519 = stripeStyleTrattegg('#d0030300', '#D00303ff', '#440202');// TODO: test no alpha


export const layerRilevamentiStyle = new olStyleStyle(
	{
		stroke: new olStyleStroke({
			color: 'magenta',
			width: 4
		}),
		fill: new olStyleFill({
			color: hexToRGB('#FF0000', 0.3)
		})
	}
);;



const layerRilevamentiStyleFun = (feature) => {
	const dati = feature.get('dati');
	const tipoGeometria = dati.tipoGeometria;
	const label = feature.get('lab');
	const text = (label == undefined) ? '' : label;
	let outVal = undefined;
	switch (tipoGeometria) {
		case "PUNTO": {
			outVal = new olStyleStyle(
				{
					image: new RegularShape(
						// {
						// 	radius: 9,
						// 	overflow: true,
						// 	fill: new olStyleFill(
						// 		{
						// 			color: 'black',
						// 		}
						// 	),
						// 	// stroke: new olStyleStroke({
						// 	// 	color: 'red', width: 2
						// 	// })
						// }
						{
							radius: 9,
							points: 4,
							angle: Math.PI / 4,
							overflow: true,
							fill: new olStyleFill(
								{
									color: 'magenta',
								}
							)
						}
					),
					text: new olStyleText({
						font: '12px Calibri,sans-serif',
						fill: new olStyleFill({color: '#000000'}),
						stroke: new olStyleStroke({
							color: '#FFFFFF', width: 2
						}),
						//offsetX: 5,
						offsetY: 5,
						overflow: true,
						text: 'RIL:'+ text
					})
				}
			);
		}; break;
		case "POLIGONO": {
			outVal = new olStyleStyle(
				{
					stroke: new olStyleStroke({
						color: 'magenta',
						width: 4
					}),
					fill: new olStyleFill({
						color: hexToRGB('#FF0000', 0.3)
					}),
					text: new olStyleText({
						font: '12px Calibri,sans-serif',
						fill: new olStyleFill({color: '#000000'}),
						stroke: new olStyleStroke({
							color: '#FFFFFF', width: 2
						}),
						// offsetX: 5,
						// offsetY: 5,
						overflow: true,
						text: 'RIL:'+ text
					})
				}
			);
		}; break;
		default: {
			outVal = new olStyleStyle(
				{
					stroke: new olStyleStroke(
						{
							color: 'magenta',
							width: 4
						}
					),
					text: new olStyleText({
						font: '12px Calibri,sans-serif',
						fill: new olStyleFill({color: '#000000'}),
						stroke: new olStyleStroke({
							color: '#FFFFFF', width: 2
						}),
						// offsetX: 5,
						// offsetY: 5,
						overflow: true,
						text: 'RIL:'+ text
					})
				}
			);
		}; break;
	}
	return outVal;
}


export const layerPforStyle = new olStyleStyle(
	{
		fill: new olStyleFill({
			color: hexToRGB('#FFFFFF', 0.2)
		}),
		stroke: new olStyleStroke({
			color: '#000000',
			width: 4
		})
	}
);

export const layerUoStyleOpts = {
	fill: new olStyleFill({
		color: hexToRGB('#FFFFFF', 0.2)
	}),
	stroke: new olStyleStroke({
		color: 'orange',
		width: 2
	})
};
export const layerUoStyle = new olStyleStyle(layerUoStyleOpts);


const layerUoStyleFun = (feature) => {

	const label = feature.get('lab');
	const text = (label == undefined) ? '' : 'UO:' + label;
	return (text == undefined)
		? new olStyleStyle(layerUoStyleOpts)
		: new olStyleStyle(
			{
				...layerUoStyleOpts,
				text: new olStyleText({
					font: '12px Calibri,sans-serif',
					fill: new olStyleFill({color: '#000000'}),
					stroke: new olStyleStroke({
						color: '#FFFFFF', width: 2
					}),
					// offsetX: 5,
					// offsetY: 5,
					overflow: true,
					text: text
				})
			}
		);
}


const layerAstStyleOpts =  {
	image: new olStyleCircle(
		{
			radius: 7,
			overflow: true,
			fill: new olStyleFill(
				{
					color: 'orange',
				}
			),
			// stroke: new olStyleStroke({
			// 	color: 'orange', width: 2
			// })
		}
	)
};

const layerAdStyleOpts =  {
	image: new olStyleCircle(
		{
			radius: 9,
			overflow: true,
			fill: new olStyleFill(
				{
					color: 'red',
				}
			),
			// stroke: new olStyleStroke({
			// 	color: 'red', width: 2
			// })
		}
	)
};

const layerImpStyleOpts =  {
	image: new RegularShape(
		{
			radius: 6,
			points: 4,
			angle: Math.PI / 4,
			overflow: true,
			fill: new olStyleFill(
				{
					color: 'yellow',
				}
			),
			// stroke: new olStyleStroke({
			// 	color: 'black', width: 2
			// })
		}
	)
};

const layerAsrStyleOpts =  {
	image: new RegularShape(
		{
			radius: 9,
			overflow: true,
			fill: new olStyleFill(
				{
					color: 'blue',
				}
			)
		}
	)
};


const layerAstStyle = new olStyleStyle(layerAstStyleOpts);
const layerAdStyle = new olStyleStyle(layerAdStyleOpts);
const layerImpStyle = new olStyleStyle(layerImpStyleOpts);
const layerAsrStyle = new olStyleStyle(layerAsrStyleOpts);

const layerAstStyleFun = (feature) => new olStyleStyle(
	{
		...layerAstStyleOpts,
		text: new olStyleText({
			font: '12px Calibri,sans-serif',
			fill: new olStyleFill({color: '#000000'}),
			stroke: new olStyleStroke({
				color: '#FFFFFF', width: 2
			}),
			offsetX: 5,
			offsetY: 15,
			overflow: true,
			text: 'AST:'+ feature.get('dati').nomeArea
		})
	}
);

const layerAdStyleFun = (feature) => new olStyleStyle(
	{
		...layerAdStyleOpts,
		text: new olStyleText({
			font: '12px Calibri,sans-serif',
			fill: new olStyleFill({color: '#000000'}),
			stroke: new olStyleStroke({
				color: '#FFFFFF', width: 2
			}),
			offsetX: 5,
			offsetY: 25,
			overflow: true,
			text: 'Ad:'+ feature.get('dati').nomeArea
		})
	}
);

const layerImpStyleFun = (feature) => new olStyleStyle(
	{
		...layerImpStyleOpts,
		text: new olStyleText({
			font: '12px Calibri,sans-serif',
			fill: new olStyleFill({color: '#000000'}),
			stroke: new olStyleStroke({
				color: '#FFFFFF', width: 2
			}),
			offsetX: 5,
			offsetY: -15,
			overflow: true,
			text: 'Imp:'+ feature.get('dati').nomeArea
		})
	}
);

const layerAsrStyleFun = (feature) => new olStyleStyle(
	{
		...layerAsrStyle,
		text: new olStyleText({
			font: '12px Calibri,sans-serif',
			fill: new olStyleFill({color: '#000000'}),
			stroke: new olStyleStroke({
				color: '#FFFFFF', width: 2
			}),
			offsetX: 5,
			offsetY: -15,
			overflow: true,
			text: 'Imp:'+ feature.get('dati').nomeArea
		})
	}
);

const layerAltrStyleFun = (feature) => {
	const dati = feature.get('dati');
	if (dati.isAreaDimostrativa) {
		return layerAdStyleFun(feature);
	}
	else {
		if (dati.isAreaTradizionale) {
			return layerAstStyleFun(feature);
		}
		else {
			if (dati.isImposto) {
				return layerImpStyleFun(feature);
			}
			else {
				if (dati.isAreaRelascopica) {
					return layerAsrStyleFun(feature);
				}
			}
		}
	}

}


const layerViabStyleFun = (feature) => {
	const dati = feature.get('dati');
	const codTipoViabilita = dati.codTipoViabilita;
	let strokeIn = undefined;
	let strokeOut = undefined;
	switch (codTipoViabilita) {
		case "1": {
			strokeIn = new olStyleStroke({
					color: 'red',
					width: 2
				}
			);
			strokeOut = new olStyleStroke({
					color: 'black',
					width: 6
				}
			);
		}; break;
		case "2": {
			strokeIn = new olStyleStroke({
					color: 'yellow',
					width: 2
				}
			);
			strokeOut = new olStyleStroke({
					color: 'black',
					width: 6
				}
			);
		}; break;
		case "3": {
			strokeIn = new olStyleStroke({
					color: 'white',
					width: 2
				}
			);
			strokeOut = new olStyleStroke({
					color: 'red',
					width: 6
				}
			);
		}; break;
		case "4": {
			strokeIn = new olStyleStroke({
					color: 'blue',
					lineDash: [5],
					width: 4
				}
			);
		}; break;
	}
	if (strokeOut == undefined)  {
		return new olStyleStyle({stroke: strokeIn});
	}
	else {
		const outVal = [
			new olStyleStyle({stroke: strokeOut}),
			new olStyleStyle({stroke: strokeIn})
		];
		return outVal;
	}
}




// @formatter:off
//TODO: verificare gli undefined - messi a mano
const mappedStyles = {
	PFOR:                       		    { s: layerPforStyle,    g: 'LineString'},
	ACQUE_PUBBLICHE:                        { s: style503,    g: 'LineString'},
	ACQUE_PUBBLICHE_RISPETTO:               { s: style504,    g: 'Polygon'},
	ALTIMETRIA_1200:                        { s: style505,    g: 'Polygon'},
	AREE_PROTETTE:                          { s: undefined,   g: 'Polygon'},
	BOSCHI:                                 { s: style517,    g: 'Polygon'},
	COSTA_LAGHI:                            { s: style502,    g: 'Polygon'},
	COSTA_MARE:                             { s: style501,    g: 'Polygon'},
	DECRETI_ARCHEOLOGICI:                   { s: style514,    g: 'Polygon'},
	EX_1497_AB:                             { s: style518, g: 'Polygon'},
	EX_1497_CD:                             { s: style519,    g: 'Polygon'},
	GEOMORFOLOGICI_TIPIZZATI:               { s: style515,    g: 'Point'},
	HABITAT:                                { s: undefined,   g: 'Polygon'},
	LINEE_ARCHEOLOGICHE:                    { s: style508,    g: 'LineString'},
	NAT2K:                                  { s: undefined,   g: 'Polygon'},
	PAI_RISCHIO_ALLUVIONE:                  { s: undefined,   g: 'Polygon'},
	PAI_RISCHIO_FRANA:                      { s: undefined,   g: 'Polygon'},
	PAI_RISCHIO_VALANGA:                    { s: undefined,   g: 'Polygon'},
	PUNTI_ARCHEOLOGICI:                     { s: style511,    g: 'Point'},
	PUNTI_ARCHEOLOGICI_TIPIZZATI:           { s: style513,    g: 'Point'},
	RISPETTO_GEOMORFOLOGIA:                 { s: style516,    g: 'Polygon'},
	RISPETTO_LINEE_ARCHEOLOGICHE:           { s: style509,    g: 'Polygon'},
	RISPETTO_LINEE_ARCHEOLOGICHE_TIPIZZATE: { s: style510,    g: 'Polygon'},
	RISPETTO_PUNTI_ARCHEOLOGICI:            { s: style512,    g: 'Polygon'},
	USI_CIVICI:                             { s: style506, g: 'Polygon'},
	ZONE_UMIDE:                             { s: style507, g: 'Polygon'},
	AREE_PAESAGGISTICHE:                    { s: undefined, g: 'Polygon'}
};
// @formatter:on

const colorStyles = {};

export function getLayerStyle(layerCode) {
	let style = mappedStyles[layerCode]?.s;
	if (style == undefined) {
		style = colorStyles[layerCode];
		if (style == undefined) {
			const nomeColor = getRandomColor(layerCode);
			style = new olStyleStyle(
				{
					fill: new FillPattern(
						{
							pattern: "hatch",
							ratio: 1,
							color: nomeColor,
							offset: 3,
							scale: 1,
							fill: new olStyleFill({color: hexToRGB(nomeColor, 0.25)}),
							size: 5,
							spacing: 10,
							angle: -45
						}
					)
				}
			);
			colorStyles[layerCode] = style;
			return style;
		}
		else {
			return style;
		}
	}
	else {
		return style;
	}
}


export function getLayerTitleFromStyle(style, type, title) {
	const srcData = Legend.getLegendImage(
		{
			style: style, // todo: mettere lo stile corretto
			typeGeom: type ?? "Polygon", // todo verifica poligon e linee e altro
		}
	).toDataURL();
	return `<img class="h-100" src="${srcData}"/>${title}`;
}




// const layer101AllStyle = getLayerStyle('PAI_RISCHIO_ALLUVIONE');
// const layer101FraStyle = getLayerStyle('PAI_RISCHIO_FRANA');
// const layer101ValStyle = getLayerStyle('PAI_RISCHIO_VALANGA');
// const layer105Style = getLayerStyle('NAT2K');
// const layer106Style = getLayerStyle('AREE_PROTETTE');

// const layer501Style = getLayerStyle('COSTA_MARE');
// const layer502Style = getLayerStyle('COSTA_LAGHI');
// const layer503Style = getLayerStyle('ACQUE_PUBBLICHE');
// const layer504Style = getLayerStyle('ACQUE_PUBBLICHE_RISPETTO');

const layer105Style = getLayerStyle('NAT2K');
const layer106Style = getLayerStyle('AREE_PROTETTE');
const layer101aStyle = getLayerStyle('PAI_RISCHIO_ALLUVIONE');
const layer101fStyle = getLayerStyle('PAI_RISCHIO_FRANA');
const layer101vStyle = getLayerStyle('PAI_RISCHIO_VALANGA');
const layer501Style = getLayerStyle('COSTA_MARE');
const layer502Style = getLayerStyle('COSTA_LAGHI');
const layer503Style = getLayerStyle('ACQUE_PUBBLICHE');
const layer504Style = getLayerStyle('ACQUE_PUBBLICHE_RISPETTO');
const layer505Style = getLayerStyle('ALTIMETRIA_1200');
const layer506Style = getLayerStyle('USI_CIVICI');
const layer507Style = getLayerStyle('ZONE_UMIDE');
const layer508Style = getLayerStyle('LINEE_ARCHEOLOGICHE');
const layer509Style = getLayerStyle('RISPETTO_LINEE_ARCHEOLOGICHE');
const layer510Style = getLayerStyle('RISPETTO_LINEE_ARCHEOLOGICHE_TIPIZZATE');
const layer511Style = getLayerStyle('PUNTI_ARCHEOLOGICI');
const layer512Style = getLayerStyle('RISPETTO_PUNTI_ARCHEOLOGICI');
const layer513Style = getLayerStyle('PUNTI_ARCHEOLOGICI_TIPIZZATI');
const layer514Style = getLayerStyle('DECRETI_ARCHEOLOGICI');
const layer515Style = getLayerStyle('GEOMORFOLOGICI_TIPIZZATI');
const layer516Style = getLayerStyle('RISPETTO_GEOMORFOLOGIA');
const layer517Style = getLayerStyle('BOSCHI');
const layer518Style = getLayerStyle('EX_1497_AB');
const layer519Style = getLayerStyle('EX_1497_CD');
const layer208Style = getLayerStyle('HABITAT');
const layer215Style = getLayerStyle('AREE_PAESAGGISTICHE');

//console.log(environment.layers);
const layerNames = [
	'rilevamenti', 'pfor', 'uo', 'ast', 'ad', 'viab', 'imp', 'altr',
	'osm', 'orto', 'crt', 'catasto',
	//Aree Protette
	'NAT2K', 'AREE_PROTETTE', 'HABITAT',
	 //PAI
	'PAI_RISCHIO_ALLUVIONE', 'PAI_RISCHIO_FRANA', 'PAI_RISCHIO_VALANGA',
	//PPTR Vincoli Paesaggistici
	"PUNTI_ARCHEOLOGICI_TIPIZZATI", "USI_CIVICI", "BOSCHI", "RISPETTO_GEOMORFOLOGIA", "ACQUE_PUBBLICHE", "LINEE_ARCHEOLOGICHE",
	"RISPETTO_LINEE_ARCHEOLOGICHE", "ALTIMETRIA_1200", "ACQUE_PUBBLICHE_RISPETTO", "RISPETTO_LINEE_ARCHEOLOGICHE_TIPIZZATE",
	"RISPETTO_PUNTI_ARCHEOLOGICI", "ZONE_UMIDE", "DECRETI_ARCHEOLOGICI", "GEOMORFOLOGICI_TIPIZZATI", "EX_1497_AB", "EX_1497_CD",
	"COSTA_MARE", "PUNTI_ARCHEOLOGICI", "COSTA_LAGHI",
	//Aree Paesaggistiche
	"AREE_PAESAGGISTICHE"
];

const groups = [
	{
		name: 'base',
		title: 'Sfondo',
		single: true,
		visible: true,
		layers: ['osm', 'orto', 'crt', 'catasto']
	},
	{
		name: 'prot',
		title: 'Aree Protette',
		single: false,
		visible: false,
		layers: [
			'NAT2K', 'AREE_PROTETTE', 'HABITAT'
		]
	},
	{
		name: 'pai',
		title: 'PAI Rischio Idrogeologico',
		single: false,
		visible: false,
		layers: [
			'PAI_RISCHIO_ALLUVIONE', 'PAI_RISCHIO_FRANA', 'PAI_RISCHIO_VALANGA'
		]
	},
	{
		name: 'ptpr',
		title: 'PTPR B',
		single: false,
		visible: false,
		layers: [
			"PUNTI_ARCHEOLOGICI_TIPIZZATI", "USI_CIVICI", "BOSCHI", "RISPETTO_GEOMORFOLOGIA", "ACQUE_PUBBLICHE", "LINEE_ARCHEOLOGICHE",
			"RISPETTO_LINEE_ARCHEOLOGICHE", "ALTIMETRIA_1200", "ACQUE_PUBBLICHE_RISPETTO", "RISPETTO_LINEE_ARCHEOLOGICHE_TIPIZZATE",
			"RISPETTO_PUNTI_ARCHEOLOGICI", "ZONE_UMIDE", "DECRETI_ARCHEOLOGICI", "GEOMORFOLOGICI_TIPIZZATI", "EX_1497_AB", "EX_1497_CD",
			"COSTA_MARE", "PUNTI_ARCHEOLOGICI", "COSTA_LAGHI"
		]
	},
	{
		name: 'paes',
		title: 'Vincoli',
		single: false,
		visible: false,
		layers: [
			'AREE_PAESAGGISTICHE'
		]
	}
];
const groupsMap = Object.fromEntries(groups.map(g => [g.name, g]));
const gruppoLayerIdx = Object.fromEntries(groups.map(g => g.layers.map(l => [l, g.name])).flat());



const layers = layerNames.map(
	(name) => {
		const outVal = [name];
		//console.log(outVal);


		if (name == "osm") {
			outVal.push(new OSM(
					{
						crossOrigin: 'anonymous'
					}
				)
			);
		}
		else {
			const layerDef = environment.layers[name];
			//console.log(layerDef);

			if (layerDef == undefined) {
				outVal.push(vectorBuilder);
			}
			else {
				const wms = layerDef.wms;
				if (wms == undefined) {
					const wfs = layerDef.wfs;
					if (wfs == undefined) {
						const xyz = layerDef.xyz;
						if (xyz == undefined) {
							outVal.push(vectorBuilder);
						}
						else {
							const {url, maxZoom, projection} = xyz;
							outVal.push(
								new olSourceXYZ(
									{
										url, maxZoom,
										crossOrigin: 'anonymous',
										projection: projection
									}
								)
							);
						}
					}
					else {
						const {url, layerName, projection} = wfs;
						const version = wfs.version??"1.1.0";
						const request = wfs.request??"GetFeature";
						outVal.push(
							new olSourceVector(
								{
									format: new GeoJSON(),
									url: function (extent) {
										return (
											`${url}?service=WFS&version=${version}&request=${request}&typename=${layerName}` +
											`&outputFormat=application/json&srsname=${projection}&`+
											'bbox=' + extent.join(',') + ',' + viewSrid
										);
									},
									strategy: bbox
								}
							)
						);
					}
				}
				else {
					const {url, layerName, projection} = wms;
					const version = wms.version??"1.1.0";
					const request = wms.request??"GetMap";
					outVal.push(
						new TileWMS(
							{
								url: url,
								params: {
									REQUEST: request,
									CRS: projection,
									FORMAT: "image/png",
									SERVICE: "WMS",
									VERSION: version,
									LAYERS: layerName
								},
								strategy: tile(createXYZ({tileSize: 512})),
								projection: projection,
								serverType: 'geoserver',
								transition: 0,
								crossOrigin: 'anonymous'
							}
						)
					);
				}
			}
		}

		//console.log(outVal);
		return outVal;
	}
);
//console.log(layers);
const layerSources = Object.fromEntries(layers);

const layerCatasto = (src) => new olLayerTile(
	{
		displayInLayerSwitcher: true,
		title: "Catasto",
		baseLayer: false,
		visible: false,
		source: src
	}
);

const layerOrto = (src) => new olLayerTile(
	{
		displayInLayerSwitcher: true,
		title: "Ortofoto 2012",
		baseLayer: false,
		visible: false,
		source: src
	}
);

const layerOsm = (src) => new olLayerTile(
	{
		displayInLayerSwitcher: true,
		title: "Open Street Map",
		baseLayer: false,
		visible: true,
		source: src
	}
);

const getVectorLayerBuilder = (title, styleFun, opts={}) => (
	(src) => new olLayerVector(
		{
			displayInLayerSwitcher: opts.displayInLayerSwitcher??true,
			opacity: opts.opacity??0.5,
			title: title,
			source: src,
			style: styleFun??style
		}
	)
);

const layerNAT2K = getVectorLayerBuilder(getLayerTitleFromStyle(layer105Style, 'Polygon', 'Natura 2000'), layer105Style);
const layerAREE_PROTETTE = getVectorLayerBuilder(getLayerTitleFromStyle(layer106Style, 'Polygon', 'Aree Protette'), layer106Style);
const layerPAI_RISCHIO_ALLUVIONE = getVectorLayerBuilder(getLayerTitleFromStyle(layer101aStyle, 'Polygon', 'Rischio Alluvione'), layer101aStyle);
const layerPAI_RISCHIO_FRANA = getVectorLayerBuilder(getLayerTitleFromStyle(layer101fStyle, 'Polygon', 'Rischio Frana'), layer101fStyle);
const layerPAI_RISCHIO_VALANGA = getVectorLayerBuilder(getLayerTitleFromStyle(layer101vStyle, 'Polygon', 'Rischio Valanga'), layer101vStyle);
const layerCOSTA_MARE = getVectorLayerBuilder(getLayerTitleFromStyle(layer501Style, 'Polygon', 'Costa Mare'), layer501Style);
const layerCOSTA_LAGHI = getVectorLayerBuilder(getLayerTitleFromStyle(layer502Style, 'Polygon', 'Costa Laghi'), layer502Style);
const layerACQUE_PUBBLICHE = getVectorLayerBuilder(getLayerTitleFromStyle(layer503Style, 'Polygon', 'Acque Pubbliche'), layer503Style);
const layerACQUE_PUBBLICHE_RISPETTO = getVectorLayerBuilder(getLayerTitleFromStyle(layer504Style, 'Polygon', 'Acque Pubbliche Rispetto'), layer504Style);
const layerALTIMETRIA_1200 = getVectorLayerBuilder(getLayerTitleFromStyle(layer505Style, 'Polygon', 'Altimetria 1200'), layer505Style);
const layerUSI_CIVICI = getVectorLayerBuilder(getLayerTitleFromStyle(layer506Style, 'Polygon', 'Usi Civici'), layer506Style);
const layerZONE_UMIDE = getVectorLayerBuilder(getLayerTitleFromStyle(layer507Style, 'Polygon', 'Zone Umide'), layer507Style);
const layerLINEE_ARCHEOLOGICHE = getVectorLayerBuilder(getLayerTitleFromStyle(layer508Style, 'Polygon', 'Linee Archeologiche'), layer508Style);
const layerRISPETTO_LINEE_ARCHEOLOGICHE = getVectorLayerBuilder(getLayerTitleFromStyle(layer509Style, 'Polygon', 'Rispetto Linee Archeologiche'), layer509Style);
const layerRISPETTO_LINEE_ARCHEOLOGICHE_TIPIZZATE = getVectorLayerBuilder(getLayerTitleFromStyle(layer510Style, 'Polygon', 'Linee Archeologiche Tipizzate'), layer510Style);
const layerPUNTI_ARCHEOLOGICI = getVectorLayerBuilder(getLayerTitleFromStyle(layer511Style, 'Polygon', 'Punti Archeologici'), layer511Style);
const layerRISPETTO_PUNTI_ARCHEOLOGICI = getVectorLayerBuilder(getLayerTitleFromStyle(layer512Style, 'Polygon', 'Rispetto Punti Archeologici'), layer512Style);
const layerPUNTI_ARCHEOLOGICI_TIPIZZATI = getVectorLayerBuilder(getLayerTitleFromStyle(layer513Style, 'Polygon', 'Punti Archeologici Tipizzati'), layer513Style);
const layerDECRETI_ARCHEOLOGICI = getVectorLayerBuilder(getLayerTitleFromStyle(layer514Style, 'Polygon', 'Decreti Archeologici'), layer514Style);
const layerGEOMORFOLOGICI_TIPIZZATI = getVectorLayerBuilder(getLayerTitleFromStyle(layer515Style, 'Polygon', 'Geomorfologici Tipizzati'), layer515Style);
const layerRISPETTO_GEOMORFOLOGIA = getVectorLayerBuilder(getLayerTitleFromStyle(layer516Style, 'Polygon', 'Ruspetto Geomorfologia'), layer516Style);
const layerBOSCHI = getVectorLayerBuilder(getLayerTitleFromStyle(layer517Style, 'Polygon', 'Boschi'), layer517Style);
const layerEX_1497_AB = getVectorLayerBuilder(getLayerTitleFromStyle(layer518Style, 'Polygon', 'Ex 1497 AB'), layer518Style);
const layerEX_1497_CD = getVectorLayerBuilder(getLayerTitleFromStyle(layer519Style, 'Polygon', 'Ex 1497 CD'), layer519Style);
const layerHABITAT = getVectorLayerBuilder(getLayerTitleFromStyle(layer208Style, 'Polygon', 'Habitat Prioritari'), layer208Style);
const layerAREE_PAESAGGISTICHE = getVectorLayerBuilder(getLayerTitleFromStyle(layer215Style, 'Polygon', 'Aree Paesaggistiche'), layer215Style);

// const layerPaiAll = getVectorLayerBuilder(getLayerTitleFromStyle(layer101AllStyle, 'Polygon', 'PAI: Rischio Alluvione'), layer101AllStyle);
// const layerPaiFra = getVectorLayerBuilder(getLayerTitleFromStyle(layer101FraStyle, 'Polygon', 'PAI: Rischio Frana'), layer101FraStyle);
// const layerPaiVal = getVectorLayerBuilder(getLayerTitleFromStyle(layer101ValStyle, 'Polygon', 'PAI: Rischio Valanga'), layer101ValStyle);

const layerRilevamenti = getVectorLayerBuilder(getLayerTitleFromStyle(layerRilevamentiStyle, 'Polygon', 'Rilievi in Campo'), layerRilevamentiStyleFun);
const layerPfor = getVectorLayerBuilder(getLayerTitleFromStyle(layerPforStyle, 'Polygon', 'Area di intervento'), layerPforStyle);
const layerUo = getVectorLayerBuilder(getLayerTitleFromStyle(layerUoStyle, 'Polygon', 'Unità Omogenee'), layerUoStyleFun);

const layerAst = getVectorLayerBuilder(getLayerTitleFromStyle(layerAstStyle, 'Point', 'Aree di Saggio Tradizionali'), layerAstStyleFun);
const layerAd = getVectorLayerBuilder(getLayerTitleFromStyle(layerAdStyle, 'Point', 'Aree Dimostrative'), layerAdStyleFun);
const layerImp = getVectorLayerBuilder(getLayerTitleFromStyle(layerImpStyle, 'Point', 'Imposti'), layerImpStyleFun);

const layerAltr = getVectorLayerBuilder(getLayerTitleFromStyle(layerAstStyle, 'Point', 'Altri Strati Informativi'), layerAltrStyleFun);
const layerViab = getVectorLayerBuilder('Viabilita', layerViabStyleFun);

const layerCrt = (src) => new olLayerTile(
	{
		displayInLayerSwitcher: true,
		opacity: 1,
		title: "Carta Tecnica Regionale",
		baseLayer: false,
		visible: false,
		source: src
	}
);


const layersDef = {
	rilevamenti: layerRilevamenti,
	pfor: layerPfor,
	uo: layerUo,
	viab: layerViab,
	ast: layerAst,
	ad: layerAd,
	imp: layerImp,
	altr: layerAltr,
	orto: layerOrto,
	osm: layerOsm,
	crt: layerCrt,
	catasto: layerCatasto,
	NAT2K: layerNAT2K,
	AREE_PROTETTE: layerAREE_PROTETTE,
	PAI_RISCHIO_ALLUVIONE: layerPAI_RISCHIO_ALLUVIONE,
	PAI_RISCHIO_FRANA: layerPAI_RISCHIO_FRANA,
	PAI_RISCHIO_VALANGA: layerPAI_RISCHIO_VALANGA,
	COSTA_MARE: layerCOSTA_MARE,
	COSTA_LAGHI: layerCOSTA_LAGHI,
	ACQUE_PUBBLICHE: layerACQUE_PUBBLICHE,
	ACQUE_PUBBLICHE_RISPETTO: layerACQUE_PUBBLICHE_RISPETTO,
	ALTIMETRIA_1200: layerALTIMETRIA_1200,
	USI_CIVICI: layerUSI_CIVICI,
	ZONE_UMIDE: layerZONE_UMIDE,
	LINEE_ARCHEOLOGICHE: layerLINEE_ARCHEOLOGICHE,
	RISPETTO_LINEE_ARCHEOLOGICHE: layerRISPETTO_LINEE_ARCHEOLOGICHE,
	RISPETTO_LINEE_ARCHEOLOGICHE_TIPIZZATE: layerRISPETTO_LINEE_ARCHEOLOGICHE_TIPIZZATE,
	PUNTI_ARCHEOLOGICI: layerPUNTI_ARCHEOLOGICI,
	RISPETTO_PUNTI_ARCHEOLOGICI: layerRISPETTO_PUNTI_ARCHEOLOGICI,
	PUNTI_ARCHEOLOGICI_TIPIZZATI: layerPUNTI_ARCHEOLOGICI_TIPIZZATI,
	DECRETI_ARCHEOLOGICI: layerDECRETI_ARCHEOLOGICI,
	GEOMORFOLOGICI_TIPIZZATI: layerGEOMORFOLOGICI_TIPIZZATI,
	RISPETTO_GEOMORFOLOGIA: layerRISPETTO_GEOMORFOLOGIA,
	BOSCHI: layerBOSCHI,
	EX_1497_AB: layerEX_1497_AB,
	EX_1497_CD: layerEX_1497_CD,
	HABITAT: layerHABITAT,
	AREE_PAESAGGISTICHE: layerAREE_PAESAGGISTICHE
}

export function getInteriorPoint(g) {
	const geometryType = g.getType();
	switch (geometryType) {
		case 'Polygon':
			return g.getInteriorPoint();
		case 'MultiPolygon':
			return getInteriorPoint(g.getPolygon(0));
		case 'Point':
			return g;
		case 'MultiPoint':
			return getInteriorPoint(g.getPoint(0));
		case 'LineString':
			return new Point(g.getFirstCoordinate(), "XY");
		case 'MultiLineString':
			return getInteriorPoint(g.getLineString(0));
		case 'GeometryCollection':
			return getInteriorPoint(g.getGeometries()[0]);
		default: {
			throw new Error(`Geometria non gestita ${geometryType}`);
		}
	}
}


const exportOptions = {
	useCORS: true,
	ignoreElements: function (element) {
		//console.log(element);
		let res = false;
		const className = element.className;
		if (className != undefined && typeof className == 'string' && className != '') {
			console.log(className);
			res = (
				typeof className == 'string' &&
				className.includes('ol-control') &&
				!className.includes('map-compass') &&
				!className.includes('ol-scale') &&
				(
					!className.includes('ol-attribution') ||
					!className.includes('ol-uncollapsible')
				)
			);
			if (res) {
				console.log(element);
			}
		}
		return res;

	},
};



const layerStile = {
	Color: '#0000FF',
	TextColor: '#FFFFFF',
};

export function getDotStyle(color, radius) {
	return new olStyleStyle(
		{
			image: new olStyleCircle(
				{
					radius: radius??3,
					fill: new olStyleFill(
						{
							color: color,
						}
					),
				}
			),
			geometry: function (feature) {
				// return the coordinates of the first ring of the polygon
				const geom = feature.getGeometry();
				const type = geom.getType();
				//if (type == "Point" || ty)
				switch (type) {
					case "Point":
						return geom;
					case "MultiPoint":
						return geom;
					case "GeometryCollection": {
						const geoms = geom.getGeometries();
						const coordinates = geoms.map(
							geom => {
								//const coordinates = geom.getCoordinates();
								const gType = geom.getType();
								const coord = geom.getCoordinates();
								switch (gType) {
									case "Point":
										return [coord];
									case "LineString":
										return coord;
									case "Polygon":
										return coord.reduce(
											(pvRes, curr) => {
												if (pvRes) {
													return pvRes.concat(curr);
												} else {
													return curr;
												}
											}
										);
									default: {
										throw new Error(`Geometria non gestita ${gType}`);
									}
								}
							}
						);
						const flatCoordinates = coordinates.reduce(
							(pvRes, curr) => {
								if (pvRes) {
									return pvRes.concat(curr);
								} else {
									return curr;
								}
							}
						);

						if (flatCoordinates.length > 1) {
							return new MultiPoint(flatCoordinates, "XY");
						} else {
							return new Point(flatCoordinates, "XY");
						}
					}
						;
					case "MultiPolygon": {
						const geoms = geom.getPolygons();
						const coordinates = geoms.map(
							geom => geom.getCoordinates().reduce(
								(pvRes, curr) => {
									if (pvRes) {
										return pvRes.concat(curr);
									} else {
										return curr;
									}
								}
							)
						);
						const flatCoordinates = coordinates.reduce(
							(pvRes, curr) => {
								if (pvRes) {
									return pvRes.concat(curr);
								} else {
									return curr;
								}
							}
						);
						if (flatCoordinates.length > 1) {
							return new MultiPoint(flatCoordinates, "XY");
						} else {
							return new Point(flatCoordinates, "XY");
						}
					}
						;
					default: {
						const coordinates = geom.getCoordinates();
						const flatCoordinates = coordinates.reduce(
							(pvRes, curr) => {
								if (pvRes) {
									return pvRes.concat(curr);
								} else {
									return curr;
								}
							}
						);
						if (flatCoordinates.length > 1) {
							return new MultiPoint(flatCoordinates, "XY");
						} else {
							return new Point(flatCoordinates, "XY");
						}
					}
				}
			},
		}
	);
}

function formatLength(length) {
	//const length = line.getLength();
	let output;
	if (length > 1000) {
		output = (Math.round((length / 1000) * 100) / 100) + ' km';
	} else {
		output = (Math.round(length * 100) / 100) + ' m';
	}
	return output;
};

function formatArea(area) {
	let output;
	if (area > 1000000) {
		output = (Math.round(area / 10000) / 100) + ' km\xB2';
	}
	else {
		if (area > 10000) {
			output = (Math.round(area / 100) / 100) + ' ha';
		}
		else {
			output = Math.round(area) + ' m\xB2';
		}
	}
	return output;
};

const labelStyleTextOpts = {
	font: '14px Calibri,sans-serif',
	fill: new olStyleFill({
		color: 'rgba(255, 255, 255, 1)',
	}),
	backgroundFill: new olStyleFill({
		color: 'rgba(0, 0, 0, 0.7)',
	}),
	padding: [3, 3, 3, 3],
	textBaseline: 'bottom',
	offsetY: -15
};

const labelSmallStyleTextOpts = {
	font: '10px Calibri,sans-serif',
	fill: new olStyleFill({
		color: 'rgba(255, 255, 255, 1)',
	}),
	backgroundFill: new olStyleFill({
		color: 'rgba(0, 0, 0, 0.7)',
	}),
	padding: [3, 3, 3, 3],
	textBaseline: 'bottom',
	offsetY: -15
};

const labelStyleImgOpts = {
	radius: 8,
	points: 3,
	angle: Math.PI,
	displacement: [0, 10],
	fill: new olStyleFill({
		color: 'rgba(0, 0, 0, 0.7)',
	}),
};

const labelStyleImg = new RegularShape(labelStyleImgOpts);

export function getTextStyle(point, labelText, labelMeasure, small) {
	const text = [labelText, labelMeasure].filter(x => x != undefined).join('\n');
	const textStyleOpts = small ? labelSmallStyleTextOpts : labelStyleTextOpts;

	const style = new Style(
		{
			text: new olStyleText(
				{
					...textStyleOpts,
					text: text
				}
			),
			image: new RegularShape(labelStyleImgOpts),
			geometry: point
		}
	);
	return style;
}



export function getSegmentStylesAtResolution(pointA, pointB, resolution) {
	const segment = new LineString([pointA, pointB]);
	const segLen = segment.getLength();
	const segLenPx = segLen / resolution;
	const styles = [];
	if (!resolution || segLenPx > 100) {
		const segStyles = getLineTextStyles(segment, undefined, undefined, true, resolution);
		segStyles.forEach(
			style => {
				styles.push(style);
			}
		);
	}
	return styles;
}

export function getLineTextStyles(geometry, labelText, splitLines, small, resolution) {
	const styles = [];
	if (splitLines) {
		geometry.forEachSegment(
			(a, b) => {
				// const segment = new LineString([a, b]);
				// const segLen = segment.getLength();
				// const segLenPx = segLen * resolution;

				// if (!resolution || segLenPx > 100) {
				// 	const segStyles = getLineTextStyles(segment, undefined, undefined, true, resolution);
				// 	segStyles.forEach(
				// 		style => {
				// 			styles.push(style);
				// 		}
				// 	);
				// }

				const segStyles = getSegmentStylesAtResolution(a, b, resolution);
				segStyles.forEach(
					style => {
						styles.push(style);
					}
				);
			}
		);
	}
	else {
		const labelLength = formatLength(geometry.getLength());
		const point = new Point(geometry.getCoordinateAt(0.5));
		const style = getTextStyle(point, labelText, labelLength, small);
		styles.push(style);
	}
	return styles;
}

export function getPolygonTextStyles(geometry, labelText, splitLines, resolution) {
	const styles = [];
	const area = geometry.getArea();
	//const perimetro = geometry.getLength();
	if (area > 0) {
		const labelArea = formatArea(area);
		//const labelPerimetro = formatLength(perimetro);
		const point = geometry.getInteriorPoint();
		const style = getTextStyle(point, labelText, labelArea);
		styles.push(style);
	}

	if (splitLines) {
		const ringsCoordinates = geometry.getCoordinates();
		ringsCoordinates.forEach(
			ringsoordinates => {
				const line = new LineString(ringsoordinates);
				line.forEachSegment(
					(a, b) => {
						const segStyles = getSegmentStylesAtResolution(a, b, resolution);
						segStyles.forEach(
							style => {
								styles.push(style);
							}
						);
						// const segment = new LineString([a, b]);
						// const segStyles = getLineTextStyles(segment, undefined, undefined, true, resolution);
						// segStyles.forEach(
						// 	style => {
						// 		styles.push(style);
						// 	}
						// );
					}
				);
			}
		)
	}
	return styles;
}

export function getGeometryTextStyles(geometry, labelText, splitLines, resolution) {
	const styles = [];
	const type = geometry.getType();
	switch (type) {
		case 'Polygon': {
			const polStyles = getPolygonTextStyles(geometry, labelText, splitLines, resolution);
			polStyles.forEach(
				style => {
					styles.push(style);
				}
			);
		}; break;
		case 'LineString': {
			const lineStyles = getLineTextStyles(geometry, labelText, false);
			lineStyles.forEach(
				style => {
					styles.push(style);
				}
			);
		}; break;
		case 'Point': {
			const style = getTextStyle(geometry, labelText);
			styles.push(style);
		}; break;
		case 'MultiPolygon': {
			const subs = geometry.getPolygons();
			subs.forEach(
				poly => {
					const polStyles = getPolygonTextStyles(poly, labelText, splitLines, resolution);
					polStyles.forEach(
						style => {
							styles.push(style);
						}
					);
				}
			);
		}; break;
		case 'GeometryCollection': {
			const subs = geometry.getGeometries();
			subs.forEach(
				geom => {
					const polStyles = getGeometryTextStyles(geom, labelText, splitLines, resolution);
					polStyles.forEach(
						style => {
							styles.push(style);
						}
					);
				}
			);
		}; break;
		case 'MultiLineString': {
			const subs = geometry.getLineStrings();
			subs.forEach(
				line => {
					const lineStyles = getLineTextStyles(line, labelText);
					lineStyles.forEach(
						style => {
							styles.push(style);
						}
					);
				}
			);
		}; break;
		case 'MultiPoint': {
			const subs = geometry.getPoints();
			subs.forEach(
				point => {
					const style = getTextStyle(point, labelText);
					styles.push(style);
				}
			);
		}; break;
	}
	return styles;
}

export function getFeatureTextStyles(feature, resolution, splitLines) {
	const geometry = feature.getGeometry();
	const labelText = feature.get('lab');
	const styles = getGeometryTextStyles(geometry, labelText, splitLines, resolution);
	return styles;
}

export const stylesMap = {
	highlighted: (feature, resolution) => [
		new olStyleStyle(
			{
				fill: new olStyleFill({
					color: hexToRGB('#FFFF00', 0.2)
				}),
				stroke: new olStyleStroke({
					color: layerStile.Color,
					width: 2
				})
			}
		),
		...getFeatureTextStyles(feature, resolution),
		getDotStyle('#FFFF00', 6)
	],
	normal: (feature, resolution) => [
		new olStyleStyle(
			{
				fill: new olStyleFill({
					color: hexToRGB(layerStile.Color, 0.1)
				}),
				stroke: new olStyleStroke({
					color: layerStile.Color,
					width: 1
				})
			}
		),
		...getFeatureTextStyles(feature, resolution),
		getDotStyle(layerStile.Color)
	],
	selected: (feature, resolution) => [
		new olStyleStyle(
			{
				fill: new olStyleFill({
					color: hexToRGB('#00FF00', 0.3)
				}),
				stroke: new olStyleStroke({
					color: layerStile.Color,
					width: 3
				})
			}
		),
		...getFeatureTextStyles(feature, resolution),
		getDotStyle('#00FF00', 6)
	]
};


export class BaseMap {
	inquadro = undefined;
	mainSource = [];
	mainLayer = undefined;
	originalFeatures = [];
	features = [];

	selFeatures = new olCollection();
	highFeatures = new olCollection();

	selectedFeature = undefined;
	viewOverlay = false;
	view = undefined;
	popupOverlay = undefined;
	mapController = undefined;
	featureLimiti = undefined;
	currentInteractions = [];
	hasData = false;
	viewSrid = undefined;
	shapeSrid = undefined;

	srcLayerUtenteMap = undefined;
	areaConstraintControl = undefined;
	totalArea = undefined;


	getFeatureFromWkt(wktString, opts, properties) {
		const g = format.readGeometry(
			wktString, opts
		);
		// const g = format.readGeometry(wktString);
		// if (g != undefined) {
		// 	if (opts != undefined) {
		// 		g.transform(opts.dataProjection, opts.featureProjection);
		// 	}
		// }

		const interiorPoint = getInteriorPoint(g);

		const feature = new olFeature(g);
		feature.setProperties({interiorPoint, ...properties});
		return feature;
	}

	loadLayerUtenteFeatures(layerConf, layerSrcData) {
		const shapeField = layerConf.shapeField??"shape";
		const labelField = layerConf.labelField;
		return layerSrcData.map(
			(dati, idx) => {
				if (dati != undefined) {
					const lab = (labelField != undefined && dati[labelField] != undefined)
						? dati[labelField].toString()
						: undefined;
					const feature = this.getFeatureFromWkt(
						dati[shapeField],
						{
							dataProjection: layerConf.shapeSrid??this.viewSrid,
							featureProjection: this.viewSrid
						},
						{dati, idx, lab}
					);
					return feature;
				} else {
					return undefined;
				}
			}
		);
	}

	setInteractions(interactions) {
		this.currentInteractions.forEach(
			(i) => {
				this.mapController.removeInteraction(i);
			}
		);

		this.currentInteractions = interactions;

		this.currentInteractions.forEach(
			(i) => {
				this.mapController.addInteraction(i);
			}
		);
	}

	constructor(selectCb, highilightCb) {
		this.selectCb = selectCb;
		this.highilightCb = highilightCb;
		this.selFeatures = new olCollection();
		this.mainSource = new olSourceVector({wrapX: false});
		this.mainLayer = new olLayerVector(
			{
				title: "Lavoro",
				displayInLayerSwitcher: false,
				source: this.mainSource,
				visible: true,
				style: stylesMap['normal']
			}
		);
		this.select = new olInteractionSelect(
			{
				condition: (mapBrowserEvent) => {
					// impedisce selezioni con doppio click e selezioni multiple (con tasto shift)
					const orEvent = mapBrowserEvent.originalEvent;
					return (
						mapBrowserEvent.type == MapBrowserEventType.SINGLECLICK
						&& !(orEvent.altKey || orEvent.ctrlKey || orEvent.shiftKey)
					);
				},
				layers: [this.mainLayer],
				features: this.selFeatures,
				style: stylesMap["selected"]
			}
		);
		this.highlight = new olInteractionSelect(
			{
				//condition: pointerMove,
				layers: [this.mainLayer],
				features: this.highFeatures,
				condition: (e) => {
					if (e.type == 'pointermove') {
						//console.log(e);
						if (this.selFeatures.getLength() == 0) {
							return true;
						}
						else {
							const feat = this.selFeatures.item(0);
							if (feat) {
								const feats = this.mapController.getFeaturesAtPixel(
									e.pixel,
									{
										layerFilter: (layer) => {
											//console.log(layer);
											return layer == this.mainLayer;
										}
									}
								);
								return !feats.includes(feat);
							}
						}
					}
					else {
						return false;
					}
				},
				style: stylesMap["highlighted"]
			}
		);

		if (selectCb && typeof (selectCb) == 'function') {
			this.select.on(
				'select',
				(e) => {
					selectCb(e.selected, e.deselected);
				}
			);
		}
		this.select.on(
			'select',
			(e) => {
				this.mostraOverlay(undefined);
				this.selectedFeature = (e.selected && e.selected.length == 1) ? e.selected[0] : undefined;
				//this.gotoElement(element);
			}
		);
		if (highilightCb && typeof (highilightCb) == 'function') {
			this.highlight.on(
				'select',
				(e) => {
					highilightCb(e.selected, e.deselected);
				}
			);
		}
	}
	exportGeoJson(anchor) {
		let writer = new GeoJSON();

		let featsArr = this.mainSource.getFeatures().map(
			f => new olFeature({geometry: f.getGeometry()})
		);

		let src = new olSourceVector({
			features: featsArr
		});

		let geojsonStr = writer.writeFeatures(src.getFeatures());
		let fileData = new Blob([geojsonStr], {type: 'application/json'});
		const reader = new FileReader();

		const evHandler = () => {
			if (reader.result) {
				anchor.href = reader.result;
				anchor.click();
			}
		};
		reader.addEventListener(
			"load",
			evHandler,
			//false,
			{once: true}
		);
		reader.readAsDataURL(fileData);
	}

	getInteriorPoint(g) {
		const geometryType = g.getType();
		switch (geometryType) {
			case 'Polygon':
				return g.getInteriorPoint();
			case 'MultiPolygon':
				return this.getInteriorPoint(g.getPolygon(0));
			case 'Point':
				return g;
			case 'MultiPoint':
				return this.getInteriorPoint(g.getPoint(0));
			case 'LineString':
				return new Point(g.getFirstCoordinate(), "XY");
			case 'MultiLineString':
				return this.getInteriorPoint(g.getLineString(0));
			case 'GeometryCollection':
				return this.getInteriorPoint(g.getGeometries()[0]);
			default: {
				throw new Error(`Geometria non gestita ${geometryType}`);
			}
		}
	}

	updateData(conf, dati) {
		console.log("updateData");
		this.conf = conf;
		this.hasData = false;
		this.totalArea = 0;
		if (conf && dati && Array.isArray(dati)) {
			if (conf.mappa) {
				const shapeField = conf.mappa.shape.attribute;
				const labelField = conf.mappa.shape.label;
				const shapeSrid = conf.mappa.shape.srid;
				this.viewSrid = conf.mappa.view.srid;
				this.shapeSrid = conf.mappa.shape.srid;
				
				const opts = (shapeSrid == undefined) ? undefined : {
					dataProjection: shapeSrid,
					featureProjection: this.viewSrid
				};
				this.originalFeatures = dati.map(
					(dati, idx) => {
						if (dati != undefined) {
							const wkt = dati[shapeField];
							if (wkt) {
								const g = format.readGeometry(
									dati[shapeField],
									opts
								);

								// const g = format.readGeometry(dati[shapeField]);
								// if (g != undefined) {
								// 	if (opts != undefined) {
								// 		g.transform(opts.dataProjection, opts.featureProjection);
								// 	}
								// }


								const lab = (labelField != undefined && dati[labelField] != undefined)
									? dati[labelField].toString()
									: undefined;

								const interiorPoint = this.getInteriorPoint(g);

								if (g.getArea) {
									this.totalArea += g.getArea();
								}

								const feature = new olFeature(g);
								feature.setProperties({dati, idx, interiorPoint, lab});
								this.hasData = true;
								return feature;
							}
							else {
								return undefined;
							}
						}
						else {
							return undefined;
						}
					}
				);
				this.features = this.originalFeatures.filter(
					x => (x != undefined)
				);
				this.mainSource.clear();
				this.mainSource.addFeatures(
					this.features
				);
			}
		}
		if (this.areaConstraintControl) {
			this.areaConstraintControl.nextArea = this.totalArea;
		}

	}

	indexOfRow(v) {
		return v ? this.dati.indexOf(v) : undefined;
	}

	getStyle(tipo) {
		return stylesMap[tipo];
	}

	evidenzia(row, idx) {
		const selected = this.highFeatures.getArray()[0];
		const newselFeat = this.originalFeatures[idx];
		if (selected != newselFeat) {
			this.highFeatures.getArray().forEach(
				feat => {
					this.highlight.restorePreviousStyle_(feat);
				}
			);
			this.highFeatures.clear();

			if (newselFeat) {
				this.highFeatures.push(newselFeat);
				this.highlight.applySelectedStyle_(newselFeat);
			}
		}
	}

	seleziona(row, idx) {
		//console.log({seleziona: {row, idx}});

		this.mostraOverlay(undefined);

		const selected = this.selFeatures.getArray()[0];
		const newselFeat = this.originalFeatures[idx];
		if (selected != newselFeat) {
			//this.selectedRowIdx = idx;
			if (selected) {
				this.select.restorePreviousStyle_(selected);
				// selected.forEach(
				// 	feat => {
				// 		this.select.restorePreviousStyle_(feat);
				// 	}
				// );
			}
			this.selFeatures.clear();
			if (newselFeat) {
				this.selFeatures.push(newselFeat);
				this.select.applySelectedStyle_(newselFeat);
			}
			this.selectedFeature = newselFeat;
			this.gotoElement(newselFeat);
		}
	}

	gotoElement(element) {
		if (element) {
			const intPoit = element.get('interiorPoint');
			const size = this.mapController.getSize();
			if (intPoit && size) {
				// this.view.centerOn(intPoit.getCoordinates(), size, size.map(x => x / 2));
				this.mapController.getView().centerOn(intPoit.getCoordinates(), size, size.map(x => x / 2));
			}
		}
	}


	//mostraOverlay(element) {
	mostraOverlay(element) {
		if (this.popupOverlay) {
			if (element) {
				if (this.popupOverlay.getPosition() == null) {
					const intPoit = element.get('interiorPoint');
					if (this.popupOverlay && intPoit) {
						this.popupOverlay.setPosition(intPoit.getCoordinates());
						//this.popupOverlay.getElement().scrollIntoView({block: "start", inline: "nearest"});
						return;
					}
				}
			}
			this.popupOverlay.setPosition(null);
		}
	}

	mostraOverlayAtPoint(point) {
		if (point) {
			this.popupOverlay.setPosition(point.getCoordinates());
			return;
		}
		this.popupOverlay.setPosition(null);
	}

	get selectedRowIdx() {
		const selected = this.selFeatures.getArray()[0];
		if (selected) {
			return selected.get('idx');
		} else {
			return undefined;
		}
	}
	drawMap(
		mapElement, popupElement, 
		conf,
		dati, context,
		dictionariesData,
		callBack
	) {
		this.areaConstraintControl = undefined;
		this.viewSrid = conf.mappa.view.srid;
		this.shapeSrid = conf.mappa.shape.srid;
		if (mapElement && popupElement) {
			this.popupOverlay = new olOverlay({
				element: popupElement,
				autoPan: true
			});

			console.log("draw map");

			const mapLayers = [];
			const schedaLayer = conf.mappa.layers;

			const layers = {};
			const layersSrc = {};
			const layerGroups = {};
			const useGroup = true;
			console.log(environment.layers);
			this.srcLayerUtenteMap = {};
			Object.entries(environment.layers).forEach(
			//Object.entries([]).forEach(
				([layerName, def]) => {
					console.log(layerName);
					const layerCreate = layersDef[layerName];
					if (layerCreate == undefined) {
						throw new Error(`Layer ${layerName} non trovato`);
					}
					else {
						const layerSrcData = dictionariesData ? dictionariesData[layerName] : undefined;

						const layerSrcCreate = layerSources[layerName];
						let layerSrc = undefined;
						if (layerSrcCreate == vectorBuilder) {
							if (layerSrcData && schedaLayer) {
								const layer = schedaLayer[layerName];
								const layerConf = layer.conf??{};

								const layerFeats = this.loadLayerUtenteFeatures(layerConf, layerSrcData);

								layerSrc = layerSrcCreate(layerFeats);
								this.srcLayerUtenteMap[layerName] = layerSrc;
							}
						}
						else {
							layerSrc = layerSrcCreate;
						}

						if (layerSrc) {
							const layer = layerCreate(layerSrc);
							layers[layerName] = layer;
							layersSrc[layerName] = layerSrc;
							if (useGroup) {
								const groupName = gruppoLayerIdx[layerName];
								if (groupName == undefined) {
									mapLayers.push(layer);
								}
								else {
									let group = layerGroups[groupName];
									if (group == undefined) {
										const groupDef = groupsMap[groupName]
										layerGroups[groupName] = group = new LayerGroup(
											{
												openInLayerSwitcher: true,
												title: groupDef.title,
												fold: 'close',
												visible: groupDef.visible
											}
										);
										mapLayers.push(group);
									}
									group.getLayers().push(layer);
								}
							}
							else {
								mapLayers.push(layer);
							}
						}
					}
				}
			);

			if (context.shapeEnte) {
				const viewSrid = conf.mappa.view.srid;
				const shapeSrid = `EPSG:${context.sridShapeEnte}`;
				const opts = (shapeSrid == undefined) ? undefined : {
					dataProjection: shapeSrid,
					featureProjection: viewSrid
				};

				const vectorLimiti = new olSourceVector();
				this.featureLimiti = new olFeature({
					geometry: format.readGeometry(
						context.shapeEnte, opts
					),
					name: 'Limiti',
				});
				vectorLimiti.addFeature(
					this.featureLimiti
				);

				const limitiStyle = new olStyleStyle({
					stroke: new olStyleStroke({
						color: 'rgba(255, 255, 0, 1.0)',
						width: 2,
					}),
				});

				const layerLimiti = new olLayerVector({
					title: getLayerTitleFromStyle(limitiStyle, 'Polygon', "Limiti"),
					source: vectorLimiti,
					style: limitiStyle
				});

				mapLayers.push(layerLimiti); //aggiungo limiti
			}

			mapLayers.push(this.mainLayer); //aggiungo il layer utente

			this.mainLayer.setZIndex(1001);


			const mapInteractions = olInteractiondefaults();
			const mapControls = olControldefaults({
				attribution: false,
				rotate: true,
				zoom: true
			});

			const layerSwitcher = new LayerSwitcher({
				// collapsed: false,
				// mouseover: true
				show_progress: true,
			});
			mapControls.extend([layerSwitcher]);

			mapControls.extend(
				[
					new ScaleLine(
						{
							units: "metric",
							bar: true,
							steps: 2,
							text: true,
							minWidth: 100,
							maxWidth: 200
						}
					)
				]
			);

			if (conf.areaConstraint) {
				this.areaConstraintControl = new AreaConstraintControl(
					context,
					{...conf.areaConstraint}
				);
				mapControls.extend(
					[
						this.areaConstraintControl
					]
				);
			}

			this.updateData(conf, dati);

			this.view = new olView({
				center: [0, 0],
				zoom: 2,
				maxZoom: conf.mappa.view.maxZoom??undefined,
				//projection: 'EPSG:4326'
				projection: conf.mappa.view.srid, // 'EPSG:3857'
			});
			this.mapController = new olMap(
				{
					layers: mapLayers,
					target: mapElement,
					view: this.view,
					overlays: [this.popupOverlay],
					interactions: mapInteractions,
					controls: mapControls
				}
			);
			this.setInteractions([this.highlight, this.select]);

			this.inquadraGeometrie(context, conf);
			callBack();
		}
	}

	inquadraGeometrie(context, conf) {
		let inquadro = undefined;
		const evalInquadro = (feat) => {
			const g = feat.getGeometry();
			const newExtent = g.getExtent();
			inquadro = (inquadro == undefined) ? newExtent : extentExtend(inquadro, newExtent);
		};
		this.mainSource.getFeatures().forEach(evalInquadro);
		if (this.srcLayerUtenteMap) {
			Object.values(this.srcLayerUtenteMap).forEach(
				(layerSrc) => {
					layerSrc.getFeatures().forEach(evalInquadro);
				}
			);
		}
		if (inquadro == undefined) {
			const viewSrid = conf.mappa.view.srid;
			const shapeSrid = `EPSG:${context.sridShapeEnte}`;
			const opts = (shapeSrid == undefined) ? undefined : {
				dataProjection: shapeSrid,
				featureProjection: viewSrid
			};

			const g = format.readGeometry(
				context.boxInquadrEnte//, opts
			);
			if (g != undefined) {
				if (opts != undefined) {
					g.transform(opts.dataProjection, opts.featureProjection);
				}
				const newExtent = g.getExtent();
				inquadro = newExtent;
			}
		}
		if (inquadro) {
			const view = this.mapController?.getView();
			if (view) {
				view.fit(
					inquadro,
					{
						size: this.mapController.getSize(),
						padding: [50, 50, 50, 50],
						duration: 500,
						//maxZoom: 18
					}
				);
			}
		}
	}

	updateConfLayers(context, conf, dictionariesData) {
		const schedaLayer = conf.layers;
		if (this.srcLayerUtenteMap && schedaLayer) {
			Object.entries(schedaLayer).forEach(
				([layerName, conf]) => {
					const newData = dictionariesData[layerName];
					const layerSrc = this.srcLayerUtenteMap[layerName];
					if (newData && layerSrc) {
						const layerFeats = this.loadLayerUtenteFeatures(conf.conf, newData);
						layerSrc.clear();
						layerSrc.addFeatures(layerFeats);
					}
				}
			);
		}
		this.inquadraGeometrie(context, conf);
	}


	getAreaConsValidity() {
		if (this.areaConstraintControl) {
			return !(this.areaConstraintControl.onError??false);
		}
		else {
			return true;
		}
	}
}
