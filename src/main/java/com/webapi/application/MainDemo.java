package com.webapi.application;

import com.webapi.application.services.libreoffice.DocumentConverter;
import java.io.IOException;

public class MainDemo
{
    public static void main(String[] args) throws IOException
    {
        //        String outputImagePath = "C:\\Users\\ASUS\\Pictures\\image_java.jpg";   // путь сохранения файла картинки
        //        String imageGerbPath = "C:\\Users\\ASUS\\Pictures\\mirea_gerb_52_65.png";    // путь к изображению герба
        //
        //        SignImageCreator signImageCreator = new SignImageCreator(); // генератор картинок
        //        SignImageCreator.setImageGerbPath(imageGerbPath);   // указываем путь к гербу
        //        signImageCreator.createSignImage(outputImagePath,
        //                "Петров Пётр Иванович",
        //                "120059595d5bb35e9f77ff73f600010059595d",
        //                "21.03.2022",
        //                "21.03.2023",
        //                true);  // создаем картинку

        // делаю экспорт файлов
        try
        {
            DocumentConverter documentConverter = new DocumentConverter();  // создаём конвертер документов

            documentConverter.convertTo(
                    "C:\\Users\\ASUS\\Downloads\\docs_conv\\титульники.rtf",
                    "C:\\Users\\ASUS\\Downloads\\docs_conv\\titilnik.docx",
                    DocumentConverter.ConvertType.CONVERT_TO_DOCX);     // конвертируем rtf в docx

            documentConverter.convertTo(
                    "C:\\Users\\ASUS\\Downloads\\docs_conv\\титульники.rtf",
                    "C:\\Users\\ASUS\\Downloads\\docs_conv\\титульник.pdf",
                    DocumentConverter.ConvertType.EXPORT_TO_PDF);       // конвертируем rtf в pdf

            System.exit(0);
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
