package org.fao.geonet.kernel.search.index;

import java.io.IOException;
import java.util.List;

import jeeves.utils.Log;

import org.apache.lucene.document.Document;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NRTManager.TrackingIndexWriter;
import org.apache.lucene.search.TermQuery;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.LuceneIndexField;

public class LuceneIndexWriterFactory {

    private LuceneIndexLanguageTracker tracker;

    public LuceneIndexWriterFactory( LuceneIndexLanguageTracker tracker ) {
        this.tracker = tracker;
    }

    public void commit() throws Exception {
        tracker.commit();
    }

    public void addDocument( String locale, Document doc, List<CategoryPath> categories ) throws Exception {
        if(Log.isDebugEnabled(Geonet.INDEX_ENGINE)) {
            Log.debug(Geonet.INDEX_ENGINE, "Adding document to "+locale+" index");
        }
        tracker.addDocument(locale, doc, categories);
    }

    /*public void deleteDocuments( final Term term ) throws Exception {
        if(Log.isDebugEnabled(Geonet.INDEX_ENGINE)) {
            Log.debug(Geonet.INDEX_ENGINE, "deleting term '"+term+"' from index");
        }
        tracker.withWriter(new Function() {
            @Override
            public void apply(TaxonomyWriter taxonomyWriter, TrackingIndexWriter input) throws CorruptIndexException, IOException {
                    input.deleteDocuments(term);
            }
        });
    }*/

    public void deleteDocuments( final Term term, boolean workspace ) throws Exception {
        if(Log.isDebugEnabled(Geonet.INDEX_ENGINE)) {
            Log.debug(Geonet.INDEX_ENGINE, "deleting term '"+term+"' from index");
        }
        final BooleanQuery query = new BooleanQuery();
        query.add(new TermQuery(term), BooleanClause.Occur.MUST);
        if(workspace) {
            query.add(new TermQuery(new Term(LuceneIndexField._IS_WORKSPACE, "true")), BooleanClause.Occur.MUST);
        }
        else {
            query.add(new TermQuery(new Term(LuceneIndexField._IS_WORKSPACE, "true")), BooleanClause.Occur.MUST_NOT);
        }

        tracker.withWriter(new Function() {
            @Override
            public void apply(TaxonomyWriter taxonomyWriter, TrackingIndexWriter input) throws CorruptIndexException, IOException {
                input.deleteDocuments(query);
            }
        });
    }


    public void createDefaultLocale() throws IOException {
        tracker.open(Geonet.DEFAULT_LANGUAGE);
    }

}