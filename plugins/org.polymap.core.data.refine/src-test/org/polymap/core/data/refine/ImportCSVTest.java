package org.polymap.core.data.refine;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.polymap.core.data.refine.impl.RefineServiceImpl;

import com.google.common.collect.Maps;
import com.google.refine.commands.importing.CreateImportingJobCommand;
import com.google.refine.commands.importing.ImportingControllerCommand;

public class ImportCSVTest {

	private RefineServiceImpl service;
	
	@Before
	public void setUp() {
		service = RefineServiceImpl.INSTANCE(new File(System.getProperty("java.io.tmpdir")));
	}
	@Test
	public void test() {
		Object response = service.post(CreateImportingJobCommand.class);
		assertEquals("{ \"jobID\" : 1 }", response.toString());
		
		//http://127.0.0.1:3333/command/core/importing-controller?controller=core%2Fdefault-importing-controller&jobID=1&subCommand=load-raw-data
		Map<String, String> params = Maps.newHashMap();
		params.put("jobID", "1");
		params.put("subCommand","load-raw-data");
		params.put("controller", "core/default-importing-controller");
		response = service.post(ImportingControllerCommand.class, params);
	}

}
