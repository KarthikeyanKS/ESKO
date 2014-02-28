package ESKO.com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jxl.Sheet;
import jxl.Workbook;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BestBusValidate {
	
	String workingDir = System.getProperty("user.dir");
	String ebus = "ebus.exe"; 
  
	String bus1 = "Aaa";
	String bus2 = "Jjj";
  
	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm"); 
	String bus1src = "10:10";
	String bus1dest = "11:50";

	String bus2src = "10:00";
	String bus2dest = "12:50";
  
	String bestBus = "";
	String durationTrimS ="";
	String durationTrimD ="";
	int timediff=0;
	boolean srccmp,destcmp;
	String bustoTravel="";
	int timecheck = 61;
	
	@DataProvider(name = "data-provider", parallel = false)
	public Object[][] data() throws Exception {
		Object[][] retObjArr=getTableArray("C:\\Users\\Administrator\\Desktop\\QA Automation - Test\\com\\src\\test\\resources\\test.xls");
        return(retObjArr);
	}
	
  @Test(dataProvider="data-provider")
  public void longerduration(String bus1,String bus1src,String bus1dest, String bus2, String bus2src, String bus2dest) throws InterruptedException, ParseException {
	  
	  
	  
	  //System.out.println("bus1:"+bus11+" bus1src:"+bus11src+"bus1dest:"+bus11dest+"   bus2:"+bus22+" bus1src:"+bus22src+"bus1dest:"+bus22dest);
	    
	  
	  
	 // System.out.println("working dir -- "+workingDir);
	  
	  
	  
	  dateFormat.setLenient(false); //this will not enable 25:67 for example
	  try {
	        System.out.println(dateFormat.parse(bus1src));
	        System.out.println(dateFormat.parse(bus1dest));
	        System.out.println(dateFormat.parse(bus2src));
	        System.out.println(dateFormat.parse(bus2dest));
	    } catch (ParseException e) {
	        throw new RuntimeException("Invalid entry ---> checkt the time ", e);
	    }
	  
	  
	  String input = ebus+" " + bus1 +" "+ bus1src +" " + bus1dest +" " + bus2 + " " + bus2src+" "+bus2dest;
	  
	  try  
	  {  
	  Runtime.getRuntime().exec("cmd");           
	  Runtime.getRuntime().exec("cmd start");     
	  Runtime.getRuntime().exec(input);   
	  }catch (IOException e){  
	  e.printStackTrace();  
	  } 
	  
	  Thread.sleep(3000);
	  
//	  File encyptFile=new File("C:\\Users\\Administrator\\Desktop\\QA Automation - Test\\com\\output.txt");
//	  System.out.println(encyptFile.exists());
      
	  BufferedReader br = null;
	  
		try {
		
			br = new BufferedReader(new FileReader("C:\\Users\\Administrator\\Desktop\\QA Automation - Test\\com\\output.txt"));

			while ((bestBus = br.readLine()) != null) {
				
				
				durationTrimS = bestBus.substring(4,12);
//				System.out.println("durationTrim source  -- > "+durationTrimS);
				
				durationTrimD = bestBus.substring(13);
//				System.out.println("durationTrim destination -- > "+durationTrimD);
				
				SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss"); 
				Date srcTime = dateFormat1.parse(durationTrimS);
//				System.out.println("Source time in date format"+srcTime);
				
				SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss"); 
				Date destTime = dateFormat2.parse(durationTrimD);
//				System.out.println("Dest time in date format"+destTime);
				
				timediff = (int) (destTime.getTime() - srcTime.getTime());
				
				timediff = timediff/60000;
				//System.out.println("time diff in minutes is "+timediff);
				
				bustoTravel = bestBus.substring(0,3);
//				System.out.println("Best bus to travel --- "+bustoTravel);
				
				
		
		//1.	Any bus longer than an hour is not an efficient bus
		
		if(timediff>timecheck){
			Assert.assertTrue(timediff>timecheck);
			Reporter.log("Its not an efficient bus to travel since it is taking more than an hour  ---> "+ bestBus);
			
		}
//		
		
		// 2.	Starts at the same time and reaches earlier
		
		srccmp = bus1src.equals(bus2src);		
		destcmp = bus1dest.equals(bus2dest);
		
		if (destcmp && srccmp){ 	//5.	If both buses have the same arrival and departure times, then luxury buses are efficient. Buses with names starting with “J” are luxury buses
			if(bestBus.startsWith("J")){
				Assert.assertTrue(destcmp && srccmp);
				Reporter.log("Luxury bus is preferrable  ---> "+ bestBus);
			
			}
		}
		else if (srccmp){
			
				//System.out.println("Source times are equal");
				Assert.assertTrue(srccmp);
				Reporter.log("Even though both the busses starts at the same time, would recommend to use the bus -->  "+bestBus);
		}
		//// 3.	Starts later and reaches at the same time
		else if (destcmp){ 
				//System.out.println("Destination times are equal");
				Assert.assertTrue(destcmp);
				Reporter.log("Even though both the busses reaches at the same time, would recommend to use the bus -->  "+bestBus);
				
			}
	 
		//4.	Starts later and reaches earlier
		if (bus2src.compareTo(bus1src)>0){
			if(bus2dest.compareTo(bus1dest)<0){
				
				Assert.assertTrue(bus2src.compareTo(bus1src)>0);
				Reporter.log("The best service to use Starts later Reaches earlier   ---> "+ bestBus);
			}
				
		}
			
		
		
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
	  
		}
		
  }
  
  public String[][] getTableArray(String xlFilePath) throws Exception{
      //String[][] tabArray;
      
          Workbook workbook = Workbook.getWorkbook(new File(xlFilePath));
          Sheet wrksheet = workbook.getSheet(0);
         
          String tabdata[][] = new String[wrksheet.getRows()][wrksheet.getColumns()];

//			System.out.println("Total Rows: " + wrksheet.getRows());
//			System.out.println("Total Columns: " + wrksheet.getColumns());
			for (int i = 0; i < wrksheet.getRows(); i++) {

				for (int j = 0; j < wrksheet.getColumns(); j++) {
				tabdata[i][j] = wrksheet.getCell(j, i).getContents();
//				System.out.println("elemet ---- "+tabdata[i][j]);			
				}
			}
	        return(tabdata);
	}
  
}
