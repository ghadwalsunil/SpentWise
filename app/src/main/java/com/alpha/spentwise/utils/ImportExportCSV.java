package com.alpha.spentwise.utils;

import android.content.Context;

import com.alpha.spentwise.customDataObjects.ExportEntryDetails;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.dataManager.DatabaseHandler;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.alpha.spentwise.dataManager.Constants.EXPORT_FILE_COLUMN_HEADERS;

public class ImportExportCSV {

    public static void exportDataAsCSV(List<ExportEntryDetails> exportEntryDetailsList, Context ctx, ProjectDetails projectDetails) throws IOException {

        String fileName = projectDetails.getProjectName() + "_Transaction.csv";
        File file = new File(ctx.getExternalFilesDir(null), fileName);
        CSVWriter writer;

        writer = new CSVWriter(new FileWriter(file));

        String[] data = EXPORT_FILE_COLUMN_HEADERS;

        writer.writeNext(data);
        for(ExportEntryDetails exportEntryDetails : exportEntryDetailsList){
            data = exportEntryDetails.toStringArray();
            writer.writeNext(data);
        }
        writer.close();
    }

    public static List<String[]> importDataFromCSV(String file){

        List<String[]> allData = null;

        try {
            FileReader filereader = new FileReader(file);

            // create csvReader object and skip first Line
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            allData = csvReader.readAll();

            filereader.close();
            csvReader.close();

            return allData;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
