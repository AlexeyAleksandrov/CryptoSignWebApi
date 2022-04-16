package com.webapi.application.handlers.Excel;

import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.uno.Exception;
import com.webapi.application.controllers.FileUploadController;
import com.webapi.application.handlers.PDF.PDFHandler;
import com.webapi.application.handlers.PDF.PDFHandlerException;
import com.webapi.application.handlers.UploadedFileHandler;
import com.webapi.application.handlers.Word.WordHandlerException;
import com.webapi.application.services.libreoffice.DocumentConverter;

import java.io.IOException;

public class ExcelHandler  extends UploadedFileHandler
{
    @Override
    public String processDocument(String fileName) throws PDFHandlerException, IOException, WordHandlerException, BootstrapException, Exception, ExcelHandlerException
    {
        if(!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))
        {
            throw new ExcelHandlerException("Данный тип файлов не поддерживается! Допуступные расширения файлов: *.xlsx и *.xls");
        }

        // экспортируем в PDF
        DocumentConverter libreOffice = new DocumentConverter();
        String outputPdfFileName = fileName.replace(".xlsx", ".pdf").replace(".xls", ".pdf");
//        outputPdfFileName = outputPdfFileName.replace("uploadedfiles", "output");
        libreOffice.convertTo(fileName, outputPdfFileName, DocumentConverter.ConvertType.EXPORT_TO_PDF);

        String outputFileName = outputPdfFileName.replace("uploadedfiles", "output");   // заменяем название папки вывода

        // обрабатываем документ как PDF
        PDFHandler pdfHandler = new PDFHandler();   // обработчик PDF
        pdfHandler.setSingImagePath(FileUploadController.singImagePath);
        pdfHandler.processDocument(outputPdfFileName, outputFileName);

        outputFileName = params.getFileName()
                .replace(".xlsx", ".pdf")
                .replace(".xls", ".pdf");
        return outputFileName;
    }
}
