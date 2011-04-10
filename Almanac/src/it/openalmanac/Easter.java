/*
 * Copyright (C) 2008 Marco Ratto
 *
 * This file is part of the project EasterJ2ME.
 *
 * EasterJ2ME is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 *
 * EasterJ2ME is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EasterJ2ME; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package it.openalmanac;

import java.util.Calendar;
import java.util.Date;

/**
 * 
 * Questo algoritmo, sviluppato dal matematico tedesco Carl Friedrich Gauss,
 * e fornisce direttamente la data della Pasqua. L'anno di cui si calcola la Pasqua
 * sia contrassegnato da Y; mod è l'operatore modulo che restituisce il resto
 * della divisione fra numeri interi (ad esempio, 13 mod 5 = 3 perchè 13
 * diviso 5 fa 2 con resto 3).
 * 
 * Si calcolano dapprima a, b e c nel seguente modo:
 * 
 * a = Y mod 19 b = Y mod 4 c = Y mod 7
 * 
 * Poi si calcolano
 * 
 * d = (19a + M) mod 30 e = (2b + 4c + 6d + N) mod 7
 * 
 * Secondo il calendario giuliano si deve usare M = 15 e N = 6, mentre per il
 * calendario gregoriano i valori di M and N variano a seconda degli anni
 * considerati, secondo la seguente tabella:
 * 
 * Anni M N 1583-1699 22 2 1700-1799 23 3 1800-1899 23 4 1900-2099 24 5
 * 2100-2199 24 6 2200-2299 25 0 2300-2399 26 1 2400-2499 25 1
 * 
 * Se d + e < 10, allora la Pasqua cade il giorno (d + e + 22) del mese di
 * marzo, altrimenti si verificherà il (d + e - 9)-esimo giorno del mese di
 * aprile.
 * 
 * Si tenga tuttavia conto delle seguenti eccezioni:
 * 
 * Se la data risultante dalla formula è il 26 aprile, allora la Pasqua
 * cadrà il giorno 19 aprile; Se la data risultante dalla formula è il 25
 * aprile e contemporaneamente d = 28, e = 6 e a > 10, allora la Pasqua cadrà
 * il 18 aprile.
 * 
 * Esempio: Data della Pasqua 2007 secondo il calendario gregoriano, in uso in
 * Italia (quindi M = 24, N = 5)
 * 
 * a = 2007 mod 19 = 12 b = 2007 mod 4 = 3 c = 2007 mod 7 = 5
 * 
 * d = (19 x 12 + 24) mod 30 = 12 e = (2 x 3 + 4 x 5 + 6 x 12 + 5) mod 7 = 5
 * 
 * Siccome d + e = 12 + 5 = 17 > 10, allora nel 2007 Pasqua cadra' il (12 + 5 -
 * 9) = 8 aprile.
 * 
 * @author Marco Ratto
 * 
 */
public class Easter {

	public final static boolean isEaster(Date date) throws EasterException {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int year = calendar.get(Calendar.YEAR);
		int dateYMD = year * 10000 + calendar.get(Calendar.MONTH) * 100
				+ calendar.get(Calendar.DAY_OF_MONTH);
		Date easter = find(year);
		calendar.setTime(easter);
		int easterYMD = year * 10000 + calendar.get(Calendar.MONTH) * 100
				+ calendar.get(Calendar.DAY_OF_MONTH);
		return (easterYMD == dateYMD);
	}

	public final static Date find(int year) throws EasterException {

		if ((year < 1573) || (year > 2499)) {
			throw new EasterException("Year out of range [1753-2499]");
		}

		int a = year % 19;
		int b = year % 4;
		int c = year % 7;

		int m = 0;
		int n = 0;

		if ((year >= 1583) && (year <= 1699)) {
			m = 22;
			n = 2;
		}
		if ((year >= 1700) && (year <= 1799)) {
			m = 23;
			n = 3;
		}
		if ((year >= 1800) && (year <= 1899)) {
			m = 23;
			n = 4;
		}
		if ((year >= 1900) && (year <= 2099)) {
			m = 24;
			n = 5;
		}
		if ((year >= 2100) && (year <= 2199)) {
			m = 24;
			n = 6;
		}
		if ((year >= 2200) && (year <= 2299)) {
			m = 25;
			n = 0;
		}
		if ((year >= 2300) && (year <= 2399)) {
			m = 26;
			n = 1;
		}
		if ((year >= 2400) && (year <= 2499)) {
			m = 25;
			n = 1;
		}

		int d = (19 * a + m) % 30;
		int e = (2 * b + 4 * c + 6 * d + n) % 7;

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);

		if (d + e < 10) {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, Calendar.MARCH);
			calendar.set(Calendar.DAY_OF_MONTH, d + e + 22);
		} else {
			calendar.set(Calendar.MONTH, Calendar.APRIL);
			int day = d + e - 9;
			if (26 == day) {
				day = 19;
			}
			if ((25 == day) && (28 == d) && (e == 6) && (a > 10)) {
				day = 18;
			}
			calendar.set(Calendar.DAY_OF_MONTH, day);
		}

		return calendar.getTime();
	}
}