import { Duration, Period } from "@js-joda/core";

export class PeriodDuration {
	public period: Period;
	public duration: Duration;
	constructor(p: Period, d: Duration) {
		this.period = p;
		this.duration = d;
	}
	static parse(s: string) : PeriodDuration {
		const parts = s.split("/");
		if (parts.length == 2) {
			const perString = parts[0];
			const perParts = perString.split("-");
			
			if (perParts.length != 3) {
				throw new Error("Formato dell'intervallo non riconosciuto (1)")
			}
			const years : number = Number.parseInt(perParts[0]);
			const months : number = Number.parseInt(perParts[1]);
			const days : number = Number.parseInt(perParts[2]);
			const period: Period = Period.of(years, months, days);

			const durString = parts[1];
			const durParts = durString.split(":");
			if (durParts.length != 3) {
				throw new Error("Formato dell'intervallo non riconosciuto (2)")
			}
			const hours: number = Number.parseInt(durParts[0]);
			const minutes: number = Number.parseInt(durParts[1]);

			const secString: string = durParts[2];
			const secParts: string[] = secString.split(".");
			const secPartsLen = secParts.length;
			if (secPartsLen > 2) {
				throw new Error("Formato dell'intervallo non riconosciuto (3)")
			}
			
			const seconds: number = Number.parseInt(secParts[0]);
			const millis: number = (secPartsLen == 1) ? 0 : Number.parseInt(secParts[1].padEnd(3, "0"));

			const duration: Duration = Duration.ofMillis(
				millis
				+ (
					1000 * (
						seconds
						+ (
							60 * (
								minutes + (
									60 * hours
								)
							)
						)
					)
				)
			);
			return new PeriodDuration(period, duration);
		}
		else {
			throw new Error("Formato dell'intervallo non riconosciuto (0)")
		}
	}
}
