export type tokenDataType = Record<string, any>;

export type mockUserType = {
	token: string,
	userData: tokenDataType
};

export type mockUsersType = (
	{
		loggedUser: string;
		users: Record<
			string,
			mockUserType
		>
	}
);
