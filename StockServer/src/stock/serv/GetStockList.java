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
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));	//��������
		Message msg = new Message();	//��Ϣ����

        //��������
		String keyword = request.getParameter("keyword");	//�û���ѯ�ؼ���
    	keyword = keyword == null ? "" : keyword.trim();
    	keyword = keyword.length() > 6 ? keyword.substring(0, 6): keyword; 
		int maxRecordCount = 50;
		try{
			//��ҳ���棨�ֲ�������
    		List<Stock> stocks = new ArrayList();	//��Ʊ���������б�
    		HashMap<String, List<Integer>> stockIndex = new HashMap();	//��Ʊ�����ѯ����
    		//ȫ�ֻ��棨Application��
        	Object stocksObj = this.getServletContext().getAttribute("stocks");
        	Object stockIndexObj = this.getServletContext().getAttribute("stockIndex");
        	//��ȫ�ֻ��治Ϊ�գ�����ص���ҳ
        	if(stocksObj != null && stockIndexObj != null){
        		stocks = (List<Stock>)stocksObj;
        		stockIndex = (HashMap<String, List<Integer>>)stockIndexObj;
        	}
        	//��ȫ�ֻ���Ϊ�գ�����ļ����ػ���
        	else{
				//��ȡ��Ʊ��������
		        CsvReader reader = new CsvReader(request.getServletContext().getRealPath("/") + "files/stockdir.csv", ',', Charset.forName("UTF-8"));
		        reader.readRecord();
		        int i = 0;
	            while (reader.readRecord()) {
	            	String[] values = reader.getValues();
	            	//д���Ʊ�б�
	            	Stock stock = new Stock();
	            	stock.setCode(values[0]);
	            	stock.setName(values[1]);
	            	stocks.add(stock);
	            	//�����Ʊ�����ѯ����
	            	for(int j = 1; j <= 6; j ++){
	            		String key = stock.getCode().substring(0, j);
	            		if(!stockIndex.containsKey(key))
	            			stockIndex.put(key, new ArrayList<Integer>());
	            		stockIndex.get(key).add(i);
	            	}
	            	i ++;
	            }
	            reader.close();
	            //д��ȫ�ֻ���
        		this.getServletContext().setAttribute("stocks", stocks);
        		this.getServletContext().setAttribute("stockIndex", stockIndex);
        	}
        	
        	//���û�����Ĺؼ���ִ�в�ѯ����ѯ�����Թؼ��ʿ�ͷ�Ĺ�Ʊ�����Ӧ�Ĺ�Ʊ
        	List<Stock> resultList = new ArrayList();
        	//�޹ؼ����򷵻�Ĭ��
        	if(keyword.equals("")){
        		resultList = stocks.subList(0, maxRecordCount);
        	}
        	//�йؼ�����ִ�в�ѯ
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
	        		throw new Exception("Keyword " + keyword + "δ��ѯ����Ʊ");
	        	}
        	}
			//������Ϣ����
			msg.setState("SUC");
			msg.setMsg("��Ʊ�б�");
			msg.setContent(resultList.toArray());
		}
		catch(Exception e){
			msg.setState("ERR");
			msg.setMsg(e.getMessage());
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		msg.setTime(df.format(new Date()));		//��ǰϵͳʱ��
		//���л�JSON������
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
