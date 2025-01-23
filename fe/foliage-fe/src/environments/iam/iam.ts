export const iamConfig : Record<string, any>= {
	issuer: '<url-oauth2-discovery>',
	redirectUri: '<indirizzo-host-pubblicazione>:<porta-pubblicazione>/<path-pubblicazione-paf>',
	logoutUrl: '<indirizzo-host-pubblicazione>:<porta-pubblicazione>/<path-pubblicazione-paf>',
	clientId: '<client-id>',
	dummyClientSecret: '<client-secret>',
	scope: 'openid',
	responseType: 'code',
	sessionChecksEnabled: false,
	// begin: richiesto se il documento non e' completamente conforme
	strictDiscoveryDocumentValidation: false,
	skipIssuerCheck: true,
	// end: richiesto se il documento non e' completamente conforme
	requireHttps: false,
	showDebugInformation: true,
	//disablePKCE: false,
	useHttpBasicAuth: true,
	timeoutFactor: 0.5
};