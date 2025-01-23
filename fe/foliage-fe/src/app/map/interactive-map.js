import { BaseMap, stylesMap, hexToRGB, getFeatureTextStyles, getDotStyle, getTextStyle, getLineTextStyles, getPolygonTextStyles } from "./base-map";

import TileLayer from 'ol/layer/Tile';
import XYZ from 'ol/source/XYZ';
import VectorSource from 'ol/source/Vector';
import VectorLayer from 'ol/layer/Vector';
import WKT from 'ol/format/WKT';
//const format = new WKT();
import {Style, Stroke, Text, Fill, Circle as CircleStyle } from 'ol/style';
import {
	Pointer as PointerInteraction,
	defaults as defaultInteractions,
	Interaction,
	KeyboardPan,
} from 'ol/interaction';
import { extend } from 'ol/extent';
import Map from 'ol/Map';
import {Draw, Modify, Snap, Select, Translate} from 'ol/interaction';
import OSM from 'ol/source/OSM';
import Group from 'ol/layer/Group';
import View from 'ol/View';
import { ScaleLine, ZoomSlider, defaults as defaultControls } from 'ol/control';
import { get as getProjection } from 'ol/proj';
import { Collection, Feature, Overlay } from 'ol';
import { Scheda } from '../components/shared/editor-scheda/editor-scheda.component';
import MapBrowserEvent from 'ol/MapBrowserEvent';
import { SelectEvent } from 'ol/interaction/Select';
import { Geometry } from 'ol/geom';
import EventType from 'ol/events/EventType';
import GeoJSON from 'ol/format/GeoJSON';


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

//import * as geom from 'jsts/org/locationtech/jts/geom';
//import * as jsts from 'jsts';
import * as geom from 'jsts/org/locationtech/jts/geom';
import * as operation from 'jsts/org/locationtech/jts/operation';
import * as io from 'jsts/org/locationtech/jts/io';
import OverlayOp from 'jsts/org/locationtech/jts/operation/overlay/OverlayOp';
import RelateOp from 'jsts/org/locationtech/jts/operation/relate/RelateOp.js'

const jsts = {
	geom,
	operation,
	io,
	OverlayOp,
	RelateOp
};


const format = new WKT();


export const itNamesMap = {
	Punti: "Point",
	Linee: "LineString",
	Poligoni: "Polygon",
};


export function contains(g1, g2) {
	const gRes = jsts.OverlayOp.difference(g2, g1);
	const areaRes = gRes.getArea();

	return (areaRes < 0.1);
}
export function coveredBy(g1, g2) {
	const res = jsts.RelateOp.covers(g2, g1);
	return res;
}
export function overlaps(g1, g2) {
	const gRes = jsts.OverlayOp.intersection(g1, g2);
	const areaRes = gRes.getArea();

	return (areaRes > 0.1);
}


export const tollRelateOp = {
	contains,
	coveredBy,
	overlaps
};

export function convertTypeName(tipoGeomItName) {
	return itNamesMap[tipoGeomItName];
}

// restituisce true quando la geometria in input 
export function getCheckGeomType(geom, geometries) {
	if (geometries.map(convertTypeName).includes(geom.getGeometryType())) {
		return true;
	}
	else {
		return false;
	}
}
// export function checkGeomType(geom, geometries) {
// 	if (geometries.map(convertTypeName).includes(geom.getGeometryType())) {
// 		return;
// 	}
// 	else {
// 		alert("E' stata rilevata una geometria incompatibile con questa scheda!");
// 		//throw new Error("Geometria incompatibile");
// 	}
// }

const parser = new jsts.io.OL3Parser();

parser.inject(Point, LineString, LinearRing, Polygon, MultiPoint, MultiLineString, MultiPolygon, GeometryCollection);
const stringWriter = new jsts.io.WKTWriter();


// restituisce l'elenco dei motivi per cui la geometria in input non soddisfa i requisiti
export function getCheckGeometria(jstsGeom, isSingle, geometries) {
	console.log("controllo geometria");
	console.log({geom: stringWriter.write(jstsGeom), isSingle, geometries});
	if (jstsGeom) {
		const probsArr = [];
		const nGeoms = jstsGeom.getNumGeometries();
		if (isSingle) {
			if (nGeoms > 1) {
				console.log("Problema");
				const mess = "Non sono ammesse geomeometrie composte";
				probsArr.push(mess);
			}
		}
		if (geometries) {
			let errPres = false;
			if (nGeoms == 1 && jstsGeom.getGeometryN == undefined) {
				if (!getCheckGeomType(jstsGeom, geometries)) {
					errPres = true;
				}
			}
			else {
				for (let i = 0; i < nGeoms; i++) {
					const subGeom = jstsGeom.getGeometryN(i);
					if (!getCheckGeomType(subGeom, geometries)) {
						errPres = true;
					}
				}
			}
			if (errPres) {
				console.log("Problema");
				const mess = "Sono ammesse soltanto geometrie di tipo " + geometries.join(' o ');
				probsArr.push(mess);
			}
		}
		return probsArr.length == 0 ? undefined : probsArr;
	}
	else {
		return undefined;
	}
}

