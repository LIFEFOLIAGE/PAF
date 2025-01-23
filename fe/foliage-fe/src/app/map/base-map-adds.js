import {
    Circle as olStyleCircle,
    Fill,
    Fill as olStyleFill,
    Icon,
    RegularShape, Stroke,
    Stroke as olStyleStroke,
    Style,
    Style as olStyleStyle, Text as olStyleText
} from "ol/style";
import {hexToRGB} from "./base-map";
import Legend from "ol-ext/legend/Legend";
import FillPattern from "ol-ext/style/FillPattern";

const larghezzaStrada = 8;
const spessoreBordoPoligono = 2;

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


//poligono blu scuro, il riempimento più chiaro
//costa mare
export const style501 = new olStyleStyle({
    stroke: new olStyleStroke({
        color: '#0b2078',
        width: spessoreBordoPoligono,
    }),
    fill: new olStyleFill({
        color: '#304fd4'
    })
});

//costa laghi
export const style502 = style501;

//linea blu
export const style503 = new olStyleStyle({
    stroke: new olStyleStroke({
        color: '#213795FF',
        width: larghezzaStrada,
    })
});

// poligono senza riempimento e con bordo blu
export const style504 = new olStyleStyle({
    stroke: new olStyleStroke({
        color: '#213795FF',
        width: spessoreBordoPoligono,
    })
});

export const style505 = stripeStyle('#964b00ff', 'transparent', '#964b007f');

// Poligono trasparente con bordo tratteggiato arancione. Il riempimento è a linee oblique.
export const style506 = bordoTratteggiatoStripeStyle('#ff8000', 'transparent', 'rgba(255,130,0,0.5)');

// polygon con bordo blu, riempimento blu con dei quadrati
// export const style507 = squareStyle('#213795FF', 'rgba(33,55,149,0.1)', 'rgba(33,55,149,0.5)');
export const style507 = squareStyle('#2137950', 'rgb(47,76,203)', 'rgb(14,24,71)');// TODO: test no alpha

// linee viola
export const style508 = new olStyleStyle({
    stroke: new olStyleStroke({
        color: '#891fd5',
        width: larghezzaStrada,
    })
});

// poligono viola, riempimento a linee orizzontali e verticali
// export const style509 = lineeOrizVertStyle('#891fd5ff', 'transparent', '#891fd57f');
export const style509 = lineeOrizVertStyle('#891fd500', '#891fd5', '#310b4c');// TODO: test no alpha

// poligono viola, riempimento a punti
// export const style510 = puntiStyle('#891fd5ff', 'transparent', '#891fd57f');
export const style510 = puntiStyle('#891fd500', '#891fd5', '#310b4c');// TODO: test no alpha

// punti viola TODO
export const style511 = new Style({
    image: new olStyleCircle({
        radius: 10,
        fill: new olStyleFill({
            color: '#891FD5FF',
        }),
    })
});

// poligono vuoto con bordo viola
export const style512 = new olStyleStyle({
    stroke: new olStyleStroke({
        color: '#891fd5',
        width: spessoreBordoPoligono,
    })
});

// Triangoli marroni TODO da testare
export const style513 = new Style({
    image: new RegularShape({
        fill: new Fill({color: '#964b00ff'}),
        stroke: new Stroke({color: '#964b00ff', width: 2}),
        points: 3,
        radius: 10,
        // rotation: Math.PI / 4,
        angle: 0,
    })
});

// poligono rosso, riempimento puntinato
// export const style514 = puntiStyle('#BF0909FF', 'transparent', '#BF09097f');
export const style514 = puntiStyle('#BF090900', '#BF0909', '#520505');// TODO: test no alpha

// Triangoli verde TODO da testare
export const style515 = new Style({
    image: new RegularShape({
        fill: new Fill({color: '#114c04'}),
        stroke: new Stroke({color: '#114c04', width: 2}),
        points: 3,
        radius: 10,
        // rotation: Math.PI / 4,
        angle: 0,
    })
});
// poligono verde, riempimento puntinato
// export const style516 = puntiStyle('#2ABF09FF', 'transparent', '#2ABF097F');
export const style516 = puntiStyle('#2ABF09FF', '#2ABF09FF', '#114c04');// TODO: test no alpha

