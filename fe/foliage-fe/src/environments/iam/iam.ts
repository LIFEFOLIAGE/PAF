export const iamConfig : Record<string, any>= {
	issuer: '<url-oauth2-discovery>',
	redirectUri: '<indirizzo-host-pubblicazione>:<porta-pubblicazione>/<path-pubblicazione-paf>',
	logoutUrl: '<indirizzo-host-pubblicazione>:<porta-pubblicazione>/<path-pubblicazione-paf>',
	clientId: '<client-id>',
	dummyClientSecret: '<client-secret>',
	scope: 'openid',
	responseType: 'code',
	sessionChecksEnabled: false,
	strictDiscoveryDocumentValidation: false,
	skipIssuerCheck: true,
	requireHttps: false,
	showDebugInformation: true,
	useHttpBasicAuth: true,
	timeoutFactor: 0.5
};