class DrawAbortOnEscape extends Interaction {
	draw = undefined;
	constructor(draw, options) {
		super();
		this.draw = draw;
	}
	handleEvent(mapBrowserEvent) {
		if (this.draw && (mapBrowserEvent.type == EventType.KEYDOWN || mapBrowserEvent.type == EventType.KEYPRESS)) {
			const keyEvent = (
				mapBrowserEvent.originalEvent
			);
			const key = keyEvent.key;
			if (key == 'Escape') {
				this.draw.abortDrawing();
				mapBrowserEvent.preventDefault(); 
				return false;
			}
		}
		return true;
	}
}

const defFil = new Fill({
		color: hexToRGB('#3399CC', 0.3),
	});
const defStroke = new Stroke({
		color: '#3399CC',
		width: 1.25,
	});

function getFill(color) {
	return new Fill({
		color: hexToRGB(color??'#3399CC', 0.3),
	});
}
function getStroke(color) {
	return new Stroke({
		color: color??'#3399CC',
		width: 1.25,
	});
}

// export function getDrawingStyle(color) {
// 	const stroke = getStroke(color);
// 	const fill = getFill(color);
// 	return (feature, resolution) => [
// 		new Style({
// 			image: new CircleStyle({
// 				fill: defFil,
// 				stroke: stroke,
// 				radius: 5,
// 			}),
// 			fill: fill,
// 			stroke: stroke
// 		}),
// 		...getFeatureTextStyles(feature, true),
// 		getDotStyle(color??'#3399CC')
// 	];
// }


export function getDrawingStyle(mainType, color) {
	return (feature, resolution) => {
		const geometry = feature.getGeometry();
		const styles = [];
		const type = geometry.getType();
		switch (type) {
			case 'Polygon': {
				const polyStile = new Style({
					fill: getFill(color??'#3399CC')
				});
				styles.push(polyStile);
				if (type == mainType) {
					const polStyles = getPolygonTextStyles(geometry, undefined, true, resolution);
					polStyles.forEach(
						style => {
							styles.push(style);
						}
					);
				}
			}; break;
			case 'LineString': {
				const lineStile = new Style({
					stroke: getStroke(color??'#3399CC')
				});
				styles.push(lineStile);
				if (type == mainType) {
					const lineStyles = getLineTextStyles(geometry, undefined, false, undefined, resolution);
					lineStyles.forEach(
						style => {
							styles.push(style);
						}
					);
				}
			}; break;
			case 'Point': {
				const pointStyle = new Style({
					image: new CircleStyle({
						fill: getFill(color??'#3399CC'),
						stroke: getStroke(color??'#3399CC'),
						radius: 5,
					})
				});
				styles.push(pointStyle);
				if (type == mainType) {
					const style = getTextStyle(geometry, undefined);
					styles.push(style);
				}
			}; break;

		}
		styles.push(getDotStyle('#3399CC'))
		return styles;
	};
}

export function getDrawStyle(color) {
	const myColor = color??'#3399CC';
	const stroke = getStroke(myColor);
	const fill = getFill(myColor);

	return (feature, resolution) => {
		return [
			new Style({
				image: new CircleStyle({
					fill: defFil,
					stroke: stroke,
					radius: 5,
				}),
				fill: fill,
				stroke: stroke
			}),
			...getFeatureTextStyles(feature, resolution, true),
			getDotStyle(myColor)
		];
	}
}

export function drawStyle(feature, resolution) {
	return [
		new Style({
			image: new CircleStyle({
				fill: defFil,
				stroke: defStroke,
				radius: 5,
			}),
			fill: defFil,
			stroke: defStroke
		}),
		...getFeatureTextStyles(feature, resolution, true),
		getDotStyle('#3399CC')
	];
}

