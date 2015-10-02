package org.polymap.core.data.refine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.polymap.core.data.refine.impl.FormatAndOptions;
import org.polymap.core.data.refine.impl.ImportResponse;
import org.polymap.core.data.refine.impl.RefineServiceImpl;
import org.polymap.core.data.refine.json.JSONUtil;

import com.google.common.collect.Maps;
import com.google.refine.commands.importing.CreateImportingJobCommand;
import com.google.refine.commands.importing.ImportingControllerCommand;
import com.google.refine.commands.project.GetModelsCommand;
import com.google.refine.commands.row.GetRowsCommand;
import com.google.refine.model.ColumnModel;
import com.google.refine.model.Row;

public class ImportCSVTest {

    private RefineServiceImpl service;


    @Before
    public void setUp() {
        service = RefineServiceImpl.INSTANCE( new File( System.getProperty( "java.io.tmpdir" ) ) );
    }


    @Test
    @Ignore
    public void testTheLongWay() throws JSONException, FileNotFoundException {
        Object response = service.post( CreateImportingJobCommand.class, null );
        assertEquals( "{ \"jobID\" : 1 }", response.toString() );

        // http://127.0.0.1:3333/command/core/importing-controller?controller=core%2Fdefault-importing-controller&jobID=1&subCommand=load-raw-data
        // Map<String,String> params = Maps.newHashMap();
        // params.put( "jobID", "1" );
        // params.put( "subCommand", "load-raw-data" );
        // params.put( "controller", "core/default-importing-controller" );
        // response = service.post( ImportingControllerCommand.class, params );
        // System.out.println( response );

        File wohngebiete = new File(
                this.getClass().getResource( "/data/wohngebiete_sachsen.csv" ).getFile() );
        response = service.importFile( new FileInputStream( wohngebiete ), "wohngebiete_sachsen.csv", "text/csv" );
        assertTrue( response.toString().startsWith( "{\"code\":\"ok\"" ) );
        JSONObject jsonResponse = new JSONObject( response.toString() );

        JSONObject fileJson = JSONUtil.getObject( jsonResponse,
                "job.config.retrievalRecord.files[0]" );
        assertNotNull( fileJson );
        assertEquals( "wohngebiete_sachsen.csv", fileJson.getString( "fileName" ) );
        assertEquals( "text/line-based/*sv", fileJson.getString( "format" ) );

        // file loaded, create the file parser parts
        Map<String,String> params = Maps.newHashMap();
        params.put( "jobID", "1" );
        params.put( "subCommand", "initialize-parser-ui" );
        params.put( "controller", "core/default-importing-controller" );
        params.put( "format", fileJson.getString( "format" ) );
        response = service.post( ImportingControllerCommand.class, params );
        jsonResponse = new JSONObject( response.toString() );
        assertEquals( "\\t", JSONUtil.getString( jsonResponse, "options.separator", null ) );

        // update/initialize all options
        params.clear();
        params.put( "jobID", "1" );
        params.put( "subCommand", "update-format-and-options" );
        params.put( "controller", "core/default-importing-controller" );
        params.put( "format", fileJson.getString( "format" ) );
        params.put( "options",
                "{\"encoding\":\"\",\"separator\":\"\\t\",\"ignoreLines\":-1,\"headerLines\":1,\"skipDataLines\":0,\"limit\":-1,\"storeBlankRows\":true,"
                        + "\"guessCellValueTypes\":false,\"processQuotes\":true,\"storeBlankCellsAsNulls\":true,\"includeFileSources\":false}" );
        response = service.post( ImportingControllerCommand.class, params );
        jsonResponse = new JSONObject( response.toString() );
        assertEquals( "ok", JSONUtil.getString( jsonResponse, "status", null ) );

        params.clear();
        params.put( "importingJobID", "1" );
        response = service.post( GetModelsCommand.class, params );
        jsonResponse = new JSONObject( response.toString() );
        assertEquals(
                JSONUtil.getString( jsonResponse, "columnModel.columns[0].originalName", null ),
                JSONUtil.getString( jsonResponse, "columnModel.columns[0].name", null ) );

        params.clear();
        params.put( "importingJobID", "1" );
        params.put( "start", "0" );
        params.put( "limit", "5" );
        response = service.post( GetRowsCommand.class, params );
        jsonResponse = new JSONObject( response.toString() );
        assertEquals( "Baugenehmigungen: Neue Wohn-u.Nichtwohngeb. einschl. Wohnh.,;;;;;;;;;;;",
                JSONUtil.getString( jsonResponse, "rows[0].cells[0].v", null ) );
        assertEquals( new Integer( 100 ), JSONUtil.getInteger( jsonResponse, "total", null ) );
        assertEquals( new Integer( 0 ), JSONUtil.getInteger( jsonResponse, "start", null ) );
        assertEquals( new Integer( 5 ), JSONUtil.getInteger( jsonResponse, "limit", null ) );

        // get loaded models again

        // try with ;
        params.clear();
        params.put( "jobID", "1" );
        params.put( "subCommand", "update-format-and-options" );
        params.put( "controller", "core/default-importing-controller" );
        params.put( "format", fileJson.getString( "format" ) );
        params.put( "options",
                "{\"encoding\":\"\",\"separator\":\";\",\"ignoreLines\":-1,\"headerLines\":1,\"skipDataLines\":0,\"limit\":-1,\"storeBlankRows\":true,"
                        + "\"guessCellValueTypes\":false,\"processQuotes\":true,\"storeBlankCellsAsNulls\":true,\"includeFileSources\":false}" );
        response = service.post( ImportingControllerCommand.class, params );
        jsonResponse = new JSONObject( response.toString() );
        assertEquals( "ok", JSONUtil.getString( jsonResponse, "status", null ) );

        // get the loaded models
        params.clear();
        params.put( "importingJobID", "1" );
        response = service.post( GetModelsCommand.class, params );
        jsonResponse = new JSONObject( response.toString() );
        assertEquals( "GENESIS-Tabelle: Temporär",
                JSONUtil.getString( jsonResponse, "columnModel.columns[0].originalName", null ) );
        assertEquals( "Column 2",
                JSONUtil.getString( jsonResponse, "columnModel.columns[1].originalName", null ) );

        params.clear();
        params.put( "importingJobID", "1" );
        params.put( "start", "0" );
        params.put( "limit", "5" );
        response = service.post( GetRowsCommand.class, params );
        jsonResponse = new JSONObject( response.toString() );
        assertEquals( "Baugenehmigungen: Neue Wohn-u.Nichtwohngeb. einschl. Wohnh.,",
                JSONUtil.getString( jsonResponse, "rows[0].cells[0].v", null ) );
        // assertNull( jsonResponse.getJSONArray( "rows" ).getJSONArray( 0
        // ).getJSONArray( 1 ),
        // JSONUtil.getObject( jsonResponse, "rows[0].cells[1]" ) );
        assertEquals( "neue Wohngeb. mit 1 od.2 Wohnungen, Räume u.Fläche d.Wohn.,",
                JSONUtil.getString( jsonResponse, "rows[1].cells[0].v", null ) );

        assertEquals( new Integer( 100 ), JSONUtil.getInteger( jsonResponse, "total", null ) );
        assertEquals( new Integer( 0 ), JSONUtil.getInteger( jsonResponse, "start", null ) );
        assertEquals( new Integer( 5 ), JSONUtil.getInteger( jsonResponse, "limit", null ) );

    }


