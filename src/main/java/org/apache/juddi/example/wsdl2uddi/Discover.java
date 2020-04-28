package org.apache.juddi.example.wsdl2uddi;

import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;

public class Discover {

	public void discover() {
		try {
			UDDIClient uddiClient = new UDDIClient("META-INF/zjt-uddi.xml");
			UDDIClerk clerk = uddiClient.getClerk("zjt");
			String businessName = uddiClient.getClientConfig().getHomeNode().getProperties()
					.getProperty("businessName");
			String keyDomain = uddiClient.getClientConfig().getHomeNode().getProperties().getProperty("keyDomain");

			// System.out.println("Do a find business using the businessKey
			// uddi:uddi.joepublisher.com:business_wsdl-business");
			BusinessEntity businessEntity = clerk.getBusinessDetail("uddi:" + keyDomain + ":business_" + businessName);
			//

			if (businessEntity != null) {
				System.out.println("Found business with name " + businessEntity.getName().get(0).getValue());
				System.out.println(
						"Number of services: " + businessEntity.getBusinessServices().getBusinessService().size());

				for (BusinessService businessService : businessEntity.getBusinessServices().getBusinessService()) {
					System.out.println("Service Name        = '" + businessService.getName().get(0).getValue() + "'");
					System.out.println("Service Key         = '" + businessService.getServiceKey() + "'");
					System.out.println(
							"Service Description = '" + businessService.getDescription().get(0).getValue() + "'");
					System.out.println(
							"BindingTemplates: " + businessService.getBindingTemplates().getBindingTemplate().size());

					for (int i = 0; i < businessService.getBindingTemplates().getBindingTemplate().size(); i++) {
						BindingTemplate bindingTemplate = businessService.getBindingTemplates().getBindingTemplate()
								.get(i);
						System.out.println("--BindingTemplate" + " " + i + ":");
						System.out.println("  bindingKey          = " + bindingTemplate.getBindingKey());
						System.out.println("  accessPoint useType = " + bindingTemplate.getAccessPoint().getUseType());
						System.out.println("  accessPoint value   = " + bindingTemplate.getAccessPoint().getValue());
						System.out.println(
								"  description         = " + bindingTemplate.getDescription().get(0).getValue());
					}
				}
			}

			// TODO JUDDI-610
			// FindTModel findBindingTModel =
			// WSDL2UDDI.createFindBindingTModelForPortType(portType, namespace);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		Discover sp = new Discover();
		sp.discover();
	}
}
