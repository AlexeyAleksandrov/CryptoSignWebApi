package com.webapi.application.models;


import java.util.Date;

public class FileConvertParamsModel
{
    private String fileName;
    private String signOwner;
    private String signCertificate;
    private String signDateStart;
    private String signDateEnd;
    private boolean drawLogo;

    public boolean isDrawLogo()
    {
        return drawLogo;
    }

    public void setDrawLogo(boolean drawLogo)
    {
        this.drawLogo = drawLogo;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getSignOwner()
    {
        return signOwner;
    }

    public void setSignOwner(String signOwner)
    {
        this.signOwner = signOwner;
    }

    public String getSignCertificate()
    {
        return signCertificate;
    }

    public void setSignCertificate(String signCertificate)
    {
        this.signCertificate = signCertificate;
    }

    public String getSignDateStart()
    {
        return signDateStart;
    }

    public void setSignDateStart(String signDateStart)
    {
        this.signDateStart = signDateStart;
    }

    public String getSignDateEnd()
    {
        return signDateEnd;
    }

    public void setSignDateEnd(String signDateEnd)
    {
        this.signDateEnd = signDateEnd;
    }
}
