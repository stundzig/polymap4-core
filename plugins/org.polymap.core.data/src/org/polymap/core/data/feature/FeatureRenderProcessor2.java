/* 
 * polymap.org
 * Copyright (C) 2009-2016, Polymap GmbH. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.core.data.feature;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.FeatureSource;
import org.geotools.filter.function.EnvFunction;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.polymap.core.data.PipelineFeatureSource;
import org.polymap.core.data.image.GetLayerTypesRequest;
import org.polymap.core.data.image.GetLegendGraphicRequest;
import org.polymap.core.data.image.GetMapRequest;
import org.polymap.core.data.image.ImageProducer;
import org.polymap.core.data.image.ImageResponse;
import org.polymap.core.data.pipeline.DataSourceDescription;
import org.polymap.core.data.pipeline.Pipeline;
import org.polymap.core.data.pipeline.PipelineExecutor.ProcessorContext;
import org.polymap.core.data.pipeline.PipelineIncubator;
import org.polymap.core.data.pipeline.PipelineProcessorSite;
import org.polymap.core.data.pipeline.TerminalPipelineProcessor;
import org.polymap.core.runtime.CachedLazyInit;
import org.polymap.core.runtime.Lazy;

/**
 * This processor renders features using the geotools {@link StreamingRenderer}. The
 * features are fetched through a sub pipeline for usecase {@link FeaturesProducer}.
 * 
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 */
public class FeatureRenderProcessor2
        implements TerminalPipelineProcessor, ImageProducer {

    private static final Log log = LogFactory.getLog( FeatureRenderProcessor2.class );

    /** Site property key to retrieve {@link #style}. */
    public static final String          STYLE_SUPPLIER = "styleSupplier";
    
    private PipelineProcessorSite       site;
    
    private Lazy<Pipeline>              pipeline;

    private Lazy<FeatureSource>         fs;

    private Supplier<Style>             style;  // = new CachedLazyInit( () -> new DefaultStyles().findStyle( fs.get() ) );

    
    @Override
    public void init( @SuppressWarnings("hiding") PipelineProcessorSite site ) throws Exception {
        this.site = site;
        
        // styleSupplier
        style = site.getProperty( STYLE_SUPPLIER );
        if (style == null) {
            log.warn( "No style for resource: " + site.dsd.get().resourceName.get() );
            style = () -> new DefaultStyles().findStyle( fs.get() );
        }
        
        // pipeline
        this.pipeline = new CachedLazyInit( () -> {
            try {
                PipelineIncubator incubator = site.incubator.get();
                DataSourceDescription dsd = new DataSourceDescription( site.dsd.get() );
                return incubator.newPipeline( FeaturesProducer.class, dsd, null );
            }
            catch (Exception e) {
                throw new RuntimeException( e );
            }            
        });
        
        // fs
        this.fs = new CachedLazyInit( () -> {
            return new PipelineFeatureSource( pipeline.get() );
        });
    }


    @Override
    public boolean isCompatible( DataSourceDescription dsd ) {
        // we are compatible to everything a feature pipeline can be build for
        if (new DataSourceProcessor().isCompatible( dsd )) {
            return true;
        }
        return false;
    }


    @Override
    public void getMapRequest( GetMapRequest request, ProcessorContext context ) throws Exception {
        long start = System.currentTimeMillis();

        // MapContent
        log.debug( "Creating new MapContext... " );
        MapContent mapContent = new MapContent();
        mapContent.getViewport().setCoordinateReferenceSystem( request.getBoundingBox().getCoordinateReferenceSystem() );
        mapContent.addLayer( new FeatureLayer( fs.get(), style.get() ) );

//            // watch layer for style changes
//            LayerStyleListener listener = new LayerStyleListener( mapContextRef );
//            if (watchedLayers.putIfAbsent( layer, listener ) == null) {
//                layer.addPropertyChangeListener( listener );
//            }

        // Render
        BufferedImage result = new BufferedImage( request.getWidth(), request.getHeight(), BufferedImage.TYPE_INT_ARGB );
        result.setAccelerationPriority( 1 );
        final Graphics2D g = result.createGraphics();
        //      log.info( "IMAGE: accelerated=" + result.getCapabilities( g.getDeviceConfiguration() ).isAccelerated() );

        try {
            StreamingRenderer renderer = new StreamingRenderer();

            // error handler
            renderer.addRenderListener( new RenderListener() {
                @Override
                public void featureRenderer( SimpleFeature feature ) {
                }
                @Override
                public void errorOccurred( Exception e ) {
                    if (e.getMessage().contains( "Error transforming bbox" )) {
                        log.warn( "Renderer: " + e.getMessage() );
                    }
                    else {
                        log.error( "Renderer error: ", e );
                        drawErrorMsg( g, "Fehler bei der Darstellung.", e );
                    }
                }
            });

            // rendering hints
            RenderingHints hints = new RenderingHints(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_SPEED );
            hints.add( new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON ) );
            hints.add( new RenderingHints(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON ) );

            // geoserver compatibility to support *env* in SLD functions
            double scale = RendererUtilities.calculateOGCScale( request.getBoundingBox(), request.getWidth(), Collections.EMPTY_MAP);
            EnvFunction.setLocalValue( "wms_scale_denominator", scale );
            
            renderer.setJava2DHints( hints );
            // g.setRenderingHints( hints );

            // render params
            Map rendererParams = new HashMap();
            rendererParams.put( "optimizedDataLoadingEnabled", Boolean.TRUE );
            renderer.setRendererHints( rendererParams );

            renderer.setMapContent( mapContent );
            Rectangle paintArea = new Rectangle( request.getWidth(), request.getHeight() );
            renderer.paint( g, paintArea, request.getBoundingBox() );
        }
        catch (Throwable e) {
            log.error( "Renderer error: ", e );
            drawErrorMsg( g, null, e );
        }
        finally {
            mapContent.dispose();
            EnvFunction.clearLocalValues();
            if (g != null) { g.dispose(); }
        }
        log.debug( "   ...done: (" + (System.currentTimeMillis()-start) + "ms)." );

        context.sendResponse( new ImageResponse( result ) );
    }


    @Override
    public void getLegendGraphicRequest( GetLegendGraphicRequest request, ProcessorContext context ) throws Exception {
        // XXX Auto-generated method stub
        throw new RuntimeException( "not yet implemented." );
    }


    @Override
    public void getLayerTypesRequest( GetLayerTypesRequest request, ProcessorContext context ) throws Exception {
        // XXX Auto-generated method stub
        throw new RuntimeException( "not yet implemented." );
    }


