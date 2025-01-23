import {
	Style as olStyleStyle,
	Circle as olStyleCircle,
	Fill as olStyleFill,
	Stroke as olStyleStroke,
	Text as olStyleText,
	RegularShape
} from 'ol/style';
import {Tile as olLayerTile, Vector as olLayerVector} from 'ol/layer';
import {Vector as olSourceVector, XYZ as olSourceXYZ} from 'ol/source';
import LayerGroup from 'ol/layer/Group';
import TileWMS from 'ol/source/TileWMS';
import OSM from 'ol/source/OSM';
import GeoJSON from 'ol/format/GeoJSON';
import Legend from "ol-ext/legend/Legend";
import FillPattern from "ol-ext/style/FillPattern";
import {bbox, tile} from 'ol/loadingstrategy';
import {createXYZ} from 'ol/tilegrid';
import { environment } from 'src/environments/environment';

import {ScaleLine, defaults as olControldefaults} from 'ol/control';
import LayerSwitcher from 'ol-ext/control/LayerSwitcher';
import Compass from 'ol-ext/control/Compass';


import {Map as olMap, Collection as olCollection, Feature as olFeature, Overlay as olOverlay, View as olView} from 'ol';


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

import {
	containsExtent as extentcontainsExtent,
	clone as extentclone,
	scaleFromCenter as extentscaleFromCenter,
	extend as extentextend
} from 'ol/extent';

import WKT from 'ol/format/WKT';
import proj4 from 'proj4';
import {register} from 'ol/proj/proj4';
import {getPointResolution, get as getProjection} from 'ol/proj.js';

//import {jsPDF} from 'jspdf';
import { jsPDF } from "jspdf";
import html2canvas from 'html2canvas';


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



export const layerPforStyle = new olStyleStyle(
	{
		stroke: new olStyleStroke({
			color: '#000000',
			width: 4
		})
	}
);


export const layerUoStyle = new olStyleStyle(
	{
		stroke: new olStyleStroke({
			color: 'orange',
			width: 2
		})
	}
);


const layerUoStyleFun = (feature) => new olStyleStyle(
	{
		stroke: new olStyleStroke({
			color: 'orange',
			width: 2
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
			text: 'UO:'+ feature.get('lab')
		})
	}
);


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

const layerAstStyle = new olStyleStyle(layerAstStyleOpts);
const layerAdStyle = new olStyleStyle(layerAdStyleOpts);
const layerImpStyle = new olStyleStyle(layerImpStyleOpts);

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
			text: 'AST:'+ feature.get('lab')
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
			text: 'Ad:'+ feature.get('lab')
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
			text: 'Imp:'+ feature.get('lab')
		})
	}
);


