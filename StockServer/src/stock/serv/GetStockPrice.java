package stock.serv;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.csvreader.CsvReader;

import stock.util.Message;
import stock.util.Price;
import stock.util.Stock;

/**
 * Servlet implementation class GetStockPrice
 */
@WebServlet("/GetStockPrice")
public class GetStockPrice extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public GetStockPrice() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");  
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));	//跨域设置
		Message msg = new Message();	//消息对象

        //接收数据
		String code = request.getParameter("code");	//股票代码
    	code = code == null ? "" : code.trim().toUpperCase(); 
		try{
			//本页缓存（局部变量）
    		HashMap<String, List<Price>> stockPrice = new HashMap();	//股票价格列表
    		//全局缓存（Application）
        	Object stockPriceObj = this.getServletContext().getAttribute("stockPrice");
        	//若全局缓存不为空，则加载到本页
        	if(stockPriceObj != null){
        		stockPrice = (HashMap<String, List<Price>>)stockPriceObj;
        	}

        	List<Price> resultList = new ArrayList();
        	//若全局缓存有该股票，直接读缓存
        	if(stockPrice.containsKey(code)){
        		resultList = stockPrice.get(code);
        	}
        	//若全局缓存为空或无该股票，则从文件加载缓存
        	else{
        		String filePath = request.getServletContext().getRealPath("/") + "files/stocks/" + code + ".csv";
        		File stockFile = new File(filePath);
        		if(stockFile.exists()){
					//读取股票代码名称
			        CsvReader reader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
			        int i = 0;
		            while (reader.readRecord()) {
		            	String[] values = reader.getValues();
		            	//写入股票列表
		            	Price dailyPrice = new Price();
		            	dailyPrice.setDate(values[0].replaceAll("/", "-"));
		            	dailyPrice.setPrice(Double.parseDouble(values[1]));
		            	resultList.add(dailyPrice);
		            	i ++;
		            }
		            reader.close();
		            //写入全局缓存
		            stockPrice.put(code, resultList);
	        		this.getServletContext().setAttribute("stockPrice", stockPrice);
        		}
        		else{
        			throw new Exception("无股票" + code + "价格数据");
        		}
        	}
        	
			//设置消息对象
			msg.setState("SUC");
			msg.setMsg("股票" + code + "价格");
			msg.setContent(resultList.toArray());
		}
		catch(Exception e){
			msg.setState("ERR");
			msg.setMsg(e.getMessage());
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		msg.setTime(df.format(new Date()));		//当前系统时间
		//序列化JSON并返回
		PrintWriter out = response.getWriter();
		out.print(JSON.toJSONString(msg));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
