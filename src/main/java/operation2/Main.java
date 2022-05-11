package operation2;

import org.junit.Test;

import java.util.Arrays;

public class Main {
    private static  int mask = 9; //Mask = mask × mask
    public static void main(String[] args){
        try {
            String path = "D:\\IDEA_Project\\BigData\\src\\main\\java\\operation2\\";
            int[][][] pixels = ImagePixelTool.readPixelsFromImage(path + "origin.jpeg");
            //执行去色，输出灰度图
            int[][] grayPixs = ImagePixelTool.convertColorToGray(pixels);
            ImagePixelTool.writePixelsToImage(path + "gray.jpeg", grayPixs);	//执行存储
            //执行噪声添加，输出噪声灰度图
            int[][] grayPixelsWithNoise = ImagePixelTool.addNoise(grayPixs, 0.02);
            ImagePixelTool.writePixelsToImage(path + "gray-noise.jpeg", grayPixelsWithNoise);	//执行存储
			//图片高宽
            int height = grayPixelsWithNoise.length, width = grayPixelsWithNoise[0].length;
			//操作grayPixelsWithNoise数组实现各种滤波器
            System.out.println("数组高："+height+",数组宽"+width);


            //执行中值滤波降噪，输出降噪后噪声灰度图
            int flag = mask/2;
            int[][] grayPixelsWithMiddleFilter = new int[height-2*flag][width-2*flag];
            for (int i = flag; i < width-flag; i++) {
                for (int j = flag; j < height-flag; j++) {
                    grayPixelsWithMiddleFilter[i-flag][j-flag] = sortWithMiddleFilter(grayPixelsWithNoise,i-flag,j-flag,mask);
                }
            }
            ImagePixelTool.writePixelsToImage(path + "gray-filter-noise-"+mask+"x"+mask+".jpeg", grayPixelsWithMiddleFilter);


            //执行均值滤波器模板，输出图像
            int[][] grayPixelsWithAverageFilter = new int[height-2*flag][width-2*flag];
            for (int i = flag; i < width-flag; i++) {
                for (int j = flag; j < height-flag; j++) {
                    grayPixelsWithAverageFilter[i-flag][j-flag] = sortWithAverageFilter(grayPixs,i-flag,j-flag,mask);
                }
            }
            ImagePixelTool.writePixelsToImage(path + "gray-averagefilter-"+mask+"x"+mask+".jpeg", grayPixelsWithAverageFilter);

            if(mask==3){

                //对降噪后的图片执行拉普拉斯中值变换，输出图像边缘检测图
                int[][] grayPixelsWithLaplaceFilter = new int[height-2*flag][width-2*flag];
                for (int i = flag; i < grayPixelsWithMiddleFilter.length-flag; i++) {
                    for (int j = flag; j < grayPixelsWithMiddleFilter[0].length-flag; j++) {
                        grayPixelsWithLaplaceFilter[i-flag][j-flag] = sortWithLaplaceFilter(grayPixelsWithMiddleFilter,i-flag,j-flag,mask);
                    }
                }
                ImagePixelTool.writePixelsToImage(path + "gray-laplacefilter-"+mask+"x"+mask+".jpeg", grayPixelsWithLaplaceFilter);

                //使用高通滤波器滤波，模板1
                int[][] grayPixelsWithHighPassFilter1 = new int[height-2*flag][width-2*flag];
                for (int i = flag; i < grayPixelsWithMiddleFilter.length-flag; i++) {
                    for (int j = flag; j < grayPixelsWithMiddleFilter[0].length-flag; j++) {
                        grayPixelsWithHighPassFilter1[i-flag][j-flag] = sortWithHighPassFilter1(grayPixelsWithMiddleFilter,i-flag,j-flag,mask);
                    }
                }
                ImagePixelTool.writePixelsToImage(path + "gray-highpassfilter1-"+mask+"x"+mask+".jpeg", grayPixelsWithHighPassFilter1);


                //使用高通滤波器滤波，模板2
                int[][] grayPixelsWithHighPassFilter2 = new int[height-2*flag][width-2*flag];
                for (int i = flag; i < grayPixelsWithMiddleFilter.length-flag; i++) {
                    for (int j = flag; j < grayPixelsWithMiddleFilter[0].length-flag; j++) {
                        grayPixelsWithHighPassFilter2[i-flag][j-flag] = sortWithHighPassFilter2(grayPixelsWithMiddleFilter,i-flag,j-flag,mask);
                    }
                }
                ImagePixelTool.writePixelsToImage(path + "gray-highpassfilter2-"+mask+"x"+mask+".jpeg", grayPixelsWithHighPassFilter2);


                //使用高通滤波器滤波，模板3
                int[][] grayPixelsWithHighPassFilter3 = new int[height-2*flag][width-2*flag];
                for (int i = flag; i < grayPixelsWithMiddleFilter.length-flag; i++) {
                    for (int j = flag; j < grayPixelsWithMiddleFilter[0].length-flag; j++) {
                        grayPixelsWithHighPassFilter3[i-flag][j-flag] = sortWithHighPassFilter3(grayPixelsWithMiddleFilter,i-flag,j-flag,mask);
                    }
                }
                ImagePixelTool.writePixelsToImage(path + "gray-highpassfilter3-"+mask+"x"+mask+".jpeg", grayPixelsWithHighPassFilter3);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // 中值滤波
    public static int sortWithMiddleFilter(int[][] x,int i,int j,int mask){
        int[] sortArray = new int[mask*mask];
        int index = 0;
        for (int m = i; m < i+mask; m++) {
            for (int n = j; n < j+mask; n++) {
                sortArray[index++]=x[m][n];
            }
        }
        Arrays.sort(sortArray);
        return sortArray[mask*mask/2];
    }

    //拉普拉斯滤波器
    public static int sortWithLaplaceFilter(int[][] x,int i,int j,int mask){
        int output = 0,index=0;
        int mid = 0;
        for (int m = i; m < i+mask; m++) {
            for (int n = j; n < j+mask; n++) {
                index++;
                if(index==mask*mask/2+1){
                    mid = x[m][n];
                    continue;
                }
                output += x[m][n];
            }
        }
        output -= (8*mid);
        if (output<0) return 0;
        if (output>255) return 255;
        return output;
    }

    //均值滤波器
    public static int sortWithAverageFilter(int[][] x,int i,int j,int mask){
        int output = 0;
        for (int m = i; m < i+mask; m++) {
            for (int n = j; n < j+mask; n++) {
                output += x[m][n];
            }
        }
        return output/(mask*mask);
    }

    //高通滤波器模板1
    public static int sortWithHighPassFilter1(int[][] x,int i,int j,int mask){
        int[] sortArray = new int[mask*mask];
        int index = 0;
        for (int m = i; m < i+mask; m++) {
            for (int n = j; n < j + mask; n++) {
                sortArray[index++] = x[m][n];
            }
        }
        int output = sortArray[0]-(2*sortArray[1])+sortArray[2]-(2*sortArray[3])+(5*sortArray[4])-(2*sortArray[5])+sortArray[6]-(2*sortArray[7])+sortArray[8];
        if (output<0) return 0;
        if (output>255) return 255;
        return output;
    }

    //高通滤波器模板2
    public static int sortWithHighPassFilter2(int[][] x,int i,int j,int mask){
        int[] sortArray = new int[mask*mask];
        int index = 0;
        for (int m = i; m < i+mask; m++) {
            for (int n = j; n < j + mask; n++) {
                sortArray[index++] = x[m][n];
            }
        }
        int output = -1*sortArray[1]-sortArray[3]+(5*sortArray[4])-sortArray[5]-sortArray[7];
        if (output<0) return 0;
        if (output>255) return 255;
        return output;
    }

    //高通滤波器模板3
    public static int sortWithHighPassFilter3(int[][] x,int i,int j,int mask){
        int[] sortArray = new int[mask*mask];
        int index = 0;
        for (int m = i; m < i+mask; m++) {
            for (int n = j; n < j + mask; n++) {
                sortArray[index++] = x[m][n];
            }
        }
        int output = -sortArray[0]-sortArray[1]-sortArray[2]-sortArray[3]+(9*sortArray[4])-sortArray[5]-sortArray[6]-sortArray[7]-sortArray[8];
        if (output<0) return 0;
        if (output>255) return 255;
        return output;
    }

}