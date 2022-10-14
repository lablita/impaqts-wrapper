package it.drwolf.impaqts.wrapper.query;

import java.util.List;

// rappresenta un token in CQL: ciò che c'è tra quadre: []
public class QueryToken extends QueryElement {

	int minRepetitions = 1; // ripetizioni di questo token, da minRepetitions a maxRepetitions:
	int maxRepetitions = 1; // es. []{2,3} oppure []{0,1} se opzionale
	boolean isFilter = false;
	int filterContextLeft = 0;
	int filterContextRight = 0;
	boolean optional = false;

	public QueryToken() {
		super();
	}

	@Override
	public String getCql() {
		String cql = "";
		if (!this.tags.isEmpty()) {
			if (this.tags.get(0).stream().filter(tag -> tag.getName().equals(QueryTag.CQL)).count() > 0) {
				return this.tags.get(0).get(0).getValue();
			}
			if (this.tags.get(0).stream().filter(tag -> tag.getName().equals(QueryTag.PHRASE)).count() > 0) {
				cql = this.tags.get(0).get(0).getCQLPhrase();
			} else if (this.tags.get(0).stream().filter(tag -> tag.getName().equals(QueryTag.CHARACTER)).count() > 0) {
				cql = this.tags.get(0).get(0).getCQLCharacter();
			} else {
				cql = "[";
				for (List<QueryTag> andList : this.tags) {
					cql += "(";
					for (QueryTag orEl : andList) {
						cql += orEl.getCql() + " | ";
					}
					cql = cql.substring(0, cql.length() - 3) + ")";
					cql += " & ";
				}
				cql = cql.substring(0, cql.length() - 3);
				cql = cql + "]";
			}
		} else {
			cql = "[]";
		}
		if (this.minRepetitions != 1 || this.maxRepetitions != 1) {
			cql = cql + "{" + this.minRepetitions + "," + this.maxRepetitions + "}";
		} else if (this.optional) {
			cql = cql + "?";
		}
		return cql;
	}

	public int getFilterContextLeft() {
		return this.filterContextLeft;
	}

	public int getFilterContextRight() {
		return this.filterContextRight;
	}

	public boolean getIsFilter() {
		return this.isFilter;
	}

	public int getMaxRepetitions() {
		return this.maxRepetitions;
	}

	public int getMinRepetitions() {
		return this.minRepetitions;
	}

	public boolean getOptional() {
		return this.optional;
	}

	public void setFilterContextLeft(int filterContextLeft) {
		this.filterContextLeft = filterContextLeft;
	}

	public void setFilterContextRight(int filterContextRight) {
		this.filterContextRight = filterContextRight;
	}

	public void setIsFilter(boolean isFilter) {
		this.isFilter = isFilter;
	}

	public void setMaxRepetitions(int maxRepetitions) {
		this.maxRepetitions = maxRepetitions;
	}

	public void setMinRepetitions(int minRepetitions) {
		this.minRepetitions = minRepetitions;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

}
