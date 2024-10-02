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

import java.util.List;

public class QueryStructure extends QueryElement {

	public QueryStructure() {
		super();
	}

	@Override
	public String getCql() {
		return this.getCql(null);
	}

	public String getCql(String cqlTokenPattern) {
		String cql = cqlTokenPattern;
		if (cql == null) {
			cql = "";
		}
		if (!this.tags.isEmpty()) {
			boolean first = true;
			for (List<QueryTag> andList : this.tags) {
				if (!first) {
					cql += ")";
				}
				if (first && cqlTokenPattern == null) {
					cql += " (";
				} else {
					cql += " within (";
				}
				for (QueryTag orEl : andList) {
					cql += "< " + orEl.getCql() + "/> | ";
				}
				cql = cql.substring(0, cql.length() - 3) + ")";
				first = false;
			}
			long count_cp = cql.chars().filter(ch -> ch == ')').count();
			long count_op = cql.chars().filter(ch -> ch == '(').count();
			long diff = count_cp - count_op;
			if (diff > 0) {
				for (long i = 0; i < diff; i++) {
					cql = "(" + cql;
				}
			}
		}
		return cql;
	}

}