//    /**
//     * Static class listening to changes of the Style of a layer. This does not reference
//     * the Processor, so it does not prevent the Processor from being GCed. The finalyze()
//     * of the Processor clears the listeners. 
//     */
//    public static class LayerStyleListener {
//        
//        private LazyInit        mapContextRef;
//        
//        public LayerStyleListener( LazyInit mapContextRef ) {
//            this.mapContextRef = mapContextRef;
//        }
//
//        @EventHandler
//        public void propertyChange( PropertyChangeEvent ev ) {
//            if (ev.getPropertyName().equals( ILayer.PROP_STYLE )) {
//                log.debug( "clearing: " + mapContextRef );
//                mapContextRef.clear();
//            }
//        }
//    }

    
    protected void drawErrorMsg( Graphics2D g, String msg, Throwable e ) {
        g.setColor( Color.RED );
        g.setStroke( new BasicStroke( 1 ) );
        g.getFont().deriveFont( Font.BOLD, 12 );
        if (msg != null) {
            g.drawString( msg, 10, 10 );
        }
        if (e != null) {
            g.drawString( e.toString(), 10, 30 );
        }
    }


    /**
     * The pipeline used by this processor. This is created on demand and cached.
     */
    public Pipeline pipeline() {
        return pipeline.get();
    }
    
}