    @Test
    public void testSSV() throws JSONException, FileNotFoundException {
        // ; separated file
        File wohngebiete = new File(
                this.getClass().getResource( "/data/wohngebiete_sachsen.csv" ).getFile() );
        ImportResponse response = service.importFile( new FileInputStream( wohngebiete ), "wohngebiete_sachsen.csv", "text/csv" );
        assertEquals( ";", response.options().separator() );

        // get the loaded models
        ColumnModel columns = response.job().project.columnModel;
        assertEquals( 12, columns.columns.size() );

        List<Row> rows = response.job().project.rows;
        assertEquals( "Baugenehmigungen: Neue Wohn-u.Nichtwohngeb. einschl. Wohnh.,",
                rows.get( 0 ).cells.get( 0 ).value );
        assertEquals( 100, rows.size() );
        assertEquals( "neue Wohngeb. mit 1 od.2 Wohnungen, Räume u.Fläche d.Wohn.,",
                rows.get( 1 ).cells.get( 0 ).value );

        FormatAndOptions options = response.options();
        options.setSeparator( "\\t" );
        service.updateOptions( response.job(), options );
        columns = response.job().project.columnModel;
        assertEquals( 1, columns.columns.size() );

        rows = response.job().project.rows;
        assertEquals( "Baugenehmigungen: Neue Wohn-u.Nichtwohngeb. einschl. Wohnh.,;;;;;;;;;;;",
                rows.get( 0 ).cells.get( 0 ).value );
        assertEquals( 100, rows.size() );
    }


    @Test
    public void testMoviesTSV() throws JSONException, FileNotFoundException {
        // ; separated file
        File wohngebiete = new File(
                this.getClass().getResource( "/data/movies-condensed.tsv" ).getFile() );
        ImportResponse response = service.importFile( new FileInputStream( wohngebiete ), "wohngebiete_sachsen.csv", "text/csv" );
        assertEquals( "\\t", response.options().separator() );

        // get the loaded models
        ColumnModel columns = response.job().project.columnModel;
        assertEquals( 8, columns.columns.size() );

        List<Row> rows = response.job().project.rows;
        assertEquals( 20, rows.size() );
        assertEquals( "Jay Roach", rows.get( 3 ).cells.get( 1 ).value );
    }
}