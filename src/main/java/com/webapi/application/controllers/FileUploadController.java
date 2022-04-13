package com.webapi.application.controllers;

import com.webapi.application.handlers.PDFHandler;
import com.webapi.application.models.FileConvertParamsModel;
import com.webapi.application.services.signImage.SignImageCreator;
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
    String handleFileUpload(@RequestParam("signOwner") String signOwner,
                            @RequestParam("signCertificate") String signCertificate,
                            @RequestParam("signDateFrom") String signDateFrom,
                            @RequestParam("signDateTo") String signDateTo,
                            @RequestParam(value = "drawLogo", required = false, defaultValue = "true") boolean drawLogo,
                            @RequestParam(value = "checkTransitionToNewPage", required = false, defaultValue = "false") boolean checkTransitionToNewPage,
                            @RequestParam("file") MultipartFile file)
    {
        String fileName = "uploadedfiles/" + file.getOriginalFilename();   // получаем оригинальное название файла, который был загружен
        FileConvertParamsModel convertParams = new FileConvertParamsModel();    // модель получаемых данных, для удобства

        // заносим полученные параметры в модель данных
        convertParams.setFileName(file.getOriginalFilename());
        convertParams.setSignOwner(signOwner);
        convertParams.setSignCertificate(signCertificate);
        convertParams.setSignDateStart(signDateFrom);
        convertParams.setSignDateEnd(signDateTo);
        convertParams.setDrawLogo(drawLogo);
        convertParams.setCheckTransitionToNewPage(checkTransitionToNewPage);

        // начинаем обработку файла
        if (!file.isEmpty())
        {
            try
            {
                // сохраняем файл на устройстве
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
                stream.write(bytes);
                stream.close();

                String singImagePath = "temp/sign_image.jpg";   // путь сохранения готового изображения подписи
                String signImageLogoPath = "src/main/resources/logo/mirea_gerb_52_65.png";  // путь к гербу для изображения

                // создаём изображение подписи
                SignImageCreator signImageCreator = new SignImageCreator(); // создаём генератор изображения подписи
                signImageCreator.setImageGerbPath(signImageLogoPath);       // указываем путь к гербу
                signImageCreator.createSignImage(singImagePath, convertParams); // создаём изображение подписи

                // обрабатываем полученный файл
                if(fileName.endsWith(".pdf"))
                {
                    PDFHandler pdfHandler = new PDFHandler();   // создаём обработчик
                    pdfHandler.setSingImagePath(singImagePath); // указываем путь к картинке, которую нужно будет вставить
                    pdfHandler.setParams(convertParams);    // указываем параметры обработки
                    pdfHandler.processDocument(fileName);   // запускаем обработку
                }

                return "Файл " + fileName + " успешно загружен!";
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return "Error! Не удалось загрузить " + fileName + " => " + e.getMessage();
            }
        }
        else
        {
            return "Error! Вам не удалось загрузить " + fileName + " потому что файл пустой.";
        }
    }
}
