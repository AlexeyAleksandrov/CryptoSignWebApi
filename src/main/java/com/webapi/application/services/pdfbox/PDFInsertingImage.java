package com.webapi.application.services.pdfbox;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;

public class PDFInsertingImage
{
    public void drawImageOnDocument() throws IOException
    {
        //Loading an existing document
        File file = new File("C:\\Users\\ASUS\\Downloads\\docs_conv\\титульник2.pdf");
        PDDocument doc = PDDocument.load(file);

        //Retrieving the page
        PDPage page = doc.getPage(0);

        //Creating PDImageXObject object
        PDImageXObject pdImage = PDImageXObject.createFromFile("C:\\Users\\ASUS\\Pictures\\image_java.jpg",doc);

        //creating the PDPageContentStream object
        PDPageContentStream contents = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, false);

        //Drawing the image in the PDF document
        contents.drawImage(pdImage, 70, 250, 60*3, 25*3);

        System.out.println("Image inserted");

        //Closing the PDPageContentStream object
        contents.close();

        //Saving the document
        doc.save("C:\\Users\\ASUS\\Downloads\\docs_conv\\титульник2_out.pdf");

        //Closing the document
        doc.close();

    }

    public static void main(String args[]) throws Exception
    {
        //Loading an existing document
        File file = new File("C:\\Users\\ASUS\\Downloads\\docs_conv\\титульник2.pdf");
        PDDocument doc = PDDocument.load(file);

        PDFRenderer renderer = new PDFRenderer(doc);
        BufferedImage image = renderer.renderImage(doc.getNumberOfPages()-1);
        ImageIO.write(image, "PNG", new File("custom-render.png"));
    }
}
