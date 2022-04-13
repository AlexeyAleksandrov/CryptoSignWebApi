package com.webapi.application.handlers.Word;

import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.uno.Exception;
import com.webapi.application.handlers.IUploadedFileHandler;
import com.webapi.application.handlers.PDF.PDFHandlerException;
import com.webapi.application.services.libreoffice.DocumentConverter;
import com.webapi.application.services.msword.OpenXML;

import java.io.IOException;

public class WordHandler extends IUploadedFileHandler
{
    @Override
    public void processDocument(String fileName) throws PDFHandlerException, IOException, WordHandlerException, BootstrapException, Exception
    {
        if(!fileName.endsWith(".docx") && !fileName.endsWith(".doc") && !fileName.endsWith(".rtf"))
        {
            throw new WordHandlerException("Данный тип файлов не поддерживается! Допустимое расширение файла: *.docx");
        }

        // конвертируем в *.docx
        if(fileName.endsWith(".doc") || fileName.endsWith(".rtf"))
        {
            DocumentConverter libreOffice = new DocumentConverter();
            String outputFile = fileName.replace(".doc", ".docx");
            outputFile = outputFile.replace(".rtf", ".docx");
            libreOffice.convertTo(fileName, outputFile, DocumentConverter.ConvertType.CONVERT_TO_DOCX);
            fileName = outputFile;
        }

        // вставляем картинку
        OpenXML wordOpenXML = new OpenXML();    // создаем обработчик OpenXML документов
        if(params.getInsertType() == 0)     // если используется классическая вставка
        {
            wordOpenXML.insertImageToWord(fileName, fileName, singImagePath);   // делаем вставку в конец документа
        }
        else if (params.getInsertType() == 2)
        {
            wordOpenXML.insertImageByTag(fileName, fileName, singImagePath, params.getSignOwner()); // делаем вставку по тэгу
        }
        else
        {
            throw new WordHandlerException("Указан некорректный тип добавления подписи!");
        }

        // экспортируем в PDF
        DocumentConverter libreOffice = new DocumentConverter();
        String outputFile = fileName.replace(".docx", ".pdf");
        outputFile = outputFile.replace("uploadedfiles", "output");
        libreOffice.convertTo(fileName, outputFile, DocumentConverter.ConvertType.EXPORT_TO_PDF);
    }
}