// poligono verde, riempimento verde e con linee orizzontali
// export const style517 = lineeOrizStyle('#2ABF0900', '#2ABF0933', '#2ABF097F');
export const style517 = lineeOrizStyle('#2ABF0900', '#2abf09', '#124e04'); // TODO: test no alpha

//poligono rosso, riempimento quadrettato
// export const style518 = squareStyle('#d00303ff', '#D003031A', '#D0030380');
export const style518 = squareStyle('#d0030300', '#D0030300', '#D00303ff');// TODO: test no alpha

//poligono rosso, riempimento con linee tratteggiate oblique
// export const style519 = stripeStyleTrattegg('#d00303ff', '#D003031A', '#D0030380');
export const style519 = stripeStyleTrattegg('#d0030300', '#D00303ff', '#440202');// TODO: test no alpha


// TODO:
// export const styleRischiAlluvione = stripeStyleTrattegg('#213795FF', '#2137951A', '#21379580');
export const styleRischiAlluvione = stripeStyleTrattegg('#21379500', '#213795ff', '#121d50');// TODO: test no alpha

/*
function getLayerStyle(hexColor) {
    return new olStyleStyle({
        stroke: new olStyleStroke({
            color: hexColor,
            width: spessoreBordoPoligono,
        }),
        fill: new olStyleFill({
            color: hexToRGB(hexColor, 1)
        })
    });
}
*/

export function getLayerTitleFromStyle(style, title) {
    return `<img class="h-100" src="${Legend.getLegendImage({
        // width: 25,
        // height: 25,
        style: style,
        typeGeom: "Polygon",
    }).toDataURL()}"/>${title}`;
}

/*
export function getLayerTitleFromColor(color, title) {
    return `<img class="h-100" src="${Legend.getLegendImage({
        // width: 25,
        // height: 25,
        style: getLayerStyle(color),
        typeGeom: "Polygon",
    }).toDataURL()}"/>${title}`;
}
*/

export function getLayerTitleForGroup(groupName, codLayer, title) {
    //"codLayer": "513",
    // groupName "nome": "PUNTI_ARCHEOLOGICI_TIPIZZATI",
    return `<img class="h-100" src="${Legend.getLegendImage({
        style: getLayerStyleForGroup(groupName), // todo: mettere lo stile corretto
        typeGeom: mappedStyles[groupName].g ?? "Polygon", // todo verifica poligon e linee e altro
    }).toDataURL()}"/>${title}`;
}



export function getLayerStyleForGroup(feature) {
    let nome = "";
    if (feature.charAt) {
        nome = feature;
    } else {
        nome = feature.get('nome'); // PAI_RISCHIO_ALLUVION
    }

    let style = mappedStyles[nome]?.s;
    if (style !== undefined) {
        return style;
    }

    const nomeColor = getRandomColor(nome);

    return new Style({
        fill: new FillPattern({
            pattern: "hatch",
            ratio: 1,
            color: nomeColor,
            offset: 3,
            scale: 1,
            fill: new Fill({color: hexToRGB(nomeColor, 0.25)}),
            size: 5,
            spacing: 10,
            angle: -45
        })
    });


    /*
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
    */
}

//http://viglino.github.io/ol-ext/examples/style/map.style.pattern.html
// ["hatch", "cross", "dot", "circle", "square", "tile"]
export const testStyle = [new Style({
    fill: new FillPattern({
        pattern: "hatch",
        ratio: 1,
        color: "red",
        offset: 3,
        scale: 1,
        fill: new Fill({color: 'blue'}),
        size: 5,
        spacing: 10,
        angle: -45
    })
})];

export function getTestStyle() {
    const p = "tile";
    return [new Style({
        fill: new FillPattern({
            pattern: p,
            image: undefined,
            ratio: 1,
            icon: undefined,
            color: "blue",
            offset: 0,
            scale: 1,
            fill: new Fill({color: "rgba(255,255,255,0.3)"}),
            size: 5,
            spacing: 10,
            angle: 0
        })
    })];
}


