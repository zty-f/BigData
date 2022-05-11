package operation1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Knn {
    private static int K = 1700;
    public static void main(String[] args){
        try{
            //读取训练集
            List<int[]> trainVects = new ArrayList();		//训练集特征向量
            List<Integer> trainLabels = new ArrayList();	//训练集Label
            readDataSet("D:\\IDEA_Project\\BigData\\src\\main\\java\\operation1\\train.csv", trainVects, trainLabels);
            //读取测试集
            List<int[]> testVects = new ArrayList();		//测试集特征向量
            List<Integer> testLabels = new ArrayList();		//测试集Label
            readDataSet("D:\\IDEA_Project\\BigData\\src\\main\\java\\operation1\\test_200.csv", testVects, testLabels);
            List<Integer> res = knn(trainVects, trainLabels, testVects);
            System.out.println("正确分类标签:"+testLabels);
            System.out.println("识别分类标签:"+res);
            double cnt = 0;
            for (int i = 0; i < testLabels.size(); i++) {
                if(testLabels.get(i)==res.get(i)){
                    cnt++;
                }
            }
            System.out.println("识别结果准确率:"+cnt/testLabels.size()*100+"%");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //主要方法
    public static List<Integer> knn(List<int[]> trainVects,List<Integer> trainLabels,
                       List<int[]> testVects){
        List<Integer> res = new ArrayList<>();
        //循环遍历每个训练数据和测试数据
        for (int i = 0; i < testVects.size(); i++) {
            //数字中只包含0~9，所以使用数组记录每个测试数据对应得到训练label的数量
            int[] cnt = new int[10];
            for (int j = 0; j < trainLabels.size(); j++) {
                double d = 0;//记录每个测试点向量到训练集向量的欧氏距离
                for (int k = 0; k < 784; k++) {
                    d += Math.pow(testVects.get(i)[k] - trainVects.get(j)[k],2);
                }
                d = Math.sqrt(d);
                //分类
                if(d<K){
                    cnt[trainLabels.get(j)]++;
                }
            }
            int[] tmp = Arrays.copyOf(cnt,10);
            Arrays.sort(tmp);
            //获取label数最多的label作为该测试数据的label
            int max = tmp[9];
            int sort = 0;
            for (int j = 0; j < 10; j++) {
                if (max==cnt[j]){
                    sort = j;
                    break;
                }
            }
            res.add(sort);
        }
        return res;
    }

    //读取特征向量和label
    private static void readDataSet(String path, List<int[]> vects, List<Integer> labels) throws Exception{
        //数据集文件名
        File trainFile = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(trainFile));
        String line = "";
        br.readLine();  //忽略表头
        //遍历所有行
        while ((line = br.readLine()) != null) {   //使用readLine方法，一次读一行
            String[] items = line.split(",");
            int len = items.length;
            int[] vect = new int[len - 1];
            //读取第一列，写入label
            labels.add(Integer.parseInt(items[0]));
            //遍历后续列，写入特征向量
            for(int i = 1; i < len; i ++){
                vect[i - 1] = Integer.parseInt(items[i]);
            }
            vects.add(vect);
        }
        br.close();
    }
}