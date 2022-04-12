package com.webapi.application.controllers;

import com.webapi.application.handlers.PDFHandler;
import com.webapi.application.models.FileConvertParamsModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@Controller
public class FileUploadController
{
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String index()
    {
        return "index.html";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public @ResponseBody
    String provideUploadInfo()
    {
        return "Вы можете загружать файл с использованием того же URL.";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody
    String handleFileUpload(@RequestParam(value = "params") FileConvertParamsModel fileConvertParamsModel, @RequestParam("file") MultipartFile file)
    {
        String fileName = file.getOriginalFilename();
        fileConvertParamsModel.setFileName(fileName);

        if (!file.isEmpty())
        {
            try
            {
                byte[] bytes = file.getBytes();
                assert fileName != null;
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
                stream.write(bytes);
                stream.close();

                if(fileName.endsWith(".pdf"))
                {
                    PDFHandler pdfHandler = new PDFHandler();
                    pdfHandler.processDocument(fileConvertParamsModel);
                }

                return "Вы удачно загрузили " + fileName + " в " + fileName + "-uploaded !";
            }
            catch (Exception e)
            {
                return "Вам не удалось загрузить " + fileName + " => " + e.getMessage();
            }
        }
        else
        {
            return "Вам не удалось загрузить " + fileName + " потому что файл пустой.";
        }
    }
}