// polygon con bordo blu, riempimento blu con dei quadrati
export const style507Alt = [
    new Style({
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
            fill: new Fill({color: "rgb(47,76,203)"}),
            size: 8,
            spacing: 16,
            angle: 0
        })
    })
];

//poligono rosso, riempimento quadrettato
// export const style518 = squareStyle('#d00303ff', '#D003031A', '#D0030380');
export const style518Alt = [
    new Style({
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
    })
];

// Poligono trasparente con bordo tratteggiato arancione. Il riempimento è a linee oblique.
export const style506Alt = [
    new Style({
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
    })
];

// Poligono trasparente con bordo tratteggiato arancione. Il riempimento è a linee oblique.
export const styleBoschi = [
    new Style({
        fill: new FillPattern({
            pattern: "hatch",
            ratio: 1,
            color: '#124e04',
            offset: 3,
            scale: 1,
            size: 5,
            spacing: 10,
            angle: 90,
            fill: new Fill({color: '#2abf09'}),
        })
    })
];

export const viabilita1 = (feature) => [
    new olStyleStyle(
        {
            stroke: new olStyleStroke({
                color: "#000000",
                lineCap: 'butt',
                width: larghezzaStrada,
                zIndex: 0,
            }),
        }
    ),
    new olStyleStyle(
        {
            stroke: new olStyleStroke({
                color: "#ff0000",
                lineCap: 'butt',
                width: larghezzaStrada * 0.5,
                zIndex: 1,
            }),
        }
    ),
];

export const viabilita2 = (feature) => [
    new olStyleStyle(
        {
            stroke: new olStyleStroke({
                color: "#000000",
                lineCap: 'butt',
                width: larghezzaStrada,
                zIndex: 0,
            }),
        }
    ),
    new olStyleStyle(
        {
            stroke: new olStyleStroke({
                color: "#ffff00",
                lineCap: 'butt',
                width: larghezzaStrada * 0.5,
                zIndex: 1,
            }),
        }
    ),
];
export const viabilita3 = (feature) => [
    new olStyleStyle(
        {
            stroke: new olStyleStroke({
                color: "#ff0000",
                lineCap: 'butt',
                width: larghezzaStrada,
                zIndex: 0,
            }),
        }
    ),
    new olStyleStyle(
        {
            stroke: new olStyleStroke({
                color: "#ffffff",
                lineCap: 'butt',
                width: larghezzaStrada * 0.5,
                zIndex: 1,
            }),
        }
    )
];
export const viabilita4 = (feature) => [
    new olStyleStyle(
        {
            stroke: new olStyleStroke({
                color: "#0000ff",
                lineCap: 'butt',
                width: larghezzaStrada,
                lineDash: [larghezzaStrada * 3, larghezzaStrada * 2],
            }),
        }
    ),
];

// @formatter:off
//TODO: verificare gli undefined - messi a mano
const mappedStyles = {
    ACQUE_PUBBLICHE:                        { s: style503,    g: 'LineString'},
    ACQUE_PUBBLICHE_RISPETTO:               { s: style504,    g: 'Polygon'},
    ALTIMETRIA_1200:                        { s: style505,    g: 'Polygon'},
    AREE_PROTETTE:                          { s: undefined,   g: 'Polygon'},
    BOSCHI:                                 { s: style517,    g: 'Polygon'},
    COSTA_LAGHI:                            { s: style502,    g: 'Polygon'},
    COSTA_MARE:                             { s: style501,    g: 'Polygon'},
    DECRETI_ARCHEOLOGICI:                   { s: style514,    g: 'Polygon'},
    EX_1497_AB:                             { s: style518Alt, g: 'Polygon'},
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
    USI_CIVICI:                             { s: style506Alt, g: 'Polygon'},
    ZONE_UMIDE:                             { s: style507Alt, g: 'Polygon'},
};
// @formatter:on