export function getJstsGeomFromFeature(feat) {
	let jstsGeom =  feat.get('jstsGeom');
	if (jstsGeom == undefined) {
		const olGeom = feat.getGeometry();
		if (olGeom) {
			jstsGeom = parser.read(olGeom);
			feat.set('jstsGeom', jstsGeom);
		}
	}
	return jstsGeom;
}

export class InteractiveMap extends BaseMap {

	constructor(selectCb, highilightCb) {
		super(selectCb, highilightCb);
	}

	holingFeature = undefined;
	coordsLenth = undefined;

	drawInteraction = undefined;
	drawAbortInteraction = undefined;
	modifyInteraction = undefined;
	moveInteraction = undefined;

	drawVectorSource = undefined;
	drawLayer = undefined;
	cutVectorSource = undefined;
	cutVectorSource = undefined;

	opts = undefined;

	

	onGeomChange = (e) => {
		let linear_ring = new LinearRing(e.target.getCoordinates()[0]);
		let coordinates = this.holingFeature.getGeometry().getCoordinates();
		let geom = new Polygon(coordinates.slice(0, this.coordsLenth));
		geom.appendLinearRing(linear_ring);
		this.holingFeature.setGeometry(geom);
	}

	setCut() {
		this.drawInteraction = new Draw(
			{
				source: this.drawVectorSource,
				type: "LineString"
			}
		);
		this.drawAbortInteraction = new DrawAbortOnEscape(this.drawInteraction, {});
		this.setInteractions([this.drawInteraction, this.drawAbortInteraction]);
	}

	setDrawing(geomType) {
		this.drawInteraction = new Draw(
			{
				source: this.drawVectorSource,
				//type: "Polygon",
				type: geomType,
				style: getDrawingStyle(geomType)
			}
		);
		if (geomType == "Polygon") {
			let undoGeomChange = undefined;
			this.drawInteraction.on(
				'drawstart',
				(e) => {
					const drawingGeom = e.feature.getGeometry();
					this.holingFeature = undefined;
					this.drawVectorSource.forEachFeatureIntersectingExtent(
						drawingGeom.getExtent(), 
						(f) => {
							const type = f.getGeometry().getType();
							if (type == "Polygon") {
								this.holingFeature = f;
							}
						
						}
					);
					if (this.holingFeature) {
						this.coordsLenth = this.holingFeature.getGeometry().getCoordinates().length;
						undoGeomChange = drawingGeom.on('change', this.onGeomChange);
					}
				}
			);
			this.drawInteraction.on(
				'drawend',
				(e) => {
					if (this.holingFeature) {
						this.holingFeature = undefined;
						if (undoGeomChange) {
							unByKey(undoGeomChange);
							undoGeomChange = undefined;
						}
						setTimeout(
							() => {
								this.drawVectorSource.removeFeature(e.feature);
							},
							5
						);
					}
				}
			);
		}
		this.drawAbortInteraction = new DrawAbortOnEscape(this.drawInteraction, {});
		this.setInteractions([this.drawInteraction, this.drawAbortInteraction, this.modifyInteraction, this.moveInteraction]);
	}

	startJoin(idx) {
		if (idx != undefined) {
			this.updatingFeature = this.originalFeatures[idx];
			if (this.updatingFeature) {
				this.mainSource.removeFeature(this.updatingFeature);

				this.selectionFeatSource = new VectorSource(
					{
						wrapX: false
					}
				);
				this.selectionFeatSource.addFeature(this.updatingFeature);

				this.selectionFeatLayer = new VectorLayer(
					{
						source: this.selectionFeatSource,
						visible: true,
						style: getDrawStyle('#00FF00')
					}
				);
				this.mapController.addLayer(this.selectionFeatLayer);

				this.selGeomFeatures = new Collection();

				this.selectionFeat = new Select(
					{
						layers: [this.mainLayer],
						features: this.selGeomFeatures,
						style:  getDrawStyle('#FFFF00')
					}
				);


				this.setInteractions([this.selectionFeat]);

			}
			else {
				throw Error(`Missing cut element at ${idx} position`);
			}
		}
		else {
			throw Error("Missing cut element index");
		}
	}

	addFeatureToMainSource(newFeature) {
		// const attr = this.conf?.mappa?.shape?.areaAttribute
		// if (attr) {
		// 	const datiElemento = newFeature.get('dati');
		// 	datiElemento[attr] = newFeature.getGeometry().getArea()??0;
		// }
		this.mainSource.addFeature(newFeature);
	}

