package org.polymap.core.data.refine.impl;

import com.google.refine.importing.ImportingJob;
import com.google.refine.model.Project;

public class ImportResponse {

    private ImportingJob     job;

    private FormatAndOptions options;


    public void setJob( ImportingJob job ) {
        this.job = job;
    }


    public void setOptions( FormatAndOptions options ) {
        this.options = options;
    }


    public ImportingJob job() {
        return job;
    }


    public FormatAndOptions options() {
        return options;
    }
}
