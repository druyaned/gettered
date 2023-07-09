package com.github.druyaned.gettered.sources;

import com.github.druyaned.gettered.Gettered;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

/**
 * A wrapper for the JavaFileObject that overrides I/O and content methods with getters.
 * 
 * @author druyaned
 * @see Gettered
 * @see UnitToRewrite
 */
public class GetteredFileObject implements JavaFileObject {
    
//-Fields-------------------------------------------------------------------------------------------
    
    private final JavaFileObject origin;
    private final UnitToRewrite unitToRewrite;
    private final String getteredContent;
    
//-Constructors-------------------------------------------------------------------------------------
    
    /**
     * Constructs a wrapper for the {@link JavaFileObject}
     * that overrides I/O and content methods with getters.
     * 
     * @param unitToRewrite a unit with missed getters that should be rewritten.
     */
    public GetteredFileObject(UnitToRewrite unitToRewrite) {
        this.origin = unitToRewrite.getUnit().getSourceFile();
        this.unitToRewrite = unitToRewrite;
        Rewriter rewriter = new Rewriter(unitToRewrite);
        this.getteredContent = rewriter.getGetteredContent();
    }
    
//-Methods------------------------------------------------------------------------------------------
    
    public UnitToRewrite getUnitToRewrite() {
        return unitToRewrite;
    }
    
    public String getGetteredContent() {
        return getteredContent;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(getteredContent.getBytes());
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        return new StringReader(getteredContent);
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return getteredContent;
    }

//-Default-methods----------------------------------------------------------------------------------
    
    @Override
    public Writer openWriter() throws IOException {
        return origin.openWriter();
    }
    
    @Override
    public OutputStream openOutputStream() throws IOException {
        return origin.openOutputStream();
    }

    @Override
    public Kind getKind() {
        return origin.getKind();
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {
        return origin.isNameCompatible(simpleName, kind);
    }

    @Override
    public NestingKind getNestingKind() {
        return origin.getNestingKind();
    }

    @Override
    public Modifier getAccessLevel() {
        return origin.getAccessLevel();
    }

    @Override
    public URI toUri() {
        return origin.toUri();
    }

    @Override
    public String getName() {
        return origin.getName();
    }
    
    @Override
    public long getLastModified() {
        return origin.getLastModified();
    }
    
    @Override
    public boolean delete() {
        return origin.delete();
    }
    
}