	stopJoin() {
		if (this.updatingFeature) {
			this.addFeatureToMainSource(this.updatingFeature);
			//this.seleziona(this.updatingFeature.get('dati'), this.updatingFeature.get('idx'));
			this.updatingFeature = undefined;
		}
		this.restoreInteractions();
		if (this.selectionFeat) {
			this.selectionFeat.dispose();
			this.selectionFeat = undefined;
		}
		if (this.selGeomFeatures) {
			this.selGeomFeatures.clear();
			this.selGeomFeatures.dispose();
			this.selGeomFeatures = undefined;
		}
		if (this.selectionFeatLayer) {
			this.selectionFeatLayer.dispose();
			this.selectionFeatLayer = undefined;
		}
		if (this.selectionFeatSource) {
			this.selectionFeatSource.clear();
			this.selectionFeatSource.dispose();
			this.selectionFeatSource = undefined;
		}
	}

	startDisjoint(idx) {
		if (idx != undefined) {
			this.updatingFeature = this.originalFeatures[idx];
			if (this.updatingFeature) {
				const geom = this.updatingFeature.getGeometry();
				const gType = geom.getType();
				let arrGeom = undefined;
				switch (gType) {
					case "GeometryCollection": {
						arrGeom = geom.getGeometries();
					};break;
					case "MultiPoint": {
						arrGeom = geom.getPoints();
					};break;
					case "MultiLineString": {
						arrGeom = geom.getLineStrings();
					};break;
					case "MultiPolygon": {
						arrGeom = geom.getPolygons();
					};break;
					default: {
						return false;
					}
				}
				this.mainSource.removeFeature(this.updatingFeature);
				this.selGeomFeatures = new Collection();
				this.selectionFeatSource = new VectorSource(
					{
						wrapX: false,
						features: arrGeom.map(g => new Feature(g))
					}
				);
				this.selectionFeatLayer = new VectorLayer(
					{
						source: this.selectionFeatSource,
						visible: true,
						style: drawStyle
					}
				);
				this.mapController.addLayer(this.selectionFeatLayer);

				this.selectionFeat = new Select(
					{
						layers: [this.selectionFeatLayer],
						features: this.selGeomFeatures,
						style: getDrawStyle('#00FF00')
					}
				);

				this.setInteractions([this.selectionFeat]);
				return true;
			}
			else {
				throw Error(`Missing cut element at ${idx} position`);
			}
		}
		else {
			throw Error("Missing cut element index");
		}
	}

	startCut(idx) {
		if (idx != undefined) {
			this.updatingFeature = this.originalFeatures[idx];
			if (this.updatingFeature) {
				this.drawVectorSource = new VectorSource(
					{
						wrapX: false
					}
				);
				this.cutVectorSource = new VectorSource(
					{
						wrapX: false
					}
				);
				const geom = this.updatingFeature.getGeometry();
				const gType = geom.getType();
				switch (gType) {
					// case "Polygon": {
					// 	this.drawVectorSource.addFeature(this.updatingFeature.clone());
					// }; break;
					case "MultiPolygon": {
						this.cutVectorSource.addFeatures(geom.getPolygons().map(p => new Feature(p)));
					}; break;
					case "GeometryCollection": {
						this.cutVectorSource.addFeatures(geom.getGeometries().map(g => new Feature(g)));
					}; break;
					default: {
						this.cutVectorSource.addFeature(this.updatingFeature.clone());
					}
				}

				this.mainSource.removeFeature(this.updatingFeature);

				this.cutLayer = new VectorLayer(
					{
						source: this.cutVectorSource,
						visible: true,
						style: drawStyle
					}
				);
				this.drawLayer = new VectorLayer(
					{
						source: this.drawVectorSource,
						visible: true
					}
				);

				this.mapController.addLayer(this.cutLayer);
				this.mapController.addLayer(this.drawLayer);
				this.setCut();
			}
			else {
				throw Error(`Missing cut element at ${idx} position`);
			}
		}
		else {
			throw Error("Missing cut element index");
		}
	}

