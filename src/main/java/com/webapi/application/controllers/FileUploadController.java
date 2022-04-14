package com.webapi.application.controllers;

import com.webapi.application.handlers.PDF.PDFHandler;
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
    String handleFileUpload(@RequestParam("signOwner") String signOwner, @RequestParam("signCertificate") String signCertificate, @RequestParam("signDateFrom") String signDateFrom, @RequestParam("signDateTo") String signDateTo, @RequestParam(value = "drawLogo", required = false, defaultValue = "true") boolean drawLogo, @RequestParam(value = "checkTransitionToNewPage", required = false, defaultValue = "false") boolean checkTransitionToNewPage, @RequestParam("file") MultipartFile file)
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
        convertParams.setInsertType(0);

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

                if (fileName.endsWith(".docx") || fileName.endsWith(".doc") || fileName.endsWith(".rtf"))
                {
                    WordHandler wordHandler = new WordHandler();
                    wordHandler.setSingImagePath(singImagePath);
                    wordHandler.setParams(convertParams);
                    wordHandler.processDocument(fileName);
                }

                // обрабатываем полученный файл
                if (fileName.endsWith(".pdf"))
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

    //    @RequestMapping(value="/download", method=RequestMethod.GET)
    //    @ResponseBody
    //    public FileSystemResource downloadFile(@RequestParam(value="file") String fileName)
    //    {
    //        return new FileSystemResource(new File("output/" + fileName));
    //    }

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
