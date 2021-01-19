package it.drwolf.impaqts.wrapper.query;

import java.util.ArrayList;

public class QueryPattern {
    ArrayList<QueryToken> tokPattern;
    QueryStructure structPattern;

    public QueryPattern() {

    }

    public ArrayList<QueryToken> getTokPattern() {
        return tokPattern;
    }

    public QueryStructure getStructPattern() {
        return structPattern;
    }

    public void setTokPattern(ArrayList<QueryToken> tokPattern) {
        this.tokPattern = tokPattern;
    }

    public void setStructPattern(QueryStructure structPattern) {
        this.structPattern = structPattern;
    }

    public String getCql() {
        String cql = "";
        if (tokPattern != null && !tokPattern.isEmpty()) {
            // caso in cui ho esattamente 2 token e il secondo è un filtro con contesto
            if (tokPattern.size() == 2 && tokPattern.get(1).getIsFilter()
                    && tokPattern.get(0).getMinRepetitions() == 1 && tokPattern.get(0).getMaxRepetitions() == 1
                    && tokPattern.get(1).getMinRepetitions() == 1 && tokPattern.get(1).getMaxRepetitions() == 1) {
                cql = "(meet " + tokPattern.get(0).getCql() + " " + tokPattern.get(1).getCql();
                cql += " -" + tokPattern.get(1).getFilterContextLeft() + " " + tokPattern.get(1).getFilterContextRight() + ")";
            }
            // caso normale
            else {
                for (QueryToken t : tokPattern) {
                    cql += t.getCql();
                }
            }
            if (structPattern != null) {
                cql = structPattern.getCql(cql);
            }
        }
        // caso in cui faccio la query solo sulla struttura
        else if (structPattern != null) {
            cql = structPattern.getCql();
        }
        return cql;
    }
}
