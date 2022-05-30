package stock.serv;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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

import stock.util.Detail;
import stock.util.Message;
import stock.util.Price;
import stock.util.Stock;

/**
 * Servlet implementation class GetStockData
 */
@WebServlet("/GetStockData")
public class GetStockData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetStockData() {
        super();
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
		String code = request.getParameter("code");		//股票代码
		String date = request.getParameter("date");		//日期
		int maxRecordCount = 50;
		try{
			if(code == null || code.equals(""))
				throw new Exception("无code参数");
			if(date == null || date.equals(""))
				throw new Exception("无date参数");
			code = code.trim().toUpperCase();
			date = date.replaceAll("-(?=\\d-)", "-0").replaceAll("-(?=\\d$)", "-0");//为日期补0，以适配文件名中的日期格式
			
			//本页缓存（局部变量）
    		HashMap<String, HashMap<String, Detail>> stockDetail = new HashMap();	//股票日数据
    		//全局缓存（Application）
        	Object stockDetailObj = this.getServletContext().getAttribute("stockDetail");
        	//若全局缓存不为空，则加载到本页
        	if(stockDetailObj != null){
        		stockDetail = (HashMap<String, HashMap<String, Detail>>)stockDetailObj;
        	}
        	
        	Detail resultDetail = null;
        	//若全局缓存有该日期和股票，直接读缓存
        	if(stockDetail.containsKey(date) && stockDetail.get(date).containsKey(code)){
        		resultDetail = stockDetail.get(date).get(code);
        	}
        	//若全局缓存有该日期(但无股票)
        	else if(stockDetail.containsKey(date)){
    			throw new Exception("日期" + date + "无股票" + code + "数据");
        	}
        	//若全局缓存为空，则从文件加载缓存
        	else{
				//读取对应日期数据文件
        		String dateFilePath = request.getServletContext().getRealPath("/") + "files/values/股票日行情" + date + ".csv";
        		File dateFile = new File(dateFilePath);
        		if(!dateFile.exists())
        			throw new Exception("无日期" + date + "数据");
		        CsvReader reader = new CsvReader(dateFilePath, ',', Charset.forName("UTF-8"));
		        reader.readRecord();	//跳过表头
		        int i = 0;
		        HashMap<String, Detail> detailDict = new HashMap();
	            while (reader.readRecord()) {
	            	String[] values = reader.getValues();
	            	//写入日期内所有股票数据
	            	Detail detail = new Detail();
	            	detail.setCode(values[0]);			//股票代码
	            	detail.setSt(values[1] == "TRUE");	//是否ST
	            	detail.setOpen(Double.parseDouble(values[2]));	//开盘价
	            	detail.setClose(Double.parseDouble(values[3]));	//收盘价
	            	detail.setHigh(Double.parseDouble(values[4]));	//最高价
	            	detail.setLow(Double.parseDouble(values[5]));	//最低价
	            	detail.setVolume(Long.parseLong(values[6]));	//交易量
	            	detail.setMoney(new BigDecimal(values[7]));		//交易金额
	            	detail.setPaused(values[12] == "TRUE");			//是否停牌
	            	detailDict.put(values[0], detail);
	            	if(detail.getCode().equals(code)){
	            		resultDetail = detail;
	            	}
	            }
	            reader.close();
	            //写入全局缓存
            	stockDetail.put(date, detailDict);
        		this.getServletContext().setAttribute("stockDetail", detailDict);
        		if(resultDetail == null){
        			throw new Exception("日期" + date + "无股票" + code + "数据");
        		}
        	}
        	
			//设置消息对象
			msg.setState("SUC");
			msg.setMsg(date + "股票" + code + "数据");
			msg.setContent(resultDetail);
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
