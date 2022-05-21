package experiment2;

import com.csvreader.CsvReader;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @version V1.0
 * @ClassName: experiment2.IrisClass.java
 * @Copyright swpu
 * @author: zty-f
 * @date: 2022-05-21 14:48
 * @Description: 鸢尾花分类
 */
public class IrisClass {
    public static void main(String[] args) throws Exception {
        //样本集准备
        List<String> attrTags = new ArrayList<>();  //定义标称值
        attrTags.add("Iris-setosa");
        attrTags.add("Iris-versicolor");
        attrTags.add("Iris-virginica");
        //创建模型Instances，模型名为iris
        Instances data = new Instances("iris", new ArrayList(Arrays.asList(new Attribute[]{
                new Attribute("x1"),	 			//数值类型属性
                new Attribute("y1"),	 			//数值类型属性
                new Attribute("x2"),	 			//数值类型属性
                new Attribute("y2"),	 			//数值类型属性
                new Attribute("subject", attrTags)			//创建标称属性(科目)
        })), 0);

        //读取文件
        String filePath = "D:\\IDEA_Project\\BigData\\src\\main\\java\\experiment2\\iris.csv";
        ArrayList<String[]> originList = ReadFileUtil.readCSV(filePath);

        System.out.println("原始数据集大小："+originList.size());

        //将Iris数据装入样本集，并设置最后一列为标签
        for (int i = 0; i < originList.size(); i++) {
            String[] t = originList.get(i);
            //System.out.println(Arrays.toString(t));
            data.add(new DenseInstance(1.0, new double[]{
                    Double.parseDouble(t[0]), 					   //花萼长度cm 数值型
                    Double.parseDouble(t[1]), 					   //花萼宽度cm 数值型
                    Double.parseDouble(t[2]), 					   //花瓣长度cm 数值型
                    Double.parseDouble(t[3]), 					   //花瓣宽度cm 数值型
                    attrTags.indexOf(t[4]),  		   //写入第5列subject，标称型（标称值转为List下标值）
            }));
        }
        data.setClassIndex(data.numAttributes() - 1);	//设置最后一列为标签

        //打乱样本集顺序
        data.randomize(new Random());
        //划分训练集和测试集，按8:2的比例
        int trainSize = (int)(data.size() * 0.8);
        System.out.println(trainSize);
        //将data从下标0开始划一个长度为trainSize的    训练集
        Instances train = new Instances(data, 0, trainSize);
        //将data从下标trainSize开始划一个长度为data.size() - trainSize的   测试集
        Instances test = new Instances(data, trainSize, data.size() - trainSize);

        //2、模型的创建和训练
        String[] options = new String[1];	//创建选项
        options[0] = "-U";			//不修剪树（不同分类器的选项值参见2.5官方文档）
        J48 cls = new J48();			//创建J48决策树分类器对象
        cls.setOptions(options);		//设置选项
        cls.buildClassifier(train);		//训练模型

        //3、模型的测试（评估）
        Evaluation eval = new Evaluation(train);
        eval.evaluateModel(cls, test);	//执行测试
        System.out.println(eval.toSummaryString("Test Summary\n", false));  //打印测试结果摘要信息
    }
}
/*
//分类正确样本				正确次数	准确率(Accuracy)
Correctly Classified Instances	29		96.6667 %
//分类错误样本				错误次数	错误率
Incorrectly Classified Instances	1		3.3333 %
Kappa statistic				0.9495	//Kappa统计量
Mean absolute error			0.0313	//平均绝对误差(MAE)
Root mean squared error		0.1462	//均方根误差(RMSE)
Relative absolute error			7.0355 %	//相对绝对误差(RAE)
Root relative squared error		30.9282 %	//相对平方根误差
Total Number of Instances		30 		//测试集样本总数
 */
