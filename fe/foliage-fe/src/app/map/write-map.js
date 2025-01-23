
// import {
// 	Pointer as PointerInteraction,
// 	defaults as defaultInteractions,
// 	Interaction,
// 	KeyboardPan,
// } from 'ol/interaction';


// import * as geom from 'jsts/org/locationtech/jts/geom';
// import * as operation from 'jsts/org/locationtech/jts/operation';
// import * as io from 'jsts/org/locationtech/jts/io';
// import OverlayOp from 'jsts/org/locationtech/jts/operation/overlay/OverlayOp';
// import { BaseMap } from './base-map';

// const jsts = {
// 	geom: geom,
// 	operation: operation,
// 	io: io,
// 	OverlayOp: OverlayOp
// };



// class DrawAbortOnEscape extends Interaction {
// 	constructor(draw, options) {
// 		super();
// 	}
// 	handleEvent(mapBrowserEvent) {
// 		if (mapBrowserEvent.type == EventType.KEYDOWN || mapBrowserEvent.type == EventType.KEYPRESS) {
// 			const keyEvent = (
// 				mapBrowserEvent.originalEvent
// 			);
// 			const key = keyEvent.key;
// 			if (key == 'Escape') {
// 				this.draw.abortDrawing();
// 				mapBrowserEvent.preventDefault(); 
// 				return false;
// 			}
// 		}
// 		return true;
// 	}
// }



// // function handleDownEvent(this: MoveInteraction, evt: any) {
// //   const map = evt.map;

// //   const feature = map.forEachFeatureAtPixel(
// //     evt.pixel,
// //     function (feature: any) {
// //       return feature;
// //     }
// //   );

// //   if (feature) {
// //     this.mapComponent.coordinate_ = evt.coordinate;
// //     this.mapComponent.feature_ = feature;
// //   }

// //   return !!feature;
// // };

// // function handleDragEvent(this: MoveInteraction, evt: any) {
// //   const deltaX = evt.coordinate[0] - this.mapComponent.coordinate_[0];
// //   const deltaY = evt.coordinate[1] - this.mapComponent.coordinate_[1];

// //   const geometry = this.mapComponent.feature_.getGeometry();
// //   geometry.translate(deltaX, deltaY);

// //   this.mapComponent.coordinate_[0] = evt.coordinate[0];
// //   this.mapComponent.coordinate_[1] = evt.coordinate[1];
// // }

// // function handleMoveEvent(this: MoveInteraction, evt: any) {
// //   if (this.mapComponent.cursor_) {
// //     const map = evt.map;
// //     const feature = map.forEachFeatureAtPixel(
// //       evt.pixel,
// //       function (feature:any) {
// //         return feature;
// //       }
// //     );
// //     const element = evt.map.getTargetElement();
// //     if (feature) {
// //       if (element.style.cursor != this.mapComponent.cursor_) {
// //         this.mapComponent.previousCursor_ = element.style.cursor;
// //         element.style.cursor = this.mapComponent.cursor_;
// //       }
// //     } else if (this.mapComponent.previousCursor_ !== undefined) {
// //       element.style.cursor = this.mapComponent.previousCursor_;
// //       this.mapComponent.previousCursor_ = undefined;
// //     }
// //   }
// // }

// // function handleUpEvent(this: MoveInteraction) {
// //   this.mapComponent.coordinate_ = null;
// //   this.mapComponent.feature_ = null;
// //   return false;
// // }

// // class MoveInteraction extends PointerInteraction {
// //   constructor(public mapComponent: MapComponent) {
// //     super(
// //       {
// //         handleDownEvent: handleDownEvent,
// //         handleDragEvent: handleDragEvent,
// //         handleMoveEvent: handleMoveEvent,
// //         handleUpEvent: handleUpEvent,
// //       }
// //     );
// //   }
// // }

// export class WriteMap extends BaseMap{
	
// 	constructor() {
// 		super();

// 		this.move = new Translate(
// 			{
// 				filter: (feat) => {
// 					return this.selFeatures.getArray().find(f => f === feat) != undefined;
// 				}
// 			}
// 		);
// 		this.draw = new Draw({
// 			source: this.mainSource,
// 			type: "Polygon",
// 		});
// 		this.modify = new Modify({source: this.mainSource});
// 		this.drawAbort = new DrawAbortOnEscape(this.draw, {});
// 	}



