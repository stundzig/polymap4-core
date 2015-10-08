package org.polymap.core.data.refine;

import java.io.InputStream;

import org.polymap.core.data.refine.impl.FormatAndOptions;
import org.polymap.core.data.refine.impl.ImportResponse;

import com.google.refine.importing.ImportingJob;

public interface RefineService {

    ImportResponse importFile( InputStream in, String fileName, String mimeType );

    void updateOptions( ImportingJob job, FormatAndOptions options );
}
