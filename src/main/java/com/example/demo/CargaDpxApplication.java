package com.example.demo;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

import com.vasco.utils.AAL2Wrap;
import com.vasco.utils.KernelParms;
import com.vasco.utils.response.RespDPXGetTokenBlobsEx;

@SpringBootApplication

public class CargaDpxApplication implements CommandLineRunner {
	//@Autowired
	//DataSource dataSource;
	@Autowired
	MultivaConfiguration config;
	
		public static void main(String[] args) throws SQLException, ClassNotFoundException {
		SpringApplication.run(CargaDpxApplication.class, args);
	}
 
		@Override
		
		public void run(String...args) throws Exception {
			System.out.println(config.getFile());
			System.out.println(config.getfKey());
		

			String sInsertDPInstance = "INSERT INTO DPInstance VALUES (?,?,?,?,?,?)";
			String sInsertDPLicense = "INSERT INTO DPLicense VALUES (?,?,?,?,?,?,?,?,?)";
			
			int ITimeWindow=10;
	        int STimeWindow=24;
	        int DiagLevel=0;
	        int GMTAdjust=0;
	        int CheckChallenge=4;
	        int IThreshold=0;
	        int SThreshold=0;
	        int ChkInactDays=0;
	        int DeriveVector=0;
	        int SyncWindow=6;
	        int OnLineSG=2;
	        int EventWindow=100;
	        int HSMSlotID=0;
	        int StorageKeyID=0;
	        int StorageDeriveKey1=0;
	        int StorageDeriveKey2=0;
	        int StorageDeriveKey3=0;
	        int StorageDeriveKey4=0;
	      
	              
	        int [] parms={ITimeWindow, STimeWindow,      DiagLevel,        GMTAdjust,        CheckChallenge,   IThreshold,
	                      SThreshold,  ChkInactDays,     DeriveVector,     SyncWindow,       OnLineSG,         EventWindow,
	                      HSMSlotID,   StorageKeyID,     StorageDeriveKey1,StorageDeriveKey2,StorageDeriveKey3,StorageDeriveKey4};
			
	        String myMasterBlob = "";
	        KernelParms kernel  = new KernelParms(parms);
	        String StaticVector  = null;
	        String MessageVector = null;
	        String tokenType= null;         //common for all dpx
	        String [] authMode = null;      //common for all dpx
	        String [] dpData = null;        //common for all dpx
	        String [] serialAppl = null;    //common for all dpx
	        byte[][] bDPData = new byte[8][248];
	        short NumOfApps = 0;
	        String[][] myDB = new String[22400][5];
	        String ActivationVector = null;
	        int MaxInstances = 0;
	        String PLKBlob = null;
	        String TipoDeTokenEncontrado = "";
	        
	        

	        AAL2Wrap myVASCOWrapper = new AAL2Wrap();
	        String file = config.getFile();
	        String key = config.getfKey();
	        //String file = getUserInput("Enter path\filename of your DPX: ");
	        System.out.println("DPX File: "+ file); 
	        
			String [] ApplNames = myVASCOWrapper.AAL2DPXInit(file,key);
	        int NumDIGIPASS = myVASCOWrapper.getTokenCount();
			String staticVector = myVASCOWrapper.AAL2DPXGetStaticVector(kernel);
			NumOfApps = myVASCOWrapper.getAppliCount();

	        //
	          //Class.forName(sDriver); 
	         // Connection con = DriverManager.getConnection(sURL, sUser, sPass);
		//	Connection con = dataSource.getConnection();
	        
	        RespDPXGetTokenBlobsEx NewToken = null;
	        int item = 0;
	        int indexarray = 0;
	  //     try(PreparedStatement stmt = con.prepareStatement(sInsertDPLicense)) {
	        	
	        	
	        	 for (int i=0;i<NumDIGIPASS;i++){
	        		 
	        		 NewToken = myVASCOWrapper.AAL2DPXGetTokenBlobsEx(kernel); 
	                 tokenType= NewToken.getTokenType();
	                 authMode = NewToken.getAuthMode();
	                 dpData = NewToken.getStringDPData();
	                 bDPData = NewToken.getDPData();
	                 serialAppl = NewToken.getSerialAppl();
	                 ActivationVector = NewToken.getActivationVector();
	                 MaxInstances = NewToken.getSeqNumThreshold();
	                 PLKBlob = NewToken.getPKBlob();
	                 item = item + 1;
	                 
	  
	                 
	                 
	                 for (int j=0;j<NumOfApps;j++){
	                     RespDPXGetTokenBlobsEx MyDP = myVASCOWrapper.AAL2DPXGetTokenBlobsEx(kernel);
	                   	System.out.println("TokenType: " + MyDP.getTokenType()); 
	                   	System.out.println("AuthMode:  " + MyDP.getAuthMode()); 
	                   	System.out.println("PKBlob:  " + MyDP.getPKBlob());
	                   	
	                   	myDB[indexarray][0] = serialAppl[j];
	                    myDB[indexarray][1] = tokenType;
	                    myDB[indexarray][2] = authMode[j];
	                    myDB[indexarray][3] = dpData[j];
	                    myDB[indexarray][4] = ActivationVector;
	                    indexarray = indexarray + 1;
	                   	
	                   /*    stmt.setString(1, serialAppl[j]);
	                      stmt.setString(2,tokenType);
	                      stmt.setString(3,NumApps[j]);
	                      stmt.setString(4,authMode[j]);
	                     stmt.setString(5,dpData[j]);
	                     stmt.setString(6,ActivationVector);
	                     stmt.setInt(7,MyDP.getSeqNumThreshold());
	                     stmt.setString(8,MyDP.getPKBlob());
	                     stmt.setInt(9,j);*/
	                	
	                	// stmt.executeUpdate();
	                   	
	                   	  
	                     
	                 }
	                 myMasterBlob = myDB[0][3];
	                 System.out.println("Original BLOB: " + myMasterBlob);
	                 String myChallenge = myVASCOWrapper.AAL2GenerateChallenge(myMasterBlob.getBytes(), kernel);
	                 
	                 String myActMsg1 = null;
	                 String myActVector = myDB[0][4];
	               //  myActMsg1 = myVASCOWrapper.AAL2GenMessageActivation1(myMasterBlob.getBytes(), kernel, myChallenge, StaticVector, MessageVector, myActVector);
	                // String DeviceCode =  myVASCOWrapper.AAL2VerifyDeviceCode(arg0, arg1, arg2, arg3)
	                   
	                 //myVASCOWrapper.AAL2DPXClose();
	             }
	        /*	
	       } catch (SQLException e) {
	        	System.out.println(e.getMessage());
	        	myVASCOWrapper.AAL2DPXClose();
			}
	       */
		}


}