	startDrawing(geomType, idx) {
		this.drawVectorSource = new VectorSource(
			{
				wrapX: false
			}
		);
		if (idx != undefined) {
			this.updatingFeature = this.originalFeatures[idx];
			if (this.updatingFeature) {
				


				this.mainSource.removeFeature(this.updatingFeature);
				const geom = this.updatingFeature.getGeometry();

				const wktOld  = format.writeGeometry(geom);
				const obj  = this.updatingFeature;
				console.log({wktOld, obj});

				const gType = geom.getType();
				switch (gType) {
					// case "Polygon": {
					// 	this.drawVectorSource.addFeature(this.updatingFeature.clone());
					// }; break;
					case "MultiPolygon": {
						this.drawVectorSource.addFeatures(geom.getPolygons().map(p => new Feature(p)));
					}; break;
					case "GeometryCollection": {
						this.drawVectorSource.addFeatures(geom.getGeometries().map(g => new Feature(g)));
					}; break;
					default: {
						this.drawVectorSource.addFeature(this.updatingFeature.clone());
					}
				}
			}
			else {
				throw new Error(`Geometria in posizione ${idx} mancante`);
			}
		}
		else {
			this.updatingFeature = undefined;
		}
		this.drawLayer = new VectorLayer(
			{
				source: this.drawVectorSource,
				visible: true,
				style: drawStyle
			}
		);
		

		this.modifyInteraction = new Modify(
			{
				source: this.drawVectorSource,
				condition: (ev) => {
					const keyEvent = (
						ev.originalEvent
					);
					//return !keyEvent.altKey && keyEvent.ctrlKey;
					return (keyEvent.altKey || keyEvent.ctrlKey);
				}
			}
		);
		
		this.moveInteraction = new Translate(
			{
				layers: [this.drawLayer],
				condition: (ev) => {
					const keyEvent = (
						ev.originalEvent
					);
					return keyEvent.altKey && !keyEvent.ctrlKey;
					//return false;
				}
			}
		);

		this.mapController.addLayer(this.drawLayer);

		this.setDrawing(geomType);
	}
	changeDrawingMode(mode) {
		this.drawInteraction.abortDrawing();
		this.setDrawing(mode);	
	}
	stopDisjoint() {
		this.restoreInteractions();
		if (this.updatingFeature) {
			this.addFeatureToMainSource(this.updatingFeature);
			//this.seleziona(this.updatingFeature.get('dati'), this.updatingFeature.get('idx'));
			this.updatingFeature = undefined;
		}
		if (this.selectionFeat) {
			this.selectionFeat.dispose();
			this.selectionFeat = undefined;
		}
		if (this.selGeomFeatures) {
			this.selGeomFeatures.dispose();
		}
		if (this.selectionFeatLayer) {
			this.mapController.removeLayer(this.selectionFeatLayer);
			this.selectionFeatLayer.dispose();
			this.selectionFeatLayer = undefined;
		}
		if (this.selectionFeatSource) {
			this.selectionFeatSource.clear();
			this.selectionFeatSource.dispose();
			this.selectionFeatSource = null;
		}
	}
	restoreInteractions() {
		this.setInteractions([this.highlight, this.select]);
	}
	stopDrawing() {
		this.restoreInteractions();
		if (this.drawAbortInteraction) {
			this.drawAbortInteraction.dispose();
			this.drawAbortInteraction = undefined;
		}
		if (this.drawInteraction) {
			this.drawInteraction.dispose();
			this.drawInteraction = undefined;
		}
		if (this.moveInteraction) {
			this.moveInteraction.dispose();
			this.moveInteraction = undefined;
		}
		if (this.modifyInteraction) {
			this.modifyInteraction.dispose();
			this.modifyInteraction = undefined;
		}
		if (this.cutLayer) {
			this.mapController.removeLayer(this.cutLayer);
			this.cutLayer.dispose();
			this.cutLayer = undefined;
		}
		if (this.cutVectorSource) {
			this.cutVectorSource.clear();
			this.cutVectorSource.dispose();
			this.cutVectorSource = undefined;
		}
		if (this.drawLayer) {
			this.mapController.removeLayer(this.drawLayer);
			this.drawLayer.dispose();
			this.drawLayer = undefined;
		}
		if (this.drawVectorSource) {
			this.drawVectorSource.clear();
			this.drawVectorSource.dispose();
			this.drawVectorSource = undefined;	
		}
		if (this.updatingFeature) {
			this.addFeatureToMainSource(this.updatingFeature);

			//this.select.restorePreviousStyle_(this.updatingFeature);
			//this.seleziona(this.updatingFeature.get('dati'), this.updatingFeature.get('idx'));
			this.updatingFeature = undefined;
		}
	}

	
	getGeomsValidity(isSingle, geometries, geometryConstraints) {
		console.log(this.srcLayerUtenteMap);
		console.log(`Controllo di ${this.features.length} geometrie`);
		const outVal = {};
		const nGeoms = this.features.length;
		//this.features.forEach(
		//(feat, idx) => 
		let singles = {};
		for(let idx = 0; idx < nGeoms; idx++) {
			let feat = this.features[idx];
			console.log({feat, idx});
			
			if (feat) {
				const featIdx = feat.get('idx');
				const jstsGeom = getJstsGeomFromFeature(feat);


				//const olGeom = feat.getGeometry();
				//if (olGeom) {
				if (jstsGeom) {
					//const jstsGeom = parser.read(olGeom);
					let resCheck = getCheckGeometria(jstsGeom, isSingle, geometries);
					
					console.log({featIdx, resCheck});

					if (resCheck == undefined && geometryConstraints) {
						const constr = Object.entries(geometryConstraints).map(
							([k, v]) => (
								{
									relType: k,
									props: v
								}
							)
						);
						console.log(constr);
						const nConstr = constr.length;
						for (let idxConstr = 0; idxConstr < nConstr; idxConstr++) {
							const {relType, props} = constr[idxConstr];
							console.log({props, relType});
							const layerName = props.layer;
							if (layerName) {
								const layerSrc = this.srcLayerUtenteMap[layerName];
								console.log(layerSrc);
								if (layerSrc != undefined) {
									let othJstsGeom = undefined;
									if (props.isSingle) {
										console.log("single");
										if (props.predicate) {
											
										}
										else {

										}
									}
									else {
										if (singles[layerName]) {
											othJstsGeom = singles[layerName];
										}
										else {
											const jstsGeoms = layerSrc.getFeatures().map(f => getJstsGeomFromFeature(f));
											singles[layerName] = othJstsGeom = this.getUnionGeomFromJsts(jstsGeoms).unionGeom;
										}
									}
									
									const geom1 = props.reverse ? othJstsGeom : jstsGeom;
									const geom2 = props.reverse ? jstsGeom : othJstsGeom;
									let res = tollRelateOp[relType](geom1, geom2);
									if (props.opposite) {
										res = !res;
									}
									if (res){
										resCheck = [props.errMessage??"Problema"];
									}
								}
							}
							else {
								const othJstsGeom = getJstsGeomFromFeature(this.featureLimiti);
								const geom1 = props.reverse ? othJstsGeom : jstsGeom;
								const geom2 = props.reverse ? jstsGeom : othJstsGeom;
								let res = tollRelateOp[relType](geom1, geom2);
								if (props.opposite) {
									res = !res;
								}
								if (res){
									resCheck = [props.errMessage??"Problema"];
								}
							} 
						}
					}

					if (resCheck == undefined && nGeoms > 1) {
						let found = false;
						for(let othIdx = 0; !found && (othIdx < nGeoms); othIdx++) {
							if (idx != othIdx) {
								const othFeat = this.features[othIdx];
								const othJstsGeom = getJstsGeomFromFeature(othFeat);
								if (tollRelateOp.overlaps(jstsGeom, othJstsGeom)) {
									found = true;
								}
							}
						}
						if (found) {
							resCheck = ["Geometria in sovrapposizione"];
						}
					}

					if (resCheck) {
						outVal[featIdx] = resCheck;
					}
				}
			}
		}
		return outVal;
	}

