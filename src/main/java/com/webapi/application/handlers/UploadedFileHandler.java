package com.webapi.application.handlers;

import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.uno.Exception;
import com.webapi.application.handlers.PDF.PDFHandlerException;
import com.webapi.application.handlers.Word.WordHandlerException;
import com.webapi.application.models.FileConvertParamsModel;

import java.io.IOException;

public abstract class UploadedFileHandler
{
    protected String singImagePath = null;
    protected FileConvertParamsModel params = null;

    public String processDocument(String fileName) throws PDFHandlerException, IOException, WordHandlerException, BootstrapException, Exception { return null; };

    public String getSingImagePath()
    {
        return singImagePath;
    }

    public void setSingImagePath(String singImagePath)
    {
        this.singImagePath = singImagePath;
    }

    public FileConvertParamsModel getParams()
    {
        return params;
    }

    public void setParams(FileConvertParamsModel params)
    {
        this.params = params;
    }

}