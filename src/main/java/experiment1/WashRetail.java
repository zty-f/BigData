package experiment1;

import com.alibaba.fastjson.JSON;
import com.csvreader.CsvReader;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class WashRetail {
    public static void main(String[] args){
        try{
            String filePath = "D:\\IDEA_Project\\BigData\\src\\main\\java\\experiment1\\Online Retail.csv";
            ArrayList<String[]> originList = new ArrayList<String[]>();

			//读取csv文件
			CsvReader reader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
            while (reader.readRecord()) {
                originList.add(reader.getValues());
            }
            List<MonthVolumn> volumnList = new ArrayList();
            //处理无效数据并统计每月销售量，统计结果写入volumnList对象
            System.out.println(Arrays.toString(originList.get(0)));
            int size = originList.size();
            System.out.println("原始数据量："+size);//541910   第一行为表头，不计入
            //记录清除数据
            List<String[]> afterClean = new ArrayList<>();
            HashSet<String> order = new HashSet<>();
            HashSet<String> efficientOrder = new HashSet<>();
            HashSet<String> goods = new HashSet<>();
            HashSet<String> efficientGoods = new HashSet<>();
            HashSet<String> users = new HashSet<>();
            HashSet<String> efficientUsers = new HashSet<>();
            HashSet<String> countries = new HashSet<>();
            //清除无效数据
            for (int i = 1; i < size; i++) {
                String[] tmp = originList.get(i);
                order.add(tmp[0]);
                goods.add(tmp[1]);
                users.add(tmp[6]);
                countries.add(tmp[7]);
                if(tmp[0].equals("")){
                    continue;
                }
                if (tmp[1].equals("")){
                    continue;
                }
                if (tmp[3].equals("")||!tmp[3].matches("^\\d+$")||Integer.parseInt(tmp[3])<=0){
                    continue;
                }
                if (tmp[4].equals("")){
                    continue;
                }
                if (tmp[5].equals("")||!tmp[5].matches("^\\d+(\\.\\d+)?$")||Double.parseDouble(tmp[3])<=0){
                    continue;
                }
                efficientOrder.add(tmp[0]);
                efficientGoods.add(tmp[1]);
                efficientUsers.add(tmp[6]);
                afterClean.add(tmp);
            }

            System.out.println("清除后的数据量："+afterClean.size());
            System.out.println("============================================");
            System.out.println("订单总数："+order.size());
            System.out.println("有效订单总数："+efficientOrder.size());
            System.out.println("商品总数："+goods.size());
            System.out.println("有效商品总数："+efficientGoods.size());
            System.out.println("用户总数："+users.size());
            System.out.println("有效用户总数："+efficientUsers.size());
            System.out.println("国家数："+countries.size());

            //月份做键，价格做值进行统计，并通过时间排序！
            TreeMap<String,Double> map = new TreeMap<>((a,b)->{
                String[] aa = a.split("-");
                int ayear = Integer.parseInt(aa[0]);
                int amonth = Integer.parseInt(aa[1]);
                String[] bb = b.split("-");
                int byear = Integer.parseInt(bb[0]);
                int bmonth = Integer.parseInt(bb[1]);
                if(ayear!=byear){
                    return ayear-byear;
                } else if (ayear == byear && amonth != bmonth) {
                    return amonth-bmonth;
                }else {
                    return byear-ayear;
                }
            });
            for (int i = 0; i < afterClean.size(); i++) {
                //解析月份
                String date = afterClean.get(i)[4];
                String[] tt = date.split(" ")[0].split("/");
                String month = tt[2]+"-"+tt[0];
                //销售额
                Double price = Double.parseDouble(afterClean.get(i)[5])*Double.parseDouble(afterClean.get(i)[3]);
                map.put(month,map.getOrDefault(month,0.0)+price);
            }
            for(Map.Entry<String,Double> entry:map.entrySet()){
                String month = entry.getKey();
                Double price = entry.getValue();
                //MonthVolumn.Month为月份，格式为yyyy-MM
                //MonthVolumn.Volume为销售额：每月所有商品×单价的金额之和
                MonthVolumn monthVolumn = new MonthVolumn(month, price);
                volumnList.add(monthVolumn);
            }

			//输出JSON并存储至文件
            String json = JSON.toJSONString(volumnList);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(filePath.replace(".csv", ".json"))));
            bufferedWriter.write(json);
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

class MonthVolumn{
    private String Month = "";
    private double Volume = -1;

    public MonthVolumn(String month, double volume) {
        Month = month;
        Volume = volume;
    }

    public double getVolume() {
        return Volume;
    }
    public void setVolume(double volume) {
        Volume = volume;
    }
    public String getMonth() {
        return Month;
    }
    public void setMonth(String month) {
        Month = month;
    }
}