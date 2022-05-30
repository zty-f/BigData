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
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));	//��������
		Message msg = new Message();	//��Ϣ����

        //��������
		String code = request.getParameter("code");	//��Ʊ����
    	code = code == null ? "" : code.trim().toUpperCase(); 
		try{
			//��ҳ���棨�ֲ�������
    		HashMap<String, List<Price>> stockPrice = new HashMap();	//��Ʊ�۸��б�
    		//ȫ�ֻ��棨Application��
        	Object stockPriceObj = this.getServletContext().getAttribute("stockPrice");
        	//��ȫ�ֻ��治Ϊ�գ�����ص���ҳ
        	if(stockPriceObj != null){
        		stockPrice = (HashMap<String, List<Price>>)stockPriceObj;
        	}

        	List<Price> resultList = new ArrayList();
        	//��ȫ�ֻ����иù�Ʊ��ֱ�Ӷ�����
        	if(stockPrice.containsKey(code)){
        		resultList = stockPrice.get(code);
        	}
        	//��ȫ�ֻ���Ϊ�ջ��޸ù�Ʊ������ļ����ػ���
        	else{
        		String filePath = request.getServletContext().getRealPath("/") + "files/stocks/" + code + ".csv";
        		File stockFile = new File(filePath);
        		if(stockFile.exists()){
					//��ȡ��Ʊ��������
			        CsvReader reader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
			        int i = 0;
		            while (reader.readRecord()) {
		            	String[] values = reader.getValues();
		            	//д���Ʊ�б�
		            	Price dailyPrice = new Price();
		            	dailyPrice.setDate(values[0].replaceAll("/", "-"));
		            	dailyPrice.setPrice(Double.parseDouble(values[1]));
		            	resultList.add(dailyPrice);
		            	i ++;
		            }
		            reader.close();
		            //д��ȫ�ֻ���
		            stockPrice.put(code, resultList);
	        		this.getServletContext().setAttribute("stockPrice", stockPrice);
        		}
        		else{
        			throw new Exception("�޹�Ʊ" + code + "�۸�����");
        		}
        	}
        	
			//������Ϣ����
			msg.setState("SUC");
			msg.setMsg("��Ʊ" + code + "�۸�");
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
