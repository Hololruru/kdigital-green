package com.webscraping.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Controller
@RequestMapping(path = "/openapi")
public class OpenApiController {
	
	@GetMapping(path = { "/covid19" })
	public String showCovid19Form(Model model) {
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			//String today = sdf.format(new Date());
			String today = "20201130";
			
			StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1352000/ODMS_COVID_02/callCovid02Api"); /*URL*/
	        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=R%2BBEwJ49XVmTutdoCAdNq4m7wjOsdFNhVtlzkxWVeZJPhwQqWbGcD7CNeQPipFzIWAanQVw%2FrPLsGkfcYvD4AQ%3D%3D"); /*Service Key*/
	        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
	        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("500", "UTF-8")); /*한 페이지 결과 수*/
	        urlBuilder.append("&" + URLEncoder.encode("apiType","UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8")); /*결과형식(xml/json)*/
	        urlBuilder.append("&" + URLEncoder.encode("status_dt","UTF-8") + "=" + URLEncoder.encode(today, "UTF-8")); /*기준일자*/
	        
	        URL url = new URL(urlBuilder.toString());
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Content-type", "application/json");
	        
	        System.out.println("Response code: " + conn.getResponseCode());
	        
//	        BufferedReader rd;
//	        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//	            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//	        } else {
//	            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//	        }
//	        StringBuilder sb = new StringBuilder();
//	        String line;
//	        while ((line = rd.readLine()) != null) {
//	            sb.append(line);
//	        }
//	        rd.close();
//	        conn.disconnect();
//	        System.out.println(sb.toString());
	        
	        
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        
	        Document doc = builder.parse(conn.getInputStream()); // xml 문자열 -> 객체 트리
	        
	        NodeList items = doc.getElementsByTagName("item");
	        
	        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
	        
	        for (int i = 0; i < items.getLength(); i++) {
	        	Node node = items.item(i); // Node : tag, text, cdatasection, pi, declaration,
	        	//System.out.println("Node Type : " + node.getNodeType());
	        	//System.out.println("Node Name : " + node.getNodeName());
	        	
	        	HashMap<String, Object> tmp = new HashMap<>();
	        	NodeList children = node.getChildNodes();
	        	for (int j = 0; j < children.getLength(); j++) {
	        		Node child = children.item(j);
	        		//System.out.print(j + ". Node Type : " + child.getNodeType() + " / ");
	        		//System.out.print("Node Name : " + child.getNodeName() + " / ");
	        		//System.out.println("Node Value : " + child.getTextContent());
	        		if (child.getNodeType() == 1) {
	        			tmp.put(child.getNodeName(), child.getTextContent());
	        		}
	        	}
	        	data.add(tmp);
	        }
	        
	        model.addAttribute("data", data);
	        
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return "openapi/covid19";
	}

	@GetMapping(path = { "/naver-movie" })
	public String showNaverMovieSearchForm() {
		
		return "openapi/naver-movie";
	}
	
	@GetMapping(path = { "/search-movie" })
	@ResponseBody
	public String searchNaverMovie(String title, Model model) {
		
		String clientId = "SpfHcpgJIZfwSahhohl4"; //애플리케이션 클라이언트 아이디
        String clientSecret = "hcwdgwgQkw"; //애플리케이션 클라이언트 시크릿

        String text = null;
        try {
            text = URLEncoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String apiURL = "https://openapi.naver.com/v1/search/movie.xml?query=" + text;    // XML 결과
        //String apiURL = "https://openapi.naver.com/v1/search/movie.json?query="+ text; // JSON 결과

        apiURL += "&display=100";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        
        // String responseBody = get(apiURL, requestHeaders);
        // System.out.println(responseBody);
        
        try {
        	HttpURLConnection con = connect(apiURL);
        	con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode(); // 요청 전송 + 응답 수신
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                InputStream is = con.getInputStream();
                
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder parser = factory.newDocumentBuilder(); // xml parser
                Document doc = parser.parse(is); // xml string -> object tree
                
                NodeList items = doc.getElementsByTagName("item"); // <item>...</item>을 모두 찾아서 반환
                ArrayList<HashMap<String, Object>> movies = new ArrayList<>(); // 영화 목록 저장 변수
                for (int i = 0; i < items.getLength(); i++) {
                	Node item = items.item(i);
                	NodeList children = item.getChildNodes();
                	HashMap<String, Object> movie = new HashMap<>(); // 한 편의 영화 저장 변수
                	for (int j = 0; j < children.getLength(); j++) {
                		Node child = children.item(j);
                		// System.out.printf("[%s : %s]", child.getNodeName(), child.getTextContent());
                		movie.put(child.getNodeName(), child.getTextContent());
                	}
                	// System.out.println();
                	movies.add(movie);
                }
                model.addAttribute("movies", movies); // JSP에서 읽을 수 있도록 저장
            } else { // 오류 발생
            	System.out.println("오류 발생");
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
		
		return "success";
	}
	
	private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode(); // 요청 전송 + 응답 수신
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }
	
	private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl); // URL : 네트워크 요청 처리 클래스
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }
	
	private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }
}













