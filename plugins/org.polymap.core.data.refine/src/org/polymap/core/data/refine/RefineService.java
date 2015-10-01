package org.polymap.core.data.refine;

import java.io.InputStream;

import org.polymap.core.data.refine.impl.ImportResponse;

public interface RefineService {

    ImportResponse importFile( InputStream in, String fileName, String mimeType );
}
