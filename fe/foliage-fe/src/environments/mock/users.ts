import { mockUsersType } from "../defs/types";

export const mockUsers: mockUsersType = {
	loggedUser: "campelli",
	users: {
		"utente": {
			token: '<bearerTokenPerBackEnd>',
			userData: {
				sub: "<cf-utente>",
				aut: "APPLICATION_USER",
				country: "",
				birthdate: "",
				gender: "",
				iss: "",
				aud: "",
				upn: "utente",
				nbf: 1696311867,
				azp: "",
				scope: "openid",
				organization: "pec2@diprova.it",
				nickname: "<nome>",
				phone_number: "3211234567",
				exp: 1696315467,
				iat: 1696311867,
				family_name: "<cognome>",
				jti: "476e06cb-b305-40da-970a-84d2622179a6",
				email: "indirizzo@email.it"
			}
		}
	}
};