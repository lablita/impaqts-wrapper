/*
 * Copyright (C) 2024
 * EMMACorpus
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.drwolf.impaqts.wrapper.query;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

public class QueryPattern {
	// sono i blocchetti visuali
	ArrayList<QueryToken> tokPattern;

	// filtro alto livello. Esempio: tipo di implicito
	QueryStructure structPattern;

	public QueryPattern() {

	}

	@JsonIgnore
	public String getCql() {
		String cql = "";
		if (this.tokPattern != null && !this.tokPattern.isEmpty()) {
			// caso in cui ho esattamente 2 token e il secondo è un filtro con contesto
			if (this.tokPattern.size() == 2 && this.tokPattern.get(1).getIsFilter() && this.tokPattern.get(0)
					.getMinRepetitions() == 1 && this.tokPattern.get(0).getMaxRepetitions() == 1 && this.tokPattern.get(
					1).getMinRepetitions() == 1 && this.tokPattern.get(1).getMaxRepetitions() == 1) {
				cql = "(meet " + this.tokPattern.get(0).getCql() + " " + this.tokPattern.get(1).getCql();
				cql += " -" + this.tokPattern.get(1).getFilterContextLeft() + " " + this.tokPattern.get(1)
						.getFilterContextRight() + ")";
			}

			// caso normale
			else {
				for (QueryToken t : this.tokPattern) {
					cql += t.getCql();
				}
			}
			if (this.structPattern != null) {
				cql = this.structPattern.getCql(cql);
			}
		}
		// caso in cui faccio la query solo sulla struttura
		else if (this.structPattern != null) {
			cql = this.structPattern.getCql();
		}
		return cql;
	}

	public QueryStructure getStructPattern() {
		return this.structPattern;
	}

	public ArrayList<QueryToken> getTokPattern() {
		return this.tokPattern;
	}

	public void setStructPattern(QueryStructure structPattern) {
		this.structPattern = structPattern;
	}

	public void setTokPattern(ArrayList<QueryToken> tokPattern) {
		this.tokPattern = tokPattern;
	}
}