const layerViabStyleFun = (feature) => {
	const dati = feature.get('dati');
	const nome = dati.nome;
	let strokeIn = undefined;
	let strokeOut = undefined;
	switch (nome) {
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


function getDotStyle(color) {
	return new olStyleStyle(
		{
			image: new olStyleCircle(
				{
					radius: 3,
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


const format = new WKT();

Object.entries(environment.srids).forEach(
	([srid, def]) => {
		proj4.defs(srid, def);
	}
);
register(proj4);


const {mapsSrid, mapMaxZoom} = environment;


const vectorBuilder = (feats) => new olSourceVector({wrapX: false, features: feats});


//console.log(environment.layers);
const layerNames = [
	'pfor', 'uo', 'ast', 'ad', 'viab', 'imp',
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
		layers: ['osm', 'orto', 'crt', 'catasto']
	},
	{
		name: 'prot',
		title: 'Aree Protette',
		single: false,
		layers: [
			'NAT2K', 'AREE_PROTETTE', 'HABITAT'
		]
	},
	{
		name: 'pai',
		title: 'PAI Rischio Idrogeologico',
		single: false,
		layers: [
			'PAI_RISCHIO_ALLUVIONE', 'PAI_RISCHIO_FRANA', 'PAI_RISCHIO_VALANGA'
		]
	},
	{
		name: 'ptpr',
		title: 'PTPR B',
		single: false,
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
							const {url, maxZoom} = xyz;
							outVal.push(
								new olSourceXYZ(
									{
										url,
										maxZoom,
										crossOrigin: 'anonymous'
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
											'bbox=' + extent.join(',') + ',' + mapsSrid
										);
									},
									strategy: bbox
								}
							)
						);
					}
				}
				else {
					const {url, layerName, projection, maxZoom} = wms;
					const version = wms.version??"1.1.0";
					const request = wms.request??"GetMap";
					//console.log({tile, createXYZ});
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
								maxZoom: maxZoom,
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

// const layerSources = {
// 	pfor: layerPforSrc,
// 	uo: layerUoSrc,
// 	ast: layerAstSrc,
// 	ad: layerAdSrc,
// 	viab: layerViabSrc,
// 	imp: layerImpSrc,
// 	crt: layerCrtSrc,
// 	catasto: layerCatastoSrc,
// 	nat2k: layerNat2KSrc,
// 	areeProt: layerAreeProtSrc,
// 	paiAll: layerPaiAllSrc,
// 	paiFra: layerPaiFraSrc,
// 	paiVal: layerPaiValSrc
// }

const layerCatasto = (src) => new olLayerTile(
	{
		displayInLayerSwitcher: true,
		opacity: 0.5,
		title: "Catasto",
		baseLayer: false,
		visible: true,
		source: src
	}
);

const layerOrto = (src) => new olLayerTile(
	{
		title: "Ortofoto 2012",
		//opacity: 0.5,
		baseLayer: false,
		visible: true,
		source: src
	}
);

const layerOsm = (src) => new olLayerTile(
	{
		title: "Open Street Map",
		//opacity: 0.5,
		baseLayer: false,
		visible: true,
		source: src
	}
);

const getVectorLayerBuilder = (title, styleFun, opacity) => (
	(src) => new olLayerVector(
		{
			displayInLayerSwitcher: true,
			opacity: opacity??0.5,
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

const layerPfor = getVectorLayerBuilder(getLayerTitleFromStyle(layerPforStyle, 'Polygon', 'Area di intervento'), layerPforStyle, 1);
const layerUo = getVectorLayerBuilder(getLayerTitleFromStyle(layerUoStyle, 'Polygon', 'Unità Omogenee'), layerUoStyleFun, 1);

const layerAst = getVectorLayerBuilder(getLayerTitleFromStyle(layerAstStyle, 'Point', 'Aree di Saggio Tradizionali'), layerAstStyleFun, 1);
const layerAd = getVectorLayerBuilder(getLayerTitleFromStyle(layerAdStyle, 'Point', 'Aree Dimostrative'), layerAdStyleFun, 1);
const layerImp = getVectorLayerBuilder(getLayerTitleFromStyle(layerImpStyle, 'Point', 'Imposti'), layerImpStyleFun, 1);
const layerViab = getVectorLayerBuilder('Viabilita', layerViabStyleFun);

const layerCrt = (src) => new olLayerTile(
	{
		displayInLayerSwitcher: true,
		opacity: 1,
		title: "Carta Tecnica Regionale",
		baseLayer: false,
		visible: true,
		source: src
	}
);


const layersDef = {
	pfor: layerPfor,
	uo: layerUo,
	ast: layerAst,
	ad: layerAd,
	viab: layerViab,
	imp: layerImp,
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


export class BaseMap {
	inquadro = undefined;
	mainSource = [];
	layerNatura2K = [];
	mainLayer = undefined;
	originalFeatures = [];
	features = [];

	viewOverlay = false;
	// view = undefined;
	mapController = undefined;
	hasData = false;
	isReady = false;
	scaleLine = undefined;
	compass = undefined;

	constructor() {
	}

	mapElement = undefined;
	layerNames = undefined;
	layersData = undefined;
	drawMap(mapElement, layerNames, layersData, useGroup) {
		this.isReady = false;
		if (mapElement != undefined && layerNames != undefined && layersData != undefined ) {

			this.mapElement = mapElement;
			this.layerNames = layerNames;
			this.layersData = layersData;

			//console.log("draw map");
			const layers = {};
			const layersSrc = {};
			const mapLayers = [];
			const layerGroups = {};
			layerNames.forEach(
				(layerName) => {
					console.log(layerName);
					const layerCreate = layersDef[layerName];
					if (layerCreate == undefined) {
						throw new Error(`Layer ${layerName} non trovato`);
					}
					else {
						const layerSrcData = layersData[layerName];
	
						const layerSrcCreate = layerSources[layerName];
						let layerSrc = undefined;
						if (layerSrcData == undefined) {
							layerSrc = layerSrcCreate;
						}
						else {
							const layerFeats = layerSrcData.array.map(
								(dati, idx) => {
									if (dati != undefined) {
										const g = format.readGeometry(
											dati[layerSrcData.shapeField], {dataProjection: layerSrcData.shapeSrid??mapsSrid, featureProjection: mapsSrid}
										);
										const labelField = layerSrcData.labelField;
										const lab = (labelField != undefined && dati[labelField] != undefined)
											? dati[labelField].toString()
											: undefined;
			
										const interiorPoint = getInteriorPoint(g);
			
										const newExtent = g.getExtent();
										this.inquadro = (this.inquadro) ? extentextend(this.inquadro, newExtent) : newExtent;
										const feature = new olFeature(g);
										feature.setProperties({dati, idx, interiorPoint, lab});
										return feature;
									} else {
										return undefined;
									}
								}
							);
							//console.log(layerFeats);
							layerSrc = layerSrcCreate(layerFeats);
						}
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
											fold: 'close'
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
			);


			//console.log({inquadro: this.inquadro});

			//this.currentInteractions = [this.highlight, this.select];

			const mapControls = olControldefaults({
				attribution: false,
				rotate: true,
				zoom: false
			});
			const layerSwitcher = new LayerSwitcher({
				// collapsed: false,
				// mouseover: true
				show_progress: true,
			});
			mapControls.extend([layerSwitcher]);

			this.scaleLine = new ScaleLine(
				{
					units: "metric",
					bar: true,
					steps: 2,
					text: true,
					minWidth: 140,
				}
			);

			this.compass = new Compass (
				{
					className: "map-compass",
					src: 'assets/images/svg/Compass-Arrow2.svg',
					// style: new olStyleStroke (
					// 	{ 
					// 		color: 'black',
					// 		width: 1
					// 	}
					// ),
					visible: true
				}
			);

			mapControls.extend(
				[
					this.scaleLine,
					this.compass
				]
			);
			//console.log(mapLayers);
			this.mapController = new olMap(
				{
					layers: mapLayers,
					target: mapElement,
					view: new olView({
						projection: mapsSrid,
						maxZoom: mapMaxZoom
					}),
					controls: mapControls,
					interactions: []
				}
			);

			if (this.inquadro == undefined) {
				throw new Error(`Nessun dato presente`);
			}
			else {
				this.mapController.getView().fit(
					this.inquadro,
					{
						size: this.mapController.getSize(),
						callback: () => {
							this.isReady = true;
						},
						padding: [20, 20, 20, 20],
						duration: 1500
					}
				);
			}
		}
	}

	downloadPdf(dimensione, risoluzione, scala, orientamento) {
		console.log({dimensione, risoluzione, scala, orientamento});
		document.body.style.cursor = 'progress';
		const map = this.mapController;
		const scale = scala / 1000;
		const dim = orientamento == 'H' ? [dimensione.x, dimensione.y] : [dimensione.y, dimensione.x];
		const pdfOrient = orientamento == 'H' ? 'landscape' : 'portrait';
		const scaleLine = this.scaleLine;

		const width = Math.round((dim[0] * risoluzione) / 25.4);
		const height = Math.round((dim[1] * risoluzione) / 25.4);
		const viewResolution = map.getView().getResolution();
		const scaleResolution =
			scale /
			getPointResolution(
				map.getView().getProjection(),
				risoluzione / 25.4,
				map.getView().getCenter()
			);
		const prom = new Promise(
			(resolve, reject) => {
				map.once(
					'rendercomplete', 
					function () {
						exportOptions.width = width;
						exportOptions.height = height;
						const viewport = map.getViewport();
						//const viewport = mapElement;
						//console.log(viewport);
						html2canvas(viewport, exportOptions).then(
							function (canvas) {
								try {
									const pdf = new jsPDF(pdfOrient, undefined, dimensione.nome);
									pdf.addImage(
										canvas.toDataURL('image/jpeg'),
										'JPEG',
										0,
										0,
										dim[0],
										dim[1]
									);
									pdf.save('map.pdf');
									//redrawMap();
									resolve();
									console.log("fine");
								}
								catch (e) {
									reject(e);
								}
								finally {
									//Reset original map size
									scaleLine.setDpi();
									map.getTargetElement().style.width = '100%';
									map.getTargetElement().style.height = '100%';
									map.updateSize();
									map.getView().setResolution(viewResolution);
									document.body.style.cursor = 'auto';
								}
							}
						);
					}
				);
			}
		);

		
		// Set print size
		scaleLine.setDpi(risoluzione);
		map.getTargetElement().style.width = width + 'px';
		map.getTargetElement().style.height = height + 'px';
		map.updateSize();
		map.getView().setResolution(scaleResolution);

		return prom;
	}
}
