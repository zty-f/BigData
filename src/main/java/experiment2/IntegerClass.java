package experiment2;

import weka.classifiers.Evaluation;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.rules.OneR;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @version V1.0
 * @ClassName: experiment2.IntegerClass.java
 * @Copyright swpu
 * @author: zty-f
 * @date: 2022-05-21 16:04
 * @Description: 数字分类
 */
public class IntegerClass {
    public static void main(String[] args) throws Exception {
        //训练样本集准备
        List<String> attrTags = new ArrayList<>(Arrays.asList(new String[]{"0","1","2","3","4","5","6","7","8","9"}));  //定义标称值

        //创建模型Instances，模型名为train
        ArrayList<Attribute> attrs1 = new ArrayList<>();
        attrs1.add(new Attribute("subject",attrTags));
        for (int i = 0; i < 784; i++) {
            attrs1.add(new Attribute("p"+i));
        }
        Instances train = new Instances("train",attrs1,0);

        //测试样本集准备
        //创建模型Instances，模型名为test
        ArrayList<Attribute> attrs2 = new ArrayList<>();
        attrs2.add(new Attribute("subject",attrTags));
        for (int i = 0; i < 784; i++) {
            attrs2.add(new Attribute("p"+i));
        }
        Instances test = new Instances("test",attrs2,0);

        //读取训练集文件，并装入到训练样本集
        String filePath1 = "D:\\IDEA_Project\\BigData\\src\\main\\java\\experiment2\\train.csv";
        ArrayList<String[]> trainList = ReadFileUtil.readCSV(filePath1);
        for (int i = 1; i < trainList.size(); i++) {
            String[] t = trainList.get(i);
            double[] data = new double[t.length];
            data[0] = attrTags.indexOf(t[0]);
            for (int j = 1; j < t.length; j++) {
                data[j] = Double.parseDouble(t[j]);
            }
            train.add(new DenseInstance(1.0, data));
        }
        train.setClassIndex(0);

        //读取测试集文件，并装入到测试样本集
        String filePath2 = "D:\\IDEA_Project\\BigData\\src\\main\\java\\experiment2\\test_200.csv";
        ArrayList<String[]> testList = ReadFileUtil.readCSV(filePath2);
        for (int i = 1; i < testList.size(); i++) {
            String[] t = testList.get(i);
            double[] data = new double[t.length];
            data[0] = attrTags.indexOf(t[0]);
            for (int j = 1; j < t.length; j++) {
                data[j] = Double.parseDouble(t[j]);
            }
            test.add(new DenseInstance(1.0, data));
        }
        test.setClassIndex(0);

        //调用不同的分类器进行分类

        //1.J48决策树分类器
        J48Classifier(train,test);
        System.out.println("======================================================");

        //2.OneR分类器
        OneRClassifier(train,test);
        System.out.println("======================================================");

        //2.Bagging分类器
        BaggingClassifier(train,test);
        System.out.println("======================================================");

        //2.LogitBoost分类器
        LogitBoostClassifier(train,test);
        System.out.println("======================================================");

    }


    // J48决策树分类器
    public static void J48Classifier(Instances train,Instances test) throws Exception {
        //模型的创建和训练
        String[] options = new String[1];	//创建选项
        options[0] = "-U";			//不修剪树（不同分类器的选项值参见2.5官方文档）
        J48 cls = new J48();			//创建J48决策树分类器对象
        cls.setOptions(options);		//设置选项
        cls.buildClassifier(train);		//训练模型

        //模型的测试（评估）
        Evaluation eval = new Evaluation(train);
        eval.evaluateModel(cls, test);	//执行测试
        System.out.println(eval.toSummaryString("J48决策树分类器分类结果：\n", false));  //打印测试结果摘要信息
    }

    // OneR分类器
    public static void OneRClassifier(Instances train,Instances test) throws Exception {
        //模型的创建和训练
        OneR oneR = new OneR();
        oneR.setDebug(false);
        oneR.setMinBucketSize(6);
        oneR.buildClassifier(train);		//训练模型
        //模型的测试（评估）
        Evaluation eval = new Evaluation(train);
        eval.evaluateModel(oneR, test);	//执行测试
        System.out.println(eval.toSummaryString("OneR分类器分类结果：\n", false));  //打印测试结果摘要信息
    }

    // Bagging分类器
    public static void BaggingClassifier(Instances train,Instances test) throws Exception {
        //模型的创建和训练
        Bagging bagging = new Bagging();
        bagging.buildClassifier(train);		//训练模型
        //模型的测试（评估）
        Evaluation eval = new Evaluation(train);
        eval.evaluateModel(bagging, test);	//执行测试
        System.out.println(eval.toSummaryString("Bagging分类器分类结果：\n", false));  //打印测试结果摘要信息
    }

    // LogitBoost分类器
    public static void LogitBoostClassifier(Instances train,Instances test) throws Exception {
        //模型的创建和训练
        LogitBoost logitBoost = new LogitBoost();
        logitBoost.buildClassifier(train);		//训练模型
        //模型的测试（评估）
        Evaluation eval = new Evaluation(train);
        eval.evaluateModel(logitBoost, test);	//执行测试
        System.out.println(eval.toSummaryString("LogitBoost分类器分类结果：\n", false));  //打印测试结果摘要信息
    }
}
