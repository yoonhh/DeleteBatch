import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.windfire.apis.asysConnectData;
import com.windfire.apis.asys.asysUsrElement;

public class Delete {
	private static final Logger logger = Logger.getLogger(Delete.class.getName());
	private asysConnectData con = null;
	private String Hostname; // 설치예정인 서버 또는 설치된 ‘IP’ 또는 ‘도메인’ 정보
	private int Port; // XTORM 엔진 내부 통신을 위한 TCP/IP 포트
	private String Description = "Description"; // 연결되는 서버 정보 명칭
	private String ID; // XTORM 엔진 접속 ID 기본 값은 “SUPER”
	private String Password; // XTORM 엔진 접속 PASSWORD 기본 값은 “SUPER”

	// 로그를 나타내기 위한 변수설정
	public long start = System.currentTimeMillis();
	public long end = System.currentTimeMillis();

	Properties prop = new Properties();// 키값을 불러오기 위한 객체 생성
	
	public Delete() throws IOException {

		FileInputStream logFile = new FileInputStream("log4j.properties");
		
		Properties logprop = new Properties();
				
		logprop.load(logFile);
		PropertyConfigurator.configure(logprop);

		try {
			prop.load(new FileInputStream("conf/info.properties"));
			// 생성자에 텍스트 파일의 주소를 넣어주는데 src 폴더 내부에 있는 파일이라면 src/패키지이름(현재 파일로되어있음)/파일이름 으로 간단하게
			// 경로 지정을 할 수 있다.
			// 풀경로로 지정을 안해주면 제대로 못읽어옴 (src를 안붙여서 error 발생)

			Hostname = prop.getProperty("Hostname");
			Port = Integer.parseInt(prop.getProperty("Port"));
			ID = prop.getProperty("ID");
			Password = prop.getProperty("Password");
		} catch (Exception e) {
			
		}
		con = new asysConnectData(Hostname, Port, Description, ID, Password);
	
		
		
	}

	public void deleteBatch() throws IOException {

		File f = new File("conf/EID.list");
		FileReader fr = new FileReader(f);

		BufferedReader br = new BufferedReader(fr);

		asysUsrElement uePage1 = new asysUsrElement(con);

		String elementId = null;
		int all = 0;
		int success = 0;
		int fail = 0;

		while ((elementId = br.readLine()) != null) {
			uePage1.m_elementId = "XTORM_MAIN::" + elementId + "::IMAGE";
			int ret = uePage1.delete();	
			logger.info("delete : " + start / 1000.0 + "초");
			if (ret != 0) {
				logger.error("Error, failed to delete, " + uePage1.getLastError());
				fail++;

			} else {
				logger.info("Success, delete is done, " + uePage1.m_elementId);
				logger.info("delete 완료 : " + (end - start) / 1000.0 + "초");
				success++;
			}
			all++;
		}

		br.close();
		logger.info("총건수 : " + all);
		logger.info("삭제건수 : " + success);
		logger.info("에러건수 : " + fail);

	}

}
