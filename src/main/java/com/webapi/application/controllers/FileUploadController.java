package com.webapi.application.controllers;

import com.webapi.application.handlers.PDF.PDFHandler;
import com.webapi.application.handlers.UploadedFileHandler;
import com.webapi.application.handlers.Word.WordHandler;
import com.webapi.application.models.FileConvertParamsModel;
import com.webapi.application.services.signImage.SignImageCreator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;

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
    String handleFileUpload(@RequestParam("signOwner") String signOwner, @RequestParam("signCertificate") String signCertificate, @RequestParam("signDateFrom") String signDateFrom, @RequestParam("signDateTo") String signDateTo, @RequestParam(value = "drawLogo", required = false, defaultValue = "true") boolean drawLogo, @RequestParam(value = "checkNewPage", required = false, defaultValue = "false") boolean checkTransitionToNewPage, @RequestParam("insertType") String insertType, @RequestParam("file") MultipartFile file)
    {

        String currentDir = System.getProperty("user.dir");
        String fileName = currentDir + "/uploadedfiles/" + file.getOriginalFilename();   // получаем оригинальное название файла, который был загружен
        FileConvertParamsModel convertParams = new FileConvertParamsModel();    // модель получаемых данных, для удобства

        // заносим полученные параметры в модель данных
        convertParams.setFileName(file.getOriginalFilename());
        convertParams.setSignOwner(signOwner);
        convertParams.setSignCertificate(signCertificate);
        convertParams.setSignDateStart(signDateFrom);
        convertParams.setSignDateEnd(signDateTo);
        convertParams.setDrawLogo(drawLogo);
        convertParams.setCheckTransitionToNewPage(checkTransitionToNewPage);
        switch (insertType)
        {
            case "В конец документа" -> convertParams.setInsertType(0);
            case "По координатам" -> convertParams.setInsertType(1);
            case "По тэгу" -> convertParams.setInsertType(2);
            default -> convertParams.setInsertType(-1);
        }

        System.out.println("type = " + convertParams.getInsertType());

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

                UploadedFileHandler documentHandler = null; // обработчик документов
                String outputFileName = null;
                // определяем тип полученного файла
                if (fileName.endsWith(".docx") || fileName.endsWith(".doc") || fileName.endsWith(".rtf"))
                {
                    documentHandler = new WordHandler();
                }
                else if (fileName.endsWith(".pdf"))
                {
                    documentHandler = new PDFHandler();   // создаём обработчик
                }
                else
                {
                    return "Данный тип файлов не поддерживается!";
                }

                // обрабатываем полученный файл
                documentHandler.setSingImagePath(singImagePath); // указываем путь к картинке, которую нужно будет вставить
                documentHandler.setParams(convertParams);    // указываем параметры обработки
                outputFileName = documentHandler.processDocument(fileName);   // запускаем обработку

                return "OK! http://localhost:8080/download?file=" + outputFileName;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return "Error! Не удалось загрузить " + fileName + " => " + e.getMessage();
            }
        }
        else
        {
            return "Error! Не удалось загрузить файл, потому что он пустой.";
        }
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public void downloadPDFResource(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "file") String fileName) throws IOException
    {
        File file = new File("output/" + fileName);
        if (file.exists())
        {

            //get the mimetype
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null)
            {
                //unknown mimetype so set the mimetype to application/octet-stream
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);

            /**
             * In a regular HTTP response, the Content-Disposition response header is a
             * header indicating if the content is expected to be displayed inline in the
             * browser, that is, as a Web page or as part of a Web page, or as an
             * attachment, that is downloaded and saved locally.
             *
             */

            /**
             * Here we have mentioned it to show inline
             */
            response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");

            //Here we have mentioned it to show as attachment
            //response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

            response.setContentLength((int) file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            FileCopyUtils.copy(inputStream, response.getOutputStream());

        }
    }
}
