package com.webapi.application.controllers;

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
    String handleFileUpload(@RequestParam("name") String name, @RequestParam("file") MultipartFile file)
    {
        if (!file.isEmpty())
        {
            try
            {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(name)));
                stream.write(bytes);
                stream.close();
                return "Вы удачно загрузили " + name + " в " + name + "-uploaded !";
            }
            catch (Exception e)
            {
                return "Вам не удалось загрузить " + name + " => " + e.getMessage();
            }
        }
        else
        {
            return "Вам не удалось загрузить " + name + " потому что файл пустой.";
        }
    }
}
