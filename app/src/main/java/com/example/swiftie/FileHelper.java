package com.example.swiftie;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
public class FileHelper {
    private Context context;

    public FileHelper(Context context) {
        this.context = context;
    }

    // Write text to a file
    public void writeToFile(String fileName, String content) {
        File file = new File(context.getFilesDir(), fileName);
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(fos);
             BufferedWriter bw = new BufferedWriter(osw)) {

            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read text from a file
    public String readFromFile(String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        StringBuilder content = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString().trim();
    }
}
