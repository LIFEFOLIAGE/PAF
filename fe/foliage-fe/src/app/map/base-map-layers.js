/*
function getLayerStyleWithFeatureLabel(feature) {
    let hexColor = feature.get('lColor');

    return new olStyleStyle({
        stroke: new olStyleStroke({
            color: hexColor,
            width: 2,
        }),
        fill: new olStyleFill({
            // color: hexToRGB(hexColor, 0.25)
            color: hexColor
        }),
        text: new olStyleText({
            font: '12px Calibri,sans-serif',
            fill: new olStyleFill({color: '#000000'}),
            backgroundFill: new olStyleFill({color: '#FFFFFF'}),
            text: feature.get('label'),
        })
    });
}
*/

/*
// @formatter:off
function distinctColor(numOfSteps, step) {
    let r, g, b;
    let h = step / numOfSteps;
    let i = ~~(h * 6);
    let f = h * 6 - i;
    let q = 1 - f;
    switch(i % 6){
        case 0: r = 1; g = f; b = 0; break;
        case 1: r = q; g = 1; b = 0; break;
        case 2: r = 0; g = 1; b = f; break;
        case 3: r = 0; g = q; b = 1; break;
        case 4: r = f; g = 0; b = 1; break;
        case 5: r = 1; g = 0; b = q; break;
    }
    let c = "#" + ("00" + (~~(r * 255)).toString(16)).slice(-2) + ("00" + (~~(g * 255)).toString(16)).slice(-2) + ("00" + (~~(b * 255)).toString(16)).slice(-2);
    return (c);
}
// @formatter:on
*/

// const style = {
// 	'stroke-color': ['*', ['get', 'COLOR'], [220, 220, 220]],
// 	'stroke-width': 3,
// 	'stroke-offset': -1,
// 	'fill-color': ['*', ['get', 'COLOR'], [255, 255, 255, 0.6]],
// };
// class WebGLLayer extends Layer {
// 	createRenderer() {
// 		return new WebGLVectorLayerRenderer(this, {
// 			style,
// 		});
// 	}
// }

// class WebGLLayer extends Layer {
//     createRenderer() {
//         return new WebGLVectorLayerRenderer(this, {
//             fill: {
//                 attributes: {
//                     color: function (feature) {
//                         const color = asArray(feature.get('COLOR') || '#e30e0e');
//                         color[3] = 0.85;
//                         return packColor(color);
//                     },
//                     opacity: function () {
//                         return 0.6;
//                     },
//                 },
//             },
//             stroke: {
//                 attributes: {
//                     color: function (feature) {
//                         const color = [...asArray(feature.get('COLOR') || '#2ac119')];
//                         color.forEach((_, i) => (color[i] = Math.round(color[i] * 0.75))); // darken slightly
//                         return packColor(color);
//                     },
//                     width: function () {
//                         return 1.5;
//                     },
//                     opacity: function () {
//                         return 1;
//                     },
//                 },
//             },
//         });
//     }
// }

/*
let bboxWithRatio = function (ratio) {
    let lastScaledExtent = [0, 0, 0, 0];
    return function (newExtent, resolution) {
        if (extentcontainsExtent(lastScaledExtent, newExtent)) {
            return [newExtent];
        } else {
            lastScaledExtent = extentclone(newExtent);
            extentscaleFromCenter(lastScaledExtent, ratio);
            return [lastScaledExtent];
        }
    };
};
*/

import VectorImageLayer from "ol/layer/VectorImage";
import {getLayerTitleFromStyle, style501, style506Alt, styleBoschi, styleRischiAlluvione} from "./base-map-adds";
import {Vector as olSourceVector} from "ol/source";
import GeoJSON from "ol/format/GeoJSON";
import {tile} from "ol/loadingstrategy";
import {createXYZ} from "ol/tilegrid";
import VectorSource from "ol/source/Vector";

