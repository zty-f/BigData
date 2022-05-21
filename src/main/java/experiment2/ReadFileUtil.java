package experiment2;

import com.csvreader.CsvReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * @version V1.0
 * @ClassName: experiment2.ReadFileUtil.java
 * @Copyright swpu
 * @author: zty-f
 * @date: 2022-05-21 16:12
 * @Description: 读取文件工具类
 */
public class ReadFileUtil {
    public static ArrayList<String[]> readCSV(String filePath){
        //读取文件
        ArrayList<String[]> originList = new ArrayList<String[]>();
        CsvReader reader = null;
        try {
            reader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
            while (true) {
                try {
                    if (!reader.readRecord()) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                originList.add(reader.getValues());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return originList;
    }
}
