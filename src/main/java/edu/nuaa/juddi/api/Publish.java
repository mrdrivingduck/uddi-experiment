package edu.nuaa.juddi.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.ws.Endpoint;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.api_v3.Publisher;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.TransportException;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.OverviewURL;
import org.uddi.api_v3.TModel;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

public class Publish {

	static UDDIClient uddiClient;

	public void publishBusiness(UDDIClient client, String clerkName) throws Exception {
		String businessName = client.getClientConfig().getHomeNode().getProperties().getProperty("businessName");
		String keyDomain = client.getClientConfig().getHomeNode().getProperties().getProperty("keyDomain");
		UDDIClerk clerk = client.getClerk(clerkName);

		// Creating the parent business entity that will contain our service.
		BusinessEntity myBusEntity = new BusinessEntity();
		Name myBusName = new Name();
		myBusName.setValue(businessName);
		myBusEntity.getName().add(myBusName);
		myBusEntity.setBusinessKey("uddi:" + keyDomain + ":business_" + businessName);
		clerk.register(myBusEntity);
	}

	public void publishWSDL(UDDIClerk clerk) throws MalformedURLException {
		// Register the wsdls for this clerk, referenced in the wsdl2uddi-uddi.xml
		clerk.registerWsdls(new URL("http://localhost:18080"));
	}

	public static void main(String args[]) {

		System.out.println("1. 将 hello world endpoint 发布到端口 18080");
		Endpoint helloWorldEndPoint = Endpoint.create(new HelloWorldImpl());
		helloWorldEndPoint.publish("http://localhost:18080/services/helloworld");

		System.out.println("2. 向 UDDI 注册 endpoint");
		Publish sp = new Publish();
		try {
			
			uddiClient = new UDDIClient("META-INF/zjt-uddi.xml");
			String clerkName = "zjt";

			UDDIClerk clerk = uddiClient.getClerk(clerkName);
			System.out.println("设置 publisher");
			sp.setupPublisher(uddiClient, clerkName);
			System.out.println("发布 business");
			sp.publishBusiness(uddiClient, clerkName);
			System.out.println("发布 WSDL");
			sp.publishWSDL(clerk);

			System.out.println("HelloWorldImpl 等待被调用...");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// This setup needs to be done once, either using the console or using code like
	// this
	private void setupPublisher(UDDIClient client, String clerkName)
			throws DispositionReportFaultMessage, RemoteException, ConfigurationException, TransportException {

		UDDISecurityPortType security = uddiClient.getTransport("default").getUDDISecurityService();
		UDDIClerk clerk = uddiClient.getClerk(clerkName);

		// login as root so we can create joe publisher
		GetAuthToken getAuthTokenRoot = new GetAuthToken();
		getAuthTokenRoot.setUserID("root");
		getAuthTokenRoot.setCred("");
		// Making API call that retrieves the authentication token for the 'root' user.
		AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
		System.out.println("获取 root AUTHTOKEN = " + rootAuthToken.getAuthInfo());

		// Creating joe publisher THIS IS JUDDI Specific code
		JUDDIApiPortType juddiApi = uddiClient.getTransport("default").getJUDDIApiService();
		Publisher p = new Publisher();
		p.setAuthorizedName(clerk.getName());
		p.setPublisherName(clerk.getPublisher());
		p.setIsEnabled(true);
		p.setIsAdmin(false);

		// Adding the publisher to the "save" structure, using the 'root' user
		// authentication info and saving away.
		SavePublisher sp = new SavePublisher();
		sp.getPublisher().add(p);
		sp.setAuthInfo(rootAuthToken.getAuthInfo());
		juddiApi.savePublisher(sp);
		// END jUDDI specific code

		String keyDomain = client.getClientConfig().getHomeNode().getProperties().getProperty("keyDomain");

		// Every publisher should have a keyGenerator, Joe has his:
		TModel keyGenerator = new TModel();
		keyGenerator.setTModelKey("uddi:" + keyDomain + ":keygenerator");
		Name name = new Name();
		name.setValue("ZJT's Key Generator");
		keyGenerator.setName(name);
		Description description = new Description();
		description.setValue("Test publishing!");
		keyGenerator.getDescription().add(description);

		OverviewDoc overviewDoc = new OverviewDoc();
		OverviewURL overviewUrl = new OverviewURL();
		overviewUrl.setUseType("text");
		overviewUrl.setValue("http://uddi.org/pubs/uddi_v3.htm#keyGen");
		overviewDoc.setOverviewURL(overviewUrl);
		keyGenerator.getOverviewDoc().add(overviewDoc);

		CategoryBag categoryBag = new CategoryBag();
		KeyedReference keyedReference = new KeyedReference();
		keyedReference.setKeyName("uddi-org:types:keyGenerator");
		keyedReference.setKeyValue("keyGenerator");
		keyedReference.setTModelKey("uddi:uddi.org:categorization:types");
		categoryBag.getKeyedReference().add(keyedReference);

		keyGenerator.setCategoryBag(categoryBag);
		clerk.register(keyGenerator);
	}
}
