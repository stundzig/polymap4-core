package org.polymap.core.data.refine.impl;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Maps;
import com.google.refine.util.JSONUtilities;

public class FormatAndOptions {

    private final JSONObject store;


    public FormatAndOptions() {
        try {
            store = new JSONObject(
                    "{\"encoding\":\"\",\"separator\":\"\\t\",\"ignoreLines\":-1,\"headerLines\":1,\"skipDataLines\":0,\"limit\":-1,\"storeBlankRows\":true,"
                            + "\"guessCellValueTypes\":false,\"processQuotes\":true,\"storeBlankCellsAsNulls\":true,\"includeFileSources\":false}" );
        }
        catch (JSONException e) {
            throw new RuntimeException( e );
        }
    }


    public FormatAndOptions( JSONObject jsonObject ) {
        store = jsonObject;
    }


    public String encoding() {
        return store.optString( "encoding" );
    }


    public void setEncoding( String encoding ) {
        put( "encoding", encoding );
    }


    public String separator() {
        return store.optString( "separator" );
    }


    public void setSeparator( String separator ) {
        put( "separator", separator );
    }


    private void put( String key, Object value ) {
        try {
            store.put( key, value );
        }
        catch (JSONException e) {
            throw new RuntimeException( e );
        }
    }


    public JSONObject toJSON() {
        return store;
    }


    public String format() {
        return store.optString( "format" );
    }


    public void setFormat( String format ) {
        put( "format", format );
    }
}