// 	azioniToolbar = [
// 		{
// 			nome: "Consultazione",
// 			call: () => {
// 				this.setInteractions([this.select]);
// 			}
// 		},
// 		{
// 			nome: "Spostamento",
// 			call: () => {
// 				this.setInteractions([this.select, this.move]);
// 			}
// 		},
// 		{
// 			nome: "Disegno Poligono",
// 			call: () => {
// 				this.setInteractions([this.draw, this.drawAbort, this.modify]);
// 			}
// 		},
// 		{
// 			nome: "Elimina",
// 			call: () => {
// 				const feats = this.selFeatures.getArray();
// 				if (feats.length > 0) {
// 					feats.forEach(
// 						f => {
// 							this.sourceD.removeFeature(f);
// 						}
// 					)
// 				}
// 			}
// 		},
// 		{
// 			nome: "Inizia taglio",
// 			call: () => {
// 				const feats = this.selFeatures.getArray();
// 				if (feats.length == 1) {
// 					const f = feats[0];
// 					this.featOperazione = f;
// 					this.sourceOperazione =  new VectorSource({ wrapX: false });
// 					this.vectorOperazione = new VectorLayer(
// 						{
// 							source: this.sourceOperazione,
// 							visible: true
// 						}
// 					);
// 					this.mapController.addLayer(this.vectorOperazione);
// 					this.drawOperazione = new Draw(
// 						{
// 							source: this.sourceOperazione,
// 							type: 'MultiLineString',
// 							condition: (ev) => {
// 								const keyEvent = (
// 									ev.originalEvent
// 								);
// 								return !(keyEvent.altKey || keyEvent.ctrlKey);
// 							}
// 						}
// 					);
// 					this.moveOperazione = new Translate(
// 						{
// 							layers: [this.vectorOperazione],
// 							condition: (ev) => {
// 								const keyEvent = (
// 									ev.originalEvent
// 								);
// 								return keyEvent.altKey && !keyEvent.ctrlKey;
// 							}
// 						}
// 					);
// 					this.modifyOperazione = new Modify(
// 						{
// 							source: this.sourceOperazione,
// 							condition: (ev) => {
// 								const keyEvent = (
// 									ev.originalEvent
// 								);
// 								return !keyEvent.altKey && keyEvent.ctrlKey;
// 							}
// 						}
// 					);
// 					this.drawAbortOperazione = new DrawAbortOnEscape(this.drawOperazione, {});
// 					this.setInteractions([this.drawOperazione, this.moveOperazione, this.modifyOperazione, this.drawAbortOperazione]);
// 				}
// 				else {
// 					alert("Selezionare un poligono");
// 				}
// 			}
// 		},
// 		{
// 			nome: "Termina taglio",
// 			call: () => {
// 				const initialGeom = this.featOperazione?.getGeometry();
// 				const splitLines = this.sourceOperazione.getFeatures();
// 				if (this.featOperazione && initialGeom && splitLines && splitLines.length > 0) {
					
// 					(this.parser as any).inject(
// 						Point, LineString, LinearRing, Polygon, MultiPoint, MultiLineString, MultiPolygon
// 					);
					
// 					let mainGeom = this.parser.read(initialGeom);
// 					//console.log(`mainGeom: ${this.stringWriter.write(mainGeom)}`);
// 					const ring = mainGeom.getExteriorRing()
// 					//console.log(`ring: ${this.stringWriter.write(ring)}`);;
					
// 					const splitGeoms = splitLines.map(
// 						(x, i) => {
// 							const g = this.parser.read(x.getGeometry());
// 							//console.log(`cut ${i}: ${this.stringWriter.write(g)}`);
// 							return g;
// 						}
// 					);
					
// 					const lineMerger = new jsts.operation.linemerge.LineMerger();
					
// 					lineMerger.add(ring);

// 					splitGeoms.forEach(
// 					  x => {
// 					    lineMerger.add(x);
// 					  }
// 					);
					
// 					const mergeRes = lineMerger.getMergedLineStrings();
// 					mergeRes.toArray().forEach(
// 						(x: any, i: number) => {
// 							//console.log(`merge ${i}: ${this.stringWriter.write(x)}`);
// 						}
// 					);
					
// 					const unaryUnion = new jsts.operation.union.UnaryUnionOp(mergeRes);
// 					console.log(unaryUnion);
// 					const unionGeom = unaryUnion.union();
					
// 					//console.log(`union: ${this.stringWriter.write(unionGeom)}`);

					
// 					const poligonizer = new jsts.operation.polygonize.Polygonizer();
// 					poligonizer.add(unionGeom);
// 					const divided = poligonizer.getPolygons();
// 					this.sourceD.removeFeature(this.featOperazione);
// 					console.log(divided);
// 					divided.toArray().forEach(
// 						(x: any, i: number) => {
// 							const y = jsts.OverlayOp.intersection(x, mainGeom);
// 							this.addPolygon(x, y, i);
							
// 						}
// 					);
// 					this.mapController.removeLayer(this.vectorOperazione);
					
// 				}

