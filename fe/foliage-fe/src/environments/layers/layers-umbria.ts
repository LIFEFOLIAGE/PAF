export const srids = {
	"EPSG:3004": "+proj=tmerc +lat_0=0 +lon_0=15 +k=0.9996 +x_0=2520000 +y_0=0 +ellps=intl +towgs84=-104.1,-49.1,-9.9,0.971,-2.917,0.714,-11.68 +units=m +no_defs",
	"EPSG:3003": "+proj=tmerc +lat_0=0 +lon_0=9 +k=0.9996 +x_0=1500000 +y_0=0 +ellps=intl +towgs84=-104.1,-49.1,-9.9,0.971,-2.917,0.714,-11.68 +units=m +no_defs",
	"EPSG:3035": "+proj=laea +lat_0=52 +lon_0=10 +x_0=4321000 +y_0=3210000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs +type=crs",
	"EPSG:3044": "+proj=utm +zone=32 +ellps=GRS80 +units=m +no_defs ",
	"EPSG:3046": "+proj=utm +zone=34 +ellps=GRS80 +units=m +no_defs ",
	"EPSG:3857": "+proj=merc +a=6378137 +b=6378137 +lat_ts=0 +lon_0=0 +x_0=0 +y_0=0 +k=1 +units=m +nadgrids=@null +wktext +no_defs +type=crs",
	"EPSG:4258": "+proj=longlat +ellps=GRS80 +no_defs +type=crs",
	"EPSG:4265": "+proj=longlat +ellps=intl +towgs84=-104.1,-49.1,-9.9,0.971,-2.917,0.714,-11.68 +no_defs +type=crs",
	"EPSG:6706": "+proj=longlat +ellps=GRS80 +no_defs +type=crs",
	"EPSG:6707": "+proj=utm +zone=32 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs +type=crs",
	"EPSG:32633": "+proj=utm +zone=33 +datum=WGS84 +units=m +no_defs +type=crs",
	"EPSG:32632": "+proj=utm +zone=32 +datum=WGS84 +units=m +no_defs +type=crs",
	"EPSG:25833": "+proj=utm +zone=33 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs +type=crs"
};

export const mapsSrid = "EPSG:3035";

const geoserverUrl = '<url-geoserver>';

export const layersUmbria = {
	crt: {
		wms: {
			url: `${geoserverUrl}/foliage/wms`,
			projection: "EPSG:4326",
			layerName: "foliage:CTR-UMBRIA",
			maxZoom: 18
		}
	},
	catasto: {
		wms: {
			url: `${geoserverUrl}/foliage/wms`,
			projection: "EPSG:4258",
			layerName: "CP.CadastralZoning,strade,acque,CP.CadastralParcel,vestizioni"
		}
	},
	orto: {
		// xyz: {
		// 	url: 'https://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}',
		// 	maxZoom: 20
		// }
		wms: {
			url: `${geoserverUrl}/foliage/wms`,
			projection: "EPSG:4326",
			layerName: "OI.ORTOIMMAGINI.2012.32,OI.ORTOIMMAGINI.2012.33"
		}
		// xyz: {
		// 	url: 'http://www.pcn.minambiente.it/arcgis/rest/services/immagini/ortofoto_colore_12/MapServer/tile/{z}/{y}/{x}',
		// 	projection: "EPSG:3857",
		// 	//url: 'http://www.pcn.minambiente.it/arcgis/rest/services/base_new/MapServer/tile/{z}/{y}/{x}'
		// }
	},
	osm: {
	},
	"HABITAT":{
		"wfs": {
			"url": `${geoserverUrl}/wfs`,
			"projection": "EPSG:6706",
			"layerName": "foliage:UMBRIA-nat2k_habitat_prioritari_208"
		}
	},
	"AREE_PROTETTE": {
		"wfs": {
			"url": `${geoserverUrl}/wfs`,
			"projection": "EPSG:4326",
			"layerName": "foliage:LAZIO-aree_protette_106"
		}
	},
	"NAT2K": {
		"wfs": {
			"url": `${geoserverUrl}/wfs`,
			"projection": "EPSG:4326",
			"layerName": "foliage:LAZIO-sitiprotetti_natura_2000"
		}
	},
	"PAI_RISCHIO_ALLUVIONE": {
		"wfs": {
			"url": `${geoserverUrl}/wfs`,
			"projection": "EPSG:4326",
			"layerName": "foliage:LAZIO-pai_rischio_alluvione_101"
		}
	},
	"PAI_RISCHIO_FRANA": {
		"wfs": {
			"url": `${geoserverUrl}/wfs`,
			"projection": "EPSG:4326",
			"layerName": "foliage:LAZIO-pai_rischio_frana_101"
		}
	},
	"PAI_RISCHIO_VALANGA": {
		"wfs": {
			"url": `${geoserverUrl}/wfs`,
			"projection": "EPSG:4326",
			"layerName": "foliage:LAZIO-pai_rischio_valanga_101"
		}
	},
	"AREE_PAESAGGISTICHE": {
		"wfs": {
			"url": `${geoserverUrl}/wfs`,
			"projection": "EPSG:4326",
			"layerName": "foliage:UMBRIA-aree_paesaggistiche_215"
		}
	}
};