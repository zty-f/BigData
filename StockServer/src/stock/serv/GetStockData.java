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
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));	//��������
		Message msg = new Message();	//��Ϣ����

        //��������
		String code = request.getParameter("code");		//��Ʊ����
		String date = request.getParameter("date");		//����
		int maxRecordCount = 50;
		try{
			if(code == null || code.equals(""))
				throw new Exception("��code����");
			if(date == null || date.equals(""))
				throw new Exception("��date����");
			code = code.trim().toUpperCase();
			date = date.replaceAll("-(?=\\d-)", "-0").replaceAll("-(?=\\d$)", "-0");//Ϊ���ڲ�0���������ļ����е����ڸ�ʽ
			
			//��ҳ���棨�ֲ�������
    		HashMap<String, HashMap<String, Detail>> stockDetail = new HashMap();	//��Ʊ������
    		//ȫ�ֻ��棨Application��
        	Object stockDetailObj = this.getServletContext().getAttribute("stockDetail");
        	//��ȫ�ֻ��治Ϊ�գ�����ص���ҳ
        	if(stockDetailObj != null){
        		stockDetail = (HashMap<String, HashMap<String, Detail>>)stockDetailObj;
        	}
        	
        	Detail resultDetail = null;
        	//��ȫ�ֻ����и����ں͹�Ʊ��ֱ�Ӷ�����
        	if(stockDetail.containsKey(date) && stockDetail.get(date).containsKey(code)){
        		resultDetail = stockDetail.get(date).get(code);
        	}
        	//��ȫ�ֻ����и�����(���޹�Ʊ)
        	else if(stockDetail.containsKey(date)){
    			throw new Exception("����" + date + "�޹�Ʊ" + code + "����");
        	}
        	//��ȫ�ֻ���Ϊ�գ�����ļ����ػ���
        	else{
				//��ȡ��Ӧ���������ļ�
        		String dateFilePath = request.getServletContext().getRealPath("/") + "files/values/��Ʊ������" + date + ".csv";
        		File dateFile = new File(dateFilePath);
        		if(!dateFile.exists())
        			throw new Exception("������" + date + "����");
		        CsvReader reader = new CsvReader(dateFilePath, ',', Charset.forName("UTF-8"));
		        reader.readRecord();	//������ͷ
		        int i = 0;
		        HashMap<String, Detail> detailDict = new HashMap();
	            while (reader.readRecord()) {
	            	String[] values = reader.getValues();
	            	//д�����������й�Ʊ����
	            	Detail detail = new Detail();
	            	detail.setCode(values[0]);			//��Ʊ����
	            	detail.setSt(values[1] == "TRUE");	//�Ƿ�ST
	            	detail.setOpen(Double.parseDouble(values[2]));	//���̼�
	            	detail.setClose(Double.parseDouble(values[3]));	//���̼�
	            	detail.setHigh(Double.parseDouble(values[4]));	//��߼�
	            	detail.setLow(Double.parseDouble(values[5]));	//��ͼ�
	            	detail.setVolume(Long.parseLong(values[6]));	//������
	            	detail.setMoney(new BigDecimal(values[7]));		//���׽��
	            	detail.setPaused(values[12] == "TRUE");			//�Ƿ�ͣ��
	            	detailDict.put(values[0], detail);
	            	if(detail.getCode().equals(code)){
	            		resultDetail = detail;
	            	}
	            }
	            reader.close();
	            //д��ȫ�ֻ���
            	stockDetail.put(date, detailDict);
        		this.getServletContext().setAttribute("stockDetail", detailDict);
        		if(resultDetail == null){
        			throw new Exception("����" + date + "�޹�Ʊ" + code + "����");
        		}
        	}
        	
			//������Ϣ����
			msg.setState("SUC");
			msg.setMsg(date + "��Ʊ" + code + "����");
			msg.setContent(resultDetail);
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