// 			}
// 		},
// 		{
// 			nome: "Inizia riduzione",
// 			call: () => {
// 				const feats = this.selFeatures.getArray();
// 				if (feats.length == 1) {
// 					const f = feats[0];
// 					this.featOperazione = f;
// 					this.sourceOperazione =  new VectorSource({ wrapX: false });
// 					this.vectorOperazione = new VectorLayer(
// 						{
// 							source: this.sourceOperazione,
// 							visible: true
// 						}
// 					);
// 					this.mapController.addLayer(this.vectorOperazione);
// 					this.drawOperazione = new Draw(
// 						{
// 							source: this.sourceOperazione,
// 							type: 'Polygon',
// 							condition: (ev) => {
// 								const keyEvent = (
// 									ev.originalEvent
// 								);
// 								return !(keyEvent.altKey || keyEvent.ctrlKey);
// 							}
// 						}
// 					);
// 					this.moveOperazione = new Translate(
// 						{
// 							layers: [this.vectorOperazione],
// 							condition: (ev) => {
// 								const keyEvent = (
// 									ev.originalEvent
// 								);
// 								return keyEvent.altKey && !keyEvent.ctrlKey;
// 							}
// 						}
// 					);
// 					this.modifyOperazione = new Modify(
// 						{
// 							source: this.sourceOperazione,
// 							condition: (ev) => {
// 								const keyEvent = (
// 									ev.originalEvent
// 								);
// 								return !keyEvent.altKey && keyEvent.ctrlKey;
// 							}
// 						}
// 					);
// 					this.drawAbortOperazione = new DrawAbortOnEscape(this.drawOperazione, {});
// 					this.setInteractions([this.drawOperazione, this.moveOperazione, this.modifyOperazione, this.drawAbortOperazione]);
// 				}
// 				else {
// 					alert("Selezionare un poligono");
// 				}
// 			}
// 		},
// 		{
// 			nome: "Termina riduzione",
// 			call: () => {

// 				const initialGeom = this.featOperazione?.getGeometry();
// 				const holes = this.sourceOperazione.getFeatures();
// 				if (this.featOperazione && initialGeom && holes && holes.length > 0) {
					
// 					(this.parser as any).inject(
// 						Point, LineString, LinearRing, Polygon, MultiPoint, MultiLineString, MultiPolygon
// 					);
					
// 					let mainGeom = this.parser.read(initialGeom);
// 					console.log(`mainGeom: ${this.stringWriter.write(mainGeom)}`);
					
// 					const holesGeoms = holes.map(
// 						(x, i) => {
// 							const g = this.parser.read(x.getGeometry());
// 							console.log(`hole ${i}: ${this.stringWriter.write(g)}`);
// 							return g;
// 						}
// 					);
					
// 					holesGeoms.forEach(
// 						g => {
// 							mainGeom = jsts.OverlayOp.difference(mainGeom, g);
// 						}
// 					);

					
// 					this.sourceD.removeFeature(this.featOperazione);
// 					this.addPolygon(mainGeom, mainGeom, 0);
					
// 					this.mapController.removeLayer(this.vectorOperazione);
					
// 				}


// 			}
// 		}
// 	];
// 	faseCorrente: string = "";
// 	sourceOperazione!: VectorSource;
// 	vectorOperazione!: VectorLayer<VectorSource>;
// 	drawOperazione!: Draw;
// 	moveOperazione!: Translate;
// 	modifyOperazione!: Modify;
// 	drawAbortOperazione!: DrawAbortOnEscape;

// 	parser = new jsts.io.OL3Parser();
// 	stringWriter = new jsts.io.WKTWriter();

// 	addPolygon = (originalGeom: any, addGeom: any, i: number) => {
// 		if (addGeom instanceof jsts.geom.Polygon) {
// 			if (addGeom.getArea && addGeom.getArea() > 0 ) {
// 				console.log(`divided ${i}: ${this.stringWriter.write(addGeom)}`);
// 				console.log(`original ${i}: ${this.stringWriter.write(originalGeom)}`);
// 				console.log(addGeom);

// 				this.sourceD.addFeature(new Feature(this.parser.write(addGeom)));
// 			}
// 		}
// 		else {
// 			const n = addGeom.getNumGeometries();
// 			if (n > 1) {
// 				for (let j = 0; j < n; j++) {
// 					const g = addGeom.getGeometryN(j);
// 					if (g != addGeom) {
// 						this.addPolygon(originalGeom, g, j);
// 					}
// 				}
// 			}
// 		}
// 	}
	
// 	callAzione(azione: any) {
// 		this.faseCorrente = azione.nome;
// 		azione.call();
// 	}
	
// 	currentInteractions: Interaction[] = [];
// 	setInteractions(interactions: Interaction[]) {
// 		this.currentInteractions.forEach(
// 			(i: Interaction) => {
// 				this.mapController.removeInteraction(i);  
// 			}
// 		);
		
// 		this.currentInteractions = interactions;
		
// 		this.currentInteractions.forEach(
// 			(i: Interaction) => {
// 				this.mapController.addInteraction(i);  
// 			}
// 		);
// 	}

// }