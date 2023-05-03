package tools;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;
import javax.swing.JTextField;


public class Scale {

    private static JTextField lblInfo=new JTextField();
    
	public interface DLLMain extends StdCallLibrary {
		interface SENSORINFOCallback extends StdCallCallback {
			void SENSORINFO(double NetWeight, double Tare, double Grossweight, int Mode, int DecimalDigit, int UnitChar,
		            int OldAd, int FilterAd, int ZeroAd, int WeightAd, int flag);
			
		}
		
		interface IS1INFOCallback extends StdCallCallback {
			void IS1Info(String status, String sign, String Weight,
					String fUnit, int flag);
		}
		
		public void __SetCallBackFunc(String funcId, StdCallCallback IS1Info);

		public boolean __Open(String comname, int Bate);
		public boolean __Close();
		public String __GetWeight();

		public boolean __Zero(String ID);
		public boolean __Tare(String ID);
		public void __DoEvents();
		
	}
        
        public static void setTextField(JTextField j){
        lblInfo=j;}

	public static DLLMain MainDll = (DLLMain) Native.loadLibrary("SensorDll",DLLMain.class);

	public static DLLMain.IS1INFOCallback IS1INFO = new DLLMain.IS1INFOCallback(){
		
		public void IS1Info(String status, String sign, String Weight,
				String fUnit, int flag) {
			// TODO Auto-generated method stub
			lblInfo.setText(Weight);
		}
	};

	public static DLLMain.SENSORINFOCallback SENSORINFO = new DLLMain.SENSORINFOCallback() {

		String [] WeithUnit = {"公克","公斤","公吨","分(香港)","钱(香港)","两(香港)","斤(香港)",
	            "担(香港)","分(台湾)","钱(台湾)","两(台湾)","斤(台湾)","担(台湾)","盎司","磅","千磅"};
	  
	    public void SENSORINFO(double NetWeight, double Tare, double Grossweight, int Mode, int DecimalDigit, int UnitChar,
	            int OldAd, int FilterAd, int ZeroAd, int WeightAd, int flag){
	   	
	    	int flagMode = 0;
	        String unitval = WeithUnit[UnitChar];
	    }
	};
	
       
}
