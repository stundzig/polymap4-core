package org.polymap.core.data.refine.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polymap.core.data.refine.RefineService;

import com.google.common.collect.Maps;
import com.google.refine.ProjectManager;
import com.google.refine.RefineServlet;
import com.google.refine.commands.Command;
import com.google.refine.commands.importing.ImportingControllerCommand;
import com.google.refine.importing.ImportingManager;
import com.google.refine.io.FileProjectManager;

public class RefineServiceImpl implements RefineService {

	private static Log log = LogFactory.getLog(RefineServiceImpl.class);

	private static RefineServiceImpl refineService;

	static final String PARAM_BASEDIR = "param_basedir";

	private static final long AUTOSAVE_PERIOD = 5; // 5 minutes

	private final String VERSION = "2.6";
	private final String REVISION = "polymap 1";

	private final String FULLNAME = RefineServlet.FULLNAME + VERSION + " [" + REVISION + "]";

	private Map<Class<? extends Command>, Command> commandInstances = Maps.newHashMap();

	// timer for periodically saving projects
	static private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

	private RefineServiceImpl(final File baseDir) {

		log.info("Starting " + FULLNAME + "...");

		log.trace("> initialize");
		FileProjectManager.initialize(baseDir);
		// only used to call servlet.getTempDir() later on
		
		RefineImportingManager.initialize(new RefineServlet() {
			@Override
			public File getTempDir() {
				return new File(System.getProperty("java.io.tmpdir"));
			}
		});

		service.scheduleWithFixedDelay(() -> {
			ProjectManager.singleton.save(false);
		} , AUTOSAVE_PERIOD, AUTOSAVE_PERIOD, TimeUnit.MINUTES);

		log.trace("< initialize");
	}
	//
	// @SuppressWarnings("unchecked")
	// protected void activate(ComponentContext context) {
	// Dictionary<String, Object> properties = context.getProperties();
	// File baseDir = (File) properties.get(PARAM_BASEDIR);
	//
	// log.info("activated at " + baseDir.getAbsolutePath());
	// }

	public static RefineServiceImpl INSTANCE(File refineDir) {
		if (refineService == null) {
			refineService = new RefineServiceImpl(refineDir);
		}
		return refineService;
	}

	public void destroy() {
		log.trace("> destroy");

		// cancel automatic periodic saving and force a complete save.
		// if (_timer != null) {
		// _timer.cancel();
		// _timer = null;
		// }
		if (ProjectManager.singleton != null) {
			ProjectManager.singleton.dispose();
			ProjectManager.singleton = null;
		}

		log.trace("< destroy");
	}

	public Object get(Class<? extends Command> commandClazz) {
		Command command = command(commandClazz);
		RefineResponse response = createResponse();
		try {
			command.doGet(createRequest(null), response);
		} catch (ServletException | IOException e) {
			throw new RuntimeException(e);
		}
		return response.result();
	}

	public Object post(Class<? extends Command> commandClazz) {
		Command command = command(commandClazz);
		RefineResponse response = createResponse();
		try {
			command.doPost(createRequest(null), response);
		} catch (ServletException | IOException e) {
			throw new RuntimeException(e);
		}
		return response.result();
	}

	public Object post(Class<? extends Command> commandClazz, Map<String, String> params) {
		Command command = command(commandClazz);
		RefineResponse response = createResponse();
		try {
			command.doPost(createRequest(params), response);
		} catch (ServletException | IOException e) {
			throw new RuntimeException(e);
		}
		return response.result();
	}

	private RefineResponse createResponse() {
		return new RefineResponse();
	}

	private RefineRequest createRequest(Map<String, String> params) {
		return new RefineRequest(params);
	}

	private Command command(Class<? extends Command> commandClazz) {
		Command command = commandInstances.get(commandClazz);
		if (command == null) {
			try {
				command = commandClazz.newInstance();
				commandInstances.put(commandClazz, command);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return command;
	}

}
