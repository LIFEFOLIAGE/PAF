import {Feature as olFeature} from 'ol';
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

export function checkAreaInUo(featArea, featUo) {

}