package org.cobbzilla.wizard.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JavaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cobbzilla.util.json.JsonUtil;
import org.cobbzilla.wizard.filters.*;

import java.util.ArrayList;
import java.util.List;

import static org.cobbzilla.util.daemon.ZillaRuntime.die;
import static org.cobbzilla.util.daemon.ZillaRuntime.empty;

@NoArgsConstructor @AllArgsConstructor @Accessors(chain=true)
public class SearchResults<E> implements Scrubbable {

    public static final ScrubbableField[] SCRUBBABLE_FIELDS = new ScrubbableField[]{
            new ScrubbableField(SearchResults.class, "results.*", List.class)
    };
    @Override public ScrubbableField[] fieldsToScrub() { return SCRUBBABLE_FIELDS; }

    public static JavaType jsonType(Class klazz) {
        return JsonUtil.PUBLIC_MAPPER.getTypeFactory().constructParametricType(SearchResults.class, klazz);
    }

    @Getter @Setter private List<E> results = new ArrayList<>();
    @Getter @Setter private Integer totalCount;

    @JsonIgnore public int total() {
        if (totalCount == null) die("total is unknown");
        return totalCount;
    }

    @JsonIgnore public int count() { return empty(results) ? 0 : results.size(); }

    @JsonIgnore public boolean hasResults() { return !empty(results); }
    @JsonIgnore public boolean hasTotalCount() { return totalCount != null; }

    public SearchResults(List<E> results) { this.results = results; }

    public E getResult(int i) {
        return (i < 0 || i > results.size()-1) ? null : results.get(i);
    }

    public SearchResults<E> addResult (E result) {
        if (results == null) results = new ArrayList<>();
        results.add(result);
        return this;
    }

}
