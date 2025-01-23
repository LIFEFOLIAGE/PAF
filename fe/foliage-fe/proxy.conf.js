var winston = require('winston'); //(1)

function logProvider() { //(2)
  return winston.createLogger({
    level: 'debug',
    format: winston.format.combine(
      winston.format.splat(),
      winston.format.simple()
    ),
    transports: [new winston.transports.Console()],
  });
}

var PROXY_CONF = {
	"/UmbriaBE": {
		"target": "http://localhost:8080",
		"secure": false,
		"logLevel": "debug",
		logProvider: logProvider, // (3)
		cookiePathRewrite: '/local/'
	},
	"/LazioBE": {
		"target": "http://localhost:8080",
		"secure": false,
		"logLevel": "debug",
		logProvider: logProvider, // (3)
		cookiePathRewrite: '/local/'
	},
	"/iam": {
		"target": "https://10.206.228.173:9443",
		"secure": false,
		"pathRewrite": {
			"^/iam": ""
		},
		"logLevel": "debug",
		logProvider: logProvider, // (3)
		cookiePathRewrite: '/local/'
	},
	"/mock": {
		"target": "http://localhost:4200/assets",
		"secure": false,
		"logLevel": "debug",
		logProvider: logProvider, // (3)
		cookiePathRewrite: '/local/'
	},
	"/geoserver": {
		"target": "http://10.206.228.173:8080",
		"secure": false,
		"logLevel": "debug",
		logProvider: logProvider, // (3)
		cookiePathRewrite: '/local/'
	}
};

module.exports = PROXY_CONF;
