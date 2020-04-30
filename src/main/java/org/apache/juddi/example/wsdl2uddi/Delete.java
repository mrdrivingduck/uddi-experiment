package org.apache.juddi.example.wsdl2uddi;

import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;

public class Delete {
	
	static UDDIClient uddiClient;
	
	public void deleteBusiness(UDDIClerk clerk) throws Exception {
		// Deleting the parent business entity that contains our service.
		String businessName = uddiClient.getClientConfig().getHomeNode().getProperties().getProperty("businessName");
		String keyDomain = uddiClient.getClientConfig().getHomeNode().getProperties().getProperty("keyDomain");

		clerk.unRegisterBusiness("uddi:" + keyDomain + ":business_" + businessName);
	}	
	
	public void deleteWSDL(UDDIClerk clerk) {
		// Register the wsdls for this clerk, referenced in the wsdl2uddi-uddi.xml
		clerk.unRegisterWsdls();
	}

	public static void main (String args[]) {
		
		Delete sp = new Delete();
		try {
			uddiClient = new UDDIClient("META-INF/zjt-uddi.xml");
			UDDIClerk clerk = uddiClient.getClerk("zjt");
			
			sp.deleteWSDL(clerk);
			sp.deleteBusiness(clerk);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