	// checkGeometria(geom, isSingle, geometries) {
	// 	if (geom) {
	// 		const nGeoms = geom.getNumGeometries();
	// 		if (isSingle) {
	// 			if (nGeoms > 1) {
	// 				alert("Le geometrie composte non sono ammesse!");
	// 				//throw new Error("Geometria composta");
	// 			}
	// 		}
	// 		if (geometries) {
	// 			if (nGeoms == 1) {
	// 				checkGeomType(geom, geometries);
	// 			}
	// 			else {
	// 				for (let i = 0; i < nGeoms; i++) {
	// 					const subGeom = geom.getGeometryN(i);
	// 					checkGeomType(subGeom, geometries);
	// 				}
					
	// 			}
	// 		}
	// 	}
	// 	else {
	// 		alert("Presenza di dati irregolari!");
	// 		throw new Error("Geometria non definita");
	// 	}
	// }

	importWkt(wktString, dataProjection, isSingle, geometries) {
		const feature = this.getFeatureFromWkt(
			wktString,
			{
				dataProjection: dataProjection??this.viewSrid,
				featureProjection: this.viewSrid
			},
			{dati: {}}
		);


		const olGeom = feature.getGeometry();
		const geom = parser.read(olGeom);
		//this.checkGeometria(geom, isSingle, geometries);

		const wkt = stringWriter.write(geom);
		const area = geom.getArea();
		const interiorPoint = this.getInteriorPoint(olGeom);
		return Promise.resolve(
			[{wkt, area, interiorPoint}]
		);
	}
	importGeoJsonFiles(fileList, isSingle, geometries) {
		console.log(fileList);
		if (fileList != null && fileList != "") {
			for (let i = 0; i < fileList.length; i++) {
				const file = fileList.item(i);
				if (file) {
					let geoJson = new GeoJSON();

					const reader = new FileReader();
					return new Promise(
						(resolve, reject) => {
							reader.onload = (e) => {
								const val = geoJson.readFeatures(reader.result);
								
								try {
									const outVal = val.map(
										f => {
											const olGeom = f.getGeometry();
											const geom = parser.read(olGeom);

											//this.checkGeometria(geom, isSingle, geometries);
	
											const wkt = stringWriter.write(geom);
											const area = geom.getArea();
											const interiorPoint = this.getInteriorPoint(olGeom);
											return {wkt, area, interiorPoint};
										}
									);
									resolve(outVal);
								}
								catch(e) {
									reject(e);
								}
								//const wkts =  
							};
							reader.onerror = (e) => {
								reject(e);
							}
							reader.readAsText(file);
						}
					);
				}
			}
		}
	}
	getUnionGeomFromJsts(jstGeomArr) {
		if (jstGeomArr.length > 0) {
			const geomFact = new jsts.geom.GeometryFactory();
			const geomColl = new jsts.geom.GeometryCollection(jstGeomArr, geomFact);
			const unaryUnion = new jsts.operation.union.UnaryUnionOp(geomColl, geomFact);
			const unionGeom = unaryUnion.union();
			const geom = parser.write(unionGeom);
			const area = (geom.getArea ? geom.getArea() : 0);
			const wkt = stringWriter.write(unionGeom);
			return {geom, area, wkt, interiorPoint: this.getInteriorPoint(geom), unionGeom};
			// return {
			// 	wkt: stringWriter.write(unionGeom),
			// 	//feat,
			// 	interiorPoint: this.getInteriorPoint(geom)
			// };
		}
		else {
			return {};
		}
	}
	getUnionGeomFromFeats(featArr) {
		if (featArr.length > 0) {
			const olGeomsArr = featArr.map(f => f.getGeometry());
			const jstGeomArr = olGeomsArr.map(x => parser.read(x));
			return this.getUnionGeomFromJsts(jstGeomArr);

			// const geomFact = new jsts.geom.GeometryFactory();
			// const geomColl = new jsts.geom.GeometryCollection(jstGeomArr, geomFact);
			// const unaryUnion = new jsts.operation.union.UnaryUnionOp(geomColl, geomFact);
			// const unionGeom = unaryUnion.union();
			// const geom = parser.write(unionGeom);
			// const area = (geom.getArea ? geom.getArea() : 0);
			// const wkt = stringWriter.write(unionGeom);
			// return {geom, area, wkt, interiorPoint: this.getInteriorPoint(geom), unionGeom};
			// // return {
			// // 	wkt: stringWriter.write(unionGeom),
			// // 	//feat,
			// // 	interiorPoint: this.getInteriorPoint(geom)
			// // };
		}
		else {
			return {};
		}
	}
	getJointFeatures() {
		const src = this.selGeomFeatures;
		if (src && this.updatingFeature) {
			const arrFeat = src.getArray();
			const {area, wkt} = this.getUnionGeomFromFeats(arrFeat.concat(this.updatingFeature));
			
			//const area = (olGeom.getArea ? olGeom.getArea() : 0);
			return {
				newWkt: wkt,
				newArea: area,
				deletes: arrFeat.map(feat => feat.get('idx'))
			};
		}
		else {
			return {
				newWkt: undefined,
				deletes: undefined
			};
		}
	}
	getDisjointFeatures() {
		const src = this.selGeomFeatures;
		if (src) {
			const groupA = src.getArray();
			groupA.forEach(
				feat => this.selectionFeatSource.removeFeature(feat)
			);
			const groupB = this.selectionFeatSource.getFeatures();

			return [
				this.getUnionGeomFromFeats(groupA),
				this.getUnionGeomFromFeats(groupB)
			];
		}
		else {
			return undefined;
		}

	}
	getCutFeatures() {
		const src = this.drawVectorSource;
		if (src) {
			// const olGeomsArr = src.getFeatures().map(f => f.getGeometry());
			// const jstGeomArr = olGeomsArr.map(x => parser.read(x));
			// const geomFact = new jsts.geom.GeometryFactory();
			// const geomColl = new jsts.geom.GeometryCollection(jstGeomArr, geomFact);


			const splitLines = this.drawVectorSource.getFeatures();

			const res = [];
			
			function addPolygon (originalGeom, addGeom, i) {
				if (addGeom instanceof jsts.geom.Polygon) {
					if (addGeom.getArea && addGeom.getArea() > 0 ) {
						console.log(`divided ${i}: ${stringWriter.write(addGeom)}`);
						console.log(`original ${i}: ${stringWriter.write(originalGeom)}`);
						console.log(addGeom);
		
						//this.sourceD.addFeature(new Feature(parser.write(addGeom)));
						//const olGeom = parser.write(addGeom)
						res.push(
							{
								wkt: stringWriter.write(addGeom),
								interiorPoint: undefined,
								area: addGeom.getArea()
							}
						);
					}
				}
				else {
					const n = addGeom.getNumGeometries();
					if (n > 1) {
						for (let j = 0; j < n; j++) {
							const g = addGeom.getGeometryN(j);
							if (g != addGeom) {
								addPolygon(originalGeom, g, j);
							}
						}
					}
				}
			}
			
			this.cutVectorSource.getFeatures().forEach(
				mainFeat => {
					const olGeom = mainFeat.getGeometry();
					const type = olGeom.getType();
					const mainGeom = parser.read(olGeom);

					switch (type) {
						case "Polygon": {
							const ring = mainGeom.getExteriorRing()
				
							const splitGeoms = splitLines.map(
								(x, i) => {
									const g = parser.read(x.getGeometry());
									//console.log(`cut ${i}: ${stringWriter.write(g)}`);
									return g;
								}
							);
							
							const lineMerger = new jsts.operation.linemerge.LineMerger();
				
							lineMerger.add(ring);
				
							splitGeoms.forEach(
							  x => {
								lineMerger.add(x);
							  }
							);
				
							
							const mergeRes = lineMerger.getMergedLineStrings();
							// mergeRes.toArray().forEach(
							// 	(x, i) => {
							// 		console.log(`merge ${i}: ${stringWriter.write(x)}`);
							// 	}
							// );
				
							const unaryUnion = new jsts.operation.union.UnaryUnionOp(mergeRes);
							console.log(unaryUnion);
							const unionGeom = unaryUnion.union();
							
							
							//console.log(`union: ${stringWriter.write(unionGeom)}`);
				
							
							const poligonizer = new jsts.operation.polygonize.Polygonizer();
							poligonizer.add(unionGeom);
							const divided = poligonizer.getPolygons();
							console.log(divided);
				
				
							divided.toArray().forEach(
								(x, i) => {
									console.log("geometria " + i);
									const y = jsts.OverlayOp.intersection(x, mainGeom);
									addPolygon(x, y, i);
									
								}
							);
						}; break;
						case "LineString": {
							res.push(
								{
									wkt: stringWriter.write(mainGeom),
									interiorPoint: olGeom,
									area: 0
								}
							);
						}; break;
						case "Point": {
							res.push(
								{
									wkt: stringWriter.write(mainGeom),
									interiorPoint: olGeom,
									area: 0
								}
							);
						}; break;
					}
				}
			);

			return res;
		}
		else {
			throw new Error("Null source");
		}
	}

	getDrawnFeatures(isSingle) {
		const src = this.drawVectorSource;
		if (src) {
			// const olGeomsArr = src.getFeatures().map(f => f.getGeometry());
			// const jstGeomArr = olGeomsArr.map(x => parser.read(x));
			// const geomFact = new jsts.geom.GeometryFactory();
			// const geomColl = new jsts.geom.GeometryCollection(jstGeomArr, geomFact);
			// const unaryUnion = new jsts.operation.union.UnaryUnionOp(geomColl, geomFact);
			// console.log(unaryUnion);
			// const unionGeom = unaryUnion.union();
			// const geom = parser.write(unionGeom);
			
			// return {
			// 	wkt: stringWriter.write(unionGeom),
			// 	//feat,
			// 	interiorPoint: this.getInteriorPoint(geom)
			// };
			const feats = src.getFeatures();
			if (isSingle && feats.length > 1) {
				const msg = "Non sono ammesse geometrie composte!"
				alert(msg);
				throw new Error(msg);
			}
			const {wkt, geom, area, interiorPoint} = this.getUnionGeomFromFeats(feats);
			return {
				wkt,
				interiorPoint,
				area
			};
		}
		else {
			throw new Error("Null source");
		}
	}
}