const [nomeUsiCiviciLayer, usiCiviciLayer] = [
    'usi_civici',
    new VectorImageLayer({
        title: getLayerTitleFromStyle(style506Alt, "Usi civici"),
        visible: false,
        source: new olSourceVector(
            {
                format: new GeoJSON(/*{ dataProjection: 'EPSG:4326', featureProjection: 'EPSG:3857'}*/),
                url: function (newExtent) {
                    return (
                        'https://geoportale.regione.lazio.it/geoserver/geonode/usi_civici/wfs?service=WFS&' +
                        'version=1.1.0&request=GetFeature&typename=geonode:usi_civici&' +
                        'outputFormat=application/json&srsname=EPSG:3857&' +
                        'bbox=' +
                        newExtent.join(',') +
                        ',EPSG:3857'
                    );
                },
                strategy: tile(createXYZ({tileSize: 512})),
            }
        ),
        style: style506Alt,
        attributions: "Usi civici attributions"
    })];

const [nomeRischiAlluvioneLayer, rischiAlluvioneLayer] = [
    'rischi_alluvione',
    new VectorImageLayer({
        title: getLayerTitleFromStyle(styleRischiAlluvione, "Rischi alluvionali"),
        visible: false,
        source: new olSourceVector(
            {
                format: new GeoJSON(/*{ dataProjection: 'EPSG:4326', featureProjection: 'EPSG:3857'}*/),
                url: function (newExtent) {
                    return (
                        'http://wms.pcn.minambiente.it/ogc?map=/ms_ogc/wfs/PAI_rischio.map&service=WFS&' +
                        'version=1.1.0&request=GetFeature&typename=RN.PAI.RISCHIO.ALLUVIONE&' +
                        'srsname=EPSG:4326&' +
                        'bbox=' +
                        //'40.42049804842839933,13.54334090766002419,41.29077240349228362,14.27264548338861161' +
                        newExtent.join(',') +
                        ',EPSG:4326'
                    );
                },
                strategy: tile(createXYZ({tileSize: 512})),
            }
        ),
        style: styleRischiAlluvione,
        attributions: "Alluvione attributions"
    })];

const [nomeBoschiLayer, boschiLayer] = [
    'boschi',
    new VectorImageLayer({
        title: getLayerTitleFromStyle(styleBoschi, "Boschi"),
        visible: false,
        source: new VectorSource(
            {
                format: new GeoJSON(/*{ dataProjection: 'EPSG:4326', featureProjection: 'EPSG:3857'}*/),
                // url: function (newExtent) {
                //     return (
                //         'https://geoportale.regione.lazio.it/geoserver/geonode/boschi/wfs?service=WFS&' +
                //         'version=1.1.0&request=GetFeature&typename=geonode:boschi&' +
                //         'outputFormat=application/json&srsname=EPSG:3857&' +
                //         'bbox=' +
                //         newExtent.join(',') +
                //         ',EPSG:4326'
                //     );
                // },
                url: 'https://geoportale.regione.lazio.it/geoserver/geonode/boschi/wfs?service=WFS&version=1.1.0&request=GetFeature&typename=geonode:boschi&outputFormat=application/json&srsname=EPSG:3857',

                // strategy: tile(createXYZ({tileSize: 512})),
                // strategy: bbox,
                // strategy: bboxWithRatio(2)
            }
        ),
        style: styleBoschi,
        attributions: "Boschi attributions"
    })];

const [nomeCostaLaghiLayer, costaLaghiLayer] = [
    'costa_laghi',
    new VectorImageLayer({
        title: getLayerTitleFromStyle(style501, "Costa laghi"),
        visible: false,
        source: new olSourceVector(
            {
                format: new GeoJSON(/*{ dataProjection: 'EPSG:4326', featureProjection: 'EPSG:3857'}*/),
                url: function (newExtent) {
                    return (
                        'https://geoportale.regione.lazio.it/geoserver/geonode/costa_laghi/wfs?service=WFS&' +
                        'version=1.1.0&request=GetFeature&typename=geonode:costa_laghi&' +
                        'outputFormat=application/json&srsname=EPSG:3857&' +
                        'bbox=' +
                        newExtent.join(',') +
                        ',EPSG:4326'
                    );
                },
                crossOrigin: null,
                strategy: tile(createXYZ({tileSize: 512})),
            }
        ),
        style: style501,
        attributions: "Costa attributions"
    })];

export const indiceLayers = Object.fromEntries([
    [nomeCostaLaghiLayer, costaLaghiLayer],
    [nomeBoschiLayer, boschiLayer],
    [nomeRischiAlluvioneLayer, rischiAlluvioneLayer],
    [nomeUsiCiviciLayer, usiCiviciLayer]
]);
