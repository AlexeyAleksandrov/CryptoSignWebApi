package com.webapi.application;

import com.webapi.application.controllers.FileUploadController;
import com.webapi.application.handlers.Excel.ExcelHandler;
import com.webapi.application.handlers.PDF.PDFHandler;
import com.webapi.application.handlers.UploadedFileHandler;
import com.webapi.application.handlers.Word.WordHandler;
import com.webapi.application.models.FileConvertParamsModel;
import com.webapi.application.services.signImage.SignImageCreator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CMDApplication
{
    public static void main(String[] args)
    {
        args = new String[8];
        args[0] = "C:\\Users\\ASUS\\Downloads\\Otchyot_po_praktike.docx";
        args[1] = "Vladelets";
        args[2] = "46r7346t3486tr834t83";
        args[3] = "08.04.2022";
        args[4] = "08.04.2023";
        args[5] = "1";
        args[6] = "0";
        args[7] = "В конец документа";

        if(args.length < 8)
        {
            System.out.println("Недостаточно параметров!");
            System.exit(1);
        }

        File fileInput = new File(args[0]);
        String signOwner = args[1];
        String signCertificate = args[2];
        String signDateFrom = args[3];
        String signDateTo = args[4];
        boolean drawLogo = Boolean.parseBoolean(args[5]);
        boolean checkTransitionToNewPage = Boolean.parseBoolean(args[6]);
        String insertType = args[7];

        String fileInputOriginalName = fileInput.getName();
        String currentDir = System.getProperty("user.dir");
        String fileName = currentDir + "/uploadedfiles/" + fileInputOriginalName;   // получаем оригинальное название файла, который был загружен
        FileConvertParamsModel convertParams = new FileConvertParamsModel();    // модель получаемых данных, для удобства

        // заносим полученные параметры в модель данных
        convertParams.setFileName(fileInputOriginalName);
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

        // проверка корректности входных данных
        if(convertParams.getInsertType() == -1
                || (convertParams.getInsertType() == 2
                && !(fileName.endsWith(".docx") || fileName.endsWith(".doc") || fileName.endsWith(".rtf"))))
        {
            System.out.println("Error! Выбранный тип подписи неподходит для данного типа файлов!");
            System.exit(1);
        }

        // начинаем обработку файла
        if (!fileInput.exists())
        {
            try
            {
                // проверяем наличие папок для сохранения и вывода
                boolean uploadDirCreated = true;
                boolean outputDirCreated = true;

                // папка сохранения
                File uploadDir = new File("uploadedfiles/");
                if(!uploadDir.exists())
                {
                    uploadDirCreated = uploadDir.mkdir();
                }

                // папка вывода
                File outputDir = new File("output/");
                if(!outputDir.exists())
                {
                    outputDirCreated = outputDir.mkdir();
                }

                // проверка наличия
                if(!uploadDirCreated || !outputDirCreated)
                {
                    System.out.println("Не удалось загрузить файл, т.к. файловая система не позволяет выполнить сохранение!");
                    System.exit(1);
                }

                // сохраняем файл на устройстве
                byte[] bytes = new FileInputStream(fileInput).readAllBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
                stream.write(bytes);
                stream.close();

                // создаём изображение подписи
                SignImageCreator signImageCreator = new SignImageCreator(); // создаём генератор изображения подписи
                signImageCreator.setImageGerbPath(FileUploadController.signImageLogoPath);       // указываем путь к гербу
                signImageCreator.createSignImage(FileUploadController.singImagePath, convertParams); // создаём изображение подписи

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
                else if(fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))
                {
                    documentHandler = new ExcelHandler();
                }
                else
                {
                    System.out.println("Данный тип файлов не поддерживается!");
                    System.exit(1);
                }

                // обрабатываем полученный файл
                documentHandler.setSingImagePath(FileUploadController.singImagePath); // указываем путь к картинке, которую нужно будет вставить
                documentHandler.setParams(convertParams);    // указываем параметры обработки
                outputFileName = documentHandler.processDocument(fileName);   // запускаем обработку

                System.out.println("OK! http://localhost:8080/download?file=" + outputFileName);
                System.exit(0);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Error! Не удалось загрузить " + fileName + " => " + e.getMessage());
                System.exit(1);
            }
        }
        else
        {
            System.out.println("Error! Не удалось загрузить файл, потому что он пустой.");
            System.exit(1);
        }
    }
}
