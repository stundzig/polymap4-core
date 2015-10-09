package org.polymap.core.data.refine.impl;

import org.json.JSONObject;

public class CSVFormatAndOptions
        extends FormatAndOptions {

    public static CSVFormatAndOptions createDefault() {
        try {
            return new CSVFormatAndOptions( new JSONObject(
                    "{\"encoding\":\"ISO-8859-1\",\"separator\":\"\\t\",\"ignoreLines\":-1,\"headerLines\":1,\"skipDataLines\":0,\"limit\":-1,\"storeBlankRows\":false,"
                            + "\"guessCellValueTypes\":true,\"processQuotes\":true,\"storeBlankCellsAsNulls\":true,\"includeFileSources\":false}" ) );
        }
        catch (Exception e) {
            throw new RuntimeException( e );
        }
    }


    public CSVFormatAndOptions( JSONObject jsonObject ) {
        super( jsonObject );
    }


    public String encoding() {
        return store().optString( "encoding" );
    }


    public void setEncoding( String encoding ) {
        put( "encoding", encoding );
    }


    public String separator() {
        return store().optString( "separator" );
    }


    public void setSeparator( String separator ) {
        put( "separator", separator );
    }


}
