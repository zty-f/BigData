package stock.serv;


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
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;

import stock.util.Message;
import stock.util.Stock;

/**
 * Servlet implementation class GetStockList
 */
@WebServlet("/GetStockList")
public class GetStockList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetStockList() {
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
		String keyword = request.getParameter("keyword");	//用户查询关键词
    	keyword = keyword == null ? "" : keyword.trim();
    	keyword = keyword.length() > 6 ? keyword.substring(0, 6): keyword; 
		int maxRecordCount = 50;
		try{
			//本页缓存（局部变量）
    		List<Stock> stocks = new ArrayList();	//股票代码名称列表
    		HashMap<String, List<Integer>> stockIndex = new HashMap();	//股票代码查询索引
    		//全局缓存（Application）
        	Object stocksObj = this.getServletContext().getAttribute("stocks");
        	Object stockIndexObj = this.getServletContext().getAttribute("stockIndex");
        	//若全局缓存不为空，则加载到本页
        	if(stocksObj != null && stockIndexObj != null){
        		stocks = (List<Stock>)stocksObj;
        		stockIndex = (HashMap<String, List<Integer>>)stockIndexObj;
        	}
        	//若全局缓存为空，则从文件加载缓存
        	else{
				//读取股票代码名称
		        CsvReader reader = new CsvReader(request.getServletContext().getRealPath("/") + "files/stockdir.csv", ',', Charset.forName("UTF-8"));
		        reader.readRecord();
		        int i = 0;
	            while (reader.readRecord()) {
	            	String[] values = reader.getValues();
	            	//写入股票列表
	            	Stock stock = new Stock();
	            	stock.setCode(values[0]);
	            	stock.setName(values[1]);
	            	stocks.add(stock);
	            	//构造股票代码查询索引
	            	for(int j = 1; j <= 6; j ++){
	            		String key = stock.getCode().substring(0, j);
	            		if(!stockIndex.containsKey(key))
	            			stockIndex.put(key, new ArrayList<Integer>());
	            		stockIndex.get(key).add(i);
	            	}
	            	i ++;
	            }
	            reader.close();
	            //写入全局缓存
        		this.getServletContext().setAttribute("stocks", stocks);
        		this.getServletContext().setAttribute("stockIndex", stockIndex);
        	}
        	
        	//以用户输入的关键词执行查询。查询所有以关键词开头的股票代码对应的股票
        	List<Stock> resultList = new ArrayList();
        	//无关键词则返回默认
        	if(keyword.equals("")){
        		resultList = stocks.subList(0, maxRecordCount);
        	}
        	//有关键词则执行查询
        	else{
	        	keyword = keyword.trim();
	        	if(stockIndex.containsKey(keyword)){
	        		int count = stockIndex.get(keyword).size();
	        		for(int i = 0; i < count; i ++){
	        			resultList.add(stocks.get(stockIndex.get(keyword).get(i)));
	        			if(i + 1 >= maxRecordCount)
	        				break;
        			}
	        	}
	        	else{
	        		throw new Exception("Keyword " + keyword + "未查询到股票");
	        	}
        	}
			//设置消息对象
			msg.setState("SUC");
			msg.setMsg("股票列表");
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
