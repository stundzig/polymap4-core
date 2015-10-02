package org.polymap.core.data.refine.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.polymap.core.data.refine.RefineService;
import org.polymap.core.data.refine.json.JSONUtil;

import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import com.google.refine.ProjectManager;
import com.google.refine.RefineServlet;
import com.google.refine.commands.Command;
import com.google.refine.commands.importing.ImportingControllerCommand;
import com.google.refine.importing.ImportingJob;
import com.google.refine.importing.ImportingManager;
import com.google.refine.io.FileProjectManager;

public class RefineServiceImpl
        implements RefineService {

    private static Log                            log              = LogFactory
            .getLog( RefineServiceImpl.class );

    private static RefineServiceImpl              refineService;

    static final String                           PARAM_BASEDIR    = "param_basedir";

    private static final long                     AUTOSAVE_PERIOD  = 5;                    // 5
    // minutes

    private final String                          VERSION          = "2.6";

    private final String                          REVISION         = "polymap 1";

    private final String                          FULLNAME         = RefineServlet.FULLNAME
            + VERSION + " [" + REVISION + "]";

    private Map<Class<? extends Command>,Command> commandInstances = Maps.newHashMap();

    // timer for periodically saving projects
    static private ScheduledExecutorService       service          = Executors
            .newSingleThreadScheduledExecutor();


    private RefineServiceImpl( final File baseDir ) {

        log.info( "Starting " + FULLNAME + "..." );

        log.trace( "> initialize" );
        FileProjectManager.initialize( baseDir );
        // only used to call servlet.getTempDir() later on

        ImportingManagerRegistry.initialize( new RefineServlet() {

            @Override
            public File getTempDir() {
                return new File( System.getProperty( "java.io.tmpdir" ) );
            }
        } );

        service.scheduleWithFixedDelay( () -> {
            ProjectManager.singleton.save( false );
        } , AUTOSAVE_PERIOD, AUTOSAVE_PERIOD, TimeUnit.MINUTES );

        log.trace( "< initialize" );
    }
    //
    // @SuppressWarnings("unchecked")
    // protected void activate(ComponentContext context) {
    // Dictionary<String, Object> properties = context.getProperties();
    // File baseDir = (File) properties.get(PARAM_BASEDIR);
    //
    // log.info("activated at " + baseDir.getAbsolutePath());
    // }


    public static RefineServiceImpl INSTANCE( File refineDir ) {
        if (refineService == null) {
            refineService = new RefineServiceImpl( refineDir );
        }
        return refineService;
    }


    public void destroy() {
        log.trace( "> destroy" );

        // cancel automatic periodic saving and force a complete save.
        // if (_timer != null) {
        // _timer.cancel();
        // _timer = null;
        // }
        if (ProjectManager.singleton != null) {
            ProjectManager.singleton.dispose();
            ProjectManager.singleton = null;
        }

        log.trace( "< destroy" );
    }


//    public Object get( Class<? extends Command> commandClazz ) {
//        try {
//            Command command = command( commandClazz );
//            RefineResponse response = createResponse();
//            command.doGet( createRequest( null ), response );
//            return response.result();
//        }
//        catch (Exception e) {
//            throw new RuntimeException( e );
//        }
//    }
//
//
//    public Object post( Class<? extends Command> commandClazz ) {
//        try {
//            Command command = command( commandClazz );
//            RefineResponse response = createResponse();
//            command.doPost( createRequest( null ), response );
//            return response.result();
//        }
//        catch (Exception e) {
//            throw new RuntimeException( e );
//        }
//    }


    public Object post( Class<? extends Command> commandClazz, Map<String,String> params ) {
        try {
            Command command = command( commandClazz );
            RefineResponse response = createResponse();
            command.doPost( createRequest( params ), response );
            return response.result();
        }
        catch (Exception e) {
            throw new RuntimeException( e );
        }
    }


    private RefineResponse createResponse() {
        return new RefineResponse();
    }


    private RefineRequest createRequest( Map<String,String> params ) {
        return createRequest( params, null, null, null );
    }


    private RefineRequest createRequest( Map<String,String> params, Map<String,String> headers,
            InputStream file, String fileName ) {
        return new RefineRequest( params, headers, file, fileName );
    }


    private Command command( Class<? extends Command> commandClazz )
            throws InstantiationException, IllegalAccessException {
        Command command = commandInstances.get( commandClazz );
        if (command == null) {
            command = commandClazz.newInstance();
            commandInstances.put( commandClazz, command );
        }
        return command;
    }


    public ImportResponse importFile( InputStream wohng, String fileName, String mimeType ) {
        try {
            // create the job
            ImportingJob job = ImportingManager.createJob();

            // copy the file
            Map<String,String> params = Maps.newHashMap();
            params.put( "jobID", String.valueOf( job.id ) );
            params.put( "subCommand", "load-raw-data" );
            params.put( "controller", "core/filebased-importing-controller" );
            params.put( HttpHeaders.CONTENT_TYPE, mimeType );
            Map<String,String> headers = Maps.newHashMap();
            RefineResponse response = createResponse();
            command( ImportingControllerCommand.class )
                    .doPost( createRequest( params, headers, wohng, fileName ), response );

            
//             get-importing-job-status bis done
             
            log.info( "JOB:" + job.getOrCreateDefaultConfig().toString() );
             
            // initialize the parser ui
            String format = JSONUtil.getString( job.getOrCreateDefaultConfig(),
                    "retrievalRecord.files[0].format", null );
            
            while (format == null) {
                Thread.sleep( 100 );
                format = JSONUtil.getString( job.getOrCreateDefaultConfig(),
                        "retrievalRecord.files[0].format", null );
            }
            
            params.clear();
            params.put( "jobID", String.valueOf( job.id ) );
            params.put( "subCommand", "initialize-parser-ui" );
            params.put( "controller", "core/default-importing-controller" );
            params.put( "format", format );
            command( ImportingControllerCommand.class ).doPost( createRequest( params ), response );
            JSONObject initializeParserUiResponse = new JSONObject( response.result().toString() );
            if ("\\t".equals( JSONUtil.getString( initializeParserUiResponse, "options.separator", null ) )) {
                // separator is not one of , or , try some more separator
            }
            // update/initialize all options and create the project
            FormatAndOptions options = new FormatAndOptions(initializeParserUiResponse.getJSONObject( "options" ));
            options.setFormat(format);
            
            // try to find the best separator, with the most columns in the resulting model.
            params.clear();
            params.put( "jobID", String.valueOf( job.id ) );
            params.put( "subCommand", "update-format-and-options" );
            params.put( "controller", "core/default-importing-controller" );
            params.put( "format", format );
            params.put( "options", options.toJSON().toString() );
            command( ImportingControllerCommand.class ).doPost( createRequest( params ), response );
//            JSONObject updateFormatAndOptionsResponse = new JSONObject( response.result().toString() );
            
            log.info( "imported " + job + "; " + options.toJSON());
            ImportResponse resp = new ImportResponse();
            resp.setJob( job );
            resp.setOptions( options );
            
            return resp;
        }
        catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

//
//    public ColumnModel columns( String jobId ) {
//        ImportingJob job = ImportingManager.getJob( Long.parseLong( jobId ) );
//        Project project = job.project;
//        return project.columnModel;
//    }
//
//
//    public List<Row> rows( String jobId ) {
//        ImportingJob job = ImportingManager.getJob( Long.parseLong( jobId ) );
//        Project project = job.project;
//        return project.rows;
//    }


    public void updateOptions( ImportingJob job, FormatAndOptions options ) {
        try {
            RefineResponse response = createResponse();
            Map<String,String> params = Maps.newHashMap();
            params.put( "jobID", String.valueOf( job.id ) );
            params.put( "subCommand", "update-format-and-options" );
            params.put( "controller", "core/default-importing-controller" );
            params.put( "format", options.format() );
            params.put( "options", options.toJSON().toString() );
            command( ImportingControllerCommand.class ).doPost( createRequest( params ), response );
//
//            ImportResponse resp = new ImportResponse();
//            resp.setJob( job );
//            resp.setProject( job.project );
//            resp.setOptions( options );
//            
//            return resp;
        }
        catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